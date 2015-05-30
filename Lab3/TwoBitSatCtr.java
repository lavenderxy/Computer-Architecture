
public class TwoBitSatCtr {

    public final static int NBITS = 2;
    public final static int NCHARS = 4;
    public final static int MAX_VAL = 3;
    public final static int FIRST_TRUE_VAL = 2;

    public int counter;
    public char[] charMapping = null;
    public int firstTrueValue;
    
    public TwoBitSatCtr() {
	counter = 0; // Initialize to 0 to begin with
	charMapping = new char[(int)Math.pow(2.0, NBITS)]; 
    }
    
    public void inc() {
	if (counter < MAX_VAL) {
	    counter++;
	}
    }
    
    public void dec() {
	if (counter > 0) {
	    counter--;
	}
    }
    
    public int getValue() {
	return counter;
    }

    public void setChar(int i, char c) {
	if ((i < NCHARS) && ( i >=0))
	    charMapping[i] = c;
    }
    
    public char getChar() {
	return charMapping[counter];
    }

    /* if we consider the first half of values to map to false
     * and the second half of values to map to true, this 
     * function returns that mapping. useful for predictors.
     */
    public boolean isTrue() {
	return (counter >= FIRST_TRUE_VAL);
    }
}
