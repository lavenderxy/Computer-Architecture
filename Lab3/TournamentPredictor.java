/*
 * Your Tournament Branch Predictor goes here.
 * 
 * 
 */

public class TournamentPredictor extends BranchPredictor {
	
	public BimodalPredictor bpred=null;
	public GSharePredictor gpred=null;
	public int nb=1;
	public int ng=1;
	public int gh=1;
	public int ct=1;
	public int chEntries=1;
	public TwoBitSatCtr[] chooserTable = null;
	

    public TournamentPredictor(int bm_tableSizeInBits, 
			       int gs_tableSizeInBits, int gs_historyLengthInBits, 
				  int chooserTableSizeInBits) {
    
    	nb=bm_tableSizeInBits;
    	ng=gs_tableSizeInBits;
    	gh=gs_historyLengthInBits;
    	chEntries=(int)Math.pow(2.0, chooserTableSizeInBits);
    	bpred=new BimodalPredictor(nb);
    	gpred=new GSharePredictor(ng,gh);
    	
    	chooserTable = new TwoBitSatCtr[chEntries];
    	
    	for (int i = 0; i < chEntries; i++) {
    	    chooserTable[i] = new TwoBitSatCtr();
    	    chooserTable[i].setChar(0, 'B');
    	    chooserTable[i].setChar(1, 'b');
    	    chooserTable[i].setChar(2, 'g');
    	    chooserTable[i].setChar(3, 'G');
    	}

    	
    }
    
    /* Given a PC, return true if this branch predictor predicts that the branch is taken.
     * Return false if this branch predictor predicts that the branch is not taken.
     */
    boolean predictIfTaken(long PC) {
    	
    	boolean bpredresult;
    	boolean gpredresult;
    	bpredresult=bpred.predictIfTaken(PC);
    	gpredresult=gpred.predictIfTaken(PC);
    	long counter;
    	boolean branch;
    	counter = PC % chEntries;
    	
    	
    	branch=chooserTable[(int)counter].isTrue();   
    	if (branch==true)
    		return gpredresult;
    	else
    		return bpredresult;
    	

    }

    /* Now you are given the PC and told whether the branch was taken or not.
     * Use this information to update your predictor.
     */
    void update(long PC, boolean wasTaken) {
    	boolean bpredresult;
    	boolean gpredresult;
    	long counter;
    	counter = PC % chEntries;
    	bpredresult=bpred.predictIfTaken(PC);
    	gpredresult=gpred.predictIfTaken(PC);
    	
    	if (bpredresult!=gpredresult){
    		if (wasTaken==gpredresult){
    			chooserTable[(int)counter].inc();
    		}
    		else {
    			chooserTable[(int)counter].dec();
    		}
    	}
    	
    	bpred.update(PC, wasTaken);
    	gpred.update(PC, wasTaken);
    	

    }

    void printHeader() {
    	System.out.format("PC\tChooseTable\tBimodTable\tGshareTable\t\tHist\tPred\tOutcome\tResult\t\tnIncorrect\n");
    }

    /* 
     * Technically, you don't have to implement this. It's here in case you want to
     * create the debugging outputs.
     */
    void printState() {

    }


}
