package ed.inf.adbs.lightdb;

import java.util.List;

import net.sf.jsqlparser.schema.Column;

/**
 * Implementation of a ExpressionVisitor for Join expression.
 * This specifies the behaviour when visiting a Column in the context of Join expression
 * @author S1705270
 */

public class JoinDeParser extends ExpressionDeParser {
	
	private Tuple leftTuple;
	private Tuple rightTuple;
	private List<String> leftSchema;
	private List<String> rightSchema;

	/**
	 * Constructor
	 * @param leftSchema schema of left operator
	 * @param rightSchema schema of right operator
	 */
	public JoinDeParser(List<String> leftSchema, List<String> rightSchema) {
		this.leftSchema = leftSchema;
		this.rightSchema = rightSchema;
	}
	
	/**
	 * Assign to leftTuple and rightTuple the given tuples 
	 * @param leftTuple
	 * @param rightTuple
	 */
	public void assignTuples(Tuple leftTuple, Tuple rightTuple) {
		this.leftTuple = leftTuple;
		this.rightTuple = rightTuple;
	}
	
	/**
	 * When visiting a column, assign to curValue the value of the current column
	 * which is part of either leftSchema or rightSchema
	 */
	@Override
	public void visit(Column arg0) {
		String columnName = arg0.toString();

		Long value = null;
		
		for (int i = 0; i < leftSchema.size(); i++) {
			String attr = leftSchema.get(i);
			if (attr.equals(columnName)) {
				value = (long) leftTuple.getValue(i);
				break;
			}
		}
		
		if (value == null) { // given column is not found in leftSchema
			for (int i = 0; i < rightSchema.size(); i++) {
				String attr = rightSchema.get(i);
				if (attr.equals(columnName)) {
					value = (long) rightTuple.getValue(i);
					break;
				}
			}
		}
		
		this.curValue = value;
	}
}
