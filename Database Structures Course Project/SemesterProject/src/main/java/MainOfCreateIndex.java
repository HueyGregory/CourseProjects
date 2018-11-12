import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateIndexQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.InsertQuery;

public class MainOfCreateIndex {
	private ResultSet resultSet;
	private Database theDatabase;
	private Table table;
	private BTreeDatabase btreeDatabase;
	
	// info given in the object
	private String tableName;
	private String columnName;
	private String indexName;
	
	public MainOfCreateIndex(CreateIndexQuery obj) throws Exception {
		resultSet = new ResultSet();
		// need to access the database in order to get the table
		theDatabase = new Database();
		btreeDatabase = new BTreeDatabase();
		// first collect all the fields of obj into global variables of the class
		getFields(obj);
		// insert the row
		mainPart();
	}
	
	public MainOfCreateIndex(String givenTableName, String givenColName, String givenIndexName) throws Exception {
		resultSet = new ResultSet();
		// need to access the database in order to get the table
		theDatabase = new Database();
		btreeDatabase = new BTreeDatabase();
		// first collect all the fields of obj into global variables of the class
		tableName = givenTableName;
		columnName = givenColName;
		indexName = givenIndexName;
		table = theDatabase.findTable(tableName);
		mainPart();
	}
	
	private void getFields(CreateIndexQuery obj) {
		tableName = obj.getTableName();
		columnName = obj.getColumnName();
		indexName = obj.getIndexName();
		table = theDatabase.findTable(tableName);
	}
	
	private void mainPart() throws Exception {
		// creates new BTree instance for that column
		try {
			BTree btree = new BTree (tableName, table.size(), indexName, columnName);
			btreeDatabase.addBTree(btree);
			// iterate over the first row of the table to find the column
			int col = getCol();
			// iterate over all the rows with that column's index as the index
			for (int row = 1; row < table.size(); row++) {
				// call BTree.put(value of that slot)
				// the key will be the value from the slot,
				// the val will be the entire row in which that slot is located
				String key = (String) table.get(row).getElement(col);
				if (key == null) {
					continue;
				}
				
				btree.put(key, table.get(row));
			}
		}
		catch (Exception e) {
			btreeDatabase.delete(tableName, indexName);
			throw e;
		}
			
		
	}
	
	private int getCol () {
		RowOfTable firstRow = table.get(0);
		int col;
		for (col = 0; col < firstRow.size(); col++) {			
			Object rowCol = firstRow.getElement(col);
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
			if (columnName.equals(tempRowColName)) {
				break;
			}
			if (col == firstRow.size() - 1) { // means that the column does not exist 
				resultSet.setTableBoolean(false);
			}
		}

		return col;
	}
	
}
