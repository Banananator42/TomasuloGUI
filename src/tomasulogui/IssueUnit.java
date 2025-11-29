package tomasulogui;

public class IssueUnit {
  private enum EXEC_TYPE {
    NONE, LOAD, ALU, MULT, DIV, BRANCH} ;

    PipelineSimulator simulator;
    IssuedInst issuee;
    Object fu;

    public IssueUnit(PipelineSimulator sim) {
      simulator = sim;
    }

    public void execCycle() {
      // an execution cycle involves:
      // 1. checking if ROB and Reservation Station avail
      // 2. issuing to reservation station, if no structural hazard

      // to issue, we make an IssuedInst, filling in what we know
      // We check the BTB, and put prediction if branch, updating PC
      //     if pred taken, incr PC otherwise
      // We then send this to the ROB, which fills in the data fields
      // We then check the CDB, and see if it is broadcasting data we need,
      //    so that we can forward during issue

      // We then send this to the FU, who stores in reservation station

        //check to see if there is a spot in the ROB (there will be lol)
        if (simulator.getROB().isFull()) {
            return;
        }

        //first need to know what which FU it would go to
        int pc = simulator.getPC();
        int instruction = simulator.getMemory().getIntDataAtAddr(pc);
        Instruction instr = Instruction.getInstructionFromOper(instruction);
        issuee = IssuedInst.createIssuedInst(instr);
        issuee.setPC(pc);

        //determine the instruction type
        EXEC_TYPE type;
        switch (instr.getOpcode()) {
            case Instruction.INST_LW -> type = EXEC_TYPE.LOAD;
            case Instruction.INST_MUL -> type = EXEC_TYPE.MULT;
            case Instruction.INST_DIV -> type = EXEC_TYPE.DIV;
            case Instruction.INST_BEQ, Instruction.INST_BNE, Instruction.INST_BGEZ,
                 Instruction.INST_BLEZ, Instruction.INST_BGTZ, Instruction.INST_BLTZ ->
                    type = EXEC_TYPE.BRANCH;
            default -> type = EXEC_TYPE.ALU;
        }

        //set the fu variable (but do we need it?? he gave it to us)
        switch (type) {
            case LOAD -> fu = simulator.getLoader();
            case MULT -> fu = simulator.getMult();
            case DIV -> fu = simulator.getDivider();
            case BRANCH -> fu = simulator.getBranchUnit();
            case ALU -> fu = simulator.getALU();
        }

        //send a LOAD to the buffer if space is available
        if (type == EXEC_TYPE.LOAD) {
            LoadBuffer loadBuffer = (LoadBuffer) fu;
            if (loadBuffer.isReservationStationAvail()) {
                simulator.getROB().updateInstForIssue(issuee);

                //look for some forwarding
                CDB cdb = simulator.getCDB();
                if (issuee.getRegSrc1Tag() == cdb.getDataTag() && cdb.getDataValid()) {
                    issuee.setRegSrc1Value(cdb.getDataValue());
                    issuee.setRegSrc1Valid();
                }
                if (issuee.getRegSrc2Tag() == cdb.getDataTag() && cdb.getDataValid()) {
                    issuee.setRegSrc2Value(cdb.getDataValue());
                    issuee.setRegSrc2Valid();
                }

                loadBuffer.acceptIssue(issuee);
                simulator.pc.incrPC();
            }
        }
        else { //the fu must be a child of FunctionalUnit
            FunctionalUnit functionalUnit = (FunctionalUnit) fu;

            if (type == EXEC_TYPE.BRANCH) {
                BranchPredictor btb = simulator.getBTB();
                btb.predictBranch(issuee); //this will change the PC either way
            }

            if (functionalUnit.isReservationStationAvail()) {
                simulator.getROB().updateInstForIssue(issuee);

                //look for some forwarding
                CDB cdb = simulator.getCDB();
                if (issuee.getRegSrc1Tag() == cdb.getDataTag() && cdb.getDataValid()) {
                    issuee.setRegSrc1Value(cdb.getDataValue());
                    issuee.setRegSrc1Valid();
                }
                if (issuee.getRegSrc2Tag() == cdb.getDataTag() && cdb.getDataValid()) {
                    issuee.setRegSrc2Value(cdb.getDataValue());
                    issuee.setRegSrc2Valid();
                }

                functionalUnit.acceptIssue(issuee);
                simulator.pc.incrPC();
            }
        }
    }

  }
