package redlaboratory.rljl.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import redlaboratory.rljl.vm.Operation;

public class Compiler {
	
	public Compiler() {
		
	}
	
	public List<Operation> generateCode(Token program) {
		List<Operation> ops = new ArrayList<>();
		
		for (Token stmt : program.getChildTokens()) {
			ops.addAll(codeStmt(stmt));
		}
		
		return ops;
	}
	
	private List<Operation> codeStmt(Token stmt) {
		TokenType type = stmt.getChildTokens()[0].getType();
		
		if (type == TokenType.STMT_EXPR) {
			return codeStmtExpr(stmt.getChildTokens()[0]);
		}
		
		return null;
	}
	
	private List<Operation> codeStmtExpr(Token stmtExpr) {
		return codeExpr(stmtExpr.getChildTokens()[0]);
	}
	
	private List<Operation> codeExpr(Token expr) {
		TokenType type = expr.getChildTokens()[0].getType();
		
		if (type == TokenType.EXPR_DEFINE) {
			return codeExprDefine(expr.getChildTokens()[0]);
		} else if (type == TokenType.EXPR_ADD) {
			return codeExprAdd(expr.getChildTokens()[0]);
		} else if (type == TokenType.EXPR_MUL) {
			return codeExprMul(expr.getChildTokens()[0]);
		} else if (type == TokenType.DATA_NUMBER) {
			return codeDataNumber(expr.getChildTokens()[0]);
		}
		
		return null;
	}
	
	private List<Operation> codeExprDefine(Token exprDefine) {
		List<Operation> ops = new ArrayList<>();
		
		Token identifier = exprDefine.getChildTokens()[0];
		Token type = exprDefine.getChildTokens()[1];
		Token expr = exprDefine.getChildTokens()[2];
		
		int reg = reg(identifier.getDataString());
		
		ops.addAll(codeExpr(expr));
		
		return ops;
	}
	
	private List<Operation> codeExprAdd(Token exprAdd) {
		return null;
	}
	
	private List<Operation> codeExprMul(Token exprMul) {
		return null;
	}
	
	private List<Operation> codeDataNumber(Token dataNumber) {
		dataNumber.getDataString();
		
		return null;
	}
	
	private ArrayList<String> symbols = new ArrayList<String>();
	
	private int reg(String symbol) {
		if (!symbols.contains(symbol)) symbols.add(symbol);
		
		return symbols.indexOf(symbol);
	}
	
}
