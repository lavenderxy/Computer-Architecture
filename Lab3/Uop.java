/* 
 * Do not modify this file.
 */

public class Uop {
    
    public enum UopType {
	insn_LOAD(0), insn_STORE(1), insn_UBRANCH(2), insn_CBRANCH(3), insn_OTHER(4);
	
	public final int value;
	
	private UopType(int value) {
	    this.value = value;
	}
	static public void printAll() {
	    for (UopType type : UopType.values()) {
		System.out.format("Type %d: %s\n", type.value, type.toString());
	    }
	}
    }
    
    // See the documentation to understand what these variables mean.
    public long microOpCount;
    public long PC;
    public int sourceRegister1;
    public int sourceRegister2;
    public int destinationRegister;
    public String conditionRegister;
    public String TNnotBranch;
    public String loadStore;
    public long immediate;
    public long addressForMemoryOp;
    public long fallthroughPC;
    public long targetPC;
    public String macroOperation;
    public String microOperation;
	public UopType type;
		
    public Uop(String line) {

        String [] tokens = line.split("\\s+");
        
        microOpCount = Long.parseLong(tokens[0]);
        PC = Long.parseLong(tokens[1], 16);
        sourceRegister1 = Integer.parseInt(tokens[2]);
        sourceRegister2 = Integer.parseInt(tokens[3]);
        destinationRegister = Integer.parseInt(tokens[4]);
        conditionRegister = tokens[5]; // where the flags go
        TNnotBranch = tokens[6];
        loadStore = tokens[7];
        immediate = Long.parseLong(tokens[8]);
        addressForMemoryOp = Long.parseLong(tokens[9], 16);
        fallthroughPC = Long.parseLong(tokens[10], 16);
        targetPC = Long.parseLong(tokens[11], 16);
        macroOperation = tokens[12];
        microOperation = tokens[13];
    	
    	type = UopType.insn_OTHER;
    	
    	if (loadStore.equals("L")) {
    		type = UopType.insn_LOAD;
    	}
    	else if (loadStore.equals("S"))
    		type = UopType.insn_STORE;
    	else if (isBranch()) {
    		if (conditionRegister.equals("-"))
    			type = UopType.insn_UBRANCH;
    		if (conditionRegister.equals("R"))
    			type = UopType.insn_CBRANCH;
    	}
    }
    
    /*
     * Helper functions!
     */
    public boolean isBranch() {	
    	return (targetPC != 0);    	
    }
    public boolean isLoad() {	
    	return (loadStore.equals("L"));    	
    }
    public boolean isStore() {	
    	return (loadStore.equals("S"));    	
    }
    
}
