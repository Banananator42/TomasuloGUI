package tomasulogui;

public class BranchUnit
        extends FunctionalUnit {

    public static final int EXEC_CYCLES = 1;

    public BranchUnit(PipelineSimulator sim) {
        super(sim);
    }

    public int calculateResult(int station) {
         // just placeholder code
        int result = 0;
        int data1 = stations[station].getData1();
        int data2 = stations[station].getData2();
        //No switch statement here, all the calculation needs to do is compare two numbers
        result = data1 - data2;
        // switch (stations[station].getFunction()) {
        //     case BEQ -> result = data1 - data2;
        //     case BGEZ -> result = data1 - data2;
        //     case BLEZ -> result = data1 & data2;
        //     case BNE -> result = data1 | data2;
        //     case BGTZ -> result = data1 ^ data2;
        //     case BLTZ -> result = data1 << data2;
        //     case JAL -> result = data1 >>> data2;
        //     case JALR -> result = data1 >> data2;
        //     case J -> result = data1 >>> data2;
        //     case JR -> result = data1 >> data2;
        // }
        return result;
    }

    public int getExecCycles() {
        return EXEC_CYCLES;
    }
}
