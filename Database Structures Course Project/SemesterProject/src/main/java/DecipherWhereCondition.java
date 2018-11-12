import java.util.LinkedHashSet;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnID;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition.Operator;

public class DecipherWhereCondition extends RuntimeException {
	private String tableName; 	// the name of the table wanted to access
	private Table theTable; 	// the table desired by the given tableName in the query
	private int row;	// the index of the current row we are on provided by the parameter
	private Database theDatabase; // the static universal database
	private RowOfTable firstRow;	// the first row of the table (used in getVal)
	
	public DecipherWhereCondition(int givenRow, String givenTableName) {
		row = givenRow;
		theDatabase = new Database();
		setTableName(givenTableName);
	}
	
	// if ran correctly, will return a boolean.
	// @param give the where condition
	public Object getOperands (Object given) {
		// NEED TO CHECK: also implement for a case of "1.0 < GPA < 3.0" need to split it up into "1.0 < GPA" && "GPA < 3.0"
		if (given instanceof Condition) {
			Condition innerGiven = ((Condition) given);
			Object theLeft = getOperands(innerGiven.getLeftOperand());
			Operator theOperator = innerGiven.getOperator();
			Object theRight = getOperands(innerGiven.getRightOperand());
			
			return calculateOperands(theLeft, theOperator, theRight);
		}
		return given;
		
		//return isRow;
	}

	private Boolean calculateOperands(Object theLeft, Operator theOperator, Object theRight) {
		Boolean result = false;
		// if both left and right are Booleans, then send to Boolean compute method and store outcome of type Boolean in result
		if ((theLeft instanceof Boolean) && (theRight instanceof Boolean)) {
			// cast theLeft and theRight to Booleans
			Boolean theCurrLeft = (Boolean) theLeft;
			Boolean theCurrRight = (Boolean) theRight;
			// go to the Boolean compute method
			result = compareBooleans(theCurrLeft, theOperator, theCurrRight);
		}
		// if left is a column ID then send to decipherOneSide and eventually return a string or the value in the slot
		if (theLeft instanceof ColumnID) {
			// cast theLeft to columnID
			ColumnID theCurrLeft = (ColumnID) theLeft;
			// go to decipherOneSide method
			theLeft = decipherOneSide(theCurrLeft);
			if (theRight.equals("NULL")) {
				theRight = null;
			}
		}
		// if right is a columnId then send to decipherOneSide and eventually return a string or the value in the slot
		if (theRight instanceof ColumnID) {
			// cast theRight to columnID
			ColumnID theCurrRight = (ColumnID) theRight;
			// go to decipherOneSide method
			theRight = decipherOneSide(theCurrRight);
			if (theLeft.equals("NULL")) {
				theLeft = null;
			}
		}
		// if both left and right are Stings, then compare them and store result in result.
		if((theLeft instanceof String) && (theRight instanceof String)) {
			// cast theLeft and theRight to Strings
			String theCurrLeft = (String) theLeft;
			String theCurrRight = (String) theRight;
			// go to String compute method
			result = compareStrings(theCurrLeft, theOperator, theCurrRight);
		}
		
		if (theLeft == null && theRight == null) {
			result = true;
		}
		// return result, which is a Boolean.
			// true means to delete the row
			// false means to not delete the row

		return result;
	}
	
	// decipher the ColumnID to get a String value from the slot that is being referenced to in the current row
	private String decipherOneSide (ColumnID theSide) {
		// if theSide is of type ColumnID
		// column name to get to the correct column
		String columnName = theSide.getColumnName();
		String stringResult = getVal(columnName);
		return stringResult;
	}
	
	private String getVal (String givenColumnName) {
		String stringResult = null;
	// first need to find the inquired slot
		// iterate through the first row until find the column which has the same column name as givenColumnName
		Integer columnIndex = null; 	// the index of that column
		firstRow = theTable.get(0);
		if (firstRow.isEmpty()) {
			// means the table is empty, so return.
			return null;
		}
		try {
			columnIndex = getColIndex(givenColumnName);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			throw e;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("test2");
			try {
				throw e;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println("test3");
			}
		}
		
		// if columnIndex is null, it means that the inquired string, givenColumnName does not exist, so return null.
		if (columnIndex == null) {
			return null;
		}
		
		// using the index, proceed in the current row to that column
		RowOfTable theRow = theTable.get(row);
		Object theElement = theRow.getElement(columnIndex);
		if (theElement instanceof String) {
			stringResult = (String) theElement;
		}
		// NEED TO DO: if that value is an int or double or float or etc., convert it into a string
		// assign the value in that column to result
		return stringResult;
	}
	
	private Integer getColIndex (String givenColumnName) throws Exception {
		Integer columnIndex = null;
		for (int col = 0; col < firstRow.size(); col++) {
			Object tempElement = firstRow.getElement(col);
			if (tempElement instanceof ColumnDescription) {
				ColumnDescription element = (ColumnDescription) tempElement;
				if (element.getColumnName().equalsIgnoreCase(givenColumnName)) {
					// found the column, so set columnIndex to col and break;
					columnIndex = col;
					break;
				}
			}
			if (tempElement instanceof PrimaryKey) {
				PrimaryKey element = (PrimaryKey) tempElement;
				if (element.getColumnName().equalsIgnoreCase(givenColumnName)) {
					// found the column, so set columnIndex to col and break;
					columnIndex = col;
					break;
				}
			}
			// col == .size() - 1, then means that no column exists with that column name, 
			// so the query referred to a non-existant column, so set the resultSet to false.
			if ((col == firstRow.size() - 1) && (columnIndex == null)) {
				ResultSet resultSet = new ResultSet();
				resultSet.setTableBoolean(false);
				RuntimeException exception = new RuntimeException();
				throw exception;
			}
		}
		
		return columnIndex;
	}
	
	private Boolean compareBooleans (Boolean theLeft, Operator theOperator, Boolean theRight) {
		Boolean result = false;
		if (theOperator.toString().equalsIgnoreCase("and")) {
			if (theLeft == false || theRight == false) {
				result = false;
			}
			else {
				result = (theLeft && theRight);
			}
		}
		else if (theOperator.toString().equalsIgnoreCase("or")) {
			result = (theLeft || theRight);
		}
		else {
			System.out.println("Error!");
		}
		return result;
	}
	
	private Boolean compareStrings (String theLeft, Operator theOperator, String theRight) {
		Boolean result = false;
		// problem with the operator .toString
		String temp = theOperator.toString().toLowerCase();
		switch (temp) {
		case "=": // equals
			result = (theLeft.equals(theRight));
			break;
		case "<>": // not equals
			result = !(theLeft.equals(theRight));
			break;
		case "<": // less than
			result = (Double.valueOf(theLeft) < Double.valueOf(theRight));
			break;
		case "<=": // less than or equals
			result = (Double.valueOf(theLeft) <= Double.valueOf(theRight));
			break;
		case ">": // greater than
			result = (Double.valueOf(theLeft) > Double.valueOf(theRight));
			break;
		case ">=": // greater than or equals
			result = (Double.valueOf(theLeft) >= Double.valueOf(theRight));
			break;
			// need to put in the other cases
		default:
			break;
		}
		return result;
	}
	
	
	private void setTableName(String givenTableName) {
		tableName = givenTableName;
		theTable = theDatabase.findTable(givenTableName); // the table object
	}
	
}
