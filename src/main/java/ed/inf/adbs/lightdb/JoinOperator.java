package ed.inf.adbs.lightdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;

/**
 * Implementation of a JOIN operator
 * @author S1705270
 */
public class JoinOperator extends Operator {
	
	private Expression e;
	private Operator leftOperator;
	private Operator rightOperator;
	private Tuple leftTuple;
	private Tuple rightTuple;
	private JoinDeParser jdp;
	
	/**
	 * Constructor
	 * @param left the given left operator
	 * @param right the given right operator
	 * @param e the given condition
	 */
	public JoinOperator(Operator left, Operator right, Expression e) {
		leftOperator = left;
		rightOperator = right;
		List<String> tempSchema = new ArrayList<String>();
		tempSchema.addAll(left.schema());
		tempSchema.addAll(right.schema());
		schema = tempSchema;
		this.e = e;
		jdp = new JoinDeParser(left.schema(), right.schema());
		try {
			leftTuple = left.getNextTuple();
			rightTuple = right.getNextTuple();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	/**
	 * Using the login defined in the internal function moveToNextTuple(),
	 * scan through all right tuples for each left tuple and find the tuples
	 * that satisfy the given condition; if the given condition is null, simply
	 * take the cross product of every pair of tuples
	 */
	public Tuple getNextTuple() throws IOException {
		Tuple result = null;
		while (leftTuple != null && rightTuple != null) {
			if (e == null) {
				result = this.concatTuples(leftTuple, rightTuple);
			} else {
				jdp.assignTuples(leftTuple, rightTuple);
				e.accept(jdp);
				if (jdp.getCurrentCondition() == true) {					
					result = this.concatTuples(leftTuple, rightTuple);
				}
			}
			
			this.moveToNextTuple();
			
			if (result != null) {
				return result;
			}
		}
		
		return null;
	}
	
	/**
	 * Reset both the left operator and the right operator
	 */
	public void reset() throws IOException {
		leftOperator.reset();
		rightOperator.reset();
	}
	
	/**
	 * Return the schema of the current operator
	 * @return a list of attributes of the current operator 
	 */
	public List<String> schema() {
		return schema;
	}
	
	/**
	 * Move the right tuple to the next until the end of the right tuple
	 * while having the left tuple remain the same, and once the end of the right tuple
	 * is reached, reset the right tuple and change the left tuple to the next one
	 */
	private void moveToNextTuple() throws IOException {
		if (leftTuple != null) {
			if (rightTuple != null) {
				rightTuple = rightOperator.getNextTuple();				
			}
			
			if (rightTuple == null) {
				rightOperator.reset();
				leftTuple = leftOperator.getNextTuple();
				rightTuple = rightOperator.getNextTuple();
			}
		}
		return;
	}
	
	/**
	 * Concatenate the given two tuples
	 * @param left tuple 1
	 * @param right tuple 2
	 * @return concatenated tuple
	 */
	private Tuple concatTuples(Tuple left, Tuple right) {
		int[] values = new int[left.getSize() + right.getSize()];
		int idx = 0;
		for (int value : left.getValues()) {
			values[idx] = value;
			idx++;
		}
		for (int value : right.getValues()) {
			values[idx] = value;
			idx++;
		}
		return new Tuple(values);
	}
}
