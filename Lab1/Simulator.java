import java.lang.String;

import java.util.zip.GZIPInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


/*
 * This is one of two files you will modify to complete Homework #1a.
 * The places you need to change, complete, or correct things are clearly labeled.
 * You are free to make changes as needed to THIS FILE AND Uop.java ONLY
 */

public class Simulator
{    
    private static final Boolean True = null;

	private static final Boolean False = null;

	public String testTrace;
    
    // don't touch! you'll be sorry....
    public BufferedReader traceReader = null;
    
    // Program Stat Variables Go Here -- you'll need more than these two
    public long totalUops = 0;   //Uop is the whole micro-ops
    public long totalMops = 0;   //Mop is the macro-ops in the trace,the repeated PC micro-ops are seen as 1
    public long sum=0,sum1=0,sum2=0,sum3=0,sum4=0;
	public int count1=0,count2=0,count3=0,count4=0;
    
    //public long avg_micops = totalUops/totalMops;
    public double avg_micops = 0.0;
    public double avg_ins=0.0;
    public double num_bits=0.0;
    public double num_pairs=0.0;
    public double eligible_pairs=0.0;
    public long jadge1=1,jadge2;
    
    // THIS is why your homework is in Java and not C. You're welcome.

    // Question 1B
    //define UopsPerMopHistogram, FrequencyHistogram(String title, String sizeTitle, String countUnit)
    public FrequencyHistogram UopsPerMopHistogram = new FrequencyHistogram("Micro Ops Per Macrop Op", "mops consisting of", "uops");
    
    // Question 2B
    public FrequencyHistogram BytesPerMopHistogram = new FrequencyHistogram("Bytes Per Macrop Op", "mops of length", "bytes"); 
    
    // Question 3A
    public FrequencyHistogram BitsPerTargetHistogram = new FrequencyHistogram("Bits Per Target", "targets requiring", "bits"); 
    
