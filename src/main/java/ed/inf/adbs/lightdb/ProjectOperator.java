package ed.inf.adbs.lightdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

/**
 * Implementation of a SELECT operator
 * @author S1705270
 */
public class ProjectOperator extends Operator {
	
	private Operator child;
	
	/**
	 * Constructor
	 * @param child child operator
	 * @param selectItems a list of attributes mentioned in SELECT clause
	 */
	public ProjectOperator(Operator child, List<SelectItem> selectItems) {
		this.child = child;
		
		List<String> tempSchema = new ArrayList<String>();
		
		for (SelectItem item : selectItems) {
			if (item instanceof AllColumns) { // in case of selecting *
				this.schema = child.schema();
				return;
			} else { // in case of selecting specific attributes or columns
				Column column = (Column) ((SelectExpressionItem) item).getExpression();
				if (column.getTable() != null) {
					String tableName = column.getTable().getName();
					tempSchema.add(tableName + "." + column.getColumnName());
				} else {
					String columnName = column.getColumnName();
					for (String col : child.schema()) {
						int dotIndex = col.indexOf(".");
						String attr = col.substring(dotIndex + 1);
						if (attr.equals(columnName)) {
							tempSchema.add(col);
							break;
						}
					}
				}
			}
		}
		this.schema = tempSchema;
	}
	
	/**
	 * Return the next tuple with the projected columns given in the constructor
	 * @return the next tuple with the projected columns
	 */
	public Tuple getNextTuple() throws IOException {
		Tuple curTuple = this.child.getNextTuple();
		if (curTuple != null) {
			int[] values = new int[schema.size()];
			int idx = 0;
			for (String attr : this.schema) {
				for (int i = 0; i < this.child.schema().size(); i++) {
					if (this.child.schema().get(i).equals(attr)) {
						values[idx] = curTuple.getValue(i);
						idx++;
						break;
					}
				}
			}
			return new Tuple(values);
		}
		return null;
	}
	
	/**
	 * Re-setting a SELECT operator indicates re-setting its child
	 * @throws IOException 
	 */
	public void reset() throws IOException {
		child.reset();
	}

	/**
	 * Return the schema of the current operator or the child operator
	 * depending on whether the schema of the current operator has been set or not
	 * @return a list of attributes of the current operator or the child operator 
	 */
	public List<String> schema() {
		return this.schema == null ? child.schema() : this.schema;
	}
}
