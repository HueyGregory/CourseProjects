import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnID;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery.FunctionInstance;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery.OrderBy;

public class MainOfSelect {
	private ResultSet resultSet;
	private Database theDatabase;
	private BTreeDatabase btreeDatabase;
	private OrderBy[] orderArray;
	private ColumnID[] columnNames;
	private String[] tableNames;
	private Boolean isDistinct;
	private Condition theWhereCond;
	private ArrayList<FunctionInstance> theFunctions;
	//private LinkedHashSet[] distinctList;
	private LinkedList[] distinctList;
	//private LinkedHashSet distinctList;
	private Integer indexOfDistinctList;
	private int indexInResultSet;
	private String[] stringColumnNames; // the column Names of each of the columnIDs listed in columnNames, or if * then of all the columns in the table
	private String[] stringColumnTypes;// the column types of each of the columns listed in columnNames, or if * then of all the columns in the table
	
	// global variables and fields
		// tempRowOfTables = new rowOfTables object - arraylist of desired tables.
		// tempRowOfColumns = new rowOfTables object - arraylist of desired rows - if want specific columns from each table, combine all those columns together into one place
	public MainOfSelect(SelectQuery obj) throws Exception {
		resultSet = new ResultSet();
		theDatabase = new Database();
		btreeDatabase = new BTreeDatabase();
		indexOfDistinctList = 0;
		// call each method to create the table
		getFields(obj);
		mainPart();
	}

	// called from constructor
	private void getFields(SelectQuery obj) {
		// get the info and place it into global variables to pass on to the next stage of the program.
		// use the accessor methods in each class to get the fields of the object of the type select.
		// OrderBy[] orderArray = getOrderBys() - returns array of type OrderBy
			// @return the column names in the order in which they were listed in the query
		orderArray = obj.getOrderBys();
		// ColumnID[] columnNames = getSelectedColumnNames() - returns array of type ColumnID
			// @return the column names selected by this query, in the order in which they were listed in the query
		columnNames = obj.getSelectedColumnNames();
		// String[] tableNames =  getFromTableNames() - returns array of type String
			// @return the names of the tables to select data from, in the order in which they were listed in the query
		tableNames =  obj.getFromTableNames();
		// Boolean isDistinct = isDistinct() - returns boolean
			// @return indicates if the query included "DISTINCT", i.e. that no values be repeated
		isDistinct = obj.isDistinct();
		// Condition theWhereCond = getWhereCondition() - returns type Condition
			//  @return the "WHERE" condition of this query, if one exists
		theWhereCond = obj.getWhereCondition();
		// ArrayList<FunctionInstance> theFunctions = getFunctions() - returns type ArrayList<FunctionInstance>
			// @return a map that indicates which function is being applied to a given column. If there is no function on a given column, then map.get(columnID) will return null
		theFunctions = obj.getFunctions();
		// now that we have the colunmNames, construct the distinctList array
		distinctList = new LinkedList[columnNames.length];
		for (int index = 0; index < distinctList.length; index++) {
			distinctList[index] = new LinkedList();
		}
		//distinctList = new LinkedHashSet(columnNames.length);
	}
	
	// called from constructor
	private void mainPart() throws Exception {
		// add a row to resultSet due to format issues later
		//resultSet.addRow();
		for (int i = 0; i < tableNames.length; i++) {
			Table table = findTable (tableNames[i]);
			if(table == null) {
				continue; // continue with the next tableName
			}
			
		}
	}
	

	// go through each global variable methodically
		
