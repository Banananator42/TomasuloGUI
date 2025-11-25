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

        result = data1 - data2; //might need to do data2 - data1

        return result;
    }

    public int getExecCycles() {
        return EXEC_CYCLES;
    }
}
