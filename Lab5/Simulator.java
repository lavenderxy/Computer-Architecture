import java.lang.String;
import java.util.zip.GZIPInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Iterator;

public class Simulator
{    

    public String testTrace = "sjeng-1K"; // default

    // print options, by default off
    public boolean verbose_f = false;
    public boolean printScoreboard_f = false;

    // optionally limit the simulation to the first N insns
    // do not break this functionality when you complete the code!
    public boolean insnLimit_f = false;
    public int uopLimit = 0;
    public boolean doneFetching_f = false;

	//HW 5 flags
	public boolean bpred_f = false;
	public boolean npfetch_f = false;
	public boolean cache_f = false;
	
    // don't touch! you'll be sorry....
    public BufferedReader traceReader = null;
	
    // Processor Configuration
    public int machineWidth = 1;
    private final int NUM_REGS = 50;
	private final int FLAGS_REG = 49;
	public int[] scoreboard = new int[NUM_REGS];
	public int branchStall = 0;
	private GSharePredictor bpred = null;
	private Cache cache = null;


    // Program Stats
    public long totalUops = 0;
    public long totalMops = 0;
    public long cycle_count = 0;
    public double CPI = 0; // in micro-ops
    public double IPC = 0; // in micro-ops
    public int mis_penalty=5;

    /*
     * Simple constructor for the simulator.
     */
    public Simulator() {
		for(int i=0; i < NUM_REGS; ++i){
			scoreboard[i] = 0;
		}
	
		bpred = new GSharePredictor (16,16); //(TableSize(bits),HistorySize)		
		cache = new Cache (8192, 64, 2); //(Capacity(B), Bsize (B), Assoc)
    }
    

