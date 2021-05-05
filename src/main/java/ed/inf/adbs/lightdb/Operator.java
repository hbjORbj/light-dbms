package ed.inf.adbs.lightdb;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

/**
 * Abstract class for all relational operators 
 * including FROM, JOIN, SELECT, ORDER BY, DISTINCT
 * @author S1705270
 */

public abstract class Operator {
	protected List<String> schema = null;
	
	/**
	 * Get the next tuple of the table
	 * @return a Tuple object
	 */
	public abstract Tuple getNextTuple() throws IOException;
	
	/**
	 * Re-set the pointer to the beginning of the table
	 */
	public abstract void reset() throws IOException;
	
	/**
	 * Return the schema of the table
	 * @return a list of attributes
	 */
	public abstract List<String> schema();
	
	/**
	 * Scan a tuple and write it to given output file for all existing tuples
	 * @param ps a PrintStream object
	 */
	public void dump(PrintStream ps) throws IOException {
		Tuple curTuple = null;
		while ((curTuple = this.getNextTuple()) != null) {
			curTuple.dump(ps);
		}
	}
	
}
