/*
 * This file is a part of Alchemy OS project.
 *  Copyright (C) 2014, Sergey Basalaev <sbasalaev@gmail.com>
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

package alchemy.nec.syntax.statement;

import alchemy.nec.syntax.expr.Expr;

/**
 * Statement that consists of expression.
 * <pre><i>expr</i>;</pre>
 *
 * @author Sergey Basalaev
 */
public final class ExprStatement extends Statement {

	public Expr expr;

	public ExprStatement(Expr expr) {
		super(STAT_EXPR);
		this.expr = expr;
	}

	public int lineNumber() {
		return expr.lineNumber();
	}

	public Object accept(StatementVisitor v, Object args) {
		return v.visitExprStatement(this, args);
	}
}
