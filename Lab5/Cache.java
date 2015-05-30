
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

	// Cache variables	
	nSets = capacity/blockSize; 
	nTotalCacheLines = nSets/associativity; 
	nOffsetBits = log2(blockSize);
	nIndexBits = log2(nTotalCacheLines);
	nTagBits = addressSize - nIndexBits - nOffsetBits;
	
	// next create the cache tags
	cacheTags = new long[nTotalCacheLines][associativity];
	dirtyBits = new boolean[nTotalCacheLines][associativity];
	LRU_way = new int[nTotalCacheLines];
	
	// initializes cache tags to 0, dirty bits to false, and LRU bits to 0
	for (int i = 0; i < nTotalCacheLines; i++) {
	    for (int j = 0; j < associativity; j++) {
		cacheTags[i][j] = 0;
		dirtyBits[i][j] = false;
	    }
	    LRU_way[i] = 0;
	}
    }
	
    public long getTag(long addr) {
		long tag = addr >> (nIndexBits + nOffsetBits);
		return tag;	
    }
    
    public int getIndex(long addr) {
		long index = addr >> nOffsetBits;
		long mask = ~0;
		mask = mask << nIndexBits;
		mask = ~mask;
		return (int)(index & mask);
    }
    
    public long getBlockAddress(long addr) {
		addr = addr >> nOffsetBits;
		addr = addr << nOffsetBits;	
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
		int index = getIndex(PC);
		long tag = getTag(PC);
		boolean hit_f = false;
		dirtyEvic_f = false;
		for(int way = 0; way < associativity; way++){
			if(cacheTags[index][way] == tag){
				updateLRU(index, way);
				hit_f = true;
				if(!isLoad){
					dirtyBits[index][way] = true;
				}
			}
		}
		if(!hit_f){	
			int touchedWay = LRU_way[index];
			cacheTags[index][touchedWay] = tag;
			updateLRU(index, touchedWay);
			dirtyEvic_f = dirtyBits[index][touchedWay];
			if(isLoad) {  
				dirtyBits[index][touchedWay] = false;
			}
			else {
				dirtyBits[index][touchedWay] = true;
			}
		}
		return hit_f;
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
		for(int set = 0; set < nTotalCacheLines; set++){
			System.out.format("[S%d: ", set);
			for(int way = 0; way < associativity; way++){
				System.out.format("{W%d: %s, C} ", way, Long.toHexString(cacheTags[set][way]));
			}
			System.out.format("LRU=%d] ", LRU_way[set]);
		}
		System.out.format("\t| ");		
	}
    
    /* 
     * You may find this useful.
     */
    static public int log2(double x) {
	
    	return (int)(Math.log(x)/Math.log(2)+1e-10);
    	
    }

}
