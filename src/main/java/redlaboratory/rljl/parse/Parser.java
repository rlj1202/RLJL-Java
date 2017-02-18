package redlaboratory.rljl.parse;

import java.util.ArrayList;
import java.util.List;

public class Parser {
	
	public static class Error {
		
		private Token token;
		private String message;
		
		public Error(Token token, String message) {
			this.token = token;
			this.message = message;
		}
		
		public Token getToken() {
			return token;
		}
		
		public String getMessage() {
			return message;
		}
		
		@Override
		public String toString() {
			return token.getType() + ":" + token.getDataString() + ":" + token.getIndex() + ":" + message;
		}
		
	}
	
	private int indexT = 0;
	public String code;
	
	private int indexP = 0;
	public List<Token> tokens;
	public List<Error> errors;
	
	public Parser() {
		tokens = new ArrayList<Token>();
		errors = new ArrayList<Error>();
	}
	
	private boolean isAlphabet(char c) {
		return (0x0041 <= c && c <= 0x005A) || (0x0061 <= c && c <= 0x007A);
	}
	
	private boolean isDigit(char c) {
		return (0x0030 <= c && c <= 0x0039);
	}
	
	private boolean exist(String str) {
		if (indexT + str.length() > code.length()) return false;
		
		if (code.substring(indexT, indexT + str.length()).equals(str)) {
			if (indexT + str.length() + 1 > code.length()) return true;
			
			if (isAlphabet(code.charAt(indexT + str.length() - 1)) && isAlphabet(code.charAt(indexT + str.length()))) {
				return false;
			} else {
				return true;
			}
		}
		
		return false;
	}
	
	private Token getToken(int index) {
		if (index >= tokens.size()) return null;
		
		Token token = tokens.get(index);
		
		if (token.getType().equals(TokenType.ANNOTATION_LINE) || token.getType().equals(TokenType.ANNOTATION_BLOCK)) {
			indexP++;
			return getToken(index + 1);
		}
		
		return tokens.get(index);
	}
	
	private boolean exist(TokenType type) {
		return exist(type, 0);
	}
	
	private boolean exist(TokenType type, int offset) {
		Token token = getToken(indexP + offset);
		
		return token != null ? token.getType().equals(type) : false;
	}
	
	private Token accept() {
		return getToken(indexP++);
	}
	
