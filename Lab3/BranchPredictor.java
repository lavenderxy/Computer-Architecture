/* 
 * All the branch predictors that you create should be subclasses of this class.
 * DO NOT MODIFY THIS FILE. Instead, modify the subclass files.
 * Look at the Static Branch Predictors that have been created for you for Question 1
 * and use them as a model for the remaining branch predictors.
 */


abstract public class BranchPredictor {

    public BranchPredictor() {

    }
    
    /* 
     * Every subclass needs to implement this. The method takes the PC of the current 
     * instruction and then returns TRUE if the predictor says that this branch should
     * be taken, FALSE if the next instruction fetched should be PC + 4.
     */
    abstract boolean predictIfTaken(long PC);

    /* 
     * Every subclass needs to implement this except the Static Branch Predictor. 
     * The method takes the PC of the current instruction and whether the branch 
     * was taken or not.
     */
    abstract void update(long PC, boolean wasTaken);

    /* THESE PRINT STATEMENTS ARE FOR DEBUGGING PURPOSES ONLY SO THEY ARE OPTIONAL */

    /*
     * each branch predictor has a different header. 
     * implement at the subclass level, if you want.
     */
    void printHeader() {

    }

    /*
     * each branch predictor has a different internal state. 
     * implement at the subclass level, if you want.
     */
    void printState() {

    }

}
