package alchemy.nec;

import alchemy.nec.tree.*;

/**
 * Simple optimizer.
 * Folds constants and eliminates dead code.
 * @author Sergey Basalaev
 */
class Optimizer implements ExprVisitor {

	public void visitUnit(Unit u) {
		//visiting all functions
		for (int i=0; i<u.funcs.size(); i++) {
			visitFunc((Func)u.funcs.elementAt(i));
		}
		//DCE: removing unused functions
		for (int i=u.funcs.size()-1; i>=0; i--) {
			Func f = (Func)u.funcs.elementAt(i);
			if (f.hits == 0) {
				u.funcs.removeElementAt(i);
			}
		}
	}

	public void visitFunc(Func f) {
		if (f.body != null) {
			f.body = (Expr)f.body.accept(this, f);
		}
	}

	public Object visitALen(ALenExpr alen, Object scope) {
		alen.arrayexpr = (Expr)alen.arrayexpr.accept(this, scope);
		return alen;
	}

	public Object visitALoad(ALoadExpr aload, Object scope) {
		aload.arrayexpr = (Expr)aload.arrayexpr.accept(this, scope);
		aload.indexexpr = (Expr)aload.indexexpr.accept(this, scope);
		return aload;
	}

	public Object visitAStore(AStoreExpr astore, Object scope) {
		astore.arrayexpr = (Expr)astore.arrayexpr.accept(this, scope);
		astore.indexexpr = (Expr)astore.indexexpr.accept(this, scope);
		astore.assignexpr = (Expr)astore.assignexpr.accept(this, scope);
		return astore;
	}

	public Object visitAssign(AssignExpr assign, Object scope) {
		assign.expr = (Expr)assign.expr.accept(this, scope);
		return assign;
	}

