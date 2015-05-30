import java.lang.String;
import java.util.zip.GZIPInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Simulator
{    
    public String testTrace = "mcf-1K"; // default
		
    // print options, by default off
    public boolean verbose_f = false;
    public boolean printScoreboard_f = false;
    public boolean printInsnMix_f = false;
	
    // whether you require register writes to be in order, keep default as true
    public boolean enforceRegWriteOrder_f = true;

    // The simulate function will now optionally limit the simulation to the first N insns
    // do not break this functionality when you complete the code!
    public boolean insnLimit_f = false;
    public int uopLimit = 0;
	
    // don't touch! you'll be sorry....
    public BufferedReader traceReader = null;
    public final int NUM_REGS = 50;


    public FrequencyHistogram InsnTypeFrequencyHistogram =
	new FrequencyHistogram("Frequency of Each Uop Type", "uops of type", ""); 
	
    // Program Stats
    public long totalUops = 0;
    public long totalMops = 0;
    public long cycle_count = 0;
    public double CPI = 0; // in micro-ops
    public double IPC = 0; // in micro-ops
    
    // Scoreboard
    public int[] scoreboard = new int[NUM_REGS]; // should all be zeroes at this point
    
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
	//String tracePath = "/Users/yxy5665/Documents/workspace/HW2/src/traces/"+testTrace+".trace.gz";
	
	// ugly, but JAVA IO is not the point
	traceReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(tracePath))));
    }

    
    /*
     * Goes through the trace line by line (i.e., instruction by instruction) and simulates
     * the program being executed on a processor.
     * 
     * This method is currently stripped down. It will just read in each instruction one at 
     * a time, makes no updates to the scoreboard, and lets every instruction execute without 
     * checking for readiness.
     */
    public void processTrace() throws IOException  {
        String line = "";
        boolean fetch_next_insn = true;     
        Uop currUop = null;
        
	initTrace();

        if (printScoreboard_f)
	    printSBHeader();
        
        while (true) {
	    if (fetch_next_insn) {
		line = traceReader.readLine();
		fetch_next_insn = false;
		if (line == null) {
		    break;
		}
		if (insnLimit_f && totalUops == uopLimit) {
		    System.out.format("Reached insn limit of %d. Ending Simulation...\n", 
				      uopLimit);
		    break;
		}
		currUop = new Uop(line);
		// You will need this statistic for Question 1
		InsnTypeFrequencyHistogram.increment(currUop.type.value);
		totalUops++;
		if (currUop.microOpCount == 1) {
		    totalMops++;		                	
		}
		 
	    }
	    
	    if (isRegReady(currUop.sourceRegister1) && isRegReady(currUop.sourceRegister2)){
	    	if (enforceRegWriteOrder_f==true){
	    		if (currUop.destinationRegister==-1)
	    			fetch_next_insn=true;
	    		else if (isRegReady(currUop.destinationRegister) && scoreboard[currUop.destinationRegister] <currUop.exec_latency){
	    				scoreboard[currUop.destinationRegister]=currUop.exec_latency;
	    				fetch_next_insn=true;
	    				}}
	    	else if (enforceRegWriteOrder_f==false){
			    if (currUop.destinationRegister==-1)
			    	fetch_next_insn=true;
			    else {
		    		scoreboard[currUop.destinationRegister]=currUop.exec_latency;
		    		fetch_next_insn=true;
		    		}	
			    }
	    }	  
	    

	    
	    // the printing of the scoreboard is split in two parts.
	    // the reason for this is that if an input register is also an output register, 
	    // we need to print out the "Readiness" of the input register BEFORE it becomes 
	    // the output register and is suddenly not ready again.
	    // In other words, (1) print out the first half of the line 
	    //	               (2) decide whether the instruction can be executed and update 
	    //                     the scoreboard accordingly
	    //  	       (3) THEN print out the scoreboard
	    
	    
            // prints the out whether the inputs are ready or not and what the output is
	    if (printScoreboard_f)
		printSBInputOutput(currUop);
	    
	    // if insn is ready....
	    	    
	    // prints the state of the scoreboard at the end of this cycle.
	    if (printScoreboard_f)
		printScoreboard();
	    
	    // prints the insn
            if (verbose_f)
            	System.out.format("%s\n", line);
	    
            cycle_count++;   
  
         for (int i = 0; i < NUM_REGS; i++) {
        	 if (scoreboard[i] != 0)
        		 scoreboard[i]-=1;
         }
        }
        
	CPI = (double)cycle_count/totalUops; 
        IPC = (double)totalUops/cycle_count; 

    }
    
    public boolean isRegReady(int n) {
    		if (n==-1)
    			return true;
    		else if (scoreboard[n]==0)
    			return true;
    		else
    			return false;
   
    }    
    
    public void printHeader() {
	if (!printScoreboard_f) {
	    System.out.format("HW 2 Printout for xinyu.yan\n");
	    System.out.format("-----------------------------------------------------\n");
	}
    	System.out.format("Trace name  \t\t\t\t%s\n", testTrace);
    	System.out.format("Load Latency (cycles) \t\t\t%d\n", Uop.LOAD_lat);	
	if (!printScoreboard_f) {
	    System.out.format("Instruction Limit\t\t\t%s\n", insnLimit_f ? Integer.toString(uopLimit) : "none");	
	    System.out.format("Enforcing Register Write Order?\t\t%s\n\n", enforceRegWriteOrder_f ? "yes" : "no");	
	}
    }
    
    public void printSBHeader() {
	System.out.format("cycle\tuop  Src1 (ready?)\tSrc2 (ready?)\tDestReg\tLdSt\tcontents-of-the-scoreboard\n");
    }
    
    public void printSBInputOutput(Uop currUop) {
	
    	System.out.format(" %4d %4d\t%2d %s\t%2d %s\t--> %2d\t%s\t", cycle_count, totalUops, 
			  currUop.sourceRegister1, printSBRegReady((int)currUop.sourceRegister1), 
			  currUop.sourceRegister2, printSBRegReady((int)currUop.sourceRegister2),
			  currUop.destinationRegister, currUop.loadStore
			  );
    }
    
    public void printScoreboard() {
	for (int i = 0; i < NUM_REGS; i++) {
	    if (scoreboard[i] == 0)
		System.out.format("-");
	    else
		System.out.format("%d", scoreboard[i]);
	}
	System.out.format("\n");
    }
    
    public String printSBRegReady(int n) {
    	return (isRegReady(n) ? "(Ready)" : "(NotReady)");
    }
    
    public void printTraceStats() {
	
    	System.out.format("Processed %d trace lines.\n", totalUops);
    	System.out.format("Num Uops (micro-ops): \t\t\t%d\n", totalUops);
    	System.out.format("Num Mops (macro-ops): \t\t\t%d\n", totalMops);
    	System.out.format("Total Execution Time (in cycles): \t%d\n", cycle_count);
    	System.out.format("CPI (per uop): \t\t\t\t%.3f\n", CPI);
    	System.out.format("IPC (per uop): \t\t\t\t%.3f\n", IPC);
	if (printInsnMix_f) {
	    System.out.format("Trace has a mix of the following instruction types:\n");
	    Uop.UopType.printAll();
	    InsnTypeFrequencyHistogram.print();
	}
    }
    
    
}
