package ed.inf.adbs.lightdb;

import java.util.List;

import net.sf.jsqlparser.schema.Column;

/**
 * Implementation of a ExpressionVisitor for Selection expression.
 * This specifies the behaviour when visiting a Column in the context of Selection expression
 * @author S1705270
 */

public class SelectDeParser extends ExpressionDeParser {
	
	private Tuple curTuple;
	private List<String> schema;
	
	/**
	 * Constructor
	 * @param schema schema of the given operator
	 */
	public SelectDeParser(List<String> schema) {
		this.schema = schema;
	}
	
	/**
	 * Assign to curTuple the given tuple 
	 * @param tuple
	 */
	public void assignTuple(Tuple tuple) {
		curTuple = tuple;
	}
	
	/**
	 * When visiting a column, assign to curValue the value of the current column of the current tuple
	 */
	@Override
	public void visit(Column arg0) {
		String columnName = arg0.toString();
		for (int i = 0; i < schema.size(); i++) {
			String attr = schema.get(i);
			if (attr.equals(columnName)) {
				this.curValue = (long) curTuple.getValue(i);
				break;
			}
		}
	}

}
