package redlaboratory.rljl.vm;

public enum OPCode {
	NOP,
	LOAD,// reg, val
	ADD,// reg, reg, reg
	SUB,// reg, reg, reg
	MUL,// reg, reg, reg
	DIV,// reg, reg, reg
	MOD,// reg, reg, reg
	EQ,// reg, reg, reg
	GT,// reg, reg, reg
	GTE,// reg, reg, reg
	LT,// reg, reg, reg
	LTE,// reg, reg, reg
	JMP,// val
	JMP_F,// reg, val
}
