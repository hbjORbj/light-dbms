package ed.inf.adbs.lightdb;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Implementation of a Tuple object
 * @author S1705270
 */
public class Tuple {
	
	private int[] values; // all values of given data are integer
	
	/**
	 * Constructor
	 * @param values a list of integers
	 */
	public Tuple(int[] values) {
		this.values = values;
	}
	
	/**
	 * Get all the values of the current tuple as an array
	 * @return values of the current tuple
	 */
	public int[] getValues() {
		return values;
	}
	
	/**
	 * Get the value of (index)-th attribute of the current tuple
	 * @param index
	 * @return value of (index)-th attribute of the current tuple
	 */
	public int getValue(int index) {
		return values[index];
	}
	
	/**
	 * Get the number of attributes of the current tuple
	 * @return number of attributes of the current tuple
	 */
	public int getSize() {
		return values.length;
	}
	
	/**
	 * Write to the print stream the current tuple's values
	 * @throws IOException 
	 */
	public void dump(PrintStream ps) throws IOException {
		String line = "";
		if (this.getSize() >= 1) {
			for (int i = 0; i < this.getSize(); i++) {
				if (i == 0) {
					line = line + String.valueOf(values[i]);
				} else {
					line = line + "," + String.valueOf(values[i]);
				}
			}
		}
		line = line + "\n";
		ps.write(line.getBytes());
	}
	
}
