/*
 * WARNING: When we grade your assignment, we will not use this file, so
 * any changes you make to this file will be discarded.
 */

import java.io.IOException;

public class HW5
{ 

    public static void printUsage() {
	
	System.out.println("Usage: java HW5 [-hv] [-bpred -npfetch -cache] -t <tracename> -l <insnLimit>");
	System.out.println("Options:");
	System.out.println("  -h|help                 Print this help message");
	System.out.println("  -v|verbose              Optional verbose flag");
	System.out.println("  -psb                    Print the scoreboard");
	System.out.println("  -bpred                  Turn on branch prediction for HW 5.2");
	System.out.println("  -npfetch                Turn on non-perfect fetch for HW 5.3");
	System.out.println("  -cache	              Turn on cache modelling for HW 5.4");
	System.out.println("  -width <n>              superscalar width of machine");
	System.out.println("  -t|trace <tracename>    Name of trace (exclude trace.gz) ");
	System.out.println("  -l|limit <n>            Simulate only first n insns ");
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

	    // -psb
            if (arg.equals("-psb")) {
                sim.printScoreboard_f = true;
            }

		// -bpred for hw 5 part 2
            if (arg.equals("-bpred")) {
				sim.bpred_f = true;
            }
	    
		// -npfetch for hw 5 part 3
            if (arg.equals("-npfetch")) {
				sim.bpred_f = true;
				sim.npfetch_f = true;
            }

		// -cache for hw5 part 4
            if (arg.equals("-cache")) {
				sim.bpred_f = true;
				sim.npfetch_f = true;
				sim.cache_f = true;
            }

		// -width n
            if (arg.equals("-width")) {
		sim.machineWidth = Integer.parseInt(args[i++]);
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
	    sim.printHeader();
	    sim.processTrace();
	    sim.printTraceStats();
	}
    }
    
}
