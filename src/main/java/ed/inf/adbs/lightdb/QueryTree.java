package ed.inf.adbs.lightdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;

/**
 * QueryTree class creates the query plan and builds the query tree 
 * using various logics and data structures explained and described below
 * @author S1705270
 */
public class QueryTree {

	public Operator root = null;
	public List<SelectItem> selectItems;
	public FromItem fromItem;
	public List<String> tablesInFrom;
	public Distinct distinct;
	public List<OrderByElement> orderByElems;
	public Expression where;
	public List<Join> joins;
	public List<Expression> andItems;
	public HashMap<String, Expression> selectConditionMap;
	public HashMap<String, Expression> joinConditionMap;

	public QueryTree(PlainSelect plainSelect) {
		selectItems = plainSelect.getSelectItems();
		fromItem = plainSelect.getFromItem(); // first table
		joins = plainSelect.getJoins();
		distinct = plainSelect.getDistinct();
		orderByElems = plainSelect.getOrderByElements();
		where = plainSelect.getWhere();

		/*
		 * initialisations and preparations with regard to FROM and WHERE
		 * before tree building
		 */

		/* FROM clause */
		tablesInFrom = new ArrayList<String>();
		if (fromItem.getAlias() == null) { // alias of first table does not exist
			tablesInFrom.add(fromItem.toString());
		} else { // alias of first table exists
			String realTableName = fromItem.toString().split(" ")[0];
			DatabaseCatalog.aliasMap.put(fromItem.getAlias().toString().trim(), realTableName);
			tablesInFrom.add(fromItem.getAlias().toString().trim());
		}

		if (joins != null) {
			for (Join join : joins) {
				FromItem rightItem = join.getRightItem(); // each of remaining tables
				if (rightItem.getAlias() == null) {
					tablesInFrom.add(rightItem.toString());
				} else {
					String realTableName = rightItem.toString().split(" ")[0];
					DatabaseCatalog.aliasMap.put(rightItem.getAlias().toString().trim(), realTableName);
					tablesInFrom.add(rightItem.getAlias().toString().trim());
				}
			}
		}

		/* WHERE clause */
		andItems = new ArrayList<Expression>();
		if (where != null) {
			// decompose conjunction into a list of individual expressions
			while (where instanceof AndExpression) {
				AndExpression andExp = (AndExpression) where;
				andItems.add(andExp.getRightExpression());
				where = andExp.getLeftExpression();
			}
			andItems.add(where);
			if (andItems.size() > 1) {
				Collections.reverse(andItems);
			}
		}

		// For each table, store the corresponding select conditions and join conditions
		// as a list instead of a conjunction; these lists will be turned into conjunctions below
		HashMap<String, List<Expression>> tempSelectCondMap = new HashMap<String, List<Expression>>();
		HashMap<String, List<Expression>> tempJoinCondMap = new HashMap<String, List<Expression>>();
		for (String table : tablesInFrom) {
			tempSelectCondMap.put(table, new ArrayList<Expression>());
			tempJoinCondMap.put(table, new ArrayList<Expression>());
		}

		for (Expression exp : andItems) {
			List<String> relatedTables = this.getRelatedTables(exp);
			if (relatedTables == null) { // in case of comparison between constants
				String lastTable = tablesInFrom.get(tablesInFrom.size() - 1);
				tempJoinCondMap.get(lastTable).add(exp);
				continue;
			}
			int index = 0;
			for (String table : relatedTables) { // find table that appears the latest in FROM clause
				index = Math.max(index, tablesInFrom.indexOf(table));
			}
			if (relatedTables.size() > 1) { // in case of join condition
				tempJoinCondMap.get(tablesInFrom.get(index)).add(exp);
			} else { // in case of select condition
				tempSelectCondMap.get(tablesInFrom.get(index)).add(exp);
			}
		}

		// Turn the lists of select conditions and join conditions into
		// conjunctive Expressions
		selectConditionMap = new HashMap<String, Expression>();
		joinConditionMap = new HashMap<String, Expression>();
		for (String table : tablesInFrom) {
			selectConditionMap.put(table, this.createConjunction(tempSelectCondMap.get(table)));
			joinConditionMap.put(table, this.createConjunction(tempJoinCondMap.get(table)));
		}

		build();
	}

