import java.util.ArrayList;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateTableQuery;

public class MainOfCreateTable {
	// fields
	private ColumnDescription[] theDescriptions;
	private ColumnDescription thePrimaryKey;
	private String theTableName;
	
	private Database theDatabase;
	private ResultSet resultSet;
	private MainOfCreateIndex createPrimKeyIndex;
	
	public MainOfCreateTable(CreateTableQuery obj) throws Exception {
		resultSet = new ResultSet();
		theDatabase = new Database();
		// call each method to create the table
		getFields(obj);
		mainPart();
		// add the first row to resultSet
		resultSet.addFirstRow(theDatabase.findTable(theTableName).get(0));
	}

	private void getFields (CreateTableQuery obj) throws Exception {
		// get the info and place them into global variables to pass on to the next stage of the program.	
		// use the accessor methods in each class to get the fields of the object.
		// first slot of array = getColumnDescriptions() - return array of type ColumnDescription[]
			// @return descriptions of all the table columns
		theDescriptions = obj.getColumnDescriptions();
		if (theDescriptions == null) {
			// return a resultSet of false
		}
		
		// second slot of array = getPrimaryKeyColumn() - return ColumnDescription 
			// which can be either int, varchar, decimal, and boolean.  
			// if the getPrimaryKeyColumn() == null, then return a resultSet of false
			// @return the description of the column that is the primary key of the table
		thePrimaryKey = obj.getPrimaryKeyColumn();
		if (thePrimaryKey == null) {
			// return a resultSet of false
			Exception e = new Exception();
			throw e;
		}
		
		// third slot of array = getTableName() - returns String
			// @return name of the table to be created
		theTableName = obj.getTableName();
		if (theTableName == null) {
			// return a resultSet of false
			Exception e = new Exception();
			throw e;
		}
	}
	
	private void mainPart () throws Exception {
	// for the main part of creating the table
		// create a table object
		Table theTable = new Table();
		// set the table name
		theTable.setName(theTableName);
		// add that table object to the database
		theDatabase.addTable(theTable);
		// iterate over the first row of the table, which are the descriptions of each column, and was created when the table object was created
		for (int i = 0; i < theDescriptions.length; i++) {
			// the first column of the table should be set to the primary key
			theTable.addElementToRow(0, i, theDescriptions[i]);
			// all the other columns after that should be one of the descriptions from the array of the first slot of the array 
			// if a description is UNIQUE, NOT NULL, or DEFAULT, then need to do something special
			if (theDescriptions[i].equals(thePrimaryKey)) {
				ColumnDescription theElement = (ColumnDescription) theTable.get(0).getElement(i);
				PrimaryKey newElement = fromColumnDescriptionToPrimaryKey (theElement);
				theTable.setElementInRow(0, i, newElement);
				newElement.setPrimaryKey(true);
				// create a BTree for the primary key column
				createPrimKeyIndex = new MainOfCreateIndex(theTableName, newElement.getColumnName(), newElement.getColumnName());
			}
		}
		// add that table object to the database
		//theDatabase.addTable(theTable);
	}
	
	private PrimaryKey fromColumnDescriptionToPrimaryKey (ColumnDescription theElement) {
		PrimaryKey thePrimaryKeyElement = new PrimaryKey();
		thePrimaryKeyElement.setColumnType(theElement.getColumnType());
		thePrimaryKeyElement.setColumnName(theElement.getColumnName());
		thePrimaryKeyElement.setDefaultValue(theElement.getDefaultValue());
		thePrimaryKeyElement.setFractionalLength(theElement.getFractionLength());
		thePrimaryKeyElement.setHasDefault(theElement.getHasDefault());
		thePrimaryKeyElement.setNotNull(theElement.isNotNull());
		thePrimaryKeyElement.setUnique(true);	// the primary key is to always be UNIQUE
		thePrimaryKeyElement.setVarcharLength(theElement.getVarCharLength());
		thePrimaryKeyElement.setWholeNumberLength(theElement.getWholeNumberLength());
		
		return thePrimaryKeyElement;
	}
	
	// return resultSet
	
}
