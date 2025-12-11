package tomasulogui;

public abstract class FunctionalUnit {
    PipelineSimulator simulator;
    ReservationStation[] stations = new ReservationStation[2];

    public FunctionalUnit(PipelineSimulator sim) {
        simulator = sim;
    }


    public void squashAll() {
        for (int i = 0; i < stations.length; i++) {
            if (stations[i] != null) {
                stations[i].isSquashed = true;
            }
        }
    }

    public abstract int calculateResult(int station);

    public abstract int getExecCycles();

    public void execCycle(CDB cdb) {
        //todo - start executing, ask for CDB, etc.
        //PROBLEM should we always compute the first reservation station?
        for (int i = 0; i < 2; i++) {
            if (stations[i] != null && stations[i].isSquashed) {
                stations[i] = null;
            }
            else if (stations[i] != null && stations[i].data1Valid && stations[i].data2Valid) {
                calculateResult(i);
            }
        }

        if (cdb.getDataValid()) {
            for (int i = 0; i < 2; i++) {
                if (stations[i] != null) {
                    stations[i].snoop(cdb);
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