package com.alibaba.simpleEL.dialect.tiny.visitor;

import java.io.PrintWriter;
import java.util.List;

import com.alibaba.simpleEL.ELException;
import com.alibaba.simpleEL.dialect.tiny.ast.TinyELArrayAccessExpr;
import com.alibaba.simpleEL.dialect.tiny.ast.TinyELAstNode;
import com.alibaba.simpleEL.dialect.tiny.ast.TinyELBinaryOpExpr;
import com.alibaba.simpleEL.dialect.tiny.ast.TinyELBooleanExpr;
import com.alibaba.simpleEL.dialect.tiny.ast.TinyELConditionalExpr;
import com.alibaba.simpleEL.dialect.tiny.ast.TinyELIdentifierExpr;
import com.alibaba.simpleEL.dialect.tiny.ast.TinyELMethodInvokeExpr;
import com.alibaba.simpleEL.dialect.tiny.ast.TinyELNewExpr;
import com.alibaba.simpleEL.dialect.tiny.ast.TinyELNullExpr;
import com.alibaba.simpleEL.dialect.tiny.ast.TinyELNumberLiteralExpr;
import com.alibaba.simpleEL.dialect.tiny.ast.TinyELPropertyExpr;
import com.alibaba.simpleEL.dialect.tiny.ast.TinyELStringExpr;
import com.alibaba.simpleEL.dialect.tiny.ast.TinyELVariantRefExpr;
import com.alibaba.simpleEL.dialect.tiny.ast.TinyUnaryOpExpr;
import com.alibaba.simpleEL.dialect.tiny.ast.stmt.TinyELExprStatement;
import com.alibaba.simpleEL.dialect.tiny.ast.stmt.TinyELForEachStatement;
import com.alibaba.simpleEL.dialect.tiny.ast.stmt.TinyELForStatement;
import com.alibaba.simpleEL.dialect.tiny.ast.stmt.TinyELIfStatement;
import com.alibaba.simpleEL.dialect.tiny.ast.stmt.TinyELIfStatement.Else;
import com.alibaba.simpleEL.dialect.tiny.ast.stmt.TinyELIfStatement.ElseIf;
import com.alibaba.simpleEL.dialect.tiny.ast.stmt.TinyELReturnStatement;
import com.alibaba.simpleEL.dialect.tiny.ast.stmt.TinyELStatement;
import com.alibaba.simpleEL.dialect.tiny.ast.stmt.TinyELVariantDeclareItem;
import com.alibaba.simpleEL.dialect.tiny.ast.stmt.TinyELWhileStatement;
import com.alibaba.simpleEL.dialect.tiny.ast.stmt.TinyLocalVarDeclareStatement;

public class TinyELOutputVisitor extends TinyELAstVisitorAdapter {
	protected PrintWriter out;
	private String indent = "\t";
	private int indentCount = 0;

	public TinyELOutputVisitor(PrintWriter out) {
		this.out = out;
	}

	public void decrementIndent() {
		this.indentCount -= 1;
	}

	public void incrementIndent() {
		this.indentCount += 1;
	}

	public void printIndent() {
		for (int i = 0; i < this.indentCount; ++i)
			print(this.indent);
	}

	public void println() {
		print("\n");
		printIndent();
	}

	public void println(String text) {
		print(text);
		println();
	}

	public void print(String text) {
		out.print(text);
	}

	public void print(char ch) {
		out.print(ch);
	}