	public List<Token> tokenize() {
		List<Token> tokens = new ArrayList<Token>();
		
		while (indexT < code.length()) {
			char c = code.charAt(indexT);
			
			if (c == 'd') {
				if (exist("def")) {
					tokens.add(new Token(TokenType.DEF, indexT));
					indexT += 3;
					continue;
				} else if (exist("double")) {
					tokens.add(new Token(TokenType.DOUBLE, indexT));
					indexT += 6;
					continue;
				}
			} else if (c == 'a') {
				if (exist("as")) {
					tokens.add(new Token(TokenType.AS, indexT));
					indexT += 2;
					continue;
				}
			} else if (c == 'f') {
				if (exist("func")) {
					tokens.add(new Token(TokenType.FUNC, indexT));
					indexT += 4;
					continue;
				} else if (exist("false")) {
					tokens.add(new Token(TokenType.FALSE, indexT));
					indexT += 5;
					continue;
				} else if (exist("for")) {
					tokens.add(new Token(TokenType.FOR, indexT));
					indexT += 3;
					continue;
				}
			} else if (c == 's') {
				if (exist("struct")) {
					tokens.add(new Token(TokenType.STRUCT, indexT));
					indexT += 6;
					continue;
				} else if (exist("switch")) {
					tokens.add(new Token(TokenType.SWITCH, indexT));
					indexT += 6;
					continue;
				} else if (exist("string")) {
					tokens.add(new Token(TokenType.STRING, indexT));
					indexT += 6;
					continue;
				}
			} else if (c == 'i') {
				if (exist("int")) {
					tokens.add(new Token(TokenType.INT, indexT));
					indexT += 3;
					continue;
				} else if (exist("if")) {
					tokens.add(new Token(TokenType.IF, indexT));
					indexT += 2;
					continue;
				}
			} else if (c == 'r') {
				if (exist("return")) {
					tokens.add(new Token(TokenType.RETURN, indexT));
					indexT += 6;
					continue;
				}
			} else if (c == 't') {
				if (exist("true")) {
					tokens.add(new Token(TokenType.TRUE, indexT));
					indexT += 4;
					continue;
				}
			} else if (c == 'e') {
				if (exist("else")) {
					tokens.add(new Token(TokenType.ELSE, indexT));
					indexT += 4;
					continue;
				}
			} else if (c == '(') {
				tokens.add(new Token(TokenType.PAREN_L, indexT));
				indexT++;
				continue;
			} else if (c == ')') {
				tokens.add(new Token(TokenType.PAREN_R, indexT));
				indexT++;
				continue;
			} else if (c == '[') {
				tokens.add(new Token(TokenType.BRACKET_L, indexT));
				indexT++;
				continue;
			} else if (c == ']') {
				tokens.add(new Token(TokenType.BRACKET_R, indexT));
				indexT++;
				continue;
			} else if (c == '{') {
				tokens.add(new Token(TokenType.BRACE_L, indexT));
				indexT++;
				continue;
			} else if (c == '}') {
				tokens.add(new Token(TokenType.BRACE_R, indexT));
				indexT++;
				continue;
			} else if (c == '+') {
				tokens.add(new Token(TokenType.ADD, indexT));
				indexT++;
				continue;
			} else if (c == '-') {
				if (exist("->")) {
					tokens.add(new Token(TokenType.ARROW_R, indexT));
					indexT += 2;
					continue;
				} else {
					tokens.add(new Token(TokenType.SUB, indexT));
					indexT++;
					continue;
				}
			} else if (c == '*') {
				tokens.add(new Token(TokenType.MUL, indexT));
				indexT++;
				continue;
			} else if (c == '/') {
				if (exist("//")) {
					int from = indexT;
					indexT += 2;
					
					while (!exist("\n")) {
						indexT++;
					}
					indexT++;// accept '\n'
					
					tokens.add(new Token(TokenType.ANNOTATION_LINE, from, code.substring(from, indexT - 1)));// The reason why i sub 1 to indexT is to exclude '\n' char from data string.
					continue;
				} else if (exist("/*")) {
					int from = indexT;
					indexT += 2;
					
					while (!exist("*/")) {
						indexT++;
					}
					indexT += 2;// accept "*/"
					
					tokens.add(new Token(TokenType.ANNOTATION_BLOCK, from, code.substring(from, indexT)));
					continue;
				} else {
					tokens.add(new Token(TokenType.DIV, indexT));
					indexT++;
					continue;	
				}
			} else if (c == '%') {
				tokens.add(new Token(TokenType.MOD, indexT));
				indexT++;
				continue;
			} else if (c == '.') {
				tokens.add(new Token(TokenType.PERIOD, indexT));
				indexT++;
				continue;
			} else if (c == ',') {
				tokens.add(new Token(TokenType.COMMA, indexT));
				indexT++;
				continue;
			} else if (c == ':') {
				tokens.add(new Token(TokenType.COLON, indexT));
				indexT++;
				continue;
			} else if (c == ';') {
				tokens.add(new Token(TokenType.SEMICOLON, indexT));
				indexT++;
				continue;
			} else if (c == '=') {
				if (exist("==")) {
					tokens.add(new Token(TokenType.EQ, indexT));
					indexT += 2;
					continue;
				} else {
					tokens.add(new Token(TokenType.ASSIGN, indexT));
					indexT++;
					continue;
				}
			} else if (c == '&') {
				if (exist("&&")) {
					tokens.add(new Token(TokenType.LOGIC_AND, indexT));
					indexT += 2;
					continue;
				} else if (exist("&")) {
					tokens.add(new Token(TokenType.BIT_AND, indexT));
					indexT += 1;
					continue;
				}
			} else if (c == '|') {
				if (exist("||")) {
					tokens.add(new Token(TokenType.LOGIC_OR, indexT));
					indexT += 2;
					continue;
				} else if (exist("|")) {
					tokens.add(new Token(TokenType.BIT_OR, indexT));
					indexT += 1;
					continue;
				}
			} else if (c == '!') {
				if (exist("!=")) {
					tokens.add(new Token(TokenType.NEQ, indexT));
					indexT += 2;
					continue;
				} else {
					tokens.add(new Token(TokenType.LOGIC_NOT, indexT));
					indexT++;
					continue;
				}
			} else if (c == '>') {
				if (exist(">=")) {
					tokens.add(new Token(TokenType.GTE, indexT));
					indexT += 2;
					continue;
				} else {
					tokens.add(new Token(TokenType.GT, indexT));
					indexT++;
					continue;
				}
			} else if (c == '<') {
				if (exist("<=")) {
					tokens.add(new Token(TokenType.LTE, indexT));
					indexT += 2;
					continue;
				} else {
					tokens.add(new Token(TokenType.LT, indexT));
					indexT++;
					continue;
				}
			} else if (c == '\"') {
				int from = indexT;
				indexT++;
				while (indexT < code.length()) {
					char cE = code.charAt(indexT);
					
					if (cE == '\\') {// TODO
						indexT += 2;
					} else if (cE == '\"') {
						indexT++;
						break;
					} else {
						indexT++;
					}
				}
				String str = code.substring(from, indexT);
				tokens.add(new Token(TokenType.DATA_STRING, indexT, str));
				continue;
			} else if (c == ' ' || c == '\t' || c == '\n') {
				indexT++;
				continue;
			}
			
			if (isDigit(c)) {// 0 ~ 9, number
				int from = indexT;
				indexT++;
				while (indexT < code.length()) {
					char cE = code.charAt(indexT);
					
					if (cE == '.') {
						indexT++;
						
						while (indexT < code.length()) {
							cE = code.charAt(indexT);
							
							if (isDigit(cE)) {
								indexT++;
							} else {
								break;
							}
						}
						
						break;
					}
					
					if (isDigit(cE)) {
						indexT++;
					} else {
						break;
					}
				}
				String str = code.substring(from, indexT);
				tokens.add(new Token(TokenType.DATA_NUMBER, indexT, str));
			} else {// identifier
				int from = indexT;
				indexT++;
				while (indexT < code.length()) {
					char cE = code.charAt(indexT);
					
					if ( isAlphabet(cE) || isDigit(cE) || (cE == 0x005F) ) {
						indexT++;
					} else {// if cE is not alphabet and not underbar
						break;
					}
				}
				String str = code.substring(from, indexT);
				tokens.add(new Token(TokenType.IDENTIFIER, indexT, str));
			}
		}
		
		tokens.add(new Token(TokenType.EOF, indexT));
		return tokens;
	}
	
