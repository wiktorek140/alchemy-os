// array operations

type BArray;
type CArray;
type IArray;
type LArray;
type FArray;
type DArray;
type AArray;

def new_ba(len: Int): BArray;
def new_ca(len: Int): CArray;
def new_ia(len: Int): IArray;
def new_la(len: Int): LArray;
def new_fa(len: Int): FArray;
def new_da(len: Int): DArray;
def new_aa(len: Int): AArray;

def baload(ba: BArray, at: Int): Int;
def caload(ca: CArray, at: Int): Int;
def iaload(ia: IArray, at: Int): Int;
def laload(la: LArray, at: Int): Int;
def faload(fa: FArray, at: Int): Int;
def daload(da: DArray, at: Int): Int;
def aaload(aa: AArray, at: Int): Int;

def bastore(ba: BArray, at: Int, val: Int);
def castore(ca: CArray, at: Int, val: Int);
def iastore(ia: IArray, at: Int, val: Int);
def lastore(la: LArray, at: Int, val: Long);
def fastore(fa: FArray, at: Int, val: Float);
def dastore(da: DArray, at: Int, val: Double);
def aastore(aa: AArray, at: Int, val: Any);

def balen(ba: BArray): Int;
def calen(ca: CArray): Int;
def ialen(ia: IArray): Int;
def lalen(la: LArray): Int;
def falen(fa: FArray): Int;
def dalen(da: DArray): Int;
def aalen(aa: AArray): Int;

def bacopy(src: BArray, sofs: Int, dst: BArray, dofs: Int, len: Int);
def cacopy(src: CArray, sofs: Int, dst: CArray, dofs: Int, len: Int);
def iacopy(src: IArray, sofs: Int, dst: IArray, dofs: Int, len: Int);
def lacopy(src: LArray, sofs: Int, dst: LArray, dofs: Int, len: Int);
def facopy(src: FArray, sofs: Int, dst: FArray, dofs: Int, len: Int);
def dacopy(src: DArray, sofs: Int, dst: DArray, dofs: Int, len: Int);
def aacopy(src: AArray, sofs: Int, dst: AArray, dofs: Int, len: Int);
