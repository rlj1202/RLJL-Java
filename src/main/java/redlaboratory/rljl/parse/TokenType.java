package redlaboratory.rljl.parse;

public enum TokenType {
	DEF("def"),// def
	AS("as"),// as
	
	FUNC("func"),// func
	STRUCT("struct"),// struct
	INT("int"),// int
	DOUBLE("double"),// double
	BOOLEAN("boolean"),// boolean
	STRING("string"),// string
	
	TRUE("true"),// true
	FALSE("false"),// false
	
	ASSIGN("="),// =
	
	ADD("+"),// +
	SUB("-"),// -
	MUL("*"),// *
	DIV("/"),// /
	MOD("%"),// %
	
	BIT_AND("&"),// &
	BIT_OR("|"),// |
	BIT_SHIFT_R(">>"),// >>
	BIT_SHIFT_L("<<"),// <<
	
	IF("if"),// if
	ELSE("else"),// else
	SWITCH("switch"),// switch
	FOR("for"),// for
	WHILE("while"),// while
	
	EQ("=="),// ==
	NEQ("!="),// !=
	GT(">"),// >
	GTE(">="),// >=
	LT("<"),// <
	LTE("<="),// <=
	
	LOGIC_AND("&&"),// &&
	LOGIC_OR("||"),// ||
	LOGIC_NOT("!"),// !
	
	ARROW_R("->"),// ->
	
	PAREN_L("("),// (
	PAREN_R(")"),// )
	BRACE_L("{"),// {
	BRACE_R("}"),// }
	BRACKET_L("["),// [
	BRACKET_R("]"),// ]
	
	DOUBLE_QUOTE("\""),// "
	SINGLE_QUOTE("\'"),// '
	
	PERIOD("."),// .
	COMMA(","),// ,
	
	COLON(":"),// :
	SEMICOLON(";"),// ;
	
	RETURN("return"),// return
	
	ANNOTATION_LINE(),
	ANNOTATION_BLOCK(),
	
	TYPE(),
	TYPE_INT(),
	TYPE_DOUBLE(),
	TYPE_STRUCT(),
	TYPE_FUNC(),
	TYPE_BOOLEAN(),
	TYPE_STRING(),
	TYPE_ARRAY(),// type[] or identifier[]. (identifier should be pointer to structure)
	
	DATA_STRING(),
	DATA_NUMBER(),
	
	IDENTIFIER(),
	
	VARIABLE(),

	EXPR(),
	EXPR_STRUCT(),
	EXPR_FUNC(),
	EXPR_CALL_FUNC(),
	EXPR_DEFINE(),
	EXPR_ASSIGN(),
	EXPR_REF_ARRAY(),
	EXPR_REF_DOT(),// priority 5 (highest)
	EXPR_BIT_AND(),// priority 4
	EXPR_BIT_OR(),// priority 4
	EXPR_MUL(),// priority 3
	EXPR_DIV(),// priority 3
	EXPR_MOD(),// priority 3
	EXPR_ADD(),// priority 2
	EXPR_SUB(),// priority 2
	EXPR_EQ(),// priority 1
	EXPR_NEQ(),// priority 1
	EXPR_GT(),// priority 1
	EXPR_GTE(),// priority 1
	EXPR_LT(),// priority 1
	EXPR_LTE(),// priority 1
	EXPR_LOGIC_AND(),// priority 0
	EXPR_LOGIC_OR(),// priority 0 (lowest)
	EXPR_LOGIC_NOT(),// priority 5 (highest as ref_dot)
	
	STMT(),
	STMT_CALL_FUNC(),
	STMT_RETURN(),
	STMT_IF(),
	STMT_FOR(),
	STMT_EXPR(),
	STMT_BLOCK(),
	
	PROGRAM(),
	
	EOF(),
	;
	
	private String str;
	
	private TokenType() {
		this(null);
	}
	
	private TokenType(String str) {
		this.str = str;
	}
	
	public String getString() {
		return str;
	}
	
}