	public Token parseProgram() {
		Token result = new Token(TokenType.PROGRAM);
		result.setChildTokens(new Token[0]);
		
		int stmts = 0;
		while (!exist(TokenType.EOF)) {
			Token stmt = parseStmt();
			
			if (stmt != null) {
				stmts++;
				
				Token[] tmp = result.getChildTokens();
				result.setChildTokens(new Token[stmts]);
				
				for (int i = 0; i < tmp.length; i++) result.getChildTokens()[i] = tmp[i];
				result.getChildTokens()[tmp.length] = stmt;
			} else {
				errors.add(new Error(tokens.get(indexP), "statement in program"));
				return null;
			}
		}
		
		return result;
	}
	
	private Token parseStmtBlock() {
		Token result = new Token(TokenType.STMT_BLOCK);
		result.setChildTokens(new Token[0]);
		
		if (exist(TokenType.BRACE_L)) {
			accept();
			
			int stmts = 0;
			while (!exist(TokenType.BRACE_R)) {
				Token stmt = parseStmt();
				
				if (stmt != null) {
					stmts++;
					
					Token[] tmp = result.getChildTokens();
					result.setChildTokens(new Token[stmts]);
					
					for (int i = 0; i < tmp.length; i++) result.getChildTokens()[i] = tmp[i];
					result.getChildTokens()[tmp.length] = stmt;
				} else {
					errors.add(new Error(tokens.get(indexP), "statement in block"));
					return null;
				}
			}
			
			accept();
		} else {
			Token stmt = parseStmt();
			
			if (stmt != null) {
				result.setChildTokens(new Token[] {stmt});
			} else {
				errors.add(new Error(tokens.get(indexP), "stmt in single line block"));
				return null;
			}
		}
		
		System.out.println("parse block " + result.toString());
		return result;
	}
	
