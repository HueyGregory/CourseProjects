import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnValuePair;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.UpdateQuery;

public class MainOfUpdate {
	// fields
	private String tableName; // the given table name
	private Condition whereCondition; // the given condition
	private ColumnValuePair[] colVal; // the given column-value pairs
	private Table theTable; // the table retrieved from the database using tableName
	private Database theDatabase; // access the database to retrieve the table
	private ResultSet resultSet; // the resultSet, where return true or false depending on if the query referred to any non-existent columns or tables
	private RowOfTable firstRow; 	// first row of the table which holds all the column descriptions
	private BTreeDatabase bTreeDatabase;
	
	public MainOfUpdate(UpdateQuery obj) throws Exception {
		resultSet = new ResultSet();
		theDatabase = new Database();
		bTreeDatabase = new BTreeDatabase();
		
		getFields(obj);
		mainPart();
		
	}
	
	private void getFields(UpdateQuery obj) {
		// get the info and place it into global variables to pass on to the next stage of the program.
			// use the accessor methods in each class to get the fields of the object of the type select.
			// first slot of array = getTableName() - return String
				// @return the name of the table to update
		tableName = obj.getTableName();
			// second slot of array - getWhereCondition() - return of type Condition
				// @return the "where" condition which dictates which rows will be updated
		whereCondition = obj.getWhereCondition();
			// third slot of array = getColumnValuePairs() - return array of type ColumnValuePair[] - ColumnValuePair(ColumnID col, String value)
				// @return the column-value pairs in the order in which they were listed in the query
		colVal = obj.getColumnValuePairs();
	}

	
	
	private void mainPart () throws Exception {
	// first find the appropriate table(s) by iterating over the database until find the first table
		theTable = theDatabase.findTable(tableName);
		if (theTable == null) {
			// means that the requested table does not exist, so return a resultSet of null
			resultSet.setTableBoolean(false);
			return;
		}
		// need the first row in order to see where each column description, like SSNUM and the primary key, is.
		firstRow = theTable.get(0);
		if (firstRow.isEmpty()) {
			// MIGHT NEED TO DO: return false resultSet;
			return;
		}
		
		// if the colVal is null, then nothing to update, but since the query did not try to refer to any non-existent columns or tables, then the resultSet will not be set to false here
		if (colVal == null) { 
			return;
		}
		Integer[] indexArray = fillIndexArray();
		// if the column name == a column name in the btree database, which means that that column has been indexed, so put that row into the btree
		for(int index = 0; index < indexArray.length; index++) {
			//LinkedHashSet<RowOfTable> rows = new LinkedHashSet<RowOfTable>();
			ArrayList<RowOfTable> rows = new ArrayList<RowOfTable>();
			if (whereCondition != null) {
				BTree btree = bTreeDatabase.findBTree(tableName, colVal[index].getColumnID().getColumnName());
				if (btree != null) {
					yesBtree(btree, indexArray, index);
					
				}
				// else if the columnIds of the whereConditions have btrees, then get the rows which fulfill those conditions
				else {
					noBtree(indexArray);
				}
			}
			else {
				checkCond(indexArray);
			}
		}
		//checkCond(indexArray);
		
	}
	private void noBtree(Integer[] indexArray) throws Exception {
		BTreeInCondition btreeInCond = new BTreeInCondition(tableName);
		Object listObj = btreeInCond.getListRows(whereCondition);
		if (listObj == null) {
			checkCond(indexArray);
			return;
		}
		
		LinkedHashSet<RowOfTable> rowsSet = null;
		if (listObj instanceof LinkedHashSet) {
			rowsSet = (LinkedHashSet<RowOfTable>) listObj;
		}
		if (rowsSet == null || rowsSet.isEmpty()) {
			checkCond(indexArray);
			return;
		}
		//LinkedHashSet<RowOfTable> rows = new LinkedHashSet<RowOfTable>();
		for (RowOfTable row : rowsSet) {
			rowUpdate(indexArray, row);
		}
	}

