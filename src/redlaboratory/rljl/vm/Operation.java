package redlaboratory.rljl.vm;

import java.util.Arrays;

public class Operation {
	
	public final OPCode type;
	public final int[] operands;
	
	public Operation(OPCode type, int...operands) {
		this.type = type;
		this.operands = operands;
	}
	
	@Override
	public String toString() {
		return type.toString() + "\t" + Arrays.toString(operands);
	}
	
}