	private Token parseStmt() {
		Token result = new Token(TokenType.STMT);
		result.setChildTokens(new Token[1]);
		
		if (exist(TokenType.RETURN)) {
			Token stmtReturn = parseStmtReturn();
			
			if (stmtReturn != null) {
				result.getChildTokens()[0] = stmtReturn;
				
				if (exist(TokenType.SEMICOLON)) {
					accept();
				} else {
					errors.add(new Error(tokens.get(indexP), "semicolon"));
					return null;
				}
			} else {
				errors.add(new Error(tokens.get(indexP), "stmtReturn"));
				return null;
			}
		} else if (exist(TokenType.IF)) {
			Token stmtIf = parseStmtIf();
			
			if (stmtIf != null) {
				result.getChildTokens()[0] = stmtIf;
			} else {
				errors.add(new Error(tokens.get(indexP), "stmtIf"));
				return null;
			}
		} else if (exist(TokenType.FOR)) {
			Token stmtFor = parseStmtFor();
			
			if (stmtFor != null) {
				result.getChildTokens()[0] = stmtFor;
			} else {
				errors.add(new Error(tokens.get(indexP), "stmtFor"));
				return null;
			}
		} else if (exist(TokenType.SEMICOLON)) {
			accept();
			
			result.getChildTokens()[0] = null;
		} else {
			Token expr = parseExpr();
			
			if (expr != null) {
				result.getChildTokens()[0] = new Token(TokenType.STMT_EXPR, new Token[] {expr});
				
				if (exist(TokenType.SEMICOLON)) {
					accept();
				} else {
					errors.add(new Error(tokens.get(indexP), "parse stmt_expr: semicolon"));
					return null;
				}
			} else {
				errors.add(new Error(tokens.get(indexP), "parse stmt_expr: expr"));
				return null;
			}
		}
		
		System.out.println("parse stmt " + result.toString());
		return result;
	}
	
	private Token parseStmtFor() {
		Token result = new Token(TokenType.STMT_FOR);
		result.setChildTokens(new Token[4]);
		
		if (exist(TokenType.FOR)) {
			accept();
			
			if (exist(TokenType.PAREN_L)) {
				accept();
				
				Token initExpr = parseExpr();
				
				if (initExpr != null) {
					result.getChildTokens()[0] = initExpr;
					
					if (exist(TokenType.SEMICOLON)) {
						accept();
						
						Token exprCond = parseExpr();
						
						if (exprCond != null) {
							result.getChildTokens()[1] = exprCond;
							
							if (exist(TokenType.SEMICOLON)) {
								accept();
								
								Token expr = parseExpr();
								
								if (expr != null) {
									result.getChildTokens()[2] = expr;
									
									if (exist(TokenType.PAREN_R)) {
										accept();
										
										Token block = parseStmtBlock();
										
										if (block != null) {
											result.getChildTokens()[3] = block;
										} else {
											errors.add(new Error(tokens.get(indexP), "parse stmt for: block"));
											return null;
										}
									} else {
										errors.add(new Error(tokens.get(indexP), "parse stmt for: paren r"));
										return null;
									}
								} else {
									errors.add(new Error(tokens.get(indexP), "parse stmt for: expr"));
									return null;
								}
							} else {
								errors.add(new Error(tokens.get(indexP), "parse stmt for: semicolon after cond expr"));
								return null;
							}
						} else {
							errors.add(new Error(tokens.get(indexP), "parse stmt for: condition expr"));
							return null;
						}
					} else {
						errors.add(new Error(tokens.get(indexP), "parse stmt for: first semicolon"));
						return null;
					}
				} else {
					errors.add(new Error(tokens.get(indexP), "parse stmt for: initialize stmt"));
					return null;
				}
			} else {
				errors.add(new Error(tokens.get(indexP), "parse stmt for: paren L"));
				return null;
			}
		}
		
		return result;
	}
	
	private Token parseStmtIf() {
		Token result = new Token(TokenType.STMT_IF);
		result.setChildTokens(new Token[3]);
		
		if (exist(TokenType.IF)) {
			accept();
			
			Token expr = parseExpr();
			
			if (expr != null) {
				result.getChildTokens()[0] = expr;
				
				Token trueBlock = parseStmtBlock();
				
				if (trueBlock != null) {
					result.getChildTokens()[1] = trueBlock;
					
					if (exist(TokenType.ELSE)) {
						accept();
						
						if (exist(TokenType.IF)) {
							Token stmtIf = parseStmtIf();
							
							if (stmtIf != null) {
								result.getChildTokens()[2] = stmtIf;
							} else {
								errors.add(new Error(tokens.get(indexP), "parse block in stmt if: else if block"));
								return null;
							}
						} else {
							Token falseBlock = parseStmtBlock();
							
							if (falseBlock != null) {
								result.getChildTokens()[2] = falseBlock;
							} else {
								errors.add(new Error(tokens.get(indexP), "parse block in stmt if: false block"));
								return null;
							}
						}
					}
				} else {
					errors.add(new Error(tokens.get(indexP), "parse block in stmt if: true block"));
					return null;
				}
			} else {
				errors.add(new Error(tokens.get(indexP), "parse expr in stmt if: condition"));
				return null;
			}
		} else {
			errors.add(new Error(tokens.get(indexP), "token if"));
			return null;
		}
		
		return result;
	}
	