	/**
	 * Get the select condition of i-th table in FROM
	 * @param i
	 * @return select condition
	 */
	private Expression getSelectCondition(int i) {
		String tableName = tablesInFrom.get(i);
		return selectConditionMap.get(tableName);
	}

	/**
	 * Get the join condition of i-th table in FROM
	 * @param i
	 * @return join condition
	 */
	private Expression getJoinCondition(int i) {
		String tableName = tablesInFrom.get(i);
		return joinConditionMap.get(tableName);
	}

	/**
	 * Return the referenced table names in the given expression
	 * @param exp Expression which is either a select condition or join condition
	 * @return a list of table names that are referenced in the given expression
	 */
	private List<String> getRelatedTables(Expression exp) {
		List<String> relatedTables = new ArrayList<String>();
		BinaryExpression binaryExp = (BinaryExpression) exp;
		Expression leftExp = binaryExp.getLeftExpression();
		if (leftExp instanceof Column) {
			Column tempColumn = (Column) leftExp;
			if (tempColumn.getTable() != null) {
				relatedTables.add(tempColumn.getTable().getName());
			} else {
				return null;
			}
		}
		Expression rightExp = binaryExp.getRightExpression();
		if (rightExp instanceof Column) {
			Column tempColumn = (Column) rightExp;
			if (tempColumn.getTable() != null) {
				if (!(tempColumn.getTable().getName().equals(relatedTables.get(0)))) {
					relatedTables.add(tempColumn.getTable().getName());
				}
			} else {
				return null;
			}
		}
		return relatedTables;
	}

	/**
	 * Convert a list of expressions into one conjunctive expression
	 * @param expressions a list of expressions
	 * @return a conjunctive expression
	 */
	private Expression createConjunction(List<Expression> expressions) {
		Expression andExp = null;
		for (int i = 0; i < expressions.size(); i++) {
			if (andExp == null) {
				andExp = expressions.get(i);
			} else {
				andExp = new AndExpression(andExp, expressions.get(i));
			}
		}
		return andExp;
	}
	
	/**
	 * Build the query tree
	 */
	private void build() {
		Operator curRoot = null;
		
		// FROM and WHERE clause
		for (int i = 0; i < tablesInFrom.size(); i++) {
			// FROM clause: scanning tables
			String curTableName = tablesInFrom.get(i);
			Table curTable = DatabaseCatalog.getTable(curTableName);
			Operator curOperator = new ScanOperator(curTable);
			
			// WHERE clause
			// Selection 
			Expression curSelectCond = this.getSelectCondition(i);

			if (curSelectCond != null) {
				curOperator = new SelectOperator((ScanOperator) curOperator, curSelectCond);
			}
			if (i == 0) {
				curRoot = curOperator;
			} else {
				// Join
				Expression curJoinCond = this.getJoinCondition(i);
				curRoot = new JoinOperator(curRoot, curOperator, curJoinCond);
			}
		}

		// Check whether or not all ORDER BY elements are projected
		boolean areOrderByElemsProjected;
		if (selectItems.get(0) instanceof AllColumns || orderByElems == null) {
			areOrderByElemsProjected = true;
		} else {
			HashSet<String> orderByAttrs = new HashSet<String>();
			for (OrderByElement orderByElem : orderByElems) {
				orderByAttrs.add(orderByElem.toString());
			}
			for (SelectItem item : selectItems) {
				orderByAttrs.remove(item.toString());
			}
			areOrderByElemsProjected = orderByAttrs.isEmpty();	
		}

		// If any of ORDER BY attributes are not projected,
		// we should perform sorting before projection
		if (orderByElems != null && !areOrderByElemsProjected) {
			curRoot = new SortOperator(curRoot, orderByElems);
		}
		
		// SELECT clause
		if (selectItems != null) {
			curRoot = new ProjectOperator(curRoot, selectItems);
		}
		
		// If all ORDER BY attributes are projected,
		// we can perform sorting after projection
		if (orderByElems != null && areOrderByElemsProjected) {
			curRoot = new SortOperator(curRoot, orderByElems);
		}

		// DISTINCT clause
		if (distinct != null) {
			if (orderByElems == null) {
				orderByElems = new ArrayList<OrderByElement>();
				curRoot = new SortOperator(curRoot, orderByElems);
			} 
			curRoot = new DuplicateEliminationOperator((SortOperator) curRoot);
		}

		root = curRoot;
	}
}
