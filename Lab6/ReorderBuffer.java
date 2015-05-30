/* 
 * Do not modify this file.
 */

import java.util.ArrayDeque;

public class ReorderBuffer {
    
    public ArrayDeque<ROBentry> entries = null;
    public int maxEntries = 64; // default value
    
    public ReorderBuffer() {
	this.entries = new ArrayDeque<ROBentry>();
    }
    
    /*
     * An ArrayDeque techincally has no maximum size.
     * A ROB, however, does.
     */
    public void setMaxEntries(int maxEntries) {
	this.maxEntries = maxEntries;
    }
    
    /*
     * Let's you see the head of the ROB
     * but does not remove the entry from the ROB
     */
    public ROBentry head() {
	return entries.getFirst();
    }
    
    /*
     * Removes the head of the ROB from the ROB
     */
    public void dequeue() {
	entries.removeFirst();
    }

    /*
     * Adds a ROB entry to the tail of the ROB
     */
    public void enqueue(ROBentry entry) {
	entries.addLast(entry);
    }
    
    public boolean isEmpty() {
	return entries.size() == 0;
    }
    
    /*
     * An ArrayDeque techincally has no maximum size.
     * A ROB, however, does.
     */
    public boolean isFull() {
	return entries.size() == maxEntries;
    }
    
}

