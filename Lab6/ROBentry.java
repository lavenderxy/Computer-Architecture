/* 
 * Do not modify this file.
 */


public class ROBentry {
    
    public Uop theUop;
    public long sequenceN; // age field
    public boolean hasIssued_f = false;
    
    public int physicalInputRegs[] = new int[Uop.MAX_INPUTS]; // physical register names
    public int physicalOutputRegs[] = new int[Uop.MAX_OUTPUTS]; // physical register names
    
    public int overwrittenRegs[] = new int[Uop.MAX_OUTPUTS]; // when this ROB entry commits, free this preg!
    
    public long fetchCycle = -1;
    public long issueCycle = -1;
    public long doneCycle = -1;
    public long commitCycle = -1;
    
    public ROBentry(Uop theUop, long sequenceN, long fetchCycle) {
	this.theUop = theUop; 
	this.sequenceN = sequenceN; 
	this.hasIssued_f = false;	
	this.fetchCycle = fetchCycle;
	
    }
    
    // Print methods!
    
    public void printCycles() {
	System.out.format("%d: %d %d %d %d", sequenceN, fetchCycle, issueCycle, doneCycle, commitCycle);
    }
    
    /*
     * prints the input mappings of the instruction
     */
    public void printInputRegisters() {
    	for (int i = 0; i < Uop.MAX_INPUTS; i++) {
	    if (theUop.inputRegs[i] != -1) {
		System.out.format(", r%d -> p%d", theUop.inputRegs[i], physicalInputRegs[i]);
	    }		
    	}			
    }
    
    /*
     * prints the output mappings of the instruction
     */
    public void printOutputRegisters() {
	for (int i = 0; i < Uop.MAX_OUTPUTS; i++) {
	    if (theUop.outputRegs[i] != -1) {
		System.out.format(", r%d -> p%d [p%d]", theUop.outputRegs[i], physicalOutputRegs[i], overwrittenRegs[i]);
	    }		
	}	
    }    
    
}