	/**
	 * CF:
	 *   const op const   =>   const
	 */
	public Object visitBinary(BinaryExpr binary, Object scope) {
		binary.lvalue = (Expr)binary.lvalue.accept(this, scope);
		binary.rvalue = (Expr)binary.rvalue.accept(this, scope);
		if (binary.lvalue instanceof ConstExpr && binary.rvalue instanceof ConstExpr) {
			Object lval = ((ConstExpr)binary.lvalue).value;
			Object rval = ((ConstExpr)binary.rvalue).value;
			if (lval instanceof Integer) {
				switch (binary.operator) {
					case '+':
						lval = new Integer(((Integer)lval).intValue() + ((Integer)rval).intValue());
						break;
					case '-':
						lval = new Integer(((Integer)lval).intValue() - ((Integer)rval).intValue());
						break;
					case '*':
						lval = new Integer(((Integer)lval).intValue() * ((Integer)rval).intValue());
						break;
					case '/':
						if (((Integer)rval).intValue() != 0) {
							lval = new Integer(((Integer)lval).intValue() / ((Integer)rval).intValue());
						} else {
							return binary;
						}
						break;
					case '%':
						if (((Integer)rval).intValue() != 0) {
							lval = new Integer(((Integer)lval).intValue() % ((Integer)rval).intValue());
						} else {
							return binary;
						}
						break;
					case '=':
						if (lval.equals(rval)) {
							lval = new Integer(0);
						} else if (((Integer)lval).intValue() < ((Integer)rval).intValue()) {
							lval = new Integer(-1);
						} else {
							lval = new Integer(1);
						}
						break;
					case '&':
						lval = new Integer(((Integer)lval).intValue() & ((Integer)rval).intValue());
						break;
					case '|':
						lval = new Integer(((Integer)lval).intValue() | ((Integer)rval).intValue());
						break;
					case '^':
						lval = new Integer(((Integer)lval).intValue() ^ ((Integer)rval).intValue());
						break;
					case Tokenizer.TT_LTLT:
						lval = new Integer(((Integer)lval).intValue() << ((Integer)rval).intValue());
						break;
					case Tokenizer.TT_GTGT:
						lval = new Integer(((Integer)lval).intValue() >> ((Integer)rval).intValue());
						break;
					case Tokenizer.TT_GTGTGT:
						lval = new Integer(((Integer)lval).intValue() >>> ((Integer)rval).intValue());
						break;
				}
			} else if (lval instanceof Long) {
				switch (binary.operator) {
					case '+':
						lval = new Long(((Long)lval).longValue() + ((Long)rval).longValue());
						break;
					case '-':
						lval = new Long(((Long)lval).longValue() - ((Long)rval).longValue());
						break;
					case '*':
						lval = new Long(((Long)lval).longValue() * ((Long)rval).longValue());
						break;
					case '/':
						if (((Long)rval).longValue() != 0) {
							lval = new Long(((Long)lval).longValue() / ((Long)rval).longValue());
						} else {
							return binary;
						}
						break;
					case '%':
						if (((Long)rval).longValue() != 0) {
							lval = new Long(((Long)lval).longValue() % ((Long)rval).longValue());
						} else {
							return binary;
						}
						break;
					case '=':
						if (lval.equals(rval)) {
							lval = new Integer(0);
						} else if (((Long)lval).longValue() < ((Long)rval).longValue()) {
							lval = new Integer(-1);
						} else {
							lval = new Integer(1);
						}
						break;
					case '&':
						lval = new Long(((Long)lval).longValue() & ((Long)rval).longValue());
						break;
					case '|':
						lval = new Long(((Long)lval).longValue() | ((Long)rval).longValue());
						break;
					case '^':
						lval = new Long(((Long)lval).longValue() ^ ((Long)rval).longValue());
						break;
					case Tokenizer.TT_LTLT:
						lval = new Long(((Long)lval).longValue() << ((Integer)rval).intValue());
						break;
					case Tokenizer.TT_GTGT:
						lval = new Long(((Long)lval).longValue() >> ((Integer)rval).intValue());
						break;
					case Tokenizer.TT_GTGTGT:
						lval = new Long(((Long)lval).longValue() >>> ((Integer)rval).intValue());
						break;
				}
			} else if (lval instanceof Float) {
				switch (binary.operator) {
					case '+':
						lval = new Float(((Float)lval).floatValue() + ((Float)rval).floatValue());
						break;
					case '-':
						lval = new Float(((Float)lval).floatValue() - ((Float)rval).floatValue());
						break;
					case '*':
						lval = new Float(((Float)lval).floatValue() * ((Float)rval).floatValue());
						break;
					case '/':
						lval = new Float(((Float)lval).floatValue() / ((Float)rval).floatValue());
						break;
					case '%':
						lval = new Float(((Float)lval).floatValue() % ((Float)rval).floatValue());
						break;
					case '=':
						if (lval.equals(rval)) {
							lval = new Integer(0);
						} else if (((Float)lval).longValue() < ((Float)rval).longValue()) {
							lval = new Integer(-1);
						} else {
							lval = new Integer(1);
						}
						break;
				}
			} else if (lval instanceof Double) {
				switch (binary.operator) {
					case '+':
						lval = new Double(((Double)lval).doubleValue() + ((Double)rval).doubleValue());
						break;
					case '-':
						lval = new Double(((Double)lval).doubleValue() - ((Double)rval).doubleValue());
						break;
					case '*':
						lval = new Double(((Double)lval).doubleValue() * ((Double)rval).doubleValue());
						break;
					case '/':
						lval = new Double(((Double)lval).doubleValue() / ((Double)rval).doubleValue());
						break;
					case '%':
						lval = new Double(((Double)lval).doubleValue() % ((Double)rval).doubleValue());
						break;
					case '=':
						if (lval.equals(rval)) {
							lval = new Integer(0);
						} else if (((Long)lval).longValue() < ((Long)rval).longValue()) {
							lval = new Integer(-1);
						} else {
							lval = new Integer(1);
						}
						break;
				}
			} else if (lval instanceof Boolean) {
				switch (binary.operator) {
					case '&':
						lval = (((Boolean)lval).booleanValue() & ((Boolean)rval).booleanValue()
								? Boolean.TRUE : Boolean.FALSE);
						break;
					case '|':
						lval = (((Boolean)lval).booleanValue() | ((Boolean)rval).booleanValue()
								? Boolean.TRUE : Boolean.FALSE);
						break;
					case '^':
						lval = (((Boolean)lval).booleanValue() ^ ((Boolean)rval).booleanValue()
								? Boolean.TRUE : Boolean.FALSE);
						break;
					case '=':
						if (lval.equals(rval)) {
							lval = new Integer(0);
						} else {
							lval = new Integer(1);
						}
						break;
				}
			}
			return new ConstExpr(lval);
		}
		return binary;
	}

	/**
	 * CF:
	 *   { ... exprN-1; none; exprN+1; ... }   =>   { ... exprN-1; exprN+1; ... }
	 *   { expr }   =>   expr
	 *   {}   =>   none
	 */
	public Object visitBlock(BlockExpr block, Object scope) {
		int i=0;
		while (i < block.exprs.size()) {
			Expr ex = (Expr)block.exprs.elementAt(i);
			ex = (Expr)ex.accept(this, block);
			if (ex instanceof NoneExpr) {
				block.exprs.removeElementAt(i);
			} else {
				block.exprs.setElementAt(ex, i);
				i++;
			}
		}
		switch (block.exprs.size()) {
			case 0:
				return new NoneExpr();
			case 1:
				Expr e = (Expr)block.exprs.elementAt(0);
				if (block.locals.isEmpty()) {
					return e;
				}
			default:
				return block;
		}
	}

	public Object visitCast(CastExpr cast, Object scope) {
		return cast.expr.accept(this, scope);
	}

	/**
	 * CF:
	 *   cast(type) number   =>   castednumber
	 */
	public Object visitCastPrimitive(CastPrimitiveExpr cast, Object scope) {
		cast.expr.accept(this, scope);
		if (cast.expr instanceof ConstExpr) {
			Object cnst = ((ConstExpr)cast.expr).value;
			switch (cast.casttype) {
				case CastPrimitiveExpr.I2L:
					cnst = new Long(((Integer)cnst).longValue());
					break;
				case CastPrimitiveExpr.I2F:
					cnst = new Float(((Integer)cnst).floatValue());
					break;
				case CastPrimitiveExpr.I2D:
					cnst = new Double(((Integer)cnst).doubleValue());
					break;
				case CastPrimitiveExpr.L2I:
					cnst = new Integer((int)((Long)cnst).longValue());
					break;
				case CastPrimitiveExpr.L2F:
					cnst = new Float(((Long)cnst).floatValue());
					break;
				case CastPrimitiveExpr.L2D:
					cnst = new Double(((Long)cnst).doubleValue());
					break;
				case CastPrimitiveExpr.F2I:
					cnst = new Integer(((Float)cnst).intValue());
					break;
				case CastPrimitiveExpr.F2L:
					cnst = new Long(((Float)cnst).longValue());
					break;
				case CastPrimitiveExpr.F2D:
					cnst = new Double(((Float)cnst).doubleValue());
					break;
				case CastPrimitiveExpr.D2I:
					cnst = new Integer(((Double)cnst).intValue());
					break;
				case CastPrimitiveExpr.D2L:
					cnst = new Long(((Double)cnst).longValue());
					break;
				case CastPrimitiveExpr.D2F:
					cnst = new Float(((Double)cnst).floatValue());
					break;
			}
			return new ConstExpr(cnst);
		}
		return cast;
	}

	public Object visitConst(ConstExpr cexpr, Object scope) {
		return cexpr;
	}

	/**
	 * DCE:
	 *   const;   =>   none
	 *   var;     =>   none
	 */
	public Object visitDiscard(DiscardExpr disc, Object scope) {
		disc.expr = (Expr)disc.expr.accept(this, scope);
		if (disc.expr instanceof ConstExpr || disc.expr instanceof VarExpr) {
			return new NoneExpr();
		}
		return disc;
	}

	/**
	 * CF:
	 *   to_str(const)   =>   "const"
	 *   strcat("str1", "str2")   =>   "str1str2"
	 *   remove calls to constant/empty functions
	 */
	public Object visitFCall(FCallExpr fcall, Object scope) {
		fcall.fload = (Expr)fcall.fload.accept(this, scope);
		for (int i=0; i<fcall.args.length; i++) {
			fcall.args[i] = (Expr)fcall.args[i].accept(this, scope);
		}
		if (fcall.fload instanceof ConstExpr) {
			Func f = (Func)((ConstExpr)fcall.fload).value;
			if (f.asVar.name.equals("to_str")
			 && fcall.args[0] instanceof ConstExpr) {
				Object cnst = ((ConstExpr)fcall.args[0]).value;
				if (cnst == null || cnst.getClass() != Func.class) {
					f.hits--;
					return new ConstExpr(String.valueOf(cnst));
				}
			} else if (f.asVar.name.equals("strcat")
			        && fcall.args[0] instanceof ConstExpr
					&& fcall.args[1] instanceof ConstExpr) {
				Object c1 = ((ConstExpr)fcall.args[0]).value;
				Object c2 = ((ConstExpr)fcall.args[1]).value;
				f.hits--;
				return new ConstExpr(String.valueOf(c1).concat(String.valueOf(c2)));
			} else if (f.body instanceof ConstExpr || f.body instanceof NoneExpr) {
				// we don't need to call function but we still
				// need to calculate its arguments
				f.hits--;
				BlockExpr block = new BlockExpr((Scope)scope);
				for (int i=0; i<fcall.args.length; i++) {
					block.exprs.addElement(new DiscardExpr(fcall.args[i]));
				}
				block.exprs.addElement(f.body);
				return block.accept(this, scope);
			}
		}
		return fcall;
	}

