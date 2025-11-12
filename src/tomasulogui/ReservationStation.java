package tomasulogui;

public class ReservationStation {
  PipelineSimulator simulator;

  int tag1;
  int tag2;
  int data1;
  int data2;
  boolean data1Valid = false;
  boolean data2Valid = false;
  // destTag doubles as branch tag
  int destTag;
  IssuedInst.INST_TYPE function = IssuedInst.INST_TYPE.NOP;

  // following just for branches
  int addressTag;
  boolean addressValid = false;
  int address;
  boolean predictedTaken = false;

  public ReservationStation(PipelineSimulator sim) {
    simulator = sim;
  }

  public int getDestTag() {
    return destTag;
  }

  public int getData1() {
    return data1;
  }

  public int getData2() {
    return data2;
  }

  public boolean isPredictedTaken() {
    return predictedTaken;
  }

  public IssuedInst.INST_TYPE getFunction() {
    return function;
  }

  public void snoop(CDB cdb) {
      //check to see if either of the tags are on the CBB
      if (cdb.dataTag == tag1 && !data1Valid) {
          data1 = cdb.dataValue;
          data1Valid = true;
      }
      if (cdb.dataTag == tag2 && !data2Valid) {
          data2 = cdb.dataValue;
          data2Valid = true;
      }
  }

  public boolean isReady() {
    return data1Valid && data2Valid;
  }

  public void loadInst(IssuedInst inst) {
      //either get the data or set the tag value depending on if the data is valid
      if (inst.getRegSrc1Valid()) {
          data1 = inst.getRegSrc1Value();
          data1Valid = true;
      }
      else {
          tag1 = inst.getRegSrc1Tag();
      }
      if (inst.getRegSrc2Valid()) {
          data2 = inst.getRegSrc2Value();
          data2Valid = true;
      }
      else {
          tag2 = inst.getRegSrc2Tag();
      }
  }
}
