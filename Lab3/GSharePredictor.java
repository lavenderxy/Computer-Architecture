/*
 * Your GShare Branch Predictor goes here.
 * 
 * 
 */

public class GSharePredictor extends BranchPredictor {

    long nbit;
    long hr; 
    public NBitHistoryRegister bhr=null;
    public TwoBitSatCtr[] branchHistoryTable = null;
    public int nEntries = 1;
    public int nHistory=1;
	
    public GSharePredictor(int tableSizeInBits, int historyLengthInBits) {
    	nbit=tableSizeInBits;
    	hr=historyLengthInBits;
    	bhr=new NBitHistoryRegister(historyLengthInBits);
   
    	nEntries = (int)Math.pow(2.0, tableSizeInBits);
    	nHistory = (int)Math.pow(2.0, historyLengthInBits);
    	branchHistoryTable = new TwoBitSatCtr[nEntries];

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
    	
    	long counter;
    	boolean branch;
    	
    	counter = (PC^ bhr.register) %nEntries;
    	//System.out.println("ccc"+(PC^nbit)%hr);
    	
    	branch=branchHistoryTable[(int)counter].isTrue();
    	
    	return branch;
    }

    /* Now you are given the PC and told whether the branch was taken or not.
     * Use this information to update your predictor.
     */
    void update(long PC, boolean wasTaken) {
    	long counter;    	
    	counter = (PC^ bhr.register) %nEntries;
    	
    	if (wasTaken==true){
        	branchHistoryTable[(int)counter].inc();
        }
        else{
        	branchHistoryTable[(int)counter].dec();
        }
    	bhr.update(wasTaken);
    	

    }

    void printHeader() {
    	System.out.format("TableState\t\tHist\tPC\tOutcome\tPred\tResult\tnIncorrect\n");
    }

    /* 
     * Technically, you don't have to implement this. It's here in case you want to
     * create the debugging outputs.
     */
    void printState() {

    }


}