	private void yesBtree(BTree btree, Integer[] indexArray, int index) throws Exception {
		ArrayList<RowOfTable> rows = new ArrayList<RowOfTable>();
		BTreeUtility btreeUtil = new BTreeUtility(btree);
		LinkedHashSet<RowOfTable> getRows = btreeUtil.get(whereCondition);
		if (getRows != null) {
			for (RowOfTable row : getRows) {
				rows.add(row);
			}
			//for (int num = 0; num < getRows.size(); num++) {
			//	rows.add(getRows.get(num));
			//}
		}
		if (rows.isEmpty()) {
			noBtree(indexArray);
		}
	
		// remove the rows in rows from the btree
		if (btree.getTableName().equals(tableName)) {
			btreeUtil.deleteFromTree(rows);
		}
	
		for (RowOfTable row: rows) {
			// make the change
			row.insert(indexArray[index], colVal[index].getValue());
			//perform the update
			rowUpdate(indexArray, row);
		}
		// insert all the rows back into the tree
		btreeUtil.addToTree(rows);
	}

	private Integer[] fillIndexArray () {
		// Integer[] indexArray = array of type int with size the same size of ColumnValuePair[] which will hold the indexes of each column is in the arraylists,
		// for example, if the first ColumnValuePair is the GPA column and the GPA column is the third column, then
		// store the number 3 in the first slot of this new array, so whenever have to insert the value into that row, then
		// can use the number in the first slot of this new array to quickly access that slot in the arraylist of that row.	
		int lengthOfColVal = colVal.length;
		Integer[] indexArray = new Integer[lengthOfColVal];
		for (int i = 0; i < lengthOfColVal; i++) {
			String colName = colVal[i].getColumnID().getColumnName();
			
			for (int num = 0; num < firstRow.size(); num++) {
				Object rowCol = firstRow.getElement(num);
				// cast the rowCol to either a primary key or columnDescription class to extract the column name from it.
				String tempRowColName = null;
				if (rowCol instanceof ColumnDescription) {
					ColumnDescription tempRowCol = (ColumnDescription) rowCol;
					tempRowColName = tempRowCol.getColumnName();
				}
				if (rowCol instanceof PrimaryKey) {
					PrimaryKey tempRowCol = (PrimaryKey) rowCol;
					tempRowColName = tempRowCol.getColumnName();
				}
				if (colName.equals(tempRowColName)) {
					indexArray[i] = num;
					break;
				}
				// num == .size() - 1, then means that no column exists with that column name, 
				// so the query referred to a non-existant column, so set the resultSet to false.
				if ((num == firstRow.size() - 1) && (indexArray[i] == null)) {
					try {
						resultSet.setTableBoolean(false);
						RuntimeException exception = new RuntimeException();
						throw exception;
					}
					// if the column does not exist, then throw a runtime exception
					catch (RuntimeException e) {
						// TODO Auto-generated catch block
						throw e;
					} 
					catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("test2");
						throw e;
					}
				}
			}
		}
		return indexArray;
	}
	
	private void checkCond (Integer[] indexArray) throws Exception {		
		// iterate through the rows of the table
		int sizeOfTable = theTable.size();
		for (int rowIndex = 1; rowIndex < sizeOfTable; rowIndex++) { // do not start at the first row or index 0 because that is the columnDescription row
			// if whereCondition is null, then it means that all rows should be update except for the first row which continue 
			// the info of each row, so update that row and continue on with the previous row and update that too.
			Boolean theBooleanResult = true;
			RowOfTable row = theTable.get(rowIndex);
			if (whereCondition != null) {
				// do something with the where condition to test something
				DecipherWhereCondition decipherWhereCondition = new DecipherWhereCondition(rowIndex, tableName);
				Object theConditionResult = decipherWhereCondition.getOperands(whereCondition);
				if (theConditionResult instanceof Boolean) {
					theBooleanResult = (Boolean) theConditionResult;
				}
			}
			if (theBooleanResult) {
				//perform the update
				rowUpdate(indexArray, row);
			}
		}
	}
	
	private void rowUpdate (Integer[] indexArray, RowOfTable currRow) throws Exception {
		//RowOfTable currRow = theTable.get(row);
		Boolean flagInsertion = true;
		// iterate through indexArray, which may mean that the program will be jumping around in the row in the table
		for (int i = 0; i < indexArray.length; i++) {
			// if the slot in indexArray is null, this means that that slot does not have a column in the table,
			// so set resultSet to false and continue
			Integer currSlot = indexArray[i];
			if (currSlot == null) {
				flagInsertion = false;
				resultSet.setTableBoolean(false);
				RuntimeException exception = new RuntimeException();
				throw exception;
			}
			if (flagInsertion) {
				ValidColumnDataType checkColType = new ValidColumnDataType(theTable);
				flagInsertion = checkColType.isDataValid(indexArray[i], colVal[i]);
				if (!flagInsertion) {
					resultSet.resetResultSet();
					resultSet.setTableBoolean(false);
					RuntimeException exception = new RuntimeException();
					throw exception;
				}
			}
			
		}
		
		if (flagInsertion) {
			//move on to the next part of the checking and updating.
			trueFlagInsertion(currRow, indexArray);
				
		}
	}
	
	private void trueFlagInsertion (RowOfTable currRow, Integer[] indexArray) {
		boolean flagInsertion = true;
		// make a copy of the old row in case need to undo the change
		RowOfTable copyRow = new RowOfTable();
		for (int colNum = 0; colNum < firstRow.size(); colNum++) {
			copyRow.add(currRow.getElement(colNum));
		}
		
		updateAndBTree(currRow, indexArray);
		
		// check if the row conforms to all the restrictions on the columns, such as NOT NULL, UNIQUE, and DEFAULT
		try {	
			ValidRowInsertion checkRow = new ValidRowInsertion(theTable);
			flagInsertion = checkRow.run(currRow, indexArray);
		}
		catch (Exception e) {
			flagInsertion = false;
		}
		
		if(!flagInsertion) {
			// undo the change and continue;
			for (int colNum = 0; colNum < firstRow.size(); colNum++) {
				currRow.insert(colNum, copyRow.getElement(colNum));
			}
		}
		if (!flagInsertion) {
			RuntimeException exception = new RuntimeException();
			throw exception;
		}
	}
	
	private void updateAndBTree(RowOfTable currRow, Integer[] indexArray) {
		ArrayList<RowOfTable> newRow = new ArrayList<RowOfTable>();
		newRow.add(currRow);
		
		for (int i = 0; i < indexArray.length; i++) {
			String theValue = colVal[i].getValue();			
			// then update that slot as indicated by the index in indexArray with value in the ColumnValuePair[] array
			// SideNote: The index of the pair in the colVal[] should be the same as the index in indexArray
			BTree btree = bTreeDatabase.findBTree(tableName, colVal[i].getColumnID().getColumnName());
			BTreeUtility btreeUtil = null;
			if (btree != null) {
				btreeUtil = new BTreeUtility(btree);
				if (btree.getTableName().equals(tableName)) {
					btreeUtil.deleteFromTree(newRow);
				}
			}
			
			currRow.insert(indexArray[i], theValue);
			
			if (theValue.equals("NULL")) {
				continue; // no need to add it to the btree
			}
			
			if (btreeUtil != null) {
				btreeUtil.addToTree(newRow);
			}
		}
	}
	
	private void undoUpdateAndBTree(RowOfTable currRow, RowOfTable correctRow, Integer[] indexArray) {
		ArrayList<RowOfTable> theRow = new ArrayList<RowOfTable>();
		theRow.add(currRow);
		for (int colValNum = 0; colValNum < colVal.length; colValNum++) {		
			// then update that slot as indicated by the index in indexArray with value in the ColumnValuePair[] array
			// SideNote: The index of the pair in the colVal[] should be the same as the index in indexArray
			BTree btree = bTreeDatabase.findBTree(tableName, colVal[colValNum].getColumnID().getColumnName());
			BTreeUtility btreeUtil = null;
			if (btree != null) {
				btreeUtil = new BTreeUtility(btree);
				if (btree.getTableName().equals(tableName)) {
					btreeUtil.deleteFromTree(theRow);
				}
			}
			Object theValue = correctRow.getElement(indexArray[colValNum]);
			currRow.insert(indexArray[colValNum], theValue);
			
			if (theValue.equals("NULL")) {
				continue; // no need to add it to the btree
			}
			
			if (btreeUtil != null) {
				btreeUtil.addToTree(theRow);
			}
		}
	}
}
