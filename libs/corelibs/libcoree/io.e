/* Core library: I/O functions
 * (C) 2011-2013 Sergey Basalaev
 * Licensed under GPL v3 with linkage exception
 */

use "list.eh"
use "textio.eh"

def read(): Int = stdin().read()
def readarray(buf: [Byte], ofs: Int, len: Int): Int = stdin().readarray(buf, ofs, len)
def skip(num: Long): Long = stdin().skip(num)
def readline(): String = utfreader(stdin()).readline()

def write(b: Int) = stdout().write(b)
def writearray(buf: [Byte], ofs: Int, len: Int) = stdout().writearray(buf, ofs, len)
def print(a: Any) = stdout().print(a)
def println(a: Any) = stdout().println(a)
def flush() = stdout().flush()

def OStream.printf(fmt: String, args: [Any]) = this.print(fmt.format(args))
def printf(fmt: String, args: [Any]) = stdout().print(fmt.format(args))

def flistfilter(path: String, glob: String): [String] {
  var files = flist(path)
  var list = new List()
  for (var i=0, i < files.len, i += 1) {
    var file = files[i]
    if (matches_glob(file, glob) || matches_glob(file, glob + '/')) list.add(file)
  }
  var strings = new [String](list.len())
  list.copyinto(0, strings, 0, strings.len)
  strings
}