	@Override
	public boolean visit(TinyELBinaryOpExpr x) {
	    switch (x.getOperator()) {
            case Add:
                print(" _add(");
                x.getLeft().accept(this);
                print(", ");
                x.getRight().accept(this);
                print(")");
                return false;
            case Multiply:
                print(" _multi(");
                x.getLeft().accept(this);
                print(", ");
                x.getRight().accept(this);
                print(")");
                return false;
            case Divide:
                print(" _div(");
                x.getLeft().accept(this);
                print(", ");
                x.getRight().accept(this);
                print(")");
                return false;
            case Subtract:
                print(" _sub(");
                x.getLeft().accept(this);
                print(", ");
                x.getRight().accept(this);
                print(")");
                return false;
            case GreaterThan:
                print(" _gt(");
                x.getLeft().accept(this);
                print(", ");
                x.getRight().accept(this);
                print(")");
                return false;
            case GreaterThanOrEqual:
                print(" _gteq(");
                x.getLeft().accept(this);
                print(", ");
                x.getRight().accept(this);
                print(")");
                return false;
            case LessThan:
                print(" _lt(");
                x.getLeft().accept(this);
                print(", ");
                x.getRight().accept(this);
                print(")");
                return false;
            case LessThanOrEqual:
                print(" _lteq(");
                x.getLeft().accept(this);
                print(", ");
                x.getRight().accept(this);
                print(")");
                return false;
            case Equality:
                print(" _eq(");
                x.getLeft().accept(this);
                print(", ");
                x.getRight().accept(this);
                print(")");
                return false;
            default:
                break;
        }
	    
		if (x.getLeft() instanceof TinyELBinaryOpExpr) {
			TinyELBinaryOpExpr left = (TinyELBinaryOpExpr) x.getLeft();
			if (left.getOperator().priority > x.getOperator().priority) {
				print('(');
				left.accept(this);
				print(')');
			} else {
				left.accept(this);
			}
		} else {
			x.getLeft().accept(this);
		}

		print(" ");
		print(x.getOperator().name);
		print(" ");

		if (x.getRight() instanceof TinyELBinaryOpExpr) {
			TinyELBinaryOpExpr right = (TinyELBinaryOpExpr) x.getRight();
			if (right.getOperator().priority >= x.getOperator().priority) {
				print('(');
				right.accept(this);
				print(')');
			} else {
				right.accept(this);
			}
		} else {
			x.getRight().accept(this);
		}

		return false;
	}

	@Override
	public boolean visit(TinyELIdentifierExpr x) {
		print(x.getName());
		return false;
	}

	@Override
	public boolean visit(TinyELNullExpr x) {
		print("null");
		return false;
	}

	@Override
	public boolean visit(TinyELPropertyExpr x) {
		if (x.getOwner() instanceof TinyELBinaryOpExpr) {
			print('(');
			x.getOwner().accept(this);
			print(')');
		} else {
			x.getOwner().accept(this);
		}

		print(".");
		print(x.getName());
		return false;
	}

	@Override
	public boolean visit(TinyELMethodInvokeExpr x) {
		if (x.getOwner() != null) {
			if (x.getOwner() instanceof TinyELBinaryOpExpr) {
				print('(');
				x.getOwner().accept(this);
				print(')');
			} else {
				x.getOwner().accept(this);
			}

			print(".");
		}
		print(x.getMethodName());
		print("(");
		printAndAccept(x.getParameters(), ", ");
		print(")");
		return false;
	}

	@Override
	public boolean visit(TinyELNewExpr x) {
		if (x.getOwner() != null) {
			if (x.getOwner() instanceof TinyELBinaryOpExpr) {
				print('(');
				x.getOwner().accept(this);
				print(')');
			} else {
				x.getOwner().accept(this);
			}

			print(".");
		}
		print("new ");

		print(x.getType());
		print("(");
		printAndAccept(x.getParameters(), ", ");
		print(")");
		return false;
	}

	@Override
	public boolean visit(TinyELNumberLiteralExpr x) {
		Number value = x.getValue();

		if (value == null) {
			print("null");
			return false;
		}

		print(x.getValue().toString());
		return false;
	}

	@Override
	public boolean visit(TinyELConditionalExpr x) {
		x.getCondition().accept(this);
		print(" ? ");
		x.getTrueExpr().accept(this);
		print(" : ");
		x.getFalseExpr().accept(this);
		return false;
	}

	@Override
	public boolean visit(TinyELExprStatement x) {
		x.getExpr().accept(this);
		print(";");
		return false;
	}

	@Override
	public boolean visit(TinyELReturnStatement x) {
		print("return ");
		x.getExpr().accept(this);
		print(";");
		return false;
	}

	@Override
	public boolean visit(TinyELBooleanExpr x) {
		if (x.getValue()) {
			print("true");
		} else {
			print("false");
		}
		return false;
	}

	@Override
	public boolean visit(TinyELArrayAccessExpr x) {
		x.getArray().accept(this);
		print("[");
		x.getIndex().accept(this);
		print("]");
		return false;
	}

	@Override
	public boolean visit(TinyELStringExpr x) {
		String value = x.getValue();
		if (value == null) {
			print("null");
		} else {
			print('"');
			for (char ch : value.toCharArray()) {
				switch (ch) {
				case '\t':
					print("\\t");
					break;
				case '\n':
					print("\\n");
					break;
				case '\r':
					print("\\r");
					break;
				case '\f':
					print("\\f");
					break;
				case '\b':
					print("\\b");
					break;
				case '\"':
					print("\\\"");
					break;
				default:
					print(ch);
				}
			}
			print('"');
		}

		return false;
	}

