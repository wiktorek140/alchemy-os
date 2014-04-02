/*
 * Copyright (C) 2014 <copyright-holder>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alchemy.libs.ui;

import alchemy.platform.UI;
import alchemy.system.UIServer;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

/**
 *
 * @author Sergey Basalaev
 */
public class UiPlatform implements UI {

	public static final int DEFAULT_WIDTH = 480;
	public static final int DEFAULT_HEIGHT = 640;

	private final JFrame frame;
	private UiScreen current;
	private final UiScreen noscreen;

	private JMenuBar menuBar = new JMenuBar();

	public UiPlatform() {
		UiMsgBox msgbox = new UiMsgBox();
		msgbox.setText("Nothing to display");
		noscreen = msgbox;

		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(menuBar, BorderLayout.SOUTH);
		current = noscreen;
		setCurrentScreen(null);
		frame.setVisible(true);
	}

	@Override public Object getCurrentScreen() {
		UiScreen screen = current;
		return (screen != noscreen) ? screen : null;
	}

	@Override public void setCurrentScreen(Object screen) {
		if (screen == null) screen = noscreen;

		frame.remove(current.getWidget());
		current = (UiScreen) screen;
		frame.setTitle(current.getTitle() + " - Alchemy OS");
		frame.add(current.getWidget(), BorderLayout.CENTER);

		rebuildMenu(current.getMenus());
		frame.pack();
	}

	@Override public void setIcon(Object icon) {
		
	}

	@Override public boolean vibrate(int millis) {
		Toolkit.getDefaultToolkit().beep();
		return false;
	}

	@Override public boolean flash(int millis) {
		Toolkit.getDefaultToolkit().beep();
		return false;
	}

	@Override public void screenTitleChanged(Object screen, String title) {
		if (screen == current) {
			frame.setTitle(title + " - Alchemy OS");
		}
	}

	@Override public void screenMenuAdded(Object screen, Object menu) {
		if (screen == current) {
			rebuildMenu(current.getMenus());
		}
	}

	@Override public void screenMenuRemoved(Object screen, Object menu) {
		if (screen == current) {
			rebuildMenu(current.getMenus());
		}
	}

	private void rebuildMenu(Set<UiMenu> set) {
		ArrayList<UiMenu> list = new ArrayList<UiMenu>(set);
		Collections.sort(list, new Comparator<UiMenu>() {
			@Override public int compare(UiMenu m1, UiMenu m2) {
				return m2.getPriority() - m1.getPriority();
			}
		});
		menuBar.removeAll();
		for (UiMenu menu : list) {
			menuBar.add(new JMenu(new MenuButtonAction(menu)));
		}
	}

	/** Action to inform UIServer about menu events. */
	private class MenuButtonAction extends AbstractAction {

		private final UiMenu menu;

		public MenuButtonAction(UiMenu menu) {
			super(menu.getLabel());
			this.menu = menu;
		}

		@Override public void actionPerformed(ActionEvent e) {
			UIServer.pushEvent(UIServer.EVENT_MENU, current, menu);
		}
	}
}