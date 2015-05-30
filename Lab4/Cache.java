
public class Cache {

    public int capacity; // in Bytes
    public int blockSize; // in Bytes
    public int associativity; // 1 for 1-way set associative, etc.
    
    // Modify the constructor to properly initialize these variables
    
    public int nSets; 
    public int nTotalCacheLines; 
    public int nOffsetBits;
    public int nIndexBits;
    public int nTagBits;
        
    // don't touch!
    public final int addressSize = 32; // in bits
    
    // tags are big numbers, store them as longs
    // 2D Arrays: first dimension = which set, second dimension = which way
    // (at first, begin with a direct mapped cache; use [0] for the second dimension)
    public long[][] cacheTags = null; 
    public boolean[][] dirtyBits = null;
    // only 1 dimension b/c LRU field is for the entire set
    // ignore this until you begin support for the n-way set associative cache
    public int[] LRU_way= null;
    
    // whether the most recent cache access resulted in a dirity eviction
    public boolean dirtyEvic_f = false;
    
    public Cache(int capacity, int blockSize, int associativity) {

	this.capacity = capacity; // in B
	this.blockSize = blockSize; // in B
	this.associativity = associativity; // 1, 2, 3... etc.
	
	// FIX THIS CODE!
	// first, set the 5 "n" variables. THESE ARE ALL WRONG
	
	nSets =this.capacity/(this.blockSize*this.associativity); 
	nTotalCacheLines = this.capacity/this.blockSize; 
	nOffsetBits = log2(this.blockSize);
	nIndexBits = log2(this.capacity/(this.blockSize*this.associativity));
	nTagBits = 32-log2(this.capacity/(this.associativity*this.blockSize))-log2(this.blockSize);
	
	// next create the cache tags
	// Note; this is also incorrect (it shouldn't be a 1 x 1 cache)
	cacheTags = new long[this.capacity][this.associativity];
	dirtyBits = new boolean[this.capacity][this.associativity];
	LRU_way = new int[this.capacity];
	
	// initializes cache tags to 0, dirty bits to false, and LRU bits to 0
	// this code is correct EXCEPT the for-loops should not stop at [1][1]
	for (int i = 0; i < 1; i++) {
	    for (int j = 0; j < 1; j++) {
		cacheTags[i][j] = 0;
		dirtyBits[i][j] = false;
	    }
	    LRU_way[i] = 0;
	}
    }
	
    public long getTag(long addr) {
    	long mask =addr >> (nIndexBits+nOffsetBits);
	    addr=mask;
    	
	return addr;
    }
    
    public int getIndex(long addr) {
    	long mask =addr >>> (nOffsetBits);
    	mask = mask << (64-nIndexBits);
    	mask = mask >>> (64-nIndexBits);
    	addr=mask;
  
	
	return (int)addr;
    }
    
    public long getBlockAddress(long addr) {
    	long mask =addr >> (nOffsetBits);
    	mask = mask << (nOffsetBits);
    	addr=mask;
	
	return addr;
    }
    
    /* this method takes a PC and a flag as to whether the access is a load or store
     * functionality in no particular order: 
     * 	(1) look up the address in the cache
     * 	(2) update the cacheTags if necessary
     * 	(3) set the dirtyEvic_f flag accordingly
     * 	(4) update the LRU_way field accordingly
     * return true if there was a hit, false if there was a miss
     * Use the "get" helper functions above. They will make your life easier.
     */
    public boolean access(long PC, boolean isLoad) {
	// FIX ME for question 8
    	//Uop currUop = null;
    	for (int i=0; i<this.associativity; i++){
    		if (cacheTags[getIndex(PC)][i]==getTag(PC)){
    			updateLRU(getIndex(PC),i);
    			
    			if (isLoad==false){
    				dirtyBits[getIndex(PC)][i]=true;
    			}  
    			return true;
    		}
    	}	
    
    	dirtyEvic_f = dirtyBits[getIndex(PC)][LRU_way[getIndex(PC)]];
    	if (isLoad==false)
    		dirtyBits[getIndex(PC)][LRU_way[getIndex(PC)]]=true;
    	else if (isLoad==true)
    		dirtyBits[getIndex(PC)][LRU_way[getIndex(PC)]]=false;
		cacheTags[getIndex(PC)][LRU_way[getIndex(PC)]]=getTag(PC);
		updateLRU(getIndex(PC),LRU_way[getIndex(PC)]);
		return false;
		

    //return false; // cache hit should return true
    }
    
    /*
     * LRU cannot be maintained with a single counter if there are
     * more than 2 ways. So we'll just use an approximation: 
     * 	If there is just one way, the LRU bit is always 0. 
     * 	If there are two ways, the LRU bit is always the way you DIDN'T just touch. 
     *  If there are more than 2 ways, the LRU bit is always 1 higher (w/wrap-around)
     *  	than the way you just touched.
     * For example, if there are 4 ways, and you touch way 0, then the new LRU should be 1.
     * 		If you touch way 3, the new LRU should be 0.
     * theSet: identifies the set in the cache we're talking about
     * touchedWay: identifies the way we just touched.
     */
    public void updateLRU(int theSet, int touchedWay) {
	// LRU remains 0
	if (associativity == 1)
	    return;
	
	if (touchedWay < (associativity-1))
	    LRU_way[theSet] = touchedWay + 1;
	else 
	    LRU_way[theSet] = 0;
    }
    
    public void printConfig(){
	System.out.format("Cache size  = %dB. Each block = %dB.\n"+
			  "%d-way set associative cache.\n", 
			  capacity, blockSize, associativity);
	System.out.format("Tag = %d bits, Index = %d bits, Offset = %d bits\n",
			  nTagBits, nIndexBits, nOffsetBits);
	System.out.format("There are %d sets total in the cache.\n"+
			  "At this associativity, that means a total of %d cache lines.\n"
			  +"(Assuming a %d-bit address.)\n",
			  nSets, nTotalCacheLines, addressSize);
    }

    public void printHeader() {
    	System.out.format("[SetNum: {WayNum: Tag,cl/dirty} LRU=WayNum]\t|"+
			  " Block-Addr\tType\tH/M\tEvicState\n");
    }
    
    public void printCache() {
	// this is NOT correct, but the formatting should help you
	System.out.format("[S0: {W0: %s, C} LRU=0]\t| ", Long.toHexString(cacheTags[0][0]));		
    }
    
    /* 
     * You may find this useful.
     */
    static public int log2(double x) {
	
    	return (int)(Math.log(x)/Math.log(2)+1e-10);
    	
    }

}
