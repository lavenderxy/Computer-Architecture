/*
 * It's probably overkill to create a branch predictor class that only ever 
 * predicts taken or not taken, but this will show you how the java subclass 
 * works.  For those of you new to Java, it will be very helpful.
 */

public class StaticPredictor extends BranchPredictor {

    boolean predictAlwaysTaken_f;

    public StaticPredictor(boolean predictAlwaysTaken) {
	predictAlwaysTaken_f = predictAlwaysTaken;

    }
    
    /* this just always returns taken or not taken for the entire run
     */
    boolean predictIfTaken(long PC) {

	return predictAlwaysTaken_f;
    }


    /*
     * there is nothing to do here because the past does
     * not affect a static branch predictor's behavior
     */
    void update(long PC, boolean wasTaken) {

	// LEAVE THIS BLANK. No update for a static predictor.

    }

    void printHeader() {

    	System.out.format("PC\tPred\tOutcome\t  Result\tnIncorrect\n");

    }

    /* Given a PC, this prints out the character that indicated which way to
     * make the prediction (N, n, t, T)
     */
    void printPredictionChar(long PC) {

	System.out.format("\t%s", predictAlwaysTaken_f ? 'T' : 'N');

    }


}
