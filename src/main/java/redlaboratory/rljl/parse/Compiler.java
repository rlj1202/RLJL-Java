package redlaboratory.rljl.parse;

import java.util.ArrayList;
import java.util.List;

import redlaboratory.rljl.vm.OPCode;
import redlaboratory.rljl.vm.Operation;

public class Compiler {
	
	public Compiler() {
		
	}
	
	public List<Operation> generateCode(Token program) {
		return codeStmtBlock(program);
	}
	
	private List<Operation> codeStmtBlock(Token stmtBlock) {
		List<Operation> ops = new ArrayList<>();
		
		pushRegs();
		
		for (Token stmt : stmtBlock.getChildTokens()) {
			ops.addAll(codeStmt(stmt));
		}
		
		popRegs();
		
		return ops;
	}
	
	private List<Operation> codeStmt(Token stmt) {
		TokenType type = stmt.getChildTokens()[0].getType();
		
		if (type == TokenType.STMT_EXPR) {
			return codeStmtExpr(stmt.getChildTokens()[0]);
		} else if (type == TokenType.STMT_IF) {
			return codeStmtIf(stmt.getChildTokens()[0]);
		} else if (type == TokenType.STMT_FOR) {
			return codeStmtFor(stmt.getChildTokens()[0]);
		}
		
		return null;
	}
	
	private List<Operation> codeStmtIf(Token stmtIf) {
		List<Operation> ops = new ArrayList<>();
		
		Token exprCondition = stmtIf.getChildTokens()[0];
		Token trueBlock = stmtIf.getChildTokens()[1];
		Token falseBlock = stmtIf.getChildTokens()[2];
		
		List<Operation> trueBlockCodes = codeStmtBlock(trueBlock);
		List<Operation> falseBlockCodes = codeStmtBlock(falseBlock);
		
		ops.addAll(codeExpr(exprCondition, requestTmpReg()));
		ops.add(new Operation(OPCode.JMP_F, requestTmpReg(), trueBlockCodes.size() + 2));
		ops.addAll(trueBlockCodes);
		ops.add(new Operation(OPCode.JMP, falseBlockCodes.size() + 1));
		ops.addAll(falseBlockCodes);
		
		return ops;
	}
	
	private List<Operation> codeStmtFor(Token stmtFor) {
		ArrayList<Operation> ops = new ArrayList<Operation>();
		
		Token exprInit = stmtFor.getChildTokens()[0];
		Token exprCond = stmtFor.getChildTokens()[1];
		Token expr = stmtFor.getChildTokens()[2];
		Token block = stmtFor.getChildTokens()[3];
		
		pushRegs();
		
		List<Operation> initCodes = codeExpr(exprInit, requestTmpReg());
		List<Operation> condCodes = codeExpr(exprCond, requestTmpReg());
		List<Operation> exprCodes = codeExpr(expr, requestTmpReg());
		List<Operation> blockCodes = codeStmtBlock(block);
		
		ops.addAll(initCodes);
		ops.addAll(condCodes);
		ops.add(new Operation(OPCode.JMP_F, requestTmpReg(), blockCodes.size() + exprCodes.size() + 2));
		ops.addAll(blockCodes);
		ops.addAll(exprCodes);
		ops.add(new Operation(OPCode.JMP, -(blockCodes.size() + exprCodes.size() + condCodes.size() + 1)));
		
		popRegs();
		
		return ops;
	}
	
	private List<Operation> codeStmtExpr(Token stmtExpr) {
		return codeExpr(stmtExpr.getChildTokens()[0], requestTmpReg());
	}
	
	private List<Operation> codeExpr(Token expr, int reg) {
		if (expr.getChildTokens() == null || expr.getChildTokens().length == 0) return new ArrayList<Operation>();
		TokenType type = expr.getChildTokens()[0].getType();
		
		switch (type) {
		case EXPR_DEFINE:
			return codeExprDefine(expr.getChildTokens()[0], reg);
		case EXPR_ASSIGN:
			return codeExprAssign(expr.getChildTokens()[0], reg);
		case EXPR_ADD:
		case EXPR_SUB:
		case EXPR_MUL:
		case EXPR_DIV:
		case EXPR_MOD:
		case EXPR_EQ:
		case EXPR_NEQ:
		case EXPR_GT:
		case EXPR_GTE:
		case EXPR_LT:
		case EXPR_LTE:
			return codeExprCal(expr.getChildTokens()[0], reg);
		case FALSE:
		case TRUE:
			return codeBoolVal(expr.getChildTokens()[0], reg);
		case DATA_NUMBER:
			return codeDataNumber(expr.getChildTokens()[0], reg);
		case IDENTIFIER:
			return codeRegData(expr.getChildTokens()[0], reg);
		default:
			break;
		}
		
		return null;
	}
	
	private List<Operation> codeRegData(Token identifier, int reg) {
		List<Operation> ops = new ArrayList<>();
		
		ops.add(new Operation(OPCode.MOVE, reg, requestReg(identifier.getDataString())));
		
		return ops;
	}
	
