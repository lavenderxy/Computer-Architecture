import java.lang.String;
import java.util.zip.GZIPInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Simulator
{    

    public String testTrace = "sjeng-1K"; // default

    // cache stuff
    public CacheStats cacheStats = null;
    public Cache myCache = null;

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
    
    int count_in=0;
    int count_store=0;
    int count_writeback=0;
    
    /*
     * Simple constructor for the simulator.
     */
    public Simulator() {
	cacheStats = new CacheStats();
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
     * one at a time, and does not access the cache. 
     */
    public void processTrace() throws IOException  {
        String line = "";    
        Uop currUop = null;
        
	initTrace();

	if (debug_f)
	    myCache.printHeader();
        
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
	    
	    // Print this before you access the cache
	    if (debug_f)
		myCache.printCache();

	    // access the cache
	    // update the cache stats
	    
	    boolean hit_f = true;

	    if (currUop.isStore() || currUop.isLoad()){
	    	hit_f=myCache.access(currUop.addressForMemoryOp, currUop.isLoad());
	    	cacheStats.updateStat(hit_f);
	    	if (hit_f==false){
	    		count_in+=myCache.blockSize;
	    		if (myCache.dirtyEvic_f==true){
	    			//System.out.println(myCache.dirtyEvic_f);
	    			count_writeback+=2*myCache.blockSize; }
	    		else if (myCache.dirtyEvic_f==false)
	    			count_writeback+=myCache.blockSize;
	    			
	    		//cacheStats.total_bytes_transferred_wt+=myCache.blockSize;
	    	}
	 
	    	//traffic into the cache=number of miss*multiplied by the block size.
	    	//count_in=count_in*myCache.blockSize;
	    	
	    	//write-through cache
	    	if (currUop.isStore()==true ){
	    		count_store+=4;  		
	    		//cacheStats.total_bytes_transferred_wt += 4;
	    	}
	    	//cacheStats.total_bytes_transferred_wt
	    	//cacheStats.total_bytes_transferred_wt+=(count_in+count_store);
	    }	  
	    
	    cacheStats.total_bytes_transferred_wt=(count_in+count_store);
	    cacheStats.total_bytes_transferred_wb=count_writeback;
	    
	    //cacheStats.updateStat(hit_f);
	    	    
	    // Print this after you access the cache
	    if (debug_f) 
		System.out.format("%s\t%s\t%s\t%s\n", 
				  Long.toHexString(myCache.getBlockAddress(currUop.addressForMemoryOp)),
				  currUop.isLoad() ? "L" : "S", 
				  hit_f ? "hit" : "miss", 
				  hit_f ? "--" : myCache.dirtyEvic_f ? "dirty" : "clean");
	    

	    // END WHERE YOU SHOULD MAKE CHANGES TO THIS FILE
	    
            // prints the insn
	    if (verbose_f)
		System.out.format("%s\n", line);
	    
        }

    	cacheStats.calculateRates();
    }
    
    
    /*
     * Prints out basic stats + the hit/miss rates of the cache.
     */
    public void printTraceStats() {
	
    	System.out.format("Processed %d trace lines.\n", totalUops);
    	System.out.format("Num Uops (micro-ops): \t\t\t%d\n", totalUops);
	
        cacheStats.print(); 
	
    }

    public void printHeader() {
	if (!debug_f) {
	    System.out.format("HW 4 Printout for i-forgot-to-replace-this-string-with-my-wustlkey\n");
	    System.out.format("-----------------------------------------------------\n");
	}
    	System.out.format("Trace name  \t\t\t\t%s\n", testTrace);
	myCache.printConfig();
	System.out.format("Instruction Limit\t\t\t%s\n", insnLimit_f ? Integer.toString(uopLimit) : "none");	
    }
    
}
