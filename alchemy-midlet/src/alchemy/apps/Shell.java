/*
 * This file is a part of Alchemy OS project.
 *  Copyright (C) 2011  Sergey Basalaev <sbasalaev@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package alchemy.apps;

import alchemy.core.Context;
import alchemy.fs.File;
import alchemy.nlib.NativeApp;
import alchemy.util.UTFReader;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Vector;
import alchemy.apps.ConsoleForm.ConsoleInputStream;

/**
 * Native shell.
 * @author Sergey Basalaev
 */
public class Shell extends NativeApp {

	public Shell() { }

	public int main(Context c, String[] args) {
		try {
			InputStream scriptinput;
			if (args.length == 0) {
				scriptinput = c.stdin;
			} else {
				scriptinput = c.fs().read(c.toFile(args[0]));
			}
			if (c.stdin instanceof ConsoleInputStream) {
				((ConsoleInputStream)c.stdin).setPrompt(c.getCurDir().toString()+'>');
			}
			UTFReader r = new UTFReader(scriptinput);
			String line;
			while ((line = r.readLine()) != null) try {
				line = line.trim();
				if (line.length() == 0) continue;
				if (line.charAt(0) == '#') continue;
				ShellCommand cc;
				try { cc = split(line); }
				catch (IllegalArgumentException e) {
					c.stderr.println(r.lineNumber()+':'+e.toString());
					return 1;
				}
				if (cc.cmd.equals("exit")) {
					int ret = 0;
					if (cc.args.length > 0) try {
						ret = Integer.parseInt(cc.args[0]);
					} catch (NumberFormatException e) { ret = 1; }
					return ret;
				} else if (cc.cmd.equals("cd")) {
					if (cc.args.length > 0) {
						File newdir = c.toFile(cc.args[0]);
						c.setCurDir(newdir);
						if (c.stdin instanceof ConsoleInputStream) {
							((ConsoleInputStream)c.stdin).setPrompt(c.getCurDir().toString()+'>');
						}
					} else {
						c.stderr.println("cd: no directory specified");
					}
				} else if (cc.cmd.equals("cls")) {
					if (c.stdin instanceof ConsoleInputStream) {
						((ConsoleInputStream)c.stdin).clearScreen();
					}
				} else {
					Context child = new Context(c);
					if (cc.in != null) {
						child.stdin = c.fs().read(c.toFile(cc.in));
					}
					if (cc.out != null) {
						File outfile = new File(cc.out);
						if (cc.appendout) {
							child.stdout = new PrintStream(c.fs().append(outfile));
						} else {
							child.stdout = new PrintStream(c.fs().write(outfile));
						}
					}
					if (cc.err != null) {
						File outfile = new File(cc.out);
						if (cc.appendout) {
							child.stdout = new PrintStream(c.fs().append(outfile));
						} else {
							child.stdout = new PrintStream(c.fs().write(outfile));
						}
					}
					child.startAndWait(cc.cmd, cc.args);
				}
			} catch (Throwable t) {
				String msg = t.getMessage();
				c.stderr.println(msg == null ? t.toString() : msg);
			}
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			c.stderr.println(e);
			return 1;
		}
	}

	private static final int MODE_CMD = 0;
	private static final int MODE_ARG = 1;
	private static final int MODE_QARG = 2;
	private static final int MODE_IN = 3;
	private static final int MODE_OUT_W = 4;
	private static final int MODE_OUT_A = 5;
	private static final int MODE_ERR_W = 6;
	private static final int MODE_ERR_A = 7;

	private ShellCommand split(String line) throws IllegalArgumentException {
		ShellCommand cc = new ShellCommand();
		Vector argv = new Vector();
		int mode = MODE_CMD;
		while (line.length() > 0) {
			int end;
			String token;
			if (line.charAt(0) == '\'') {
				end = line.indexOf('\'', 1);
				if (end < 0) throw new IllegalArgumentException("Unclosed '");
				token = line.substring(1,end-1);
				if (mode == MODE_ARG) mode = MODE_QARG;
			} else if (line.charAt(0) == '"') {
				end = line.indexOf('"', 1);
				if (end < 0) throw new IllegalArgumentException("Unclosed \"");
				token = line.substring(1, end-1);
				if (mode == MODE_ARG) mode = MODE_QARG;
			} else {
				end = line.indexOf(' ');
				if (end < 0) end = line.length();
				token = line.substring(0,end);
			}
			line = line.substring(end).trim();
			if (token.length() == 0) continue;
			switch (mode) {
				case MODE_CMD:
					cc.cmd = token;
					mode = MODE_ARG;
					break;
				case MODE_IN:
					cc.in = token;
					mode = MODE_ARG;
					break;
				case MODE_OUT_W:
					cc.out = token;
					cc.appendout = false;
					mode = MODE_ARG;
					break;
				case MODE_OUT_A:
					cc.out = token;
					cc.appendout = true;
					mode = MODE_ARG;
					break;
				case MODE_ERR_W:
					cc.err = token;
					cc.appenderr = false;
					mode = MODE_ARG;
					break;
				case MODE_ERR_A:
					cc.err = token;
					cc.appenderr = true;
					mode = MODE_ARG;
					break;
				case MODE_ARG:
					if (token.equals(">") || token.equals("1>")) mode = MODE_OUT_W;
					else if (token.equals(">>") || token.equals("1>>")) mode = MODE_OUT_A;
					else if (token.equals("2>")) mode = MODE_ERR_W;
					else if (token.equals("2>>")) mode = MODE_ERR_A;
					else if (token.equals("<")) mode = MODE_IN;
					else argv.addElement(token);
					break;
				case MODE_QARG:
					argv.addElement(token);
			}
		}
		String[] args = new String[argv.size()];
		for (int i=0; i<argv.size(); i++) {
			args[i] = argv.elementAt(i).toString();
		}
		cc.args = args;
		return cc;
	}

	private static class ShellCommand {
		public String cmd;
		public String[] args;
		public String in;
		public String out;
		public boolean appendout;
		public String err;
		public boolean appenderr;
	}
}
