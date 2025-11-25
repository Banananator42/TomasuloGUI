package tomasulogui;

public class ROBEntry {
  ReorderBuffer rob;

  // TODO - add many more fields into entry
  // I deleted most, and only kept those necessary to compile GUI
  boolean complete = false;
  boolean predictTaken = false;
  boolean mispredicted = false;
  int instPC = -1;
  int writeReg = -1;
  int writeValue = -1;

  IssuedInst.INST_TYPE opcode;
  //IssuedInst.INST_CATEGORY type;

  public ROBEntry(ReorderBuffer buffer) {
    rob = buffer;
  }

  //public IssuedInst.INST_CATEGORY getType() {
  //    return type;
  //  }

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
      int reg1 = inst.getRegSrc1();
      int reg2 = inst.getRegSrc2();
      int destReg = inst.getRegDest();
      IssuedInst.INST_TYPE opcode = inst.getOpcode();

      //Update the tag for the destination
      if (destReg != -1) {
          rob.setTagForReg(destReg, frontQ);
      }

      //ADD, ADDI, SUB, MUL, DIV, AND, ANDI, OR, ORI, XOR, XORI, SLL, SRL, SRA,
      //        LOAD, STORE, HALT,
      //        NOP, BEQ, BNE, BLTZ, BLEZ, BGEZ, BGTZ, J, JAL, JR, JALR

      //One-register instructions
      /*if (opcode == IssuedInst.INST_TYPE.ADD || opcode == IssuedInst.INST_TYPE.SUB ||
              opcode == IssuedInst.INST_TYPE.AND || opcode == IssuedInst.INST_TYPE.BEQ ||
              opcode == IssuedInst.INST_TYPE.OR || opcode == IssuedInst.INST_TYPE.XOR ||
              opcode == IssuedInst.INST_TYPE.ADDI || opcode == IssuedInst.INST_TYPE.MUL ||
              opcode == IssuedInst.INST_TYPE.DIV || opcode == IssuedInst.INST_TYPE.ANDI ||
              opcode == IssuedInst.INST_TYPE.ORI || opcode == IssuedInst.INST_TYPE.XORI ||
              opcode == IssuedInst.INST_TYPE.LOAD || opcode == IssuedInst.INST_TYPE.STORE ||
              opcode == IssuedInst.INST_TYPE.BNE || opcode == IssuedInst.INST_TYPE.BLTZ ||
              opcode == IssuedInst.INST_TYPE.BLEZ || opcode == IssuedInst.INST_TYPE.BGEZ ||
              opcode == IssuedInst.INST_TYPE.BGTZ || opcode == IssuedInst.INST_TYPE.JR ||
              opcode == IssuedInst.INST_TYPE.JALR) {*/
      if (reg1 != -1) {
          int tag = rob.getTagForReg(reg1);
          if (tag != -1) {
              inst.setRegSrc1Tag(tag);
          } else {
              inst.setRegSrc1Value(rob.getDataForReg(reg1));
              inst.setRegSrc1Valid();
          }
      }
      else {
          inst.setRegSrc1Valid();
      }

      //Two-register instructions
      /*if (opcode == IssuedInst.INST_TYPE.ADD || opcode == IssuedInst.INST_TYPE.SUB ||
              opcode == IssuedInst.INST_TYPE.AND || opcode == IssuedInst.INST_TYPE.OR ||
              opcode == IssuedInst.INST_TYPE.XOR || opcode == IssuedInst.INST_TYPE.BEQ ||
              opcode == IssuedInst.INST_TYPE.BNE) {*/
      if (reg2 != -1) {
          int tag = rob.getTagForReg(reg2);
          if (tag != -1) {
              inst.setRegSrc2Tag(tag);
          } else {
              inst.setRegSrc2Tag(frontQ);
              rob.setTagForReg(reg2, frontQ);
          }
      }
      else {
          inst.setRegSrc2Valid();
      }

      //Immediate instructions
      if (inst.type == IssuedInst.INST_CATEGORY.I_TYPE) {
          inst.setRegSrc2Value(inst.getImmediate());
          inst.setRegSrc2Valid();
      }

      //Then update the ROBEntry
      writeReg = destReg;
      //type = inst.getType();
  }

}
