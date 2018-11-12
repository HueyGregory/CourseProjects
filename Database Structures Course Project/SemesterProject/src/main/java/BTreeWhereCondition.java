import java.util.ArrayList;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnID;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition.Operator;

public class BTreeWhereCondition {
	
	private BTreeDatabase btreeDatabase;

	// return a list containing all the rows that fit the condition
	// have a list which will contain lists, which will hold the lists of rows
	private ArrayList btreeList; // will hold lists of rows
	private ArrayList finalList;
	private String tableName;
	
	public BTreeWhereCondition(String givenTableName) {
		btreeDatabase = new BTreeDatabase();
		tableName = givenTableName;
	}
	
	// iterate through the condition until find a column name
		// see if that column is indexed
			// if it is, then get its btree
				// collect all the lists of rows which meet that condition into a list

	 
	public Object getList (Object given) {
		// NEED TO CHECK: also implement for a case of "1.0 < GPA < 3.0" need to split it up into "1.0 < GPA" && "GPA < 3.0"
		if (given instanceof Condition) {
			Condition innerGiven = ((Condition) given);
			Object theLeft = getList(innerGiven.getLeftOperand());
			Operator theOperator = innerGiven.getOperator();
			Object theRight = getList(innerGiven.getRightOperand());
			
			return calculateOperands(theLeft, theOperator, theRight);
		}
		return given;
	}
	
	// called from getOperands()
	private ArrayList<RowOfTable> calculateOperands(Object theLeft, Operator theOperator, Object theRight) {
		ArrayList<RowOfTable> result = new ArrayList<RowOfTable>();
//		// if both left and right are Booleans, then send to Boolean compute method and store outcome of type Boolean in result
//		if ((theLeft instanceof Boolean) && (theRight instanceof Boolean)) {
//			// cast theLeft and theRight to Booleans
//			Boolean theCurrLeft = (Boolean) theLeft;
//			Boolean theCurrRight = (Boolean) theRight;
//			// go to the Boolean compute method
//			result = compareBooleans(theCurrLeft, theOperator, theCurrRight);
//		}
		// if left is a column ID then send to decipherOneSide and eventually return a string or the value in the slot
		if (theLeft instanceof ColumnID) {
			// cast theLeft to columnID
			ColumnID theCurrLeft = (ColumnID) theLeft;
			if (theRight instanceof String) {
				String strRight = (String) theRight; 
				// go to decipherOneSide method
				theLeft = decipherOneSide(theCurrLeft, theOperator, strRight);
				if (theLeft == null) {
					return null;
				}
			}
		}
		// if right is a columnId then send to decipherOneSide and eventually return a string or the value in the slot
		if (theRight instanceof ColumnID) {
			// cast theRight to columnID
			ColumnID theCurrRight = (ColumnID) theRight;
			// go to decipherOneSide method
			//theRight = decipherOneSide(theCurrRight);
			if (theLeft instanceof String) {
				String strLeft = (String) theLeft; 
				// go to decipherOneSide method
				theRight = decipherOneSide(theCurrRight, theOperator, strLeft);
				//theLeft = decipherOneSide(theCurrRight, theOperator, strLeft);
			}
		}
		// if both left and right are Stings, then compare them and store result in result.
		if((theLeft instanceof ArrayList) && (theRight instanceof ArrayList)) {
			// cast theLeft and theRight to Strings
			ArrayList<RowOfTable> strLeft = (ArrayList<RowOfTable>) theLeft;
			ArrayList<RowOfTable> strRight = (ArrayList<RowOfTable>) theRight;
			// go to String compute method
			//result = compareStrings(theCurrLeft, theOperator, theCurrRight);
			result = compareLists(strLeft, theOperator, strRight);
		}
		
		else if(theLeft instanceof ArrayList) {
			result = (ArrayList<RowOfTable>) theLeft;
		}
		
		else if (theRight instanceof ArrayList) {
			result = (ArrayList<RowOfTable>) theRight;
		}

		return result;
	}
	