    // Question 4A
    public FrequencyHistogram InsnTypeFrequencyHistogram = new FrequencyHistogram("Frequency of Each Uop Type", "uops of type", ""); 
 
    
    /*
     * Constructor for the simulator.
     * @param traceName the name of the trace file you wish to simulate. in .gz format
     */
    public Simulator(String traceName) throws IOException {
	
	testTrace = traceName;
	String tracePath = "../traces/"+testTrace;
	// ugly, but JAVA IO is not the point
	traceReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(tracePath))));
    }
    
    
    /*
     * Goes through the trace line by line (i.e., instruction by instruction), examining
     * each instruction in the trace. 1 line = 1 micro-op (micro-op = uop)
     */
    
    public void processTrace(boolean verbose_f) throws IOException {
        String line;
        //long jadge1=1,jadge2;
        boolean decision=true;
        long sub_add;
        long sub_judge;
        
        while (true) {
            line = traceReader.readLine();
            //System.out.println(line);
            if (line == null) {
            	
            	/*if (jadge1==1)
            		UopsPerMopHistogram.increment(1);
            	else if(jadge1!=1)
            		UopsPerMopHistogram.increment(2); */
                break;
            }
            
            Uop currUop = new Uop(line);
            
            //jadge1=1;
            jadge2=currUop.microOpCount;
            
            /*if (jadge1==jadge2)
            	decision=true;            	
            else{
            	if (jadge1==1 && jadge2==2)
            		decision=false;
            	//else if(jadge2 !=1)
            		//decision=false;
            }   */
            //jadge1=jadge2;
            
            
            // if you want to see what's going on (per loop iteration), call this fn with verbose_f = true 
            // you'll see each instruction printed to the screen. NOT recommended for long traces!
            if (verbose_f)
            	System.out.format("%s\n", line);
	    
            // each line of the trace is a micro-op, so increment each time
            totalUops++;
            
            /***************6A********************/
            if(currUop.targetPC !=0 && currUop.conditionRegister.equals("R"))
            	num_pairs++;
            /*************************************/

            // the first micro-op in the macro-op indicates a new macro-op has begun
            // this is identified by microOpCount == 1
            if (currUop.microOpCount == 1) {
            	totalMops++;
            	
            	/********** 1b****************/
            	//System.out.println(currUop.PC);
            	//int sum=0,sum1=0,sum2=0,sum3=0,sum4=0;
            	//int count1=0,count2=0,count3=0,count4=0;
            	/*if(jadge1!=1)
            		UopsPerMopHistogram.increment(2);
            	else
            		UopsPerMopHistogram.increment(1); */
            	           	
            	
            	sub_add=currUop.fallthroughPC-currUop.PC;
            	if(sub_add==2){
            		count1++;
            		sum1=2*count1;
            	} 
            	else if(sub_add==3){
            		count2++;
            		sum2=3*count2;
            	}
            	else if(sub_add==4){
            		count3++;
            		sum3=4*count3;
            	}
            	else if(sub_add==8){
            		count4++;
            		sum4=8*count4;
            	}
            	/*for (int i=0;i<totalMops;i++){
            		sum +=sub_add[i];
            	}  */
            		
            	sum=sum1+sum2+sum3+sum4;
            	//System.out.println(sum1);
            	
            }
                
            //System.out.println(currUop.microOpCount);
            //System.out.println(totalMops);
            
            
            // There are several histograms that you need to populate for this homework assignment.
            // TO DO: call increment at the right time with the right argument.
            // This is the only call you need to get the data you're looking for. 
            // These lines are provided to show you the *syntax* of the calls. 
            // They are not semantically correct -- you should not call each of these lines for 
            // every uop (as it currently does) and you should not only increment the "size 1" bucket each time.
            
            // Question 1B
            //System.out.println(currUop.microOpCount);
            /*if (decision==true)
           		UopsPerMopHistogram.increment(1);            	
            else if (decision==false)
            	UopsPerMopHistogram.increment(2);   */
            if (currUop.microOpCount == 1){
            	if(jadge1!=1)
            		UopsPerMopHistogram.increment(2);
            	else
            		UopsPerMopHistogram.increment(1);
            }
            
            
            jadge1=jadge2;
            //System.out.println(jadge1);
          
            
            // Question 2B
            if (currUop.microOpCount == 1){
            	sub_judge=currUop.fallthroughPC-currUop.PC;
            	if (sub_judge==2)
            		BytesPerMopHistogram.increment(2);
            	else if(sub_judge==3)
            		BytesPerMopHistogram.increment(3);
            	else if(sub_judge==4)
            		BytesPerMopHistogram.increment(4);
            	else if(sub_judge==8)
            		BytesPerMopHistogram.increment(8);}
            
            // Question 3A
            if (currUop.TNnotBranch !="-")
            	num_bits=2+Math.floor(HW1aHelper.log2(Math.abs(currUop.PC- currUop.targetPC)));
            else if(currUop.TNnotBranch == "-")
            	num_bits=0.0;
            //System.out.println(num_bits);
            if (num_bits==4)	
            	BitsPerTargetHistogram.increment(4);
            else if(num_bits==5)
            	BitsPerTargetHistogram.increment(5);
            else if(num_bits==7)
            	BitsPerTargetHistogram.increment(7);
            else if(num_bits==8)
            	BitsPerTargetHistogram.increment(8);

            // Question 4A This line IS correct and does not need to me moved or modified, 
            // BUT the currUop.type is NOT correctly set. Fix this in your Uop constructor inside Uop.java
            InsnTypeFrequencyHistogram.increment(currUop.type.value);
        }
        
     }
     
    public void printHeader() {
	
	HW1aHelper.printHeader("xinyu.yan", testTrace);
    }
    
    public void printTraceStats() {
	
    	System.out.format("Processed %d trace lines.\n", totalUops);
    	System.out.format("Num Uops (micro-ops): %d\n", totalUops);
    	System.out.format("Num Mops (macro-ops): %d\n", totalMops);
    	//System.out.println(totalMops);
    	avg_micops = (double)totalUops/(double)totalMops;
    	avg_ins=(double)sum/(double)totalMops;
    	eligible_pairs=num_pairs/(double)totalUops;
   

    }
    
    public void printHW1aQuestions() {
	
    	HW1aHelper.print1A(avg_micops);
    	HW1aHelper.print1B(UopsPerMopHistogram);
    	HW1aHelper.print2A(avg_ins);    	
    	HW1aHelper.print2B(BytesPerMopHistogram); 
    	HW1aHelper.print3A(BitsPerTargetHistogram);  
    	HW1aHelper.print4A(InsnTypeFrequencyHistogram); 
    	HW1aHelper.print6A(eligible_pairs);

    }
    
     
}
