package ed.inf.adbs.lightdb;

import java.util.List;

/**
 * Implementation of a Table object
 * @author S1705270
 */

public class Table {
	public String name;
	public List<String> schema;
	
	/**
	 * Constructor
	 * @param name table name
	 * @param schema table schema
	 */
	public Table(String name, List<String> schema) {
		this.name = name;
		this.schema = schema;
	}
}
