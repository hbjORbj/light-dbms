package ed.inf.adbs.lightdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.jsqlparser.statement.select.OrderByElement;

/**
 * Implementation of a ORDER BY operator
 * @author S1705270
 */

public class SortOperator extends Operator {
	
	private Operator child;
	private List<Tuple> tuples;
	private List<Integer> attrsOrder; // a list of indices of attributes
	private int curIndex;
	
	
	/**
	 * Constructor
	 * @param child the child operator
	 * @param orderByElems a list of attribute names in ORDER BY clause
	 */
	public SortOperator(Operator child, List<OrderByElement> orderByElems) {
		this.child = child;
		this.curIndex = 0;
		this.tuples = new ArrayList<Tuple>();
		this.attrsOrder = new ArrayList<Integer>();
		Tuple curTuple = null;
		try {
			while ((curTuple = child.getNextTuple()) != null) {
				tuples.add(curTuple);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Loop through the attributes mentioned in ORDER BY clause
		// and record to attrsOrder the indices of the attributes
		for (OrderByElement orderByElem : orderByElems) {
			String attr = orderByElem.toString();
			int dotIndex = attr.indexOf(".");
			if (dotIndex != -1) {				
				attr = attr.substring(dotIndex + 1);
			}
			for (int i = 0; i < child.schema().size(); i++) {
				dotIndex = child.schema().get(i).indexOf(".");
				String columnName = child.schema().get(i).substring(dotIndex + 1);
				if (attr.equals(columnName)) {
					this.attrsOrder.add(i);
					break;
				}
			}
		}
		TupleComparator tupleSortComparator = new TupleComparator(this.attrsOrder); 
		Collections.sort(tuples, tupleSortComparator);
	}
	
	/**
	 * Get the tuple at current index of stored tuples
	 * @return the next tuple
	 */
	@Override
	public Tuple getNextTuple() {
		return curIndex < tuples.size() ? tuples.get(curIndex++) : null;
	}
	
	/**
	 * Re-set the current index to zero
	 */
	@Override
	public void reset() {
		curIndex = 0;
	}

	/**
	 * Return the schema of the child operator
	 * because Sort operator does not change the schema
	 * @return a list of attributes of the child operator 
	 */
	public List<String> schema() {
		return child.schema();
	}
}
