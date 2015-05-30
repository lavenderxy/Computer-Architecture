/*
 * WARNING: When we grade your assignment, we will not use this file, so
 * any changes you make to this file will be discarded.
 */

import java.io.IOException;

public class HW2a
{ 

    public static void printUsage() {
	
	System.out.println("Usage: java HW2a [-hv] -t <tracename> -l <insnLimit>");
	System.out.println("Options:");
	System.out.println("  -h|help               Print this help message");
	System.out.println("  -v|verbose            Optional verbose flag");
	System.out.println("  -pimix                Print the instruction mix of the trace");
	System.out.println("  -psb                  Print the scoreboard");
	System.out.println("  -t|trace <tracename>  Name of trace (exclude trace.gz) ");
	System.out.println("  -l|limit <n>          Simulate only first n insns ");
	System.out.println("  -loadlat <n>          Load execution latency = n cycles (default = 2)");
	System.out.println("  -relaxRWO             Do NOT enforce that register writes must be in program order");
	System.out.println("\nExamples:");
	System.out.println("  shell>  java HW2a -v -t mcf-1K -l 500");
	System.out.println("  shell>  java HW2a -verbose -trace mcf-1M -relaxRWO");
    }

    public static void parseArgs(String [] args, Simulator sim) throws IOException {

	int i = 0;
	String arg;
		
	// use the command line arguments to customize the simulator each run
	while (i < args.length) {
	    
            arg = args[i++];
	    
	    // -help
            if (arg.equals("-help") || arg.equals("-h")) {
                printUsage();
		return;
            }
	    
	    // -verbose
            if (arg.equals("-verbose") || arg.equals("-v")) {
                System.out.println("verbose mode on");
                sim.verbose_f = true;
            }

	    // -pimix
            if (arg.equals("-pimix")) {
                sim.printInsnMix_f = true;
            }

	    // -psb
            if (arg.equals("-psb")) {
                sim.printScoreboard_f = true;
            }

	    // -t mcf-1K
	    if (arg.equals("-trace") || arg.equals("-t")) {
		sim.testTrace = args[i++];
	    }

	    // -limit 100
	    if (arg.equals("-limit") || arg.equals("-l")) {
		sim.insnLimit_f = true;
		sim.uopLimit = Integer.parseInt(args[i++]);
	    }

	    // -loadlat 3
	    if (arg.equals("-loadlat")) {
		Uop.LOAD_lat = Integer.parseInt(args[i++]);
	    }

	    // -relaxRWO 
	    if (arg.equals("-relaxRWO")) {
		sim.enforceRegWriteOrder_f = false;
	    }

	}

    }
    
    public static void main(String [] args) throws IOException {
	
    	Simulator sim = new Simulator();

	parseArgs(args, sim);
	sim.printHeader();
	sim.processTrace(); // this is still where the action takes place
	if (!sim.printScoreboard_f)
	    sim.printTraceStats();
	
    }
    
}
