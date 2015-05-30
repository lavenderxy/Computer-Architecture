/*
 * WARNING: When we grade your assignment, we will not use this file, so
 * any changes you make to this file will be discarded.
 */
import java.io.IOException;

public class HW1a
{ 

    public static void main(String [] args) throws IOException {

	// since this hw only uses 1 trace, we hard-code it here
    	Simulator sim = new Simulator("mcf-1K.trace.gz");

	sim.printHeader();

	// this is where the action takes place
	sim.processTrace(false /*verbosity*/);

	sim.printTraceStats();
	sim.printHW1aQuestions();

    }

}
