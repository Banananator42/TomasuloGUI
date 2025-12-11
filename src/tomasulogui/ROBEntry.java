package tomasulogui;

public class ROBEntry {
  ReorderBuffer rob;

  // TODO - add many more fields into entry
  // I deleted most, and only kept those necessary to compile GUI
  boolean complete = false;
  boolean predictTaken = false;
  boolean mispredicted = false;
  boolean storeDataValid = true;
  int instPC = -1;
  int writeReg = -1;
  int writeValue = -1;
  int address;
  int reg1;
  int reg2;
  int branchPredictedTarget;
  int correctBranchTarget;

  IssuedInst.INST_TYPE opcode;
  //IssuedInst.INST_CATEGORY type;

  public ROBEntry(ReorderBuffer buffer) {
    rob = buffer;
  }

  //public IssuedInst.INST_CATEGORY getType() {
  //    return type;
  //}

  public boolean isComplete() {
    return complete;
  }

  public boolean branchMispredicted() {
    return mispredicted;
  }

  public boolean getPredictTaken() {
    return predictTaken;
  }

  public int getInstPC() {
    return instPC;
  }

  public IssuedInst.INST_TYPE getOpcode () {
    return opcode;
  }


  public boolean isHaltOpcode() {
    return (opcode == IssuedInst.INST_TYPE.HALT);
  }

  public void setBranchTaken(boolean result) {
  // TODO - maybe more than simple set
  }

  public int getWriteReg() {
    return writeReg;
  }

  public int getWriteValue() {
    return writeValue;
  }

  public void setWriteValue(int value) {
    writeValue = value;
  }

  public void copyInstData(IssuedInst inst, int frontQ) {
    instPC = inst.getPC();
    inst.setRegDestTag(frontQ);

    // TODO - This is a long and complicated method, probably the most complex
    // of the project.  It does 2 things:
    // 1. update the instruction, as shown in 2nd line of code above
    // 2. update the fields of the ROBEntry, as shown in the 1st line of code above

      //First update the instruction
      //Is the register stored as a tag right now, meaning someone is waiting to write to it?
      reg1 = inst.getRegSrc1();
      reg2 = inst.getRegSrc2();
      int destReg = inst.getRegDest();
      opcode = inst.getOpcode();
      address = inst.getImmediate();
      predictTaken = inst.getBranchPrediction();
      branchPredictedTarget = inst.getBranchTgt();

      storeDataValid = true;
      for (ROBEntry entry : rob.buff) {
          if (entry != null && entry.getWriteReg() == reg2) {
              storeDataValid = false;
              break;
          }
      }
      if (storeDataValid && opcode == IssuedInst.INST_TYPE.STORE) {
          complete = true;
          writeValue = rob.getDataForReg(reg2);
      }

      //One-register instructions
      if (reg1 != -1) {
          int tag = rob.getTagForReg(reg1);
          if (tag == -1 || reg1 == 0) {
              inst.setRegSrc1Value(rob.getDataForReg(reg1));
              inst.setRegSrc1Valid();
          }
          else if (rob.getEntryByTag(tag).isComplete()) {
              inst.setRegSrc1Value(rob.getEntryByTag(tag).getWriteValue());
              inst.setRegSrc1Valid();
          }
          else {
              inst.setRegSrc1Tag(tag);
          }
      }
      else {
          inst.setRegSrc1Valid();
      }

      //Two-register instructions
      if (reg2 != -1) {
          int tag = rob.getTagForReg(reg2);
          if (tag == -1 || reg2 == 0) {
              inst.setRegSrc2Value(rob.getDataForReg(reg2));
              inst.setRegSrc2Valid();
          }
          else if (rob.getEntryByTag(tag).isComplete()) {
              inst.setRegSrc2Value(rob.getEntryByTag(tag).writeValue);
              inst.setRegSrc2Valid();
          }
          else {
              inst.setRegSrc2Tag(tag);
          }
      }
      else {
          inst.setRegSrc2Valid();
      }

      //Immediate instructions
      if (inst.getOpcode() == IssuedInst.INST_TYPE.ADDI || inst.getOpcode() == IssuedInst.INST_TYPE.ANDI ||
            inst.getOpcode() == IssuedInst.INST_TYPE.ORI || inst.getOpcode() == IssuedInst.INST_TYPE.XORI ||
            inst.getOpcode() == IssuedInst.INST_TYPE.SLL || inst.getOpcode() == IssuedInst.INST_TYPE.SRA ||
              inst.getOpcode() == IssuedInst.INST_TYPE.SRL || inst.getOpcode() == IssuedInst.INST_TYPE.LOAD ||
            inst.getOpcode() == IssuedInst.INST_TYPE.STORE)
      {
          inst.setRegSrc2Value(inst.getImmediate());
          inst.setRegSrc2Valid();
      }

      //Then update the ROBEntry
      if (opcode == IssuedInst.INST_TYPE.JAL || inst.getOpcode() == IssuedInst.INST_TYPE.JALR) {
          writeReg = 31;
      }
      else {
          writeReg = destReg;
      }

      //Update the tag for the destination
      //This should come after the source tags are set so that, if it write to a reg it reads from, it doesn't wait on itself
      if (writeReg != -1) {
          rob.setTagForReg(writeReg, frontQ);
      }
      //type = inst.getType();
  }

}