	private Token parseStmtReturn() {
		Token result = new Token(TokenType.STMT_RETURN);
		result.setChildTokens(new Token[1]);
		
		accept();// return token
		
		Token expr = parseExpr();
		
		if (expr != null) {
			result.getChildTokens()[0] = expr;
		} else {
			errors.add(new Error(tokens.get(indexP), "expr in stmt return"));
			return null;
		}
		
		System.out.println("parse stmt return" + result.toString());
		return result;
	}
	
//	private Token parseStmtCallFunc() {
//		Token result = new Token(TokenType.STMT_CALL_FUNC);
//		result.childTokens = new Token[1];
//		
//		Token exprCallFunc = parseExprCallFunc();
//		
//		if (exprCallFunc != null) {
//			result.childTokens[0] = exprCallFunc;
//			
//			if (exist(TokenType.SEMICOLON)) {
//				accept();
//			} else {
//				errors.add(new Error(tokens.get(indexP), ""));
//				return null;
//			}
//		} else {
//			errors.add(new Error(tokens.get(indexP), ""));
//			return null;
//		}
//		
//		System.out.println("parse stmt call func" + result.toString());
//		return result;
//	}
	
	
	
//	private Token parseExprs() {
//		Token result = new Token(TokenType.EXPRS);
//		result.childTokens = new Token[0];
//		
//		int exprs = 0;
//		while (true) {
//			if (exprs > 0) {
//				if (exist(TokenType.PERIOD)) {
//					accept();
//				} else {
//					break;
//				}
//			}
//			
//			Token expr = parseExpr();
//			
//			if (expr != null) {
//				exprs++;
//				
//				Token[] tmp = result.childTokens;
//				result.childTokens = new Token[exprs];
//				
//				for (int i = 0; i < tmp.length; i++) result.childTokens[i] = tmp[i];
//				result.childTokens[tmp.length] = expr;
//			} else {
//				errors.add(new Error(tokens.get(indexP), ""));
//				return null;
//			}
//		}
//		
//		return result;
//	}
	
	private Token parseExpr() {
		return parseExpr(0);
	}
	
	//	private Token parseStmtCallFunc() {
	//		Token result = new Token(TokenType.STMT_CALL_FUNC);
	//		result.childTokens = new Token[1];
	//		
	//		Token exprCallFunc = parseExprCallFunc();
	//		
	//		if (exprCallFunc != null) {
	//			result.childTokens[0] = exprCallFunc;
	//			
	//			if (exist(TokenType.SEMICOLON)) {
	//				accept();
	//			} else {
	//				errors.add(new Error(tokens.get(indexP), ""));
	//				return null;
	//			}
	//		} else {
	//			errors.add(new Error(tokens.get(indexP), ""));
	//			return null;
	//		}
	//		
	//		System.out.println("parse stmt call func" + result.toString());
	//		return result;
	//	}
		
