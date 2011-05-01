// various system functions

use "array";

def getenv(key: String): String;
def setenv(key: String, val: String);

def exec(prog: String, args: Array): Int;
def fork(prog: String, args: Array);

