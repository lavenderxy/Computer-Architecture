/*
 * WARNING: When we grade your assignment, we will not use this file, so
 * any changes you make to this file will be discarded.
 */
public class HW1aHelper {

    static public void printHeader(String wustlkey, String testTrace) {
    	System.out.format("HW 1 Printout for %s\n", wustlkey);
    	System.out.format("-------------------------------\n");
    	System.out.format("Running on trace %s.\n\n", testTrace);
	
    }
    
    static public void print1A(double avgUopPerMop) {
    	System.out.format("\n(1A): average number of micro-ops per macro-op");
    	System.out.format("\t Your answer: %.2f\n", avgUopPerMop);
    }
    
    static public void print1B(FrequencyHistogram UopsPerMopHistogram) {
    	System.out.format("\n(1B): distribution of micro-ops per macro-op\tYour Answer:\n");
    	UopsPerMopHistogram.print();
    }
    
    static public void print2A(double avgBytesPerMop) {
    	System.out.format("\n(2A): the average number of bytes per macro-op");
    	System.out.format("\t Your answer: %.2f\n", avgBytesPerMop);
    }
    
    static public void print2B(FrequencyHistogram BytesPerMopHistogram) {
    	System.out.format("\n(2B): distribution of bytes per macro-op\tYour Answer:\n");
    	BytesPerMopHistogram.print();
    }	
    
    static public void print3A(FrequencyHistogram BitsPerTargetHistogram) {
    	System.out.format("\n(3A): distribution of bytes needed to encode target.\tYour Answer:\n");
    	BitsPerTargetHistogram.print();
    }	
    
    static public void print4A(FrequencyHistogram InsnTypeFrequencyHistogram) {
    	System.out.format("\n(4A): distribution of the following instruction types:\n");
    	Uop.UopType.printAll();
    	System.out.format("Your Answer:\n");
    	InsnTypeFrequencyHistogram.print();
    }	
    
    static public void print6A(double comparebranchPairFrequency) {
    	System.out.format("\n(6A): the fraction of fusion-elligible pairs per micro-op.");
    	System.out.format("\t Your answer: %.2f\n", comparebranchPairFrequency);
    }	
    
    /* 
     * You may find this function useful for Question 3.
     */
    static public int log2(double x) {
	
    	return (int)(Math.log(x)/Math.log(2)+1e-10);
    	
    }
}		
