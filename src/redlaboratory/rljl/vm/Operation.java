package redlaboratory.rljl.vm;

public class Operation {
	
	public final OPCode type;
	public final int[] operands;
	
	public Operation(OPCode type, int...operands) {
		this.type = type;
		this.operands = operands;
	}
	
}
