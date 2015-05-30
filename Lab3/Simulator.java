import java.lang.String;
import java.util.zip.GZIPInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Simulator
{    

    public String testTrace = "sjeng-1K"; // default

    // what type of branch predictor are you using?
    public String bpredType = "static";
    public String bpredSubType = "taken";
    public BranchPredictor bpred = null;

    // this keeps the accuracy statistics of your branch predictor
    public PredictorStats bpredStats = null;

    // print options, by default off
    public boolean verbose_f = false;
    public boolean debug_f = false;

    // optionally limit the simulation to the first N insns
    // do not break this functionality when you complete the code!
    public boolean insnLimit_f = false;
    public int uopLimit = 0;
	
    // don't touch! you'll be sorry....
    public BufferedReader traceReader = null;
	
    // Program Stats
    public long totalUops = 0;
    
    /*
     * Simple constructor for the simulator.
     */
    public Simulator() {

    }

    /*
     * set up the trace
     */
    public void initTrace() throws IOException {
	
	String tracePath = "../traces/"+testTrace+".trace.gz";
	// ugly, but JAVA IO is not the point
	traceReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(tracePath))));
    }

    /*
     * Goes through the trace line by line (i.e., instruction by instruction) and 
     * simulates the program being executed on a processor.
     * 
     * This method is currently stripped down. It will just read in each instruction 
     * one at a time, and does not make any predictions. 
     */
    public void processTrace() throws IOException  {
        String line = "";    
        Uop currUop = null;
        
	initTrace();

	if (debug_f)
	    bpred.printHeader();
        
        while (true) {
	    line = traceReader.readLine();
	    if (line == null) {
		break;
	    }
	    if (insnLimit_f && totalUops == uopLimit) {
		System.out.format("Reached insn limit of %d. Ending Simulation...\n", uopLimit);
		break;
	    }
	    currUop = new Uop(line);
	    totalUops++;
	    
	    // BEGIN WHERE YOU SHOULD MAKE CHANGES TO THIS FILE
	    
	    if (currUop.targetPC !=0 && currUop.conditionRegister.equals("R")){
	    // Print this before you make any updates to the branch predictor
	    if (debug_f) {
		System.out.format("%s", Long.toHexString(currUop.PC));
		bpred.printState();
	    }
	    
	    // so that your code will compile as is...
	    boolean isTaken = true;
	    boolean predictedTaken=true;
	    boolean isPredCorrect = true;
	    //predictedTaken=bpred.predictIfTaken(currUop.PC);
	    //System.out.println(predictedTaken);
	
	 	//predictedTaken=bpred.predictIfTaken(currUop.PC);
	    if (currUop.TNnotBranch.equals("T"))
	    	isTaken=true;
	    else if (currUop.TNnotBranch.equals("N"))
	    	isTaken=false;
	   	 
	    predictedTaken=bpred.predictIfTaken(currUop.PC);
	    
	    if (bpredType.equals("static")){
	    	if (isTaken==predictedTaken){
	    		isPredCorrect=true;
	    	}
	    	else if (!(isTaken==predictedTaken))
	    		isPredCorrect=false;
	    } 
	    
	    if (bpredType.equals("bimodal")){
	    	if (isTaken==predictedTaken){
	    		isPredCorrect=true;
	    	}
	    	else if (!(isTaken==predictedTaken))
	    		isPredCorrect=false;
	    bpred.update(currUop.PC, isTaken);
	    }  
	    //bpred.update(currUop.PC, isTaken);
	    
	    if (bpredType.equals("gshare")){
	    	if (isTaken==predictedTaken){
	    		isPredCorrect=true;
	    	}
	    	else if (!(isTaken==predictedTaken))
	    		isPredCorrect=false;
	    bpred.update(currUop.PC, isTaken);
	    }
	    
	    if (bpredType.equals("tournament")){
	    	if (isTaken==predictedTaken){
	    		isPredCorrect=true;
	    	}
	    	else if (!(isTaken==predictedTaken))
	    		isPredCorrect=false;
	    bpred.update(currUop.PC, isTaken);
	    }
	    
	    bpredStats.updateStat(isPredCorrect);  
	    
	    
	    
	   
	    // Print this after you get the prediction and update the
	    // stats for the predictor
	    if (debug_f) {
		System.out.format("\t%s", (predictedTaken ? 'T' : 'N'));
		System.out.format("\t%s", currUop.TNnotBranch);
		System.out.format("\t%s", (isPredCorrect ? "  correct" : "incorrect"));
		System.out.format("\t%d", bpredStats.totalIncorrect());
		    System.out.format("\n");
	    }

	    // END WHERE YOU SHOULD MAKE CHANGES TO THIS FILE
	    
            // prints the insn
	    if (verbose_f)
		System.out.format("%s\n", line);
	    
        }
        }

	bpredStats.calculateRate(); 

    }
    
    
    /*
     * Prints out basic stats + the accuracy of the branch predictor
     */
    public void printTraceStats() {
	
    	System.out.format("Processed %d trace lines.\n", totalUops);
    	System.out.format("Num Uops (micro-ops): \t\t\t%d\n", totalUops);
	
	bpredStats.print(); 
	
    }

    public void printHeader() {
	if (!debug_f) {
	    System.out.format("HW 3 Printout for xinyu.yan\n");
	    System.out.format("-----------------------------------------------------\n");
	}
    	System.out.format("Trace name  \t\t\t\t%s\n", testTrace);
    	System.out.format("Branch Predictor: \t\t\t%s -- %s\n", bpredType, bpredSubType);	
	System.out.format("Instruction Limit\t\t\t%s\n", insnLimit_f ? Integer.toString(uopLimit) : "none");	
    }
    
}
