/*
 * This file is a part of Alchemy OS project.
 *  Copyright (C) 2011-2014, Sergey Basalaev <sbasalaev@gmail.com>
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

package alchemy.nec.syntax.expr;

import alchemy.nec.syntax.type.Type;

/**
 * Literal expression.
 * @author Sergey Basalaev
 */
public final class ConstExpr extends Expr {

	private final int line;
	private final Type type;
	public final Object value;

	public ConstExpr(int line, Type type, Object value) {
		super(EXPR_CONST);
		this.line = line;
		this.type = type;
		this.value = value;
	}

	public int lineNumber() {
		return line;
	}

	public Type returnType() {
		return type;
	}

	public Object accept(ExprVisitor v, Object args) {
		return v.visitConst(this, args);
	}
}
