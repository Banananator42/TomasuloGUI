package tomasulogui;

public abstract class FunctionalUnit {
  PipelineSimulator simulator;
  ReservationStation[] stations = new ReservationStation[2];
  
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
      //right now it naievely accepts a new instruction every time
      stations[2] = stations[1];
      stations[1].loadInst(inst);
  }

}
