package ed.inf.adbs.lightdb;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.ArrayExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.CollateExpression;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.ExtractExpression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.IntervalExpression;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.JsonExpression;
import net.sf.jsqlparser.expression.KeepExpression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.MySQLGroupConcat;
import net.sf.jsqlparser.expression.NextValExpression;
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.NumericBind;
import net.sf.jsqlparser.expression.OracleHierarchicalExpression;
import net.sf.jsqlparser.expression.OracleHint;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.expression.SignedExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeKeyExpression;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.UserVariable;
import net.sf.jsqlparser.expression.ValueListExpression;
import net.sf.jsqlparser.expression.VariableAssignment;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.XMLSerializeExpr;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseLeftShift;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseRightShift;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.IntegerDivision;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.FullTextSearch;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import net.sf.jsqlparser.expression.operators.relational.RegExpMySQLOperator;
import net.sf.jsqlparser.expression.operators.relational.SimilarToExpression;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsBooleanExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.JsonOperator;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;

/**
 * Abstract class which, by implementing ExpressionVisitor of JSQL parser,
 * supports a number of logical expressions that are required for this assignment
 * @author S1705270
 */

public abstract class ExpressionDeParser implements ExpressionVisitor {
	
	protected long curValue; // current value in the visitor
	protected boolean curCondition; // current condition in the visitor
	
	/**
	 * Return the current value in the visitor
	 * @return current value
	 */
	public long getCurrentValue() {
		return curValue;
	}
	
	/**
	 * Return the current boolean condition in the visitor
	 * @return current boolean condition
	 */
	public boolean getCurrentCondition() {
		return curCondition;
	}
	
	/**
	 * Visit a LongValue expression
	 * @param arg0 a LongValue expression
	 */
	@Override
	public void visit(LongValue arg0) {
		curValue = arg0.getValue();
	}
	
	/**
	 * Visit an AndExpression expression
	 * @param arg0 a AndExpression expression
	 */
	@Override
	public void visit(AndExpression arg0) {
		arg0.getLeftExpression().accept(this);
		boolean leftCondition = curCondition;
		arg0.getRightExpression().accept(this);
		boolean rightCondition = curCondition;
		curCondition = leftCondition && rightCondition;
	}

	/**
	 * Visit a EqualsTo expression
	 * @param arg0 a EqualsTo expression
	 */
	@Override
	public void visit(EqualsTo arg0) {
		arg0.getLeftExpression().accept(this);
		long leftValue = curValue;
		arg0.getRightExpression().accept(this);
		long rightValue = curValue;
		curCondition = (leftValue == rightValue);		
	}

	/**
	 * Visit a NotEqualsTo expression
	 * @param arg0 a NotEqualsTo expression
	 */
	@Override
	public void visit(NotEqualsTo arg0) {
		arg0.getLeftExpression().accept(this);
		long leftValue = curValue;
		arg0.getRightExpression().accept(this);
		long rightValue = curValue;
		curCondition = (leftValue != rightValue);	
	}

	/**
	 * Visit a GreaterThan expression
	 * @param arg0 a GreaterThan expression
	 */
	@Override
	public void visit(GreaterThan arg0) {
		arg0.getLeftExpression().accept(this);
		long leftValue = curValue;
		arg0.getRightExpression().accept(this);
		long rightValue = curValue;
		curCondition = (leftValue > rightValue);
	}

	/**
	 * Visit a GreaterThanEquals expression
	 * @param arg0 a GreaterThanEquals expression
	 */
	@Override
	public void visit(GreaterThanEquals arg0) {
		arg0.getLeftExpression().accept(this);
		long leftValue = curValue;
		arg0.getRightExpression().accept(this);
		long rightValue = curValue;
		curCondition = (leftValue >= rightValue);
		
	}

	/**
	 * Visit a MinorThan expression
	 * @param arg0 a MinorThan expression
	 */
	@Override
	public void visit(MinorThan arg0) {
		arg0.getLeftExpression().accept(this);
		long leftValue = curValue;
		arg0.getRightExpression().accept(this);
		long rightValue = curValue;
		curCondition = (leftValue < rightValue);
	}

	/**
	 * Visit a MinorThanEquals expression
	 * @param arg0 a MinorThanEquals expression
	 */
	@Override
	public void visit(MinorThanEquals arg0) {
		arg0.getLeftExpression().accept(this);
		long leftValue = curValue;
		arg0.getRightExpression().accept(this);
		long rightValue = curValue;
		curCondition = (leftValue <= rightValue);
	}
	
	@Override
	public void visit(BitwiseRightShift aThis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(BitwiseLeftShift aThis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(NullValue nullValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Function function) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(SignedExpression signedExpression) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(JdbcParameter jdbcParameter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(JdbcNamedParameter jdbcNamedParameter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(DoubleValue doubleValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(HexValue hexValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(DateValue dateValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(TimeValue timeValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(TimestampValue timestampValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Parenthesis parenthesis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(StringValue stringValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Addition addition) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Division division) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IntegerDivision division) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Multiplication multiplication) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Subtraction subtraction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OrExpression orExpression) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Between between) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(InExpression inExpression) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(FullTextSearch fullTextSearch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IsNullExpression isNullExpression) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IsBooleanExpression isBooleanExpression) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LikeExpression likeExpression) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(SubSelect subSelect) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(CaseExpression caseExpression) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(WhenClause whenClause) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ExistsExpression existsExpression) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AllComparisonExpression allComparisonExpression) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AnyComparisonExpression anyComparisonExpression) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Concat concat) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Matches matches) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(BitwiseAnd bitwiseAnd) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(BitwiseOr bitwiseOr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(BitwiseXor bitwiseXor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(CastExpression cast) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Modulo modulo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AnalyticExpression aexpr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ExtractExpression eexpr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IntervalExpression iexpr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OracleHierarchicalExpression oexpr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(RegExpMatchOperator rexpr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(JsonExpression jsonExpr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(JsonOperator jsonExpr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(RegExpMySQLOperator regExpMySQLOperator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(UserVariable var) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(NumericBind bind) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(KeepExpression aexpr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MySQLGroupConcat groupConcat) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ValueListExpression valueList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(RowConstructor rowConstructor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OracleHint hint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(TimeKeyExpression timeKeyExpression) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(DateTimeLiteralExpression literal) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(NotExpression aThis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(NextValExpression aThis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(CollateExpression aThis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(SimilarToExpression aThis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ArrayExpression aThis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(VariableAssignment aThis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(XMLSerializeExpr aThis) {
		// TODO Auto-generated method stub
		
	}
}
