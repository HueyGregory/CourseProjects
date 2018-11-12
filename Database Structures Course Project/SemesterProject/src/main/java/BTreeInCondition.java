import java.util.ArrayList;
import java.util.LinkedHashSet;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnID;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition.Operator;

public class BTreeInCondition {
	private Database theDatabase; // the static universal database
	private BTreeDatabase btreeDatabase;
	private Table theTable; 	// the table desired by the given tableName in the query
	private String tableName;
	private ArrayList<String> cols; // list of columns listed in the whereCondition
	private ArrayList btreeList; // will hold lists of rows
	
	public BTreeInCondition(String givenTableName) {
		theDatabase = new Database();
		btreeDatabase = new BTreeDatabase();
		tableName = givenTableName;
		theTable = theDatabase.findTable(givenTableName);
		cols = new ArrayList<String>();
	}

	// where the condition contains btrees in it.
	// put in a condition and get out a list of rows which conform to the condition
	// uses btrees wherever the column referenced is indexed
	public Object getListRows (Object given) {
		if (given instanceof Condition) {
			Condition innerGiven = ((Condition) given);
			Object theLeft = getListRows(innerGiven.getLeftOperand());
			Operator theOperator = innerGiven.getOperator();
			Object theRight = getListRows(innerGiven.getRightOperand());
			
			return getRows(theLeft, theOperator, theRight);
		}
		return given;
	}
	
	
	
	private LinkedHashSet<RowOfTable> getRows(Object theLeft, Operator theOperator, Object theRight) {
		LinkedHashSet<RowOfTable> result = new LinkedHashSet<RowOfTable>();
		// TODO Auto-generated method stub
		// if left is a column ID then send to decipherOneSide and eventually return a string or the value in the slot
		if ((theLeft instanceof LinkedHashSet) && (theRight instanceof LinkedHashSet)) {
			LinkedHashSet<RowOfTable> theLeftList = (LinkedHashSet<RowOfTable>) theLeft;
			LinkedHashSet<RowOfTable> theRightList = (LinkedHashSet<RowOfTable>) theRight;
			result = combineLinkedHashSets(theLeftList, theOperator, theRightList);
		}
		
		if (theLeft instanceof ColumnID) {
			// cast theLeft to columnID
			ColumnID theCurrLeft = (ColumnID) theLeft;
			String strRight = "";
			if (theRight instanceof String) {
				strRight = (String) theRight;
			}
			result = getLinkedHashSet(theCurrLeft.getColumnName(), theOperator, strRight);
		}
		// if right is a columnId then send to decipherOneSide and eventually return a string or the value in the slot
		if (theRight instanceof ColumnID) {
			// cast theRight to columnID
			ColumnID theCurrRight = (ColumnID) theRight;
			String strLeft = "";
			if (theLeft instanceof String) {
				strLeft = (String) theLeft;
			}
			result = getLinkedHashSet(theCurrRight.getColumnName(), theOperator, strLeft);
		}
		
		return result;
	}
	
	private LinkedHashSet<RowOfTable> combineLinkedHashSets (LinkedHashSet<RowOfTable> theLeftList, Operator theOperator, LinkedHashSet<RowOfTable> theRightList) {
		LinkedHashSet<RowOfTable> result = new LinkedHashSet<RowOfTable>();
		if (theOperator.toString().equalsIgnoreCase("and")) {
			// iterate over the left linked hash set
			if (theLeftList != null) {
				for (RowOfTable leftRow : theLeftList) {
					// iterate over the right linked hash set
					if (theRightList != null) {
						for (RowOfTable rightRow : theRightList) {
							// if row from left == row from right, then add that row to result
							if (leftRow == rightRow) {
								result.add(leftRow);
								break;
							}
						}
					}
				}
			}
		}
		else if (theOperator.toString().equalsIgnoreCase("or")) {
			// then add all the elements from the two lists to result.
			if (theLeftList != null) {
				for (RowOfTable row : theLeftList) {
					result.add(row);					
				}
			}
			if (theRightList != null) {
				for (RowOfTable row : theRightList) {
					result.add(row);					
				}
			}
		}
		return result;
	}
	
