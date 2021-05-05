package ed.inf.adbs.lightdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a FROM operator
 * @author S1705270
 */
public class ScanOperator extends Operator {
	
	private Table table;
	private BufferedReader tableReader;

	/**
	 * Constructor
	 * Using the given table, we obtain a buffered reader for the current table's data and
	 * also fill this operator's schema list in a format such that each input is "TableName.Attribute"
	 * @param table a table object that contains name and schema as properties
	 */
	public ScanOperator(Table table) {
		this.table = table;
		this.tableReader = DatabaseCatalog.getTableReader(table.name);
		this.schema = new ArrayList<String>();
		for (String attr : table.schema) {
			this.schema.add(table.name + "." + attr);
		}
	}
	
	/**
	 * Get the next tuple of the current table
	 * @return a Tuple object 
	 * @throws IOException 
	 */
	public Tuple getNextTuple() throws IOException {
		String curLine = tableReader.readLine();
		if (curLine != null) {
			String[] stringValues = curLine.split(",");
			if (stringValues.length == schema.size()) {
				int[] intValues = new int[schema.size()];
				for (int i = 0; i < schema.size(); i++) {
					intValues[i] = Integer.parseInt(stringValues[i]);
				}
				return new Tuple(intValues);
			} else {
				throw new IOException("All tuples of a table should have the same arity.");
			}
		}
		return null;
	}
	
	/**
	 * Close the reader, if any, and get a new buffered reader for the current table
	 * @throws IOException 
	 */
	public void reset() throws IOException {
		if (tableReader != null) {
			tableReader.close();
		}
		tableReader = DatabaseCatalog.getTableReader(table.name);
	}
	
	/**
	 * Return the schema of the current operator
	 * @return a list of attributes 
	 */
	public List<String> schema() {
		return schema;
	}
}