	// (third row -) find the intended tables
	// called from mainPart()
	private Table findTable (String givenTableName) throws Exception {
		Table table = theDatabase.findTable(givenTableName);
		if(table != null) { // the table exists
			
			// place the column names into another array
			initializeColNames(table);	
			
			// Add column name to beginning of row
			resultSet.addEmptyRow();
			//int slot = 0;
			for (int i = 0; i < stringColumnNames.length; i++) {
				String nameAndType = stringColumnNames[i];
				nameAndType += " " + stringColumnTypes[i];
				resultSet.addElement(0, i, nameAndType);
			}
			
			// IF THE COLUMN IS INDEXED (i.e. has a btree), then use the btree and .get() to get the things
			for (int i = 0; i < stringColumnNames.length; i++) {
				//if (table.)
			}
			
			// move on to next stage of program
			eachRowCondAndSort(givenTableName);
			// if isDistinct == true, then after the columns of all the wanted rows have been added to distinctList
			// iterate through distinctList and transfer them to the resultSet.
			if (isDistinct) {
				distinctToResultSet();
			}
			// if there is a function, then go to the function class
			if (!(theFunctions.isEmpty())) {
				// move everything from the resultSet to a tempTable
				Table tempTable = new Table();
				
				for (int rowNum = 0; rowNum < resultSet.size(); rowNum++) {
					RowOfTable currRow = resultSet.get(rowNum);
					if (currRow == null) {
						continue;
					}
					if (rowNum == 0) { // means it is the first row of the resultSet which contains all the column descriptions
						for (int givenColNum = 0; givenColNum < columnNames.length; givenColNum++) {
							Integer tableColIndex = findIndex(table, columnNames[givenColNum].getColumnName());
							if (tableColIndex == null) {
								// error - means we have the wrong column
								Exception e = new Exception();
								throw e;
							}
							tempTable.addElementToRow(rowNum, givenColNum, table.get(0).getElement(tableColIndex));
						}
						continue;
					}
					tempTable.addEmptyRow();
					for (int colNum = 0; colNum < resultSet.get(rowNum).size(); colNum++) {
						tempTable.addElementToRow(rowNum, colNum, currRow.getElement(colNum));
					}
				}
				//tempTable.deleteRow(tempTable.size() - 1);
				SelectFunctions functions = new SelectFunctions(theFunctions, tempTable);
				
			}
			
		}
		else if (table == null) {
			// return an empty resultSet because tried to access a non-existant table
			Exception e = new Exception();
			throw e;
		}
		return table;
	}
	
	// called from findTable()
	private void distinctToResultSet() {
		for (int index = 0; index < distinctList.length; index++) { // each LinkedHashSet of the disinctList array
			int row = 1;
			for (Object obj : distinctList[index]) { // each slot of the LinkedHashSet
				if (row >= resultSet.size()) {
					resultSet.addEmptyRow();
				}
				resultSet.addElement(row, index, obj);
				row++;
			}
		}
	}

	// called from findTable()
	private void initializeColNames (Table table) {
		// if * was used in place of columns, it means that all columns are selected,
		// so place all column names into stringColumnNames.
		RowOfTable firstRow = table.get(0);
		if (columnNames[0].getColumnName().equals("*")) {
			stringColumnNames = new String[table.get(0).size()];
			stringColumnTypes = new String[table.get(0).size()];
			for (int num = 0; num < table.get(0).size(); num++) {
				if (firstRow.getElement(num) instanceof ColumnDescription) {
					ColumnDescription currElement = (ColumnDescription) firstRow.getElement(num);
					stringColumnNames[num] = currElement.getColumnName();
					stringColumnTypes[num] = currElement.getColumnType().toString();
				}
				else if (firstRow.getElement(num) instanceof PrimaryKey) {
					PrimaryKey currElement = (PrimaryKey) firstRow.getElement(num);
					stringColumnNames[num] = currElement.getColumnName();
					stringColumnTypes[num] = currElement.getColumnType().toString();
				}
			}
		}
		else {
			stringColumnNames = new String[columnNames.length];
			stringColumnTypes = new String[columnNames.length];
			for (int num = 0; num < columnNames.length; num++) {
				stringColumnNames[num] = columnNames[num].getColumnName();
				// get the column type of that column by finding the index
				Integer colIndex = findIndex(table, stringColumnNames[num]);
				// and then using that index to get to that column in the first row
				// and extracting the column type
				if (firstRow.getElement(colIndex) instanceof ColumnDescription) {
					ColumnDescription currElement = (ColumnDescription) firstRow.getElement(colIndex);
					stringColumnTypes[num] = currElement.getColumnType().toString();
				}
				else if (firstRow.getElement(colIndex) instanceof PrimaryKey) {
					PrimaryKey currElement = (PrimaryKey) firstRow.getElement(colIndex);
					stringColumnTypes[num] = currElement.getColumnType().toString();
				}
			}
		}
	}
	