	private LinkedHashSet<RowOfTable> getLinkedHashSet (String colName, Operator theOperator, String theKey) {
		LinkedHashSet<RowOfTable> result = new LinkedHashSet<RowOfTable>();
		// check if that column has a btree attached to it, in which case, 
		BTree btree = btreeDatabase.findBTree(tableName, colName);
		if (btree != null) {
			result = yesBtree(btree, theOperator, theKey);
		}
		else if (btree == null) {
			result = noBtree(colName, theOperator, theKey);
		}
		return result;
	}
	
	private LinkedHashSet<RowOfTable> yesBtree (BTree btree, Operator theOperator, String theKey) {
		LinkedHashSet<RowOfTable> result = new LinkedHashSet<RowOfTable>();
		// get the list of keys - .getGoodKeys using the BTreeUtility class
		BTreeUtility btreeUtil = new BTreeUtility(btree);
		ArrayList<String> keys = btreeUtil.getGoodKeys(theOperator, theKey);
		// iterate over those keys 
		if (keys != null) {
			for (String key : keys) {
				// get the rows
				ArrayList rows = btree.get(key);
				if (rows != null) {
					// add those rows to result
					for (Object row : rows) {
						if (row instanceof RowOfTable) {
							RowOfTable MickeyMouse = (RowOfTable) row;
							result.add(MickeyMouse);
						}
					}
				}
			}
		}
		return result;
	}
	
	private LinkedHashSet<RowOfTable> noBtree (String colName, Operator theOperator, String theKey) {
		LinkedHashSet<RowOfTable> result = new LinkedHashSet<RowOfTable>();
		// get index of the column
		Integer colIndex = null;
		try {
			colIndex = getColIndex(colName);
		} 
		// if the column does not exist, then throw a runtime exception
		catch (RuntimeException e) {
			throw e;
		} 
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("test2");
			try {
				throw e;
			} catch (Exception e1) {
				e1.printStackTrace();
				System.out.println("test3");
			}
		}
		
		// iterate over the rows of the table
		for (int row = 1; row < theTable.size(); row++) {
			// get the value of the column of the row
			Object value = theTable.get(row).getElement(colIndex);
			if (value == null) {
				value = "NULL";
			}
			if (value instanceof String) {
				String strVal = (String) value;
				// call compareStrings() on them to see if the row is a good row
				Boolean isGood = compareStrings(strVal, theOperator, theKey);
				// if the row is a good row, then add it to result
				if (isGood) {
					result.add(theTable.get(row));
				}
			}
		
		}
		
		return result;
	}
	
	private Integer getColIndex (String givenColumnName) throws Exception {
		RowOfTable firstRow = theTable.get(0);
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
	
	
	// called from getLinkedHashSet(String colName)
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
			if (theLeft.equals("NULL") || theRight.equals("NULL")) {
				break;
			}
			result = (Double.valueOf(theLeft) < Double.valueOf(theRight));
			break;
		case "<=": // less than or equals
			if (theLeft.equals("NULL") || theRight.equals("NULL")) {
				break;
			}
			result = (Double.valueOf(theLeft) <= Double.valueOf(theRight));
			break;
		case ">": // greater than
			if (theLeft.equals("NULL") || theRight.equals("NULL")) {
				break;
			}
			result = (Double.valueOf(theLeft) > Double.valueOf(theRight));
			break;
		case ">=": // greater than or equals
			if (theLeft.equals("NULL") || theRight.equals("NULL")) {
				break;
			}
			result = (Double.valueOf(theLeft) >= Double.valueOf(theRight));
			break;
			// need to put in the other cases
		default:
			break;
		}
		return result;
	}
}
	