    /*
     * set up the trace as usual
     */
    public void initTrace() throws IOException {
	
	String tracePath = "../traces/"+testTrace+".trace.gz";
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

		System.out.println("Processing trace...");

        if (printScoreboard_f)
	    printSBHeader();
        
        while (true) {
        	for (int j=0; j<machineWidth; j++){
	   
        	if (branchStall>0 && bpred_f)
    			break;
		 if (fetch_next_insn) {
			line = traceReader.readLine();
			if (line == null) {
				cycle_count=cycle_count-1;
				CPI = (double)cycle_count/totalUops; 
        		IPC = (double)totalUops/cycle_count; 
				return;
			}

			if (insnLimit_f && totalUops == uopLimit) {
		    	System.out.format("Reached insn limit of %d. Ending Simulation...\n", 
				      uopLimit);
		    	cycle_count=cycle_count-1;
				CPI = (double)cycle_count/totalUops; 
        		IPC = (double)totalUops/cycle_count; 
				return;
			}
			currUop = new Uop(line, FLAGS_REG);
			totalUops++;
				
			if (currUop.microOpCount == 1) {
			    totalMops++;		                	
			}
			
			fetch_next_insn = false;	
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
			// TODO: Check if insn is ready and "execute" it.
			// This will be similar, but slightly different than what you did in 2a due
			// to the change in the way input/output regs are handled. See Uop.java.			
			//fetch_next_insn = true;
			if (isRegReady(currUop.inputRegs[0]) && isRegReady(currUop.inputRegs[1]) && isRegReady(currUop.inputRegs[2])
					&& isRegReady(currUop.outputRegs[0]) && isRegReady(currUop.outputRegs[1])){
				    fetch_next_insn=true;		    
				    if (currUop.outputRegs[0] !=-1 ){
		    			if (currUop.isLoad()){
		    				scoreboard[currUop.outputRegs[0]]=2;
		    				if (cache_f==true){
		    					if (cache.access(currUop.addressForMemoryOp, currUop.isLoad())){
		    						scoreboard[currUop.outputRegs[0]]=2;
		    					}
		    					else
		    						scoreboard[currUop.outputRegs[0]]=2+7;
		    				}
		    			}
		    				
		    			else
		    				scoreboard[currUop.outputRegs[0]]=1;    						    				
		    		
		    		}
		    		
		    		if (currUop.outputRegs[1]!=-1)
		    			scoreboard[currUop.outputRegs[1]]=1;
		    		
		    		if (bpred_f==true){
		    		if(currUop.targetPC !=0 && currUop.conditionRegister.equals("R")){
		    			boolean isTaken = true;
		    		    boolean predictedTaken=true;
		    		    boolean isPredCorrect = true;
		    		    
		    			
		    		    if (currUop.TNnotBranch.equals("T"))
		    		    	isTaken=true;
		    		    else if (currUop.TNnotBranch.equals("N"))
		    		    	isTaken=false;
		    		   	 
		    		    predictedTaken=bpred.predictIfTaken(currUop.PC);
		    		    
		    		    if (isTaken==predictedTaken){
		    	    		isPredCorrect=true;
		    	    	}
		    	    	else if (!(isTaken==predictedTaken)){
		    	    		isPredCorrect=false;
		    	    		branchStall=mis_penalty;
		    	    	}
		    	    bpred.update(currUop.PC, isTaken);
		    		    
		    		} 
		    		}
		    		
		    		if (npfetch_f==true){
			    		
		    			if (currUop.TNnotBranch.equals("T")){
		    				if (printScoreboard_f)
		        				printScoreboard();
		        	    
		        	    	// prints the insn
		                	if (verbose_f)
		                	   	System.out.format("%s\n", line);

		        
		    				break;
	    					}
		    		}
		    		
		    		if (cache_f==true){
		    			
		    		}
		    }
			
			else{
    			if (printScoreboard_f)
    				printScoreboard();
    	    
    	    	// prints the insn
            	if (verbose_f)
            	   	System.out.format("%s\n", line);

    
				break;
    		}

			
	
	    	// prints the state of the scoreboard at the end of this cycle.
	    	if (printScoreboard_f)
				printScoreboard();
	    
	    	// prints the insn
        	if (verbose_f)
        	   	System.out.format("%s\n", line);
	    	
			//Update scoreboard
			
        }
	     for(int i = 0; i < scoreboard.length; i++){
				if(scoreboard[i] != 0){
					scoreboard[i]--;
				}
			}
	     
	     if (branchStall !=0)
		   	branchStall--;
		cycle_count++;                                                

        }
        

    }
    
    public boolean isRegReady(int n) {
   		if(n == -1){
			return true;
		} else {
			return(scoreboard[n] == 0);
		}
    }    

    public void printHeader() {
	if (!printScoreboard_f) {
	    System.out.format("HW 5 Printout for xinyu.yan\n");
	    System.out.format("-----------------------------------------------------\n");
    	System.out.format("Trace name  \t\t\t\t%s\n", testTrace);
    	System.out.format("Load Latency (cycles) \t\t\t%d\n", Uop.LOAD_LAT);	
	    System.out.format("Instruction Limit\t\t\t%s\n", insnLimit_f ? Integer.toString(uopLimit) : "none");	
	}
    }
    
    public void printSBHeader() {
	System.out.format("cycle\tuop  Src1 (ready?)\tSrc2 (ready?)\tDestReg\tLdSt\tcontents-of-the-scoreboard BrSt\n");
    }
    
    public void printSBInputOutput(Uop currUop) {
	
    	System.out.format(" %4d %4d\t%2d %s\t%2d %s\t-> %2d\t%s\t", cycle_count, totalUops, 
			  currUop.inputRegs[0], printSBRegReady((int)currUop.inputRegs[0]), 
			  currUop.inputRegs[1], printSBRegReady((int)currUop.inputRegs[1]),
			  currUop.outputRegs[0], currUop.loadStore
			  );
    }
    
    public void printScoreboard() {
	for (int i = 0; i < NUM_REGS; i++) {
	    if (scoreboard[i] == 0)
			System.out.format("-");
	    else
			System.out.format("%d", scoreboard[i]);
	}
	//Print branch stall counter
	System.out.format(" ");
	if(branchStall == 0) 
		System.out.format("-");
	else
		System.out.format("%d", branchStall);

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
    }
    
    
}