	// called from calculateOperands()
	private ArrayList<RowOfTable> compareLists(ArrayList<RowOfTable> listLeft, Operator theOperator, ArrayList<RowOfTable>listRight) {
		// TODO Auto-generated method stub
		ArrayList<RowOfTable> result = new ArrayList();
		// problem with the operator .toString
		String temp = theOperator.toString().toLowerCase();
		// compare each row	
		if (theOperator.toString().equalsIgnoreCase("and")) {
			if(listLeft.isEmpty() || listRight.isEmpty()) {
				return result;
			}	
			for (RowOfTable leftRow : listLeft) {
				Boolean booleanResult = false;			
				for (RowOfTable rightRow : listRight) {
					if (leftRow == rightRow) {
						result.add(leftRow);
						break;
					}
				}
			}
		}
		else if (theOperator.toString().equalsIgnoreCase("or")) {
			if (!listLeft.isEmpty()) {
				for (RowOfTable leftRow : listLeft) {
					result.add(leftRow);
				}
			}
			if(!listRight.isEmpty()) {
				for (RowOfTable rightRow : listRight) {
					result.add(rightRow);
				}
			}
		}
		else {
			System.out.println("Error!");
		}
		return result;
	}

	// decipher the ColumnID to get a list of rows which conform to that condition
	// called from calculateOperands()
	private ArrayList<RowOfTable> decipherOneSide (ColumnID theSide, Operator theOperator, String otherSide) {
		// if theSide is of type ColumnID
		// column name to get to the correct column
		String columnName = theSide.getColumnName();
		ArrayList<RowOfTable> listResult = getRows(columnName, theOperator, otherSide);
		return listResult;
	}
		
	// called from decipherOneSide()
	private ArrayList<RowOfTable> getRows (String givenColumnName, Operator theOperator, String otherSide) {
		//String stringResult = null;
		ArrayList<RowOfTable> goodRows = new ArrayList();
	// first need to see if the column has an indexed BTree associated with it,
		// so call .findBTree()
		BTree theBtree = btreeDatabase.findBTree(tableName, givenColumnName);
		if (theBtree != null) {
			// collect all the lists of rows which meet that condition into a list\
			// first get all the keys
			BTreeUtility btreeUtil = new BTreeUtility(theBtree);
			ArrayList<String> allKeys = btreeUtil.getGoodKeys(theOperator, otherSide);
			//for (int i = 0; i < allKeys.size(); i++) {
			for (String key : allKeys) {
				// add the keys which conform to the condition to a second Arraylist by using the compareStrings()
//				if (compareStrings(key, theOperator, otherSide)) {
					for (Object row : theBtree.get(key)) {
						if (row instanceof RowOfTable) {
							RowOfTable redefinedRow = (RowOfTable) row;
							goodRows.add(redefinedRow); // get the list of rows from the Btree and put the rows into goodRows
						}
					}
//				}
			}
			// SOMETHING WRONG WITH THIS BECAUSE IF THE OPERATOR IS < OR > THAN WON'T GET ALL THE RIGHT THINGS
//			goodRows = theBtree.get(otherSide);
			return goodRows;
			
			// iterate over the second arraylist
		}
		// MIGHT NEED TO PUT IN IF THE BTREE IS NULL, THEN JUST GET THE ROWS FROM THE TABLE
		// return the arraylist
		return null;
	}
	
		
	// called from calculateOperands()
	private Boolean compareBooleans (Boolean theLeft, Operator theOperator, Boolean theRight) {
		Boolean result = false;
		if (theOperator.toString().equalsIgnoreCase("and")) {
			result = (theLeft && theRight);
		}
		else if (theOperator.toString().equalsIgnoreCase("or")) {
			result = (theLeft || theRight);
		}
		else {
			System.out.println("Error!");
		}
		return result;
	}
		
	// called from (maybe from getRows()
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
		
		
		
	// compare the lists in allLists to each other using a similar copy to the one in DecipherWhereConditionand 
		// any row which exists in all the btreeLists of allLists should be 
			// added to finalList
			// and deleted from the BTree
	// return finalList;
	

}
