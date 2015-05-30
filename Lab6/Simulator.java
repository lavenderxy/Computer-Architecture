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
    public boolean debug_f = false;
    public boolean renameOnly_f = false;

    // optionally limit the simulation to the first N insns
    // do not break this functionality when you complete the code!
    public boolean insnLimit_f = false;
    public int uopLimit = 0;
    public boolean doneFetching_f = false;
	
    // don't touch! you'll be sorry....
    public BufferedReader traceReader = null;
	
    // Processor Configuration
    public int machineWidth = 1; 
    public final int NUM_ARCH_REGISTERS = 50;  //the set of architectural registers is from zero to 49,so size is 50  
    public int nPhysicalRegisters = NUM_ARCH_REGISTERS; // must have *at least* this many

    // Processor Structures
    public int MapTable[] = null;
    public ArrayDeque<Integer> freeList = null;
    public ReorderBuffer ROB = null;
    public int[] scoreboard = null;    

    // Program Counters & Stats
    public double IPC = 0.0;
    public long nFetched = 0; 
    public long nCommitted = 0; 
    public long nROBstalls = 0; 
    public long cycleCount = 0;
    public long lastCommittedCycle = 0;

    // Simulating Memory Dependences
    public boolean modelMemoryDependences_f = false; 
    // At first: ignore memory dependences, set to false
    // if the above flag is true then we actually check the flag below
    // to determine whether we model the dependences conservatively or perfectly 
    // (false = conservative = Exp #2, true = perfect = Exp #3)
    public boolean perfectMemSched_f = false; 
    
    /*
     * Simple constructor for the simulator.
     */
    public Simulator() {
	
	MapTable = new int[NUM_ARCH_REGISTERS];
	// TODO: initialize your MapTable: for all n,
	// map architectural register n to physical register n
	//initial the map table
	for (int i=0; i<NUM_ARCH_REGISTERS;i++){
		MapTable[i]=i;
	}

	freeList = new ArrayDeque<Integer>(); //a FIFO queue of physical register names of type ArrayDeque<Integer>
	
	ROB = new ReorderBuffer();
    }
    
    /*
     * Once the simulator is constructed, the number of physical
     * registers can be specified by setting the variable
     * nPhysicalRegisters.  Once that number is set, you can set up
     * the register file and associated structures
     */ 
    public void initRegStructures() {

	scoreboard = new int[nPhysicalRegisters]; 
	
	// initialize to all zeroes
	for (int i = 0; i < this.nPhysicalRegisters; i++) {
	    scoreboard[i] = 0;
	}

	
	// now that you know the size of the p regfile, you must
	// initialize your freeList: anyone not mapped is free. 
	// add them such that the very first register you take OFF
	// The freeList has the lowest unused register number.
	// Example: to add 3, you must convert it into an Integer: 
	//     freeList.addLast(new Integer(3));
	
	
	if (nPhysicalRegisters>NUM_ARCH_REGISTERS){
		for (int i=NUM_ARCH_REGISTERS; i<nPhysicalRegisters;i++)
			freeList.addLast(i);
	}  
	
	
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
     * Commit instructions in order starting at the head of the ROB.
     * on an n-wide machine, you can commit n instructions per cycle
     */
    public void commit() {
	
	for (int i = 0; i < machineWidth; i++) {
	    if (ROB.isEmpty()) // nothing to commit. bye!
		break;

	    ROBentry head = ROB.head(); // this doesn't remove the entry from the ROB

	    // TODO: commit head of the ROB if you can, update nCommitted, print debug line
	    if (head.doneCycle<=cycleCount && !(head.doneCycle == -1)){
	    	for (int j=0; j<Uop.MAX_OUTPUTS;j++){
		    	if(!(head.theUop.outputRegs[j]==-1)){
		    		freeList.addLast(head.overwrittenRegs[j]);
		    	}
		    }
	    	ROB.dequeue();
	    	nCommitted++;
	    	lastCommittedCycle = cycleCount;	
	 	    head.commitCycle = cycleCount;
	    }
	    
	
	    // this is NOT correct, just showing you how to use the Reorder Buffer
	    //ROB.dequeue(); // this does remove the entry from the ROB
	    //nCommitted++; // increment this when you dequeue
	   	
	    
	 // print the mapping of the input registers
	    if (debug_f) {
	    	head.printCycles();
    	    //System.out.format("%d", nCommitted);
    	    head.printInputRegisters();	
    	    head.printOutputRegisters();
	    	head.theUop.printMacroMicroNames();
    	   
	    }
	    
	    // print the mapping of the output registers
	   
	    
	   // head.commitCycle++;
	    
	}
    }

    /*
     * issue instructions that have not already been issued & whose inputs are ready.
     * on an n-wide machine, you can issue n instructions per cycle
     */
    public void issue() {
	
	// This is how you can iterate through the ROB without actually removing anything from it
	// (which is what you want to do for this method)
    int count=0;
	for (Iterator<ROBentry> itr = ROB.entries.iterator(); itr.hasNext(); )  {
	    ROBentry currEntry = itr.next();
	    Uop currUop = currEntry.theUop;
	    
	    if (currEntry.hasIssued_f==false &&  isUopReady(currEntry)){
	    	currEntry.hasIssued_f=true;
	    	currEntry.issueCycle = cycleCount;
	    	currEntry.doneCycle = cycleCount + currUop.execLatency;
	    	
	    	for (int i=0; i<Uop.MAX_OUTPUTS;i++){
	    		if (!(currUop.outputRegs[i] ==-1))
	    			scoreboard[currEntry.physicalOutputRegs[i]]=currUop.execLatency;
	    	}
	    	count++;
	    	
	    	if (count == machineWidth)
	    		break;                     //stop looking for instructions to execute
	    }
	}
    }
    
    public boolean isUopReady(ROBentry currEntry){
    	//boolean flag=true;
    	Uop currUop=currEntry.theUop;
    	for (int i=0; i<Uop.MAX_INPUTS;i++){
    		if (currEntry.theUop.inputRegs[i] != -1){
    			if (scoreboard[currEntry.physicalInputRegs[i]] !=0)
    				return false;
    			   
    		}
    		
    	}
    	
    	
    	if (modelMemoryDependences_f==true){
    		if (currEntry.theUop.isLoad()){
    			//modelMemoryDependences_f=true;
    			for (Iterator<ROBentry> itr = ROB.entries.iterator(); itr.hasNext(); )  {
    				ROBentry otherEntry=itr.next();
    				Uop otherUop=otherEntry.theUop;
    				if (otherEntry != currEntry)
    					if (otherUop.isStore() && otherEntry.sequenceN<currEntry.sequenceN && !isDone(otherEntry)){
    						if (perfectMemSched_f==true){
    							if (otherUop.addressForMemoryOp == currUop.addressForMemoryOp){
    								return false;
    							} 
    						}
    						else
    							return false;
    					}
    			}
			
    		} 
    	}
    	
    	return true;
    }
    
    public boolean isDone (ROBentry entry){
    	if (entry.hasIssued_f==true && entry.doneCycle<=cycleCount)
    		return true;
    	return false;
    }
    
    /*
     * Fetches instructions, renames them, adds them to the ROB
     */
    public void fetchRename() throws IOException {
        Uop currUop = null;
        String line = "";    
        
        for (int i = 0; i < machineWidth; i++) {
	    
	    if (ROB.isFull()) {
		nROBstalls++;
		break;          //ROB is full
	    }
	    
	    line = traceReader.readLine();    //read the next micro-op from the trace
	    if (line == null) {
		doneFetching_f = true;
		return;
	    }
	    nFetched++;
	    
	    currUop = new Uop(line, NUM_ARCH_REGISTERS-1);   
	    ROBentry entry = new ROBentry(currUop, nFetched, cycleCount);
	    
	    for (int j=0; j<Uop.MAX_INPUTS;j++){
	    	if (currUop.inputRegs[j]!=-1 ){
	    		entry.physicalInputRegs[j]=MapTable[currUop.inputRegs[j]];
	    	}
	    } 
	    
	    //for each valid output register i:
	    
	    int new_p_reg=0;
	    for (int j=0; j<Uop.MAX_OUTPUTS;j++){
	    	if(currUop.outputRegs[j] !=-1 ){
	    		entry.overwrittenRegs[j] = MapTable[currUop.outputRegs[j]];
	    		new_p_reg = freeList.removeFirst();
	    		MapTable[currUop.outputRegs[j]]=new_p_reg;
	    		entry.physicalOutputRegs[j]=new_p_reg;
	    		scoreboard[new_p_reg]=-1;
	    		
	    	}
	    }
	    
	    // complete this method.
	    //nFetched=entry.fetchCycle;
	    	    
	    // here is an example of how to add an insn to the ROB
	    ROB.enqueue(entry);
	    
	  
	    //entry.fetchCycle++;
	    
	    if (verbose_f)
		System.out.format("%s\n", line);
    	}
    }
       
    /*
     * HW 6 has two parts. 
     * Here is where you decide which one to simulate.
     */
    public void processTrace() throws IOException  {
        
	initTrace();

	if (renameOnly_f)
	    simulateRenameOnly(); // PART 1
	else
	    simulateAllStages();    // PART 2

        IPC = (double)nCommitted/cycleCount; 
    }
    

    /*
     * Goes through the trace line by line (i.e., instruction by instruction) and 
     * simulates the program being executed on a processor.
     * 
     * You may modify this function, but you shouldn't have to.
     * Simply completing the methods that this method calls should suffice.
     */
    public void simulateAllStages() throws IOException  {
        
	if (debug_f)
	    printDebugHeader();
        
        while (true) {
	    
	    // notice how we simulate parallel hw pipeline stages
	    // on a serial sw simulator
	    commit();
	    
	    issue();
	    
	    fetchRename();
	    
	    cycleCount++;
	    
	    for (int i = 0; i < this.nPhysicalRegisters; i++) {
	    	if (scoreboard[i]>0)
	    		scoreboard[i]--;
		}
	    
	    
	    if (insnLimit_f && nCommitted >= uopLimit) {
		System.out.format("Reached insn limit of %d @ cycle %d. Ending Simulation...\n", uopLimit, cycleCount);
		break;
	    }
	    if (doneFetching_f && ROB.isEmpty()) {
		System.out.format("Completed Trace @ cycle %d. Ending Simulation...\n", cycleCount);
		break;
	    }
	    // this is to help you debug
	    if (cycleCount > (lastCommittedCycle + 100)) {
		System.out.format("You haven't committed an instruction in 100 cyces... Time to debug. Ending Simulation...\n");
		break;
	    }
        }
    }
    

    /*
     * To complete PART 1, complete this method.
     */
    public void simulateRenameOnly() throws IOException {    

	Uop currUop = null;
    	String line = "";
	
        if (debug_f)
	    printRenameDebugHeader();
        
        while (true) {
	    
	    line = traceReader.readLine();
	    if (line == null) {
		break;
	    }
	    
	    currUop = new Uop(line, NUM_ARCH_REGISTERS-1);
	    nFetched++;
	    nCommitted++;
	    ROBentry entry = new ROBentry(currUop, nCommitted, nCommitted); // arguments 2 & 3 don't matter for Part 1
	    
	    // perform part 1 of renaming algorithm here
	    // for each valid input register...	
	    
	    //for each valid input register i:
	    //   ROBentry.phys_reg_source[i]=MapTable[currUop.inputRegs[i]]
	    
	    for (int i=0; i<Uop.MAX_INPUTS;i++){
	    	if (currUop.inputRegs[i]!=-1 ){
	    		entry.physicalInputRegs[i]=MapTable[currUop.inputRegs[i]];
	    	}
	    } 
	    
	    //for each valid output register i:
	    
	    int new_p_reg=0;
	    for (int i=0; i<Uop.MAX_OUTPUTS;i++){
	    	if(currUop.outputRegs[i] !=-1 ){
	    		entry.overwrittenRegs[i] = MapTable[currUop.outputRegs[i]];
	    		new_p_reg = freeList.removeFirst();
	    		MapTable[currUop.outputRegs[i]]=new_p_reg;
	    		entry.physicalOutputRegs[i]=new_p_reg;
	    	}
	    }
	    
	    
	    // print the mapping of the input registers
	    if (debug_f) {
    	    	System.out.format("%d", nCommitted);
    	    	entry.printInputRegisters();	
	    }
	    
	    // perform part 2 of renaming algorithm here
	    // for each valid logical output register...
	    
	    // now "commit"
	    // free the overwritten register...
	    for (int i=0; i<Uop.MAX_OUTPUTS;i++){
	    	if(currUop.outputRegs[i]!=-1 ){
	    		freeList.addLast(entry.overwrittenRegs[i]);
	    	}
	    }
	    
	    
	    // print the mapping of the output registers
	    if (debug_f) {
		entry.printOutputRegisters();
		currUop.printMacroMicroNames();
	    }
	    
	    // prints the insn
	    if (verbose_f)
		System.out.format("%s\n", line);
	    
	    if (insnLimit_f && nCommitted >= uopLimit) {
		System.out.format("Reached insn limit of %d @ cycle %d. Ending Simulation...\n", uopLimit, cycleCount);
		break;
	    }
	    cycleCount++;
	    for (int i = 0; i < this.nPhysicalRegisters; i++) {
	    	if (scoreboard[i]>0)
	    		scoreboard[i]--;
		}
        }
    }
    
    // -------PRINT METHODS BEGIN HERE --------
   
    /*
     * Prints out basic stats + the hit/miss rates of the cache.
     */
    public void printTraceStats() {
	
    	System.out.format("Processed %d trace lines.\n", nCommitted);
    	System.out.format("Instructions:\n");
    	System.out.format("  N Fetched =    \t\t%d\n", nFetched);
    	System.out.format("  N Committed =  \t\t%d\n", nCommitted);
    	System.out.format("Stalls:\n");
    	System.out.format("  Fetch stalled (ROB full) =    %d\n", nROBstalls);
    	System.out.format("IPC: \t\t\t\t%.3f\n", IPC);	
	
    }
    
    /*
     * for Debug mode, you'll want to print headers to know what each column means
     */
    public void printRenameDebugHeader() {
	System.out.format("uop# \t Reg Mappings [freeMe] | Mop Uop\n");
    }
    
    /*
     * for Debug mode, you'll want to print headers to know what each column means
     */
    public void printDebugHeader() {
    	System.out.format("#: F I D C \t Reg Mappings [freeMe] | Mop Uop\n");
    }
    
    public void printHeader() {
	if (!debug_f) {
	    System.out.format("HW 6 Printout for xinyu.yan\n");
	    System.out.format("-----------------------------------------------------\n");
	}
    	System.out.format("Trace name  \t\t\t\t%s\n", testTrace);
	System.out.format("Instruction Limit\t\t\t%s\n", insnLimit_f ? Integer.toString(uopLimit) : "none");	
	System.out.format("Processor Configuration:\n");	
	System.out.format("  Machine Width \t\t\t%d\n", machineWidth);	
	System.out.format("  Number of Logical Registers \t\t%d\n", NUM_ARCH_REGISTERS);	
	System.out.format("  Number of Physical Registers \t\t%d\n", nPhysicalRegisters);	
	System.out.format("  ROB Size \t\t\t\t%d\n", ROB.maxEntries);	
	if (!modelMemoryDependences_f)
	    System.out.format("  NOT modeling memory dependences\n");
	else {
	    if (perfectMemSched_f)
		System.out.format("  modeling memory dependences: Perfect Memory Scheduling\n");
	    else
		System.out.format("  modeling memory dependences: Conservative Memory Scheduling\n");
	}
    }
    
}