		private Token parseExpr(int priority) {
		Token result = new Token(TokenType.EXPR);
		result.setChildTokens(new Token[1]);
		
		if (exist(TokenType.DEF)) {
			Token exprDefine = parseExprDefine();
			
			if (exprDefine != null) {
				result.getChildTokens()[0] = exprDefine;
			} else {
				errors.add(new Error(tokens.get(indexP), "parse expr: expr define"));
				return null;
			}
		} else if (exist(TokenType.DATA_NUMBER) ||
				exist(TokenType.DATA_STRING) ||
				exist(TokenType.TRUE) ||
				exist(TokenType.FALSE)) {
			result.getChildTokens()[0] = tokens.get(indexP);
			accept();
		} else if (exist(TokenType.PAREN_L)) {
			if (exist(TokenType.AS, 2)) {
				Token struct = parseExprStruct();
				
				if (struct != null) {
					if (exist(TokenType.ARROW_R)) {
						accept();
						
						Token block = parseStmtBlock();
						
						if (block != null) {
							result.getChildTokens()[0] = new Token(TokenType.EXPR_FUNC, new Token[] {struct, block});						
						} else {
							errors.add(new Error(tokens.get(indexP), "block in expr func"));
							return null;
						}
						
					} else {
						result.getChildTokens()[0] = struct;
					}
				} else {
					errors.add(new Error(tokens.get(indexP), "expr struct"));
					return null;
				}
			} else {
				accept();
				
				Token expr = parseExpr();
				
				if (expr != null) {
					result = expr;
					
					if (exist(TokenType.PAREN_R)) {
						accept();
					} else {
						errors.add(new Error(tokens.get(indexP), "parse expr: paren r"));
						return null;
					}
				} else {
					errors.add(new Error(tokens.get(indexP), "parse expr: unexpected"));
					return null;					
				}
			}
		} else if (exist(TokenType.IDENTIFIER)) {
			if (exist(TokenType.PAREN_L, 1)) {
				Token exprCallFunc = parseExprCallFunc();
				
				if (exprCallFunc != null) {
					result.getChildTokens()[0] = exprCallFunc;
				} else {
					errors.add(new Error(tokens.get(indexP), "expr call func"));
					return null;
				}
			} else if (exist(TokenType.BRACKET_L, 1)) {
				Token exprRefArray = parseExprRefArray();
				
				if (exprRefArray != null) {
					result.getChildTokens()[0] = exprRefArray;
				} else {
					errors.add(new Error(tokens.get(indexP), "expr ref array"));
					return null;
				}
			} else if (exist(TokenType.ASSIGN, 1)) {
				Token exprAssign = parseExprAssign();
				
				if (exprAssign != null) {
					result.getChildTokens()[0] = exprAssign;
				} else {
					errors.add(new Error(tokens.get(indexP), "parse expr assign"));
					return null;
				}
			} else {
				result.getChildTokens()[0] = tokens.get(indexP);
				accept();
			}
		} else if (exist(TokenType.LOGIC_NOT)) {
			Token exprLogicNot = parseExprLogicNot();
			
			if (exprLogicNot != null) {
				result.getChildTokens()[0] = exprLogicNot;
			} else {
				errors.add(new Error(tokens.get(indexP), "expr logic not"));
				return null;
			}
		} else {
			result.setChildTokens(null);
			return result;
//			errors.add(new Error(tokens.get(indexP), "unexpected"));
//			return null;
		}
		
		while (true) {
			/*
			 *     a +   b *   c / d       -   e.f
			 * ( ( a + ( b * ( c / d ) ) ) - ( e.f ) )   expression
			 *       2     3     3         2    5         priority
			 * 1. start with parsed expression.
			 * 2. check the next token.
			 * if calculation token exists
			 * 		3. check the current priority of the token.
			 * 		4. compare previous and current priority. ( start priority is zero. )
			 * 		if previous is less-equal then current
			 * 			5. start parsing an expression after the token with the info about current priority. ( by starting new thread following these steps )
			 * 			6. return the new expression containing the two expression, one from step <1> and the other one from step <5>
			 * 		if not
			 * 			5. finish parsing expression and back to step <1> with the parsed expression.
			 * if not
			 * 		3. return the parsed expression.
			 */
			
			int curPriority;
			TokenType type;
			if (exist(TokenType.PERIOD)) { curPriority = 5; type = TokenType.EXPR_REF_DOT; }
			else if (exist(TokenType.BIT_AND)) { curPriority = 4; type = TokenType.EXPR_BIT_AND; }
			else if (exist(TokenType.BIT_OR)) { curPriority = 4; type = TokenType.EXPR_BIT_OR; }
			else if (exist(TokenType.MUL)) { curPriority = 3; type = TokenType.EXPR_MUL; }
			else if (exist(TokenType.DIV)) { curPriority = 3; type = TokenType.EXPR_DIV; }
			else if (exist(TokenType.MOD)) { curPriority = 3; type = TokenType.EXPR_MOD; }
			else if (exist(TokenType.ADD)) { curPriority = 2; type = TokenType.EXPR_ADD; }
			else if (exist(TokenType.SUB)) { curPriority = 2; type = TokenType.EXPR_SUB; }
			else if (exist(TokenType.EQ)) { curPriority = 1; type = TokenType.EXPR_EQ; }
			else if (exist(TokenType.NEQ)) { curPriority = 1; type = TokenType.EXPR_NEQ; }
			else if (exist(TokenType.GT)) { curPriority = 1; type = TokenType.EXPR_GT; }
			else if (exist(TokenType.GTE)) { curPriority = 1; type = TokenType.EXPR_GTE; }
			else if (exist(TokenType.LT)) { curPriority = 1; type = TokenType.EXPR_LT; }
			else if (exist(TokenType.LTE)) { curPriority = 1; type = TokenType.EXPR_LTE; }
			else if (exist(TokenType.LOGIC_AND)) { curPriority = 0; type = TokenType.EXPR_LOGIC_AND; }
			else if (exist(TokenType.LOGIC_OR)) { curPriority = 0; type = TokenType.EXPR_LOGIC_OR; }
			else break;
			
			if (priority <= curPriority) {
				accept();
				
				Token expr = parseExpr(curPriority);
				
				if (expr != null) {
					result = new Token(TokenType.EXPR, new Token[] {new Token(type, new Token[] {result, expr})});
				} else {
					errors.add(new Error(tokens.get(indexP), "expr: " + type.toString().toLowerCase()));
					return null;
				}
			} else {
				break;
			}
		}
		
		System.out.println("parse expr " + result.toString());
		return result;
	}
	
