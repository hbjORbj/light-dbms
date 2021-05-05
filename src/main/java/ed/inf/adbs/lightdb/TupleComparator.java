package ed.inf.adbs.lightdb;

import java.util.Comparator;
import java.util.List;

/**
 * Implementation of a comparator for Tuple objects. 
 * @author S1705270
 *
 */
public class TupleComparator implements Comparator<Tuple> {
	private List<Integer> attrsOrder;
	
	/**
	 * Constructor
	 * @param attrsOrder a list of integers (order to be followed for comparing)
	 */
	public TupleComparator(List<Integer> attrsOrder) {
		this.attrsOrder = attrsOrder;
	}
	
	/**
	 * Compare two Tuple objects following the order specified in attrsOrder
	 * @param tuple1 first Tuple object
	 * @param tuple2 second Tuple object
	 * @return comparison value that indicates the order between the two Tuple objects
	 */
	@Override
	public int compare(Tuple tuple1, Tuple tuple2) {
		if (tuple1.getSize() == tuple2.getSize()) {
			for (int attrIndex : attrsOrder) {
				int comparisonValue = Integer.compare(tuple1.getValue(attrIndex), tuple2.getValue(attrIndex));
				if (comparisonValue != 0) {
					return comparisonValue; 
				}
			}
			// in case two tuples have the same value in the attributes given in ORDER BY
			for (int i = 0; i < tuple1.getSize(); i++) {
				if (!attrsOrder.contains(i)) {					
					int comparisonValue = Integer.compare(tuple1.getValue(i), tuple2.getValue(i));
					if (comparisonValue != 0) {
						return comparisonValue;
					}
				}
			}
			return 0;
		} else {
			throw new Error("Two tuples must have the same arity in order to be compared.");
		}
	}
}
