package redlaboratory.rljl.vm;

public class VM {
	
	private int index;
	private Operation[] operations;
	private int[] reg;
	
	public VM(Operation[] operations) {
		this.operations = operations;
		
		this.index = 0;
		reg = new int[10];
	}
	
	public void run() {
		while (index < operations.length) {
			process();
		}
	}
	
	public int[] getRegs() {
		return reg;
	}
	
	private void process() {
		Operation op = operations[index];
		
		if (op.type == OPCode.ADD) {
			reg[op.operands[0]] = reg[op.operands[1]] + reg[op.operands[2]];
		} else if (op.type == OPCode.SUB) {
			reg[op.operands[0]] = reg[op.operands[1]] - reg[op.operands[2]];
		} else if (op.type == OPCode.MUL) {
			reg[op.operands[0]] = reg[op.operands[1]] * reg[op.operands[2]];
		} else if (op.type == OPCode.DIV) {
			reg[op.operands[0]] = reg[op.operands[1]] / reg[op.operands[2]];
		} else if (op.type == OPCode.MOD) {
			reg[op.operands[0]] = reg[op.operands[1]] % reg[op.operands[2]];
		} else if (op.type == OPCode.AND) {
			reg[op.operands[0]] = reg[op.operands[1]] & reg[op.operands[2]];
		} else if (op.type == OPCode.OR) {
			reg[op.operands[0]] = reg[op.operands[1]] | reg[op.operands[2]];
		} else if (op.type == OPCode.NOT) {
			reg[op.operands[0]] = ~reg[op.operands[1]];
		} else if (op.type == OPCode.LOAD) {
			reg[op.operands[0]] = op.operands[1];
		} else if (op.type == OPCode.MOVE) {
			reg[op.operands[0]] = reg[op.operands[1]];
		} else if (op.type == OPCode.EQ) {
			reg[op.operands[0]] = reg[op.operands[1]] == reg[op.operands[2]] ? 1 : 0;
		} else if (op.type == OPCode.NEQ) {
			reg[op.operands[0]] = reg[op.operands[1]] != reg[op.operands[2]] ? 1 : 0;
		} else if (op.type == OPCode.GT) {
			reg[op.operands[0]] = reg[op.operands[1]] > reg[op.operands[2]] ? 1 : 0;
		} else if (op.type == OPCode.GTE) {
			reg[op.operands[0]] = reg[op.operands[1]] >= reg[op.operands[2]] ? 1 : 0;
		} else if (op.type == OPCode.LT) {
			reg[op.operands[0]] = reg[op.operands[1]] < reg[op.operands[2]] ? 1 : 0;
		} else if (op.type == OPCode.LTE) {
			reg[op.operands[0]] = reg[op.operands[1]] <= reg[op.operands[2]] ? 1 : 0;
		} else if (op.type == OPCode.JMP) {
			index += op.operands[0];
			return;
		} else if (op.type == OPCode.JMP_F) {
			if (reg[op.operands[0]] == 0) {
				index += op.operands[1];
				return;
			}
		}
		
		index++;
	}
	
}
