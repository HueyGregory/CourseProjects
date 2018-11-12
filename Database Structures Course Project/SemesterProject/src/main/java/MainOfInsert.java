import java.math.BigDecimal;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription.DataType;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnID;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnValuePair;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.InsertQuery;

public class MainOfInsert { // insert a new row into a given table
	private String tableName; 	// the name of the table wanted to access
	private ColumnValuePair[] colVal; 	// array with the column-value pair
	private Database theDatabase; 	// static database which holds all the tables
	private BTreeDatabase bTreeDatabase;
	private RowOfTable firstRow; 	// first row of the table which holds all the column descriptions
	private Table theTable; 	// the table desired by the given tableName in the query
	private int indexOfRow; 	// the index of the new row in the table.
	private ResultSet resultSet;
	
	public MainOfInsert(InsertQuery obj) throws Exception {
		resultSet = new ResultSet();
		// need to access the database in order to get the table
		theDatabase = new Database();
		bTreeDatabase = new BTreeDatabase();
		// first collect all the fields of obj into global variables of the class
		getFields(obj);
		// insert the row
		mainPart();
	}
	
	private void getFields (InsertQuery obj) {
		
	//	get the info and place it into global variables to pass on to the next stage of the program.
		// use the accessor methods in each class to get the fields of the object of the type insert.
		// first slot of array = getTableName() - return String
			// @return the table name into which to insert data
		tableName = obj.getTableName();
		// second slot of array = getColumnValuePairs() - return array of type ColumnValuePair[] - ColumnValuePair(ColumnID col, String value)
			// @return the column-value pairs in the order in which they were listed in the INSERT query
		colVal = obj.getColumnValuePairs();
	}
	
	private void mainPart() throws Exception {
		// first find the appropriate table(s) by iterating over the database until find the first table
		theTable = theDatabase.findTable(tableName);
		if (theTable == null) {
			Exception e = new Exception();
			throw e;
		}
		// need the first row in order to see where each column description, like SSNUM and the primary key, is.
		firstRow = theTable.get(0);
		if (firstRow.isEmpty()) {
			Exception e = new Exception();
			throw e;
		}
		// MIGHT NEED TO PUT IN A CHECK TO MAKE SURE THAT ADDING THE ROW TO AFTER THE PREVIOUS FILLED ROW add a new row to the table if the last row is not null or is filled.
		theTable.addRow();
		boolean flagInserted = checkData();


		// NEED TO CHECK THAT THE SLOTS IN THE COLUMNS MARKED AS NOT_NULL, UNIQUE, OR WITH DEFAULT VALUES WILL FULFILL THOSE REQUIREMENTS
		if (flagInserted) {
			ValidRowInsertion validRowInsertion = new ValidRowInsertion(theTable);
			try {
				Integer[] indexArray = new Integer[theTable.getLastRow().size()];
				for (int i = 0; i < theTable.getLastRow().size(); i++) {
					indexArray[i] = i;
				}
				flagInserted = validRowInsertion.run(theTable.getLastRow(), indexArray);
			} catch (Exception e) {
				resultSet.setTableBoolean(false);
				flagInserted = false;
			} 
		}
		// if there was a problem with the data being inserted, 
		// then set the flagInserted to false so as to delete that line from the table
		
		// if nothing has been inserted or error inserting, get rid of the new row
		if (!flagInserted) {
			deleteRow();
		}
		
	}
	
	private Boolean checkData () {
		// iterate over the colVal array and use the firstRow to find which slot the colVal value goes in. 		
		// the first slot of each columnValuePair slot is the columnId, so need to get to that column
		// so iterate over the columns in the table until arrive at that columnId
		boolean flagInserted = false;
		for (int i = 0; i < colVal.length; i++) {
			Integer theIndex = findIndex(colVal[i].getColumnID().getColumnName());
			if (theIndex == null) {
				resultSet.setTableBoolean(false);
				flagInserted = false;
				break;
				//continue;
			}
			// Data Validation: When data is inserted into the table via an INSERT query, you must check that all the data
			// being inserted is the correct data type for the column it is being inserted into.
			ValidColumnDataType checkData = new ValidColumnDataType(theTable);
			if (!(checkData.isDataValid(theIndex, colVal[i]))) {
				resultSet.setTableBoolean(false);
				flagInserted = false;
				break;
				//continue;
			}
			// now insert the pair into its proper place and change the flag for insertion 
			flagInserted = true;
			
			// if the column name == a column name in the btree database, which means that that column has been indexed, so put that row into the btree
			BTree theBtree = bTreeDatabase.findBTree(tableName, colVal[i].getColumnID().getColumnName());
			if (theBtree != null) {
				theBtree.put(colVal[i].getValue(), theTable.getLastRow());
			}
			insertion(theIndex, colVal[i].getValue());	
		}
		return flagInserted;
	}
	
	private void deleteRow() {
		// delete the row from the btree
		RowOfTable theRow = theTable.get(theTable.size() - 1);
		for (int index = 0; index < theRow.size(); index++) {
			Object element = theRow.getElement(index);
			if (element == null) {
				continue;
			}
			String strElement = "";
			if (element instanceof String) {
				strElement = (String) element;
			}
			String currColName = colName(index);
			BTree theBtree = bTreeDatabase.findBTree(tableName, currColName);
			if (theBtree != null) {
				if (theBtree.getTableName().equals(tableName)) {
					BTreeUtility btreeUtil = new BTreeUtility(theBtree);
					btreeUtil.deleteRow(strElement, currColName, theRow);
				}
			}
			
		}
		// delete the new row
		theTable.deleteRow(theTable.size() - 1);
	}
	
	private Integer findIndex (String colValName) {		
		for (int num = 0; num < firstRow.size(); num++) {
			String currColName = colName(num);
			if (currColName.equalsIgnoreCase(colValName)) {
				return num;
			}
		}
		return null;
	}
	
	private String colName (int index) {
		String currColName = "";
		if (firstRow.getElement(index) instanceof ColumnDescription) {
			ColumnDescription currElement = (ColumnDescription) firstRow.getElement(index);
			currColName = currElement.getColumnName();
		}
		else if (firstRow.getElement(index) instanceof PrimaryKey) {
			PrimaryKey currElement = (PrimaryKey) firstRow.getElement(index);
			currColName = currElement.getColumnName();
		}
		else {
			// error!
		}
		return currColName;
	}
	
	private void insertion (int theIndex, String theValue) {
		// using the given theIndex, insert theValue into its appropriate place in the new row
		// first need to access the new row
		RowOfTable theRow = theTable.getLastRow();
//		if (theValue.equals("NULL")) { // means that a null is inserted
//			theValue = null;
//		}
		// then need to insert the value.
		theRow.insert(theIndex, theValue);
	}
	
}
