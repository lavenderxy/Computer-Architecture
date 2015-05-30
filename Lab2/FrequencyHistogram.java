/*
 * WARNING: When we grade your assignment, we will not use this file, so
 * any changes you make to this file will be discarded.
 */

import java.util.HashMap;
import java.lang.Long;

/* For Homework #1a you will be asked to track the frequency of certain events.
 * For 1B, you are recording how often you see macro-ops of size N micro-ops.
 * For 2B, you are recording how often you see macro-ops of size N bytes.
 *   etc.
 * 
 * This class will help you track this information. Each time you encounter
 * an occurrence of event n, simply call increment(n).
 * n might be the number of micro-ops per macro op.
 * It might be the number of bytes per macro op.
 * It might be the enumerated type of the instruction type.
 * You get the point.
 * This structure will record everything for you.
 * This file already works fine -- Do not modify this file!
 */
public class FrequencyHistogram extends HashMap<Long, Long>{
	
	// this makes Eclipse happy. don't ask why
	private static final long serialVersionUID = 1L;

	double totalCounts;
	double totalSize;
	String title = "";
	String sizeTitle = "";
	String countUnit = "";
	
	public FrequencyHistogram(String title, String sizeTitle, String countUnit) {
	    super();
	    totalCounts = 0.0;
	    totalSize = 0.0;
	    this.title = title;
	    this.sizeTitle = sizeTitle;
	    this.countUnit = countUnit;
	  }

    public void increment(int size) {
    	increment((long)size);
    }	
	
    public void increment(Long size) {
    	totalCounts++;
    	totalSize += size;
    	Long count = get(size);
    	if (count == null) {
        	// first time, counter = 1
        	put(size, (long)1);
        } else {
        	// increment counter
        	put(size, count+1);
        }
    }

    public double getOnePercent(Long key) {
        	return 100*get(key)/totalCounts;
    }    

    public void printOnePercent(Long key) {
    	double percent = getOnePercent(key);
    	System.out.format("  Percent of %s %2d %s: %4.1f%%\n", sizeTitle, key, countUnit, percent);
}     
    
     public void print() {
    	System.out.format("Distribution of %s in Trace:\n", title);
    	for (Long key: keySet()) {
        	printOnePercent(key);
        }
    }

     public double getAverageSize() {
    	 
	 long tally = 0;
         // go through each key in the set
         for (Long key: keySet()) {
	     long count = get(key);
	     tally += key * count;    	         	 
         }
         return (double)tally/totalCounts;
     }

	
}
