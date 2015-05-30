/*
 * WARNING: When we grade your assignment, we will not use this file, so
 * any changes you make to this file will be discarded.
 */

import java.io.IOException;

public class HW6
{ 

    public static void printUsage() {
	
	System.out.println("Usage: java HW6 [-hv] -t <tracename> -l <insnLimit>");
	System.out.println("Options:");
	System.out.println("  -h|help                 Print this help message");
	System.out.println("  -v|verbose              Optional verbose flag");
	System.out.println("  -debug                  Optional debug prints $ contents");
	System.out.println("  -renameOnly             set this flag for part 1 of HW 6");
	System.out.println("  -modelMem               model memory dependences (default = false)");
	System.out.println("  -perfectSched           model perfect memory scheduling (default = false)");
	System.out.println("  -preg <n>               number of physical registers");
	System.out.println("  -rob <n>                number of ROB entries");
	System.out.println("  -width <n>              superscalar width of machine");
	System.out.println("  -t|trace <tracename>    Name of trace (exclude trace.gz) ");
	System.out.println("  -l|limit <n>            Simulate only first n insns ");
	System.out.println("\nExamples:");
	System.out.println("  shell>  java HW6 -debug -renameOnly -preg 60");
	System.out.println("  shell>  java HW6 -debug -preg 64 -rob 4 -width 1");
	System.out.println("  shell>  java HW6 -debug -preg 64 -rob 4 -width 1 -modelMem");
	System.out.println("  shell>  java HW6 -debug -preg 64 -rob 4 -width 1 -modelMem -perfectMem");
	System.out.println("  shell>  java HW6 -verbose -trace sjeng-1M -l 1000000");

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

	    // -renameOnly
            if (arg.equals("-renameOnly")) {
                System.out.println("PART 1: Rename Only");
                sim.renameOnly_f = true;
            }

	    // -modelMem
            if (arg.equals("-modelMem")) {
                System.out.println("Experiments 2 & 3: Modeling Memory Dependences");
                sim.modelMemoryDependences_f = true;
            }

	    // -perfectSched
            if (arg.equals("-perfectSched")) {
                System.out.println("Experiment 3: Modeling Perfect Memory Scheduling");
                sim.perfectMemSched_f = true;
            }

	    // -preg n
            if (arg.equals("-preg")) {
		sim.nPhysicalRegisters = Integer.parseInt(args[i++]);
            }

	    // -rob n
            if (arg.equals("-rob")) {
		sim.ROB.setMaxEntries(Integer.parseInt(args[i++]));
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
	    
	    sim.initRegStructures(); // with default value or the one we just read in
	    
	    sim.printHeader();
	    sim.processTrace();
	    sim.printTraceStats();
	}
    }
    
}
