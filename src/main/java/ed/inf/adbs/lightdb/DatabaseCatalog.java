package ed.inf.adbs.lightdb;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * DatabaseCatalog class keeps track of the file directories of data including tables and schema, 
 * the corresponding schemas of tables, the aliases and original names of tables.
 * @author S1705270
 */

public class DatabaseCatalog {
	
	private static DatabaseCatalog instance;
	private static String dbDir; 
	public static HashMap<String, List<String>> schemaMap; // map a table name to a list of its attribute names
	public static HashMap<String, String> aliasMap; // map an alias table name to its original table name
	
	/**
	 * Ensure that only one instance exists and return the instance.
	 * @param dir the file directory of the data
	 * @return a DatabaseCatalog instance
	 * @throws IOException 
	 */
	public static DatabaseCatalog getInstance(String dir) throws IOException {
		DatabaseCatalog.dbDir = dir;
		String schemaDir = dir + "/schema.txt";
		instance = new DatabaseCatalog();
		BufferedReader schemaReader = new BufferedReader(new FileReader(schemaDir));
		schemaMap = new HashMap<String, List<String>>();
		aliasMap = new HashMap<String, String>();
		String curLine = null;
		while ((curLine = schemaReader.readLine()) != null) {
			String[] elems = curLine.split(" ");
			if (elems.length >= 2) { // there is one or more attributes
				String tableName = elems[0];
				List<String> attrs = new ArrayList<String>();
				for (int i = 1; i < elems.length; i++) {
					attrs.add(elems[i]);
				}
				schemaMap.put(tableName, attrs);
			}
		}
		schemaReader.close();
		return instance;
	}
	
	public static DatabaseCatalog getInstance() {
		return instance;
	}
	
	public static Table getTable(String tableName) {
		return new Table(tableName, getSchema(tableName));
	}
	
	public static BufferedReader getTableReader(String tableName) {
		// Get the real table name, in case given name is an alias name
		if (aliasMap.containsKey(tableName)) {
			tableName = aliasMap.get(tableName);
		}
		String tableDir = dbDir + "/data/" + tableName + ".csv";
		try {
			BufferedReader tableReader = new BufferedReader(new FileReader(tableDir));
			return tableReader;
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<String> getSchema(String tableName) {
		// Get the real table name, in case given name is an alias name
		if (aliasMap.containsKey(tableName)) {
			tableName = aliasMap.get(tableName);
		}
		return schemaMap.get(tableName);
	}
	
}
