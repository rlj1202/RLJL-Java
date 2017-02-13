package redlaboratory.rljl.parse;

public class Token {
	
	private TokenType type;
	private Token[] childTokens;
	
	private int index;
	private String str;
	
	public Token(TokenType type) {
		this(type, -1);
	}
	
	public Token(TokenType type, int index) {
		this(type, index, type.getString());
	}
	
	public Token(TokenType type, int index, String str) {
		this(type, index, str, new Token[] {});
	}
	
	public Token(TokenType type, Token[] childTokens) {
		this(type, -1, type.getString(), childTokens);
	}
	
	public Token(TokenType type, int index, String str, Token[] childTokens) {
		this.type = type;
		this.childTokens = childTokens;
		
		this.index = index;
		this.str = str;
	}
	
	public TokenType getType() {
		return type;
	}
	
	public Token[] getChildTokens() {
		return childTokens;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getDataString() {
		return str;
	}
	
	public Token[] setChildTokens(Token[] childTokens) {
		return (this.childTokens = childTokens);
	}
	
	@Override
	public String toString() {
		return toString(0);
	}
	
	private String toString(int layer) {
		String result = "";
		
		for (int i = 0; i < layer; i++) result += "	";
		result += type.toString();
		
		if (str != null) result += " = " + str;
		
		if (childTokens.length > 0) {
			result += " {\n";
			
			for (Token token : childTokens) {
				if (token == null) {
					for (int i = 0; i < layer + 1; i++) result += "	";
					result += "null";
				} else {
					result += token.toString(layer + 1);
				}
				
				result += "\n";
			}
			
			for (int i = 0; i < layer; i++) result += "	";
			result += "}";
		}
		
		return result;
	}
	
}
