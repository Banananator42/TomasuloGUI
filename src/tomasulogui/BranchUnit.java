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

        int tag = stations[station].getDestTag();
        ROBEntry robEntry = simulator.getROB().getEntryByTag(tag);

        //assume not mispredicted, update later in function
        boolean mispredicted = false;

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

//        if (isBranchTaken != stations[station].isPredictedTaken()) {
//            simulator.squashAllInsts();
//            return 1;
//        }



        //If it is JAL or JALR, the target may have been calculated
        //Let the BTB know, set the address, and see if it was mispredicted
        if (stations[station].getFunction() == IssuedInst.INST_TYPE.JR ||
                stations[station].getFunction() == IssuedInst.INST_TYPE.JALR) {
            simulator.getBTB().setBranchAddress(robEntry.getInstPC(), data1); //train BTB
            if (robEntry.branchPredictedTarget != data1) { //predicted wrong address, so set PC correctly
                simulator.setPC(data1);
                robEntry.correctBranchTarget = data1;
                mispredicted = true;
            }
            else {
                robEntry.correctBranchTarget = robEntry.branchPredictedTarget;
            }
        }

        simulator.getBTB().setBranchResult(robEntry.getInstPC(), isBranchTaken); //train the BTB with branch data?*/
        stations[station] = null;
        robEntry.setWriteValue(robEntry.getInstPC() + 4); //writeValue is used for JAL and JALR
        robEntry.complete = true;
        mispredicted |= isBranchTaken != robEntry.predictTaken;

        robEntry.mispredicted = mispredicted;

        return 0;
    }

    public int getExecCycles() {
        return EXEC_CYCLES;
    }
}