	private List<Operation> codeExprAssign(Token exprAssign, int reg) {
		List<Operation> ops = new ArrayList<>();
		
		Token identifier = exprAssign.getChildTokens()[0];
		Token expr = exprAssign.getChildTokens()[1];
		
		reg = requestReg(identifier.getDataString());
		
		ops.addAll(codeExpr(expr, requestTmpReg()));
		ops.add(new Operation(OPCode.MOVE, reg, requestTmpReg()));
		
		return ops;
	}
	
	private List<Operation> codeExprDefine(Token exprDefine, int reg) {
		List<Operation> ops = new ArrayList<>();
		
		Token identifier = exprDefine.getChildTokens()[0];
		Token type = exprDefine.getChildTokens()[1];
		Token expr = exprDefine.getChildTokens()[2];
		
		reg = allocateReg(identifier.getDataString());
		
		if (expr != null) {
			ops.addAll(codeExpr(expr, requestTmpReg()));
			ops.add(new Operation(OPCode.MOVE, reg, requestTmpReg()));
		}
		
		return ops;
	}
	
	private List<Operation> codeBoolVal(Token exprBool, int reg) {
		ArrayList<Operation> ops = new ArrayList<>();
		
		if (exprBool.getType() == TokenType.TRUE) ops.add(new Operation(OPCode.LOAD, reg, 1));
		if (exprBool.getType() == TokenType.FALSE) ops.add(new Operation(OPCode.LOAD, reg, 0));
		
		return ops;
	}
	
	private List<Operation> codeExprCal(Token exprCal, int reg) {
		OPCode code = null;
		if (exprCal.getType() == TokenType.EXPR_ADD) code = OPCode.ADD;
		else if (exprCal.getType() == TokenType.EXPR_SUB) code = OPCode.SUB;
		else if (exprCal.getType() == TokenType.EXPR_MUL) code = OPCode.MUL;
		else if (exprCal.getType() == TokenType.EXPR_DIV) code = OPCode.DIV;
		else if (exprCal.getType() == TokenType.EXPR_MOD) code = OPCode.MOD;
		else if (exprCal.getType() == TokenType.EXPR_EQ) code = OPCode.EQ;
		else if (exprCal.getType() == TokenType.EXPR_NEQ) code = OPCode.NEQ;
		else if (exprCal.getType() == TokenType.EXPR_GT) code = OPCode.GT;
		else if (exprCal.getType() == TokenType.EXPR_GTE) code = OPCode.GTE;
		else if (exprCal.getType() == TokenType.EXPR_LT) code = OPCode.LT;
		else if (exprCal.getType() == TokenType.EXPR_LTE) code = OPCode.LTE;
		
		List<Operation> ops = new ArrayList<>();
		
		Token l = exprCal.getChildTokens()[0];
		Token r = exprCal.getChildTokens()[1];
		
		ops.addAll(codeExpr(l, reg));
		ops.addAll(codeExpr(r, reg + 1));
		ops.add(new Operation(code, reg, reg, reg + 1));
		
		return ops;
	}
	
	private List<Operation> codeDataNumber(Token dataNumber, int reg) {
		List<Operation> ops = new ArrayList<>();
		
		int test = (int) Double.parseDouble(dataNumber.getDataString());
		
		ops.add(new Operation(OPCode.LOAD, reg, test));
		
		return ops;
	}
	
	private ArrayList<ArrayList<String>> symbolsPerDepth = new ArrayList<ArrayList<String>>(); 
	private int requiredRegs = 0;// max regs
	
	private void pushRegs() {
		symbolsPerDepth.add(new ArrayList<String>());
		
		System.out.println("push regs");
	}
	
	private void popRegs() {
		symbolsPerDepth.remove(symbolsPerDepth.size() - 1);
		
		System.out.println("pop regs");
	}
	
	private int allocateReg(String symbol) {
		ArrayList<String> symbols = symbolsPerDepth.get(symbolsPerDepth.size() - 1);
		if (!symbols.contains(symbol)) symbols.add(symbol);
		
		int reg = 0;
		
		for (int i = 0; i < symbolsPerDepth.size() - 1; i++) reg += symbolsPerDepth.get(i).size();
		reg += symbols.indexOf(symbol);
		
		System.out.println("allocate reg " + symbol + " : " + reg);
		
		return reg;
	}
	
	private int requestReg(String symbol) {
		int reg = -1;
		
		for (int i = symbolsPerDepth.size() - 1; i >= 0; i--) {
			ArrayList<String> symbols = symbolsPerDepth.get(i);
			
			if ( ( reg = symbols.indexOf(symbol) ) != -1 ) {
				for (int j = 0; j < i; j++) reg += symbolsPerDepth.get(j).size();
				break;
			}
		}
		
		System.out.println("request reg " + symbol + " : " + reg);
		return reg;
	}
	
	private int requestTmpReg() {
		int reg = 0;
		
		for (int i = 0; i < symbolsPerDepth.size(); i++) reg += symbolsPerDepth.get(i).size();
		
		System.out.println("request tmp reg : " + reg);
		
		return reg;
	}
	
}