	@Override
	public boolean visit(TinyELVariantRefExpr x) {
		print(x.getName());
		return false;
	}

	@Override
	public boolean visit(Else x) {
		print(" else {");
		incrementIndent();
		println();

		printAndAccept(x.getStatementList());

		decrementIndent();
		println();
		print("}");
		return false;
	}

	@Override
	public boolean visit(ElseIf x) {
		print(" else if (");
		x.getCondition().accept(this);
		print(") {");
		incrementIndent();

		println();
		printAndAccept(x.getStatementList());

		decrementIndent();
		println();
		print("}");
		return false;
	}

	@Override
	public boolean visit(TinyELIfStatement x) {
		print(" if (");
		x.getCondition().accept(this);
		print(") {");
		incrementIndent();
		println();

		printAndAccept(x.getStatementList());

		decrementIndent();
		println();
		print("}");

		for (ElseIf elseIf : x.getElseIfList()) {
			elseIf.accept(this);
		}

		if (x.getElse() != null) {
			x.getElse().accept(this);
		}

		return false;
	}

	@Override
	public boolean visit(TinyLocalVarDeclareStatement x) {
		print(x.getType());
		print(" ");
		for (int i = 0, size = x.getVariants().size(); i < size; ++i) {
			if (i != 0) {
				print(", ");
			}
			x.getVariants().get(i).accept(this);
		}
		print(";");

		return false;
	}

	@Override
	public boolean visit(TinyELVariantDeclareItem x) {
		print(x.getName());
		if (x.getInitValue() != null) {
			print(" = ");
			x.getInitValue().accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(TinyELWhileStatement x) {
		print("while(");
		x.getCondition().accept(this);
		print(") {");
		incrementIndent();
		println();

		printAndAccept(x.getStatementList());

		decrementIndent();
		println();
		print("}");
		return false;
	}

	@Override
	public boolean visit(TinyELForEachStatement x) {
		print("for(");
		print(x.getType());
		print(' ');
		print(x.getVariant());
		print(" : ");
		x.getTargetExpr().accept(this);
		print(") {");
		incrementIndent();
		println();

		printAndAccept(x.getStatementList());

		decrementIndent();
		println();
		print("}");
		return false;
	}

	@Override
	public boolean visit(TinyELForStatement x) {
		print("for(");
		if (x.getVariants().size() != 0) {
			print(x.getType());
			print(' ');
			for (int i = 0, size = x.getVariants().size(); i < size; ++i) {
				if (i != 0) {
					print(", ");
				}
				x.getVariants().get(i).accept(this);
			}
		}
		print("; ");
		if (x.getCondition() != null) {
			x.getCondition().accept(this);
		}
		print("; ");
		if (x.getPostExpr() != null) {
			x.getPostExpr().accept(this);
		}
		print(") {");
		incrementIndent();
		println();

		printAndAccept(x.getStatementList());

		decrementIndent();
		println();
		print("}");
		return false;
	}

	@Override
	public boolean visit(TinyUnaryOpExpr x) {
		switch (x.getOperator()) {
		case Plus:
			print("+");
			x.getExpr().accept(this);
			break;
		case Minus:
			print("-");
			x.getExpr().accept(this);
			break;
		case PreIncrement:
			print("++");
			x.getExpr().accept(this);
			break;
		case PreDecrement:
			print("--");
			x.getExpr().accept(this);
			break;
		case PostIncrement:
			x.getExpr().accept(this);
			print("++");
			break;
		case PostDecrement:
			x.getExpr().accept(this);
			print("--");
			break;
		case Not:
		    print("!");
		    if (x.getExpr() instanceof TinyELBinaryOpExpr) {
		        print("(");
		        x.getExpr().accept(this);
		        print(")");
		    } else {
		        x.getExpr().accept(this);		        
		    }
		    break;
		default:
			throw new ELException("TOOD");
		}
		return false;
	}

	protected void printAndAccept(List<? extends TinyELStatement> statements) {
		for (int i = 0, size = statements.size(); i < size; ++i) {
			if (i != 0) {
				println();
			}
			statements.get(i).accept(this);
		}
	}

	protected void printAndAccept(List<? extends TinyELAstNode> nodes, String seperator) {
		for (int i = 0, size = nodes.size(); i < size; ++i) {
			if (i != 0) {
				print(seperator);
			}
			nodes.get(i).accept(this);
		}
	}
}