	private Token parseExprLogicNot() {
		Token result = new Token(TokenType.EXPR_LOGIC_NOT);
		result.setChildTokens(new Token[1]);
		
		if (exist(TokenType.LOGIC_NOT)) {
			accept();
			
			Token expr = parseExpr(5);
			
			if (expr != null) {
				result.getChildTokens()[0] = expr;
			} else {
				errors.add(new Error(tokens.get(indexP), "parse expr logic not: expr"));
				return null;
			}
		} else {
			errors.add(new Error(tokens.get(indexP), "parse expr logic not: token '!'"));
			return null;
		}
		
		return result;
	}
	
	private Token parseExprAssign() {
		Token result = new Token(TokenType.EXPR_ASSIGN);
		result.setChildTokens(new Token[2]);
		
		if (exist(TokenType.IDENTIFIER)) {
			result.getChildTokens()[0] = accept();
			
			if (exist(TokenType.ASSIGN)) {
				accept();
				
				Token expr = parseExpr();
				
				if (expr != null) {
					result.getChildTokens()[1] = expr;
				} else {
					errors.add(new Error(tokens.get(indexP), "parse expr assign: expr"));
					return null;
				}
			} else {
				errors.add(new Error(tokens.get(indexP), "parse expr assign: assign token"));
				return null;
			}
		} else {
			errors.add(new Error(tokens.get(indexP), "parse expr assign: identifier"));
			return null;
		}
		
		return result;
	}
	
	private Token parseExprDefine() {
		Token result = new Token(TokenType.EXPR_DEFINE);
		result.setChildTokens(new Token[3]);
		
		if (exist(TokenType.DEF)) {
			accept();// DEF
			
			if (exist(TokenType.IDENTIFIER)) {
				result.getChildTokens()[0] = tokens.get(indexP);
				
				accept();
				
				if (exist(TokenType.AS)) {
					accept();
					
					Token type = parseType();
					
					if (type != null) {
						result.getChildTokens()[1] = type;
					} else {
						errors.add(new Error(tokens.get(indexP), "parse expr def: type"));
						return null;
					}
				} else {
					result.getChildTokens()[1] = null;
				}
				
				if (exist(TokenType.ASSIGN)) {
					accept();
					
					Token expr = parseExpr();
					
					if (expr != null) {
						result.getChildTokens()[2] = expr;					
					} else {
						errors.add(new Error(tokens.get(indexP), "expr"));
						return null;
					}
				} else {
					result.getChildTokens()[2] = null;
				}
			}
		} else {
			System.out.println("parse expr define: token def");
			return null;
		}
		
		System.out.println("parse expr define");
		return result;
	}

	private Token parseExprRefArray() {
		Token result = new Token(TokenType.EXPR_REF_ARRAY);
		result.setChildTokens(new Token[2]);
		
		if (exist(TokenType.IDENTIFIER)) {
			result.getChildTokens()[0] = tokens.get(indexP);
			accept();
			
			if (exist(TokenType.BRACKET_L)) {
				accept();
				
				Token expr = parseExpr();
				
				if (expr != null) {
					result.getChildTokens()[1] = expr;
					
					if (exist(TokenType.BRACKET_R)) {
						accept();
					} else {
						errors.add(new Error(tokens.get(indexP), "bracket r"));
						return null;
					}
				} else {
					errors.add(new Error(tokens.get(indexP), "expr"));
					return null;
				}
			}
		} else {
			errors.add(new Error(tokens.get(indexP), "identifier"));
			return null;
		}
		
		System.out.println("parse expr ref array" + result.toString());
		return result;
	}
	
	private Token parseExprCallFunc() {
		Token result = new Token(TokenType.EXPR_CALL_FUNC);
		result.setChildTokens(new Token[1]);
		
		Token id = tokens.get(indexP);
		result.getChildTokens()[0] = id;
		accept();
		
		if (exist(TokenType.PAREN_L)) {
			accept();
			
			int exprs = 0;
			while (!exist(TokenType.PAREN_R)) {
				if (exprs > 0) {
					if (exist(TokenType.COMMA)) accept();
					else {
						errors.add(new Error(tokens.get(indexP), "comma"));
						return null;
					}
				}
				
				Token expr = parseExpr();
				
				if (expr != null) {
					exprs++;
					
					Token[] tmp = result.getChildTokens();
					result.setChildTokens(new Token[exprs + 1]);
					
					for (int i = 0; i < tmp.length; i++) result.getChildTokens()[i] = tmp[i];
					result.getChildTokens()[tmp.length] = expr;
				} else {
					errors.add(new Error(tokens.get(indexP), "expr"));
					return null;
				}
			}
			
			accept();
		} else {
			errors.add(new Error(tokens.get(indexP), ""));
			return null;
		}
		
		System.out.println("parse expr call func" + result.toString());
		return result;
	}
	
