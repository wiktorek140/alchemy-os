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
 */

package alchemy.core;

/**
 * Function is an atomic piece of program execution.
 * Subclasses should provide implementation of
 * the {@link #exec(Context, Object[]) exec()} method.
 * <p/>
 * The {@link #signature signature} of a function is used in
 * stack trace output generated by {@link Context#dumpCallStack()}.
 * Also {@link HashLibrary} uses signatures as hash keys.
 * <p/>
 * When calling one function from another method
 * {@link #call(Context, Object[]) call()} should be used
 * in preference to <code>exec()</code>.
 * <p/>
 * This class also supplies a set of boxing/unboxing methods
 * to reduce code.
 *
 * @author Sergey Basalaev
 */
public abstract class Function {

	/** Function signature. */
	public final String signature;

	/**
	 * Constructor for subclasses
	 * @param sig    function signature
	 */
	protected Function(String sig) {
		if (sig == null) throw new NullPointerException();
		this.signature = sig;
	}

	/**
	 * Executes this function.
	 * @param c     execution context
	 * @param args  function arguments
	 * @return function result
	 * @throws Exception if an exception occurs
	 */
	protected abstract Object exec(Context c, Object[] args) throws Exception;

	/**
	 * Calls this function with given arguments.
	 * This method registers function on context call stack and should
	 * be used in preference of <code>exec</code> in function calls.
	 * 
	 * @param c    execution context
	 * @param args function arguments
	 * @return function result
	 * @throws Exception if an exception occurs during execution of function
	 */
	public final Object call(Context c, Object[] args) throws Exception {
		//synchronized (c.callStack) {
		c.callStack.push(this);
		//}
		Object result = exec(c, args);
		//synchronized (c.callStack) {
		c.callStack.pop();
		//}
		return result;
	}

	/**
	 * Returns string representation of this object.
	 * This method is used in output generated by
	 * <code>dumpCallStack</code>. By default this
	 * method returns function {@link #signature}
	 * though subclasses may provide additional
	 * information such as library name.
	 *
	 * @see Context#dumpCallStack()
	 */
	public String toString() {
		return signature;
	}

	public static final Integer ONE = new Integer(1);
	public static final Integer ZERO = new Integer(0);
	public static final Integer M_ONE = new Integer(-1);

	/** Boxing method for integer values. */
	protected static final Integer Ival(int value) {
		return new Integer(value);
	}

	/** Boxing method for boolean values.
	 * Method converts <code>true</code> to <code>Integer(1)</code>
	 * and <code>false</code> to <code>Integer(0)</code>.
	 */
	protected static final Integer Ival(boolean value) {
		return value ? ONE : ZERO;
	}

	/** Boxing method for long values. */
	protected static final Long Lval(long value) {
		return new Long(value);
	}

	/** Boxing method for float values. */
	protected static final Float Fval(float value) {
		return new Float(value);
	}

	/** Boxing method for double values. */
	protected static final Double Dval(double value) {
		return new Double(value);
	}

	/** Unboxing method for Integer values. */
	protected static final int ival(Object obj) {
		if (obj == null) return 0;
		return ((Integer)obj).intValue();
	}

	/** Unboxing method for Integer values.
	 * Method returns <code>false</code> iff <code>obj</code>
	 * is <code>Integer(0)</code>.
	 */
	protected static final boolean bval(Object obj) {
		if (obj == null) return false;
		return ((Integer)obj).intValue() != 0;
	}

	/** Unboxing method for Long values. */
	protected static final long lval(Object obj) {
		if (obj == null) return 0l;
		return ((Long)obj).longValue();
	}

	/** Unboxing method for Float values. */
	protected static final float fval(Object obj) {
		if (obj == null) return 0f;
		return ((Float)obj).floatValue();
	}

	/** Unboxing method for Double values. */
	protected static final double dval(Object obj) {
		if (obj == null) return 0d;
		return ((Double)obj).doubleValue();
	}
}
