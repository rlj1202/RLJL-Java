package redlaboratory.rljl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import redlaboratory.rljl.parse.Parser;
import redlaboratory.rljl.parse.Token;
import redlaboratory.rljl.vm.Operation;
import redlaboratory.rljl.parse.Parser.Error;

public class Test {
	
	public void test() {
		int a1 = 1;
		int a2 = 1;
		int a3 = 1;
		int a4 = 1;
		int a5 = 1;
		
		int i = a1 + a2 + a3 + a4 + a5;
	}
	
	public static void main(String[] args) throws IOException {
		InputStream is = Parser.class.getResourceAsStream("/res/code.rljl");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		String code = "";
		
		String str;
		while ((str = br.readLine()) != null) {
			code += str + "\n";
		}
		
		Parser parser = new Parser();
		parser.code = code;
		List<Token> tokens = parser.tokenize();
		for (Token token : tokens) {
			System.out.println(token.toString());
		}
		
		parser.tokens = tokens;
		Token block = parser.parseProgram();
		
		if (block != null) System.out.println("FINISH: " + block.toString());		
		
		for (Error error : parser.errors) {
			System.out.println("ERROR " + error.toString());
			System.out.println(code.replace('	', ' '));
			for (int i = 0; i < error.getToken().getIndex(); i++) System.out.print(" ");
			System.out.println("^");
		}
		
		redlaboratory.rljl.parse.Compiler comp = new redlaboratory.rljl.parse.Compiler();
		List<Operation> ops = comp.generateCode(block);
		for (Operation op : ops) System.out.println(op.toString());
	}
	
}
