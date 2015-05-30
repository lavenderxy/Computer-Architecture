/*
 * WARNING: When we grade your assignment, we will not use this file, so
 * any changes you make to this file will be discarded.
 */

import java.io.IOException;

public class HW3
{ 

    static boolean defaultBPred_f = true;

    public static void printUsage() {
	
	System.out.println("Usage: java HW3 [-hv] -t <tracename> -l <insnLimit>");
	System.out.println("Options:");
	System.out.println("  -h|help                 Print this help message");
	System.out.println("  -v|verbose              Optional verbose flag");
	System.out.println("  -debug                  Optional debug prints BPred contents");
	System.out.println("  -bpred <type> <subtype> What kind of branch predictor to use");
	System.out.println("      -bpred static taken | not-taken");
	System.out.println("      -bpred bimodal A     2^A entries");
	System.out.println("      -bpred gshare C D    2^C entries, D bits of history");
	System.out.println("      -bpred tour A C D F  2^F chooser entries");
	System.out.println("  -t|trace <tracename>    Name of trace (exclude trace.gz) ");
	System.out.println("  -l|limit <n>            Simulate only first n insns ");
	System.out.println("\nExamples:");
	System.out.println("  shell>  java HW3 -v -t sjeng-1K -l 500");
	System.out.println("  shell>  java HW3 -verbose -trace sjeng-1M ");
	System.out.println("  shell>  java HW3 -bpred static taken -debug");
	System.out.println("  shell>  java HW3 -bpred bimodal 3 -debug");
	System.out.println("  shell>  java HW3 -bpred gshare 4 3 -debug");
	System.out.println("  shell>  java HW3 -bpred tour 3 4 4 3 -debug");

    }

    public static boolean parseArgs(String [] args, Simulator sim) throws IOException {

	int i = 0;
	String arg;
		
	// use the command line arguments to customize the simulator each run
	while (i < args.length) {
	    
            arg = args[i++];
	    
	    // -help
            if (arg.equals("-help") || arg.equals("-h")) {
                printUsage();
		return false;
            }
	    
	    // -verbose
            if (arg.equals("-verbose") || arg.equals("-v")) {
                System.out.println("verbose mode on");
                sim.verbose_f = true;
            }

	    // -debug
            if (arg.equals("-debug")) {
                System.out.println("debug mode on");
                sim.debug_f = true;
            }

	    // -bpred static taken
            if (arg.equals("-bpred")) {
		sim.bpredType = args[i++];

		if (sim.bpredType.equals("static")) {
		    sim.bpredSubType = args[i++];
		    sim.bpred = new StaticPredictor(sim.bpredSubType.equals("taken")); // default = not-taken
		} 
		
		if (sim.bpredType.equals("bimodal")) {
		    int tableSizeInBits = Integer.parseInt(args[i++]);
		    sim.bpredSubType = "2^"+tableSizeInBits+"entries, each has 2-bit sat ctrs";
		    sim.bpred = new BimodalPredictor(tableSizeInBits);
		}

		if (sim.bpredType.equals("gshare")) {
		    int tableSizeInBits = Integer.parseInt(args[i++]);
		    int historyLengthInBits = Integer.parseInt(args[i++]);
		    sim.bpredSubType = "2^"+tableSizeInBits+"entries, each has 2-bit sat ctrs, Plus BHR of length "+historyLengthInBits+" bits";
		    sim.bpred = new GSharePredictor(tableSizeInBits, historyLengthInBits);
		}

		if (sim.bpredType.equals("tour")) {
		    int bm_tableSizeInBits = Integer.parseInt(args[i++]);
		    int gs_tableSizeInBits = Integer.parseInt(args[i++]);
		    int gs_historyLengthInBits = Integer.parseInt(args[i++]);
		    int chooserTableSizeInBits = Integer.parseInt(args[i++]);
		    sim.bpredSubType = "Bimodal: 2^"+bm_tableSizeInBits+" entries, each has 2-bit sat ctrs\n"+
			"GSHARE: 2^"+gs_tableSizeInBits+" entries, each has 2-bit sat ctrs, Plus BHR of length "+gs_historyLengthInBits+" bits\n"+
			"Chooser Table: 2^"+chooserTableSizeInBits+" entries, each has 2-bit sat ctrs";
		    sim.bpred = new TournamentPredictor(bm_tableSizeInBits, 
							gs_tableSizeInBits, gs_historyLengthInBits,
							chooserTableSizeInBits);
		}

		sim.bpredStats = new PredictorStats(sim.bpredType, sim.bpredSubType);
		defaultBPred_f = false;
            }

	    // -t sjeng-1K
	    if (arg.equals("-trace") || arg.equals("-t")) {
		sim.testTrace = args[i++];
	    }

	    // -limit 100
	    if (arg.equals("-limit") || arg.equals("-l")) {
		sim.insnLimit_f = true;
		sim.uopLimit = Integer.parseInt(args[i++]);
	    }

	}
	return true;

    }
    
    public static void main(String [] args) throws IOException {
	
    	Simulator sim = new Simulator();
	
	if (parseArgs(args, sim)) {
	    
	    // if the command line doesn't specify a branch predictor,
	    // we go with the default
	    if (defaultBPred_f) {
		sim.bpred = new StaticPredictor(false); // default = not-taken
		sim.bpredStats = new PredictorStats(sim.bpredType, sim.bpredSubType);
	    }
	    
	    sim.printHeader();
	    sim.processTrace(); // this is still where the action takes place
	    sim.printTraceStats();
	}
    }
    
}
