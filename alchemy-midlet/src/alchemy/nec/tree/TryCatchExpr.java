/*
 * This file is a part of Alchemy OS project.
 *  Copyright (C) 2011-2013, Sergey Basalaev <sbasalaev@gmail.com>
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

package alchemy.nec.tree;

/**
 * Try-catch expression.
 * <pre>
 * <b>try</b>
 *   <i>tryexpr</i>
 * <b>catch</b> (<i>varname</i>)
 *   <i>catchexpr</i>
 * </pre>
 * 
 * @author Sergey Basalaev
 */
public class TryCatchExpr extends Expr {
	
	public Expr tryexpr;
	public Expr catchexpr;
	public Var catchvar;

	public TryCatchExpr() { }

	public Type rettype() {
		return tryexpr.rettype();
	}

	public int lineNumber() {
		return tryexpr.lineNumber();
	}
	
	public Object accept(ExprVisitor v, Object data) {
		return v.visitTryCatch(this, data);
	}
}
