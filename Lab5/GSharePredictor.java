/*
 * Your GShare Branch Predictor goes here.
 * 
 * 
 */

public class GSharePredictor extends BranchPredictor {

    public int nEntries = 1; // default
    public TwoBitSatCtr[] branchHistoryTable = null;
    public NBitHistoryRegister gshareHistoryRegister = null;
    public int nHistoryBits = 1; // default
    
    public GSharePredictor(int tableSizeInBits, int historyLengthInBits) {
	nEntries = (int)Math.pow(2.0, tableSizeInBits);
	branchHistoryTable = new TwoBitSatCtr[nEntries];
	gshareHistoryRegister = new NBitHistoryRegister(historyLengthInBits);

	for (int i = 0; i < nEntries; i++) {
	    branchHistoryTable[i] = new TwoBitSatCtr();
	    branchHistoryTable[i].setChar(0, 'N');
	    branchHistoryTable[i].setChar(1, 'n');
	    branchHistoryTable[i].setChar(2, 't');
	    branchHistoryTable[i].setChar(3, 'T');
	}

    }
    
    /* Given a PC, return true if this branch predictor predicts that the branch is taken.
     * Return false if this branch predictor predicts that the branch is not taken.
     */
    boolean predictIfTaken(long PC) {
	
	int PC_xor_history = (int)PC ^ gshareHistoryRegister.register;
	int whichEntry = PC_xor_history % nEntries;
	return branchHistoryTable[whichEntry].isTrue();

    }

    /* Now you are given the PC and told whether the branch was taken or not.
     * Use this information to update your predictor.
     */
    void update(long PC, boolean wasTaken) {

	int PC_xor_history = (int)PC ^ gshareHistoryRegister.register;
	int whichEntry = PC_xor_history % nEntries;
	if (wasTaken)
	    branchHistoryTable[whichEntry].inc();
	else
	    branchHistoryTable[whichEntry].dec();
	
	gshareHistoryRegister.update(wasTaken);

    }

    void printHeader() {
    	System.out.format("PC\tTableState\t\tHist\tPred\tOutcome\t  Result\tnIncorrect\n");
    }

    /* 
     * Technically, you don't have to implement this. It's here in case you want to
     * create the debugging outputs.
     */
    void printState() {

	System.out.format("\t");
	for (int i = 0; i < nEntries; i++) {
	    System.out.format("%c", branchHistoryTable[i].getChar());
	}
	System.out.format("\t");
	gshareHistoryRegister.print();
	
    }


}
