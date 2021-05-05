package ed.inf.adbs.lightdb;

import java.io.IOException;
import java.util.List;

/**
 * Implementation of a DISTINCT operator
 * which eliminates duplicate tuples in the table
 * @author S1705270
 */

public class DuplicateEliminationOperator extends Operator {
	
	private SortOperator child;
	private Tuple prevTuple = null;
	
	/**
	 * Constructor
	 * @param sortOp a SortOperator instance
	 */
	public DuplicateEliminationOperator(SortOperator sortOp) {
		this.child = sortOp;
	}
	
	/**
	 * Ensure that the tuple is not a duplicate and return it
	 * @return the next tuple which is not a duplicate
	 */
	@Override
	public Tuple getNextTuple() {
		if (prevTuple == null) {
			prevTuple = this.child.getNextTuple();
		} else {
			Tuple curTuple = null;
			while ((curTuple = this.child.getNextTuple()) != null) {
				Boolean areTuplesEqual = true;	
				// check if two tuples are equal
				for (int i = 0; i < curTuple.getSize(); i++) {
					if (curTuple.getValue(i) != prevTuple.getValue(i)) {
						areTuplesEqual = false;
						break;
					}
				}
				if (areTuplesEqual) {
					// if current tuple and prev tuple are the same,
					// skip to the next tuple without returning it
					continue;
				} else {
					// otherwise, return the prev tuple as there are no more duplicate tuples
					// of the previous tuple
					break;
				}
			}
			prevTuple = curTuple;
		}
		return prevTuple;
	}
	
	/**
	 * Re-setting a DISTINCT operator indicates re-setting its child
	 * @throws IOException 
	 */
	public void reset() throws IOException {
		child.reset();
	}

	/**
	 * Return the schema of the child operator
	 * because DISTINCT operator does not change the schema
	 * @return a list of attributes of the child operator 
	 */
	public List<String> schema() {
		return child.schema();
	}
}
