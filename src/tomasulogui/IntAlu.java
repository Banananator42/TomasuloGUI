package tomasulogui;

public class IntAlu extends FunctionalUnit{
  public static final int EXEC_CYCLES = 1;

  public IntAlu(PipelineSimulator sim) {
    super(sim);
  }


  public int calculateResult(int station) {
     // just placeholder code
    int result=0;
    int data1 = stations[station].getData1();
    int data2 = stations[station].getData2();
    //ADD, ADDI, SUB, MUL, DIV, AND, ANDI, OR, ORI, XOR, XORI, SLL, SRL, SRA
    switch (stations[station].getFunction()) {
        case ADD, ADDI -> result = data1 + data2;
        case SUB -> result = data1 - data2;
        case AND, ANDI -> result = data1 & data2;
        case OR, ORI -> result = data1 | data2;
        case XOR, XORI -> result = data1 ^ data2;
        case SLL -> result = data1 << data2;
        case SRL -> result = data1 >>> data2;
        case SRA -> result = data1 >> data2;
    }
    return result;
  }

  public int getExecCycles() {
    return EXEC_CYCLES;
  }
}
