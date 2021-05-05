package ed.inf.adbs.lightdb;

import java.io.IOException;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;

/**
 * Implementation of a Selection operator
 * @author S1705270
 */

public class SelectOperator extends Operator {
	
	Expression e;
	SelectDeParser sdp;
	ScanOperator child;

	/**
	 * Constructor
	 * @param scanOp a ScanOperator instance
	 * @param e a selection condition
	 */
	public SelectOperator( ScanOperator scanOp, Expression e ) {
		this.child = scanOp;
		this.e = e;
		this.sdp = new SelectDeParser(scanOp.schema()); // create a ExpressionVisitor for selection
	}
	
	/**
	 * Return the next tuple that satisfies the given condition
	 * @return a Tuple object
	 */
	public Tuple getNextTuple() throws IOException {
		Tuple curTuple = null;
		while ((curTuple = this.child.getNextTuple()) != null) {
			if (e == null) {
				return curTuple;
			}
			sdp.assignTuple(curTuple);
			e.accept(sdp);
			if (sdp.getCurrentCondition() == true) {
				return curTuple;
			}
		}
		return null;
	}
		
	/**
	 * Re-setting a Selection operator indicates re-setting its child
	 * @throws IOException 
	 */
	public void reset() throws IOException {
		child.reset();
	}
	
	/**
	 * Return the schema of the child operator
	 * because Selection operator does not change the schema
	 * @return a list of attributes of the child operator 
	 */
	public List<String> schema() {
		return child.schema();
	}
}