	// (fifth row -) check rows for conformity to the where condition
	// called from findTable()
	private void eachRowCondAndSort (String givenTableName) {
		Table table = theDatabase.findTable(givenTableName);
		if (theWhereCond != null) {
			LinkedHashSet<RowOfTable> rows = getRows (givenTableName);
			if (rows == null || rows.isEmpty()) {
				return;
			}
		// create an exact copy of the main table so that won't sort the main table and cause trouble
			Table tempTable = copyTable(rows, table.get(0));
			if (tempTable == null || tempTable.isTableEmpty()) {
				return;
			}
			SelectSort sort = new SelectSort(orderArray);
			tempTable = sort.sortTable(tempTable);
		
			indexInResultSet = 1;
			for (int i = 1; i < tempTable.size(); i++) {
				dealDistinct(tempTable, i);
			}
		}
		else if (theWhereCond == null) {
			Table tempTable = copyTable(table);
			SelectSort sort = new SelectSort(orderArray);
			tempTable = sort.sortTable(tempTable);
			indexInResultSet = 1;
			for (int rowNum = 1; rowNum < tempTable.size(); rowNum++) { // start at 1 because the first row of the table is the mainRow
				dealDistinct(tempTable, rowNum);
			}
		}
		// delete the last row of resultSet because it is empty.
		resultSet.deleteLastRow();
	}
	
	// called from eachRowCondAndSort()
	private Table copyTable(LinkedHashSet<RowOfTable> rows, RowOfTable firstRow) {
		Table tempTable = new Table();
		// add the first row of the table, which includes the descriptions of all the columns
		tempTable.addEmptyRow();
		for (int colNum = 0; colNum < firstRow.size(); colNum++) {
			tempTable.addElementToRow(0, colNum, firstRow.getElement(colNum));
		}
		int rowNum = 1; // start at 1 because the first row is already occupied
		
		//for (int rowNum = 0; rowNum < rows.size(); rowNum++) {
		for (RowOfTable row : rows) {
			tempTable.addEmptyRow();
			for (int col = 0; col < row.size(); col++) {
				tempTable.addElementToRow(rowNum, col, row.getElement(col));	
			}
			rowNum++;
		}
		tempTable.deleteRow(tempTable.size() - 1);
		
		return tempTable;
	}
	
	// called from eachRowCondAndSort()
	private Table copyTable(Table givenTable) {
		Table tempTable = new Table();
		for (int rowNum = 0; rowNum < givenTable.size(); rowNum++) {
			tempTable.addEmptyRow();
			for (int colNum = 0; colNum < givenTable.get(rowNum).size(); colNum++) {
				tempTable.addElementToRow(rowNum, colNum, givenTable.get(rowNum).getElement(colNum));
			}
		}
		tempTable.deleteRow(tempTable.size() - 1);
		
		return tempTable;
	}
	
	// called from eachRowCondAndSort();
	// get all the rows of the table which conform to the where condition
	private LinkedHashSet<RowOfTable> getRows(String givenTableName) {
		Table table = theDatabase.findTable(givenTableName);
		Object listObj = (new BTreeInCondition (givenTableName)).getListRows(theWhereCond);
		if (listObj == null) {
			return null;
		}
		
		LinkedHashSet<RowOfTable> getRows = null;
		if (listObj instanceof LinkedHashSet) {
			getRows = (LinkedHashSet<RowOfTable>) listObj;
		}
		
		return getRows;
	}

