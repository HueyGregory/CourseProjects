import java.util.ArrayList;
import java.util.LinkedHashSet;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnID;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition.Operator;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.DeleteQuery;

public class MainOfDelete<E> { 
	private Database theDatabase;	// static database which holds all the tables
	private String tableName; 	// the name of the table wanted to access
	private Condition whereCondition; 	// the "Where" condition which dictates which rows will be deleted
	private Table theTable; 	// the table desired by the given tableName in the query
	private ArrayList theOpsArraylist;
	
	private BTreeDatabase btreeDatabase;
	
// remove rows (ex. specific students and not specific features) from the database 
	public MainOfDelete(DeleteQuery obj) {
		// need to access the database in order to get the table
		theDatabase = new Database();
		btreeDatabase = new BTreeDatabase();
		// the arraylist which will order the operations and include parentheses when needed
		theOpsArraylist = new ArrayList();
		// first collect all the fields of obj into global variables of the class
		getFields(obj);
		// delete the row
		mainPart();
	}
	private void getFields (DeleteQuery obj) {
	// get the info and place it into global variables to pass on to the next stage of the program.
	// use the accessor methods in each class to get the fields of the object of the type select.
		// first slot of array = getTableName() - returns String
			// @return the tableName
		setTableName(obj.getTableName());
		// second slot of array = getWhereCondition() - returns Condition
			// @return the "WHERE" condition which dictates which rows will be deleted
		whereCondition = obj.getWhereCondition();
		
	}
	
	private void mainPart() {
	// for the main part of deleting the row from the table
		// iterate through the rows of the table
		if(theTable.size() > 0 && whereCondition != null) { // just checking that the columns indicated in the where condition exist, or else will return a resultSet of false
			DecipherWhereCondition decipherWhereCondition = new DecipherWhereCondition(0, tableName);
			decipherWhereCondition.getOperands(whereCondition);
		}
		
		// delete the rows from the btree
		ArrayList<BTree> allBTrees = btreeDatabase.getAllBTrees(tableName);
		//ArrayList<RowOfTable> rowsToDelete = new ArrayList<RowOfTable>();
		if (allBTrees != null) {
			for (BTree btree : allBTrees) {
				BTreeUtility btreeUtil = new BTreeUtility(btree);
				LinkedHashSet<RowOfTable> rowsToDelete = btreeUtil.get(whereCondition);
				if (rowsToDelete != null) {
					// need to first transfer everything over to an arraylist before sending it to deleteFromTree
					ArrayList<RowOfTable> deleteRows = new ArrayList<RowOfTable>(rowsToDelete.size());
					for (RowOfTable row : rowsToDelete) {
						deleteRows.add(row);
					}
					btreeUtil.deleteFromTree(deleteRows);
				}
			}
		}
	
		int sizeOfTable = theTable.size();
		for (int row = sizeOfTable - 1; row > 0; row--) { // do not proceed to the first row or index 0 because that is the columnDescription row
			ArrayList<RowOfTable> theRow = new ArrayList<RowOfTable>(); // create a new arraylist to pass to the btree deleteFromTree() method
			theRow.add(theTable.get(row)); // place that row into the arraylist
			// if whereCondition is null, then it means that all rows should be deleted except for the first row which continue 
			// the info of each row, so delete that row and continue on with the previous row and delete that too.
			if (whereCondition == null) {
				// put in for the btree to also delete the row
				if(allBTrees != null) {
					for (BTree btree : allBTrees) {
						BTreeUtility btreeUtil = new BTreeUtility(btree);
						if (btree.getTableName().equals(tableName)) {
							btreeUtil.deleteFromTree(theRow);
						}
					}
				}
				theTable.deleteRow(row);
				continue;
			}
			// do something with the where condition to test something
			DecipherWhereCondition decipherWhereCondition = new DecipherWhereCondition(row, tableName);
			Object theConditionResult = decipherWhereCondition.getOperands(whereCondition);
			if (theConditionResult instanceof Boolean) {
				Boolean theBooleanResult = (Boolean) theConditionResult;
				if (theBooleanResult) {
					// put in for the btree to also delete the row
					if(allBTrees != null) {
						for (BTree btree : allBTrees) {
							BTreeUtility btreeUtil = new BTreeUtility(btree);
							if (btree.getTableName().equals(tableName)) {
								btreeUtil.deleteFromTree(theRow);
							}
						}
					}
					theTable.deleteRow(row);
				}
			}
		}
	}
	
	private void setTableName(String givenTableName) {
		tableName = givenTableName;
		theTable = theDatabase.findTable(givenTableName); // the table object
	}
	
	
	// return resultSet

}
