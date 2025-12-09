package tomasulogui;

public class BranchUnit
        extends FunctionalUnit {

    public static final int EXEC_CYCLES = 1;

    public BranchUnit(PipelineSimulator sim) {
        super(sim);
    }

    public int calculateResult(int station) {
         // just placeholder code
        int branchCompare = 0;
        int data1 = stations[station].getData1();
        int data2 = stations[station].getData2();

        branchCompare = data1 - data2; //might need to do data2 - data1
        boolean isBranchTaken = false; //might need to exist elsewhere, added here for now
        switch (stations[station].getFunction()) {
            case BEQ -> isBranchTaken = branchCompare == 0;
            case BGEZ -> isBranchTaken = branchCompare >= 0;
            case BLEZ -> isBranchTaken = branchCompare <= 0;
            case BNE -> isBranchTaken = branchCompare != 0;
            case BGTZ -> isBranchTaken = branchCompare > 0;
            case BLTZ -> isBranchTaken = branchCompare < 0;
            //need to implement J functions
            //NEED TO set MISS button false in ROB if getBranchPrediction()
            case JAL, JALR, J, JR -> isBranchTaken = true;
        }

        if (isBranchTaken != stations[station].isPredictedTaken()) {
            simulator.squashAllInsts();
            return 1;
        }

        int tag = stations[station].getDestTag();
        simulator.getROB().getEntryByTag(tag).complete = true;
        simulator.getBTB().setBranchResult(simulator.getPC(), isBranchTaken); //train the BTB with branch data?*/
        stations[station] = null;
        ReorderBuffer rob = simulator.getROB();
        rob.buff[tag].setWriteValue(rob.getInstPC(tag) + 4);
        rob.buff[tag].complete = true;

        return 0;
    }

    public int getExecCycles() {
        return EXEC_CYCLES;
    }
}