	// (fourth row -) if isDistinct == true, then only give the unique values of the columns, no repeats
	// called from eachRowCondAndSort()
	private void dealDistinct(Table givenTable, int givenRow) {
		// all column placements of a row should be on the same line.
		// Each row should have its own row in resultSet
		if (isDistinct) {
			// place the selected columns in columnNames of each row into a global variable, distinctList
			// the program will later on deal with the transfer from distinctList to resultSet in findTable
			// after finished iterating through the entire table
			// now Iterate over columnNames and find the columns' indexes in the first row of givenTable.
			Integer firstColIndex = findIndex(givenTable, stringColumnNames[0]);
			if (firstColIndex == null) { // meaning that that column does not exist
				RuntimeException e = new RuntimeException();
				throw e;
			}
			Object firstValue = givenTable.get(givenRow).getElement(firstColIndex);
			//if (value != null) {
				//distinctList[index].add(value);
			LinkedList firstCol = distinctList[0];
			//for (int i = 0; i < currCol.length; i++) {
			int rowNum = 0;
			boolean exists = false;
			boolean checkEquality = false;
			for (Object listValue : firstCol) {
				if ((firstValue == null) || (listValue == null)) {
					if ((firstValue == null) && (listValue == null)) {
						checkEquality = true;
					}
				}
				else if (firstValue.equals(listValue)) {
					checkEquality = true;
				}
				if (checkEquality) {
					for (int i = 0; i < distinctList.length; i++) {
						LinkedList currCol = distinctList[i];
						if (currCol == null) {
							continue;
						}
						Object currListValue = currCol.get(rowNum);
						Integer tempColIndex = findIndex(givenTable, stringColumnNames[i]);
						Object currValue = givenTable.get(givenRow).getElement(tempColIndex);
						if (currValue == null) {
							if (currListValue != null) {
								break;
							}
							else if (currListValue == null) {
								continue;
							}
						}
						if (!(currValue.equals(currListValue))) {
							//exists = false; // so this row does not equal the current Row we are checking, so continue on to the next row
							break;
						}
						if (i == distinctList.length - 1) {
							// already has a row, so won't be distinct
							return;
						}
					}
				
				}
				rowNum++;
			}
			// if got up to here, it means that the row does not already exist in distinctList, so add it
			for (int index = 0; (index < distinctList.length) && (index < stringColumnNames.length); index++) {
				Integer colIndex = findIndex(givenTable, stringColumnNames[index]);
				if (colIndex == null) { // meaning that that column does not exist
					continue;
				}
				Object value = givenTable.get(givenRow).getElement(colIndex);
				if (value != null) {
					distinctList[index].add(value);
				}
			}
		}
		else if (!isDistinct) {
			// place the column directly into the resultSet, bypassing needing distinctList
			for (int index = 0; index < stringColumnNames.length; index++) {
				// using that index, copy the value from the index of givenRow of givenTable to distinctList
				Integer theIndex = findIndex(givenTable, stringColumnNames[index]);
				if (theIndex == null) {
					continue;
				}
				Object value = givenTable.get(givenRow).getElement(theIndex);
				//if (value != null) {
					resultSet.addElement(indexInResultSet/*givenRow - 1 */, index, value); // the first row contains the names of the rows selected
				//}
			}
			resultSet.addEmptyRow(); // if after the last row of the table, so this adding line would be extraneous, but will be deleted in checkCondition()
			indexInResultSet++;
		}
	}
	
	// called from dealDistinct()
	private Integer findIndex (Table givenTable, String colsValName) {	
		RowOfTable firstRow = givenTable.get(0);
		for (int num = 0; num < firstRow.size(); num++) {
			String currColName = "";
			if (firstRow.getElement(num) instanceof ColumnDescription) {
				ColumnDescription currElement = (ColumnDescription) firstRow.getElement(num);
				currColName = currElement.getColumnName();
			}
			else if (firstRow.getElement(num) instanceof PrimaryKey) {
				PrimaryKey currElement = (PrimaryKey) firstRow.getElement(num);
				currColName = currElement.getColumnName();
			}
			else {
				// error!
				
			}
			if (currColName.equalsIgnoreCase(colsValName)) {
				return num;
			}
		}
		RuntimeException e = new RuntimeException();
		throw e;
		//return null; // if no column was found with the same name as colsValName
	}
}