	private Token parseExprStruct() {
		Token result = new Token(TokenType.EXPR_STRUCT);
		result.setChildTokens(new Token[0]);
		
		accept();// PAREN_L
		
		int variables = 0;
		while (!exist(TokenType.PAREN_R)) {
			if (variables > 0) {
				if (exist(TokenType.COMMA)) {
					accept();
				} else {
					errors.add(new Error(tokens.get(indexP), "comma in struct"));
					return null;
				}
			}
			
			Token variable = parseVariable();
			
			if (variable != null) {
				variables++;
				
				Token[] tmp = result.getChildTokens();
				result.setChildTokens(new Token[variables]);
				
				for (int i = 0; i < tmp.length; i++) result.getChildTokens()[i] = tmp[i];
				result.getChildTokens()[tmp.length] = variable;
			} else {
				errors.add(new Error(tokens.get(indexP), "variable in struct"));
				return null;
			}
		}
		
		accept();
		
		System.out.println("parse expr struct " + result.toString());
		return result;
	}
	
	private Token parseVariable() {
		Token result = new Token(TokenType.VARIABLE);
		result.setChildTokens(new Token[2]);
		
		if (exist(TokenType.IDENTIFIER)) {
			result.getChildTokens()[0] = tokens.get(indexP);
			accept();
			
			if (exist(TokenType.AS)) {
				accept();
				
				Token type = parseType();
				
				if (type != null) {
					result.getChildTokens()[1] = type;
				} else {
					errors.add(new Error(tokens.get(indexP), "type"));
					return null;
				}
			} else {
				errors.add(new Error(tokens.get(indexP), "as"));
				return null;
			}
		} else {
			errors.add(new Error(tokens.get(indexP), "identifier"));
			return null;
		}
		
		System.out.println("parse variable " + result.toString());
		return result;
	}
	
	private Token parseType() {
		Token result = new Token(TokenType.TYPE);
		result.setChildTokens(new Token[1]);
		
		if (exist(TokenType.INT) ||
				exist(TokenType.BOOLEAN) ||
				exist(TokenType.DOUBLE) ||
				exist(TokenType.FUNC) ||
				exist(TokenType.STRUCT) ||
				exist(TokenType.STRING)) {
			if (tokens.get(indexP).getType().equals(TokenType.INT)) result.getChildTokens()[0] = new Token(TokenType.TYPE_INT, tokens.get(indexP).getIndex());
			if (tokens.get(indexP).getType().equals(TokenType.BOOLEAN)) result.getChildTokens()[0] = new Token(TokenType.TYPE_BOOLEAN, tokens.get(indexP).getIndex());
			if (tokens.get(indexP).getType().equals(TokenType.DOUBLE)) result.getChildTokens()[0] = new Token(TokenType.TYPE_DOUBLE, tokens.get(indexP).getIndex());
			if (tokens.get(indexP).getType().equals(TokenType.FUNC)) result.getChildTokens()[0] = new Token(TokenType.TYPE_FUNC, tokens.get(indexP).getIndex());
			if (tokens.get(indexP).getType().equals(TokenType.STRUCT)) result.getChildTokens()[0] = new Token(TokenType.TYPE_STRUCT, tokens.get(indexP).getIndex());
			if (tokens.get(indexP).getType().equals(TokenType.STRING)) result.getChildTokens()[0] = new Token(TokenType.TYPE_STRING, tokens.get(indexP).getIndex());
			accept();
			
			
			if (exist(TokenType.BRACKET_L)) {
				accept();
				
				result.getChildTokens()[0] = new Token(TokenType.TYPE_ARRAY, new Token[] {result.getChildTokens()[0]});
				
				if (exist(TokenType.BRACKET_R)) {
					accept();
				} else {
					errors.add(new Error(tokens.get(indexP), "bracket right"));
					return null;
				}
			}
		} else {
			errors.add(new Error(tokens.get(indexP), "unexpected"));
			return null;
		}
		
		System.out.println("parse type " + result.toString());
		return result;
	}
	
}