	/**
	 * DCE:
	 *   if (true)  expr1 else expr2   =>   expr1
	 *   if (false) expr1 else expr2   =>   expr2
	 * CF:
	 *   if (if? true else false) expr1 else expr2  =>  if? expr1 else expr2
	 *   // the latter is common due to implementation of comparison
	 */
	public Object visitIf(IfExpr expr, Object scope) {
		//optimize children
		expr.condition = (Expr)expr.condition.accept(this, scope);
		expr.ifexpr = (Expr)expr.ifexpr.accept(this, scope);
		expr.elseexpr = (Expr)expr.elseexpr.accept(this, scope);
		//test condition
		if (expr.condition instanceof ConstExpr) {
			Object cond = ((ConstExpr)expr.condition).value;
			switch (expr.type) {
				case IfExpr.TRUE:
					if (Boolean.TRUE.equals(cond)) {
						return expr.ifexpr;
					} else {
						return expr.elseexpr;
					}
				case IfExpr.FALSE:
					if (Boolean.FALSE.equals(cond)) {
						return expr.ifexpr;
					} else {
						return expr.elseexpr;
					}
				case IfExpr.NULL:
					if (cond == null) {
						return expr.ifexpr;
					} else {
						return expr.elseexpr;
					}
				case IfExpr.NOTNULL:
					if (cond != null) {
						return expr.ifexpr;
					} else {
						return expr.elseexpr;
					}
				case IfExpr.ZERO:
					if (((Integer)cond).intValue() == 0) {
						return expr.ifexpr;
					} else {
						return expr.elseexpr;
					}
				case IfExpr.NOTZERO:
					if (((Integer)cond).intValue() != 0) {
						return expr.ifexpr;
					} else {
						return expr.elseexpr;
					}
				case IfExpr.NEG:
					if (((Integer)cond).intValue() < 0) {
						return expr.ifexpr;
					} else {
						return expr.elseexpr;
					}
				case IfExpr.NOTNEG:
					if (((Integer)cond).intValue() >= 0) {
						return expr.ifexpr;
					} else {
						return expr.elseexpr;
					}
				case IfExpr.POS:
					if (((Integer)cond).intValue() > 0) {
						return expr.ifexpr;
					} else {
						return expr.elseexpr;
					}
				case IfExpr.NOTPOS:
					if (((Integer)cond).intValue() <= 0) {
						return expr.ifexpr;
					} else {
						return expr.elseexpr;
					}
			}
		} else if (expr.condition instanceof IfExpr) {
			IfExpr innerif = (IfExpr)expr.condition;
			if (innerif.ifexpr instanceof ConstExpr
			 && ((ConstExpr)innerif.ifexpr).value == Boolean.TRUE
			 && innerif.elseexpr instanceof ConstExpr
			 && ((ConstExpr)innerif.elseexpr).value == Boolean.FALSE) {
				expr.condition = innerif.condition;
				expr.type = innerif.type;
			}
		}
		return expr;
	}

	public Object visitNewArray(NewArrayExpr newarray, Object scope) {
		newarray.lengthexpr = (Expr)newarray.lengthexpr.accept(this, scope);
		return newarray;
	}

	public Object visitNewArrayByEnum(NewArrayByEnumExpr newarray, Object scope) {
		for (int i=0; i < newarray.initializers.length; i++) {
			newarray.initializers[i] = (Expr)newarray.initializers[i].accept(this, scope);
		}
		return newarray;
	}

	public Object visitNone(NoneExpr none, Object scope) {
		return none;
	}

	/** CF:
	 *   !const   =>   const
	 *   -const   =>   const
	 */
	public Object visitUnary(UnaryExpr unary, Object scope) {
		unary.expr = (Expr)unary.expr.accept(this, scope);
		if (unary.expr instanceof ConstExpr) {
			Object cnst = ((ConstExpr)unary.expr).value;
			if (unary.operator == '!') {
				return new ConstExpr(cnst.equals(Boolean.TRUE) ? Boolean.FALSE : Boolean.TRUE);
			} else if (unary.operator == '-') {
				if (cnst instanceof Integer) {
					int oldval = ((Integer)cnst).intValue();
					return new ConstExpr(new Integer(-oldval));
				} else if (cnst instanceof Long) {
					long oldval = ((Long)cnst).longValue();
					return new ConstExpr(new Long(-oldval));
				} else if (cnst instanceof Float) {
					float oldval = ((Float)cnst).floatValue();
					return new ConstExpr(new Float(-oldval));
				} else if (cnst instanceof Double) {
					double oldval = ((Double)cnst).doubleValue();
					return new ConstExpr(new Double(-oldval));
				}
			}
		}
		return unary;
	}

	public Object visitVar(VarExpr vexpr, Object scope) {
		return vexpr;
	}

	/**
	 * DCE:
	 *  while (false) expr;   =>   none
	 */
	public Object visitWhile(WhileExpr wexpr, Object scope) {
		//optimize children
		wexpr.condition = (Expr)wexpr.condition.accept(this, scope);
		wexpr.body = (Expr)wexpr.body.accept(this, scope);
		//test condition
		if (wexpr.condition instanceof ConstExpr) {
			ConstExpr boolConst = (ConstExpr)wexpr.condition;
			if (boolConst.value.equals(Boolean.FALSE)) {
				return new NoneExpr();
			} //TODO: while(true) cycle
		}
		return wexpr;
	}
}
