package tomasulogui;

public abstract class FunctionalUnit {
    PipelineSimulator simulator;
    ReservationStation[] stations = new ReservationStation[2];

    int writeBackVal = -1;
    int writeBackTag = -1;
    boolean requestWriteback = false;
    int writeBackStation = -1;

    public FunctionalUnit(PipelineSimulator sim) {
        simulator = sim;
    }


    public void squashAll() {
        for (int i = 0; i < stations.length; i++) {
            stations[i].isSquashed = true;
        }
    }

    public abstract int calculateResult(int station);

    public abstract int getExecCycles();

    public void execCycle(CDB cdb) {
        //todo - start executing, ask for CDB, etc.
        //PROBLEM should we always compute the first reservation station?
        for (int i = 0; i < 2; i++) {
            if (stations[i] != null && stations[i].data1Valid && stations[i].data2Valid) {
                if(this instanceof IntAlu) {
                    writeBackVal = calculateResult(i);
                    requestWriteback = true;
                    writeBackTag = stations[i].destTag;
                    writeBackStation = i;
                }
                else if(this instanceof BranchUnit) {
                    int branchCompare = calculateResult(i);
                    boolean isBranchTaken = false; //might need to exist elsewhere, added here for now
                    switch (stations[i].getFunction()) {
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
                    if(isBranchTaken) {
                        requestWriteback = true;
                        writeBackTag = stations[i].destTag;
                        writeBackStation = i;
                    }
                    simulator.getBTB().setBranchResult(simulator.getPC(), isBranchTaken); //train the BTB with branch data?
                }
            }
        }
    }

    public boolean isReservationStationAvail() { //modeled after method in LoadBuffer
        for (int i=0; i < stations.length; i++) {
            if (stations[i] == null) {
                return true;
            }
        }
        return false;
    }

    public void acceptIssue(IssuedInst inst) {
        // todo - fill in reservation station (if available) with data from inst
        //Pretty much copied this over from LoadBuffer
        int slot=0;
        for (slot=0; slot < 2; slot++) {
            if (stations[slot] == null) {
                break;
            }
        }
        if (slot == 2) {
            throw new MIPSException("Loader accept issue: slot not available");
        }

        ReservationStation entry = new ReservationStation(simulator);
        stations[slot] = entry;
        entry.loadInst(inst);
    }
}