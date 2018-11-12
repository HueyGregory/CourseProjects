import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;

public class ResultSet {
	// what is being returned or printed out.
	// create a new table
	private static Table resultSet = new Table();
	
	//public ResultSet () {
	//	resultSet = new Table();
	//}
//	1. For a SELECT, a ResultSet containing the values that matched the query
	public Integer size() {
		return resultSet.size();
	}
	
	public void addElement (int row, int column, Object obj) {
		resultSet.addElementToRow(row, column, obj);
	}
	
	public void insertElement (int row, int column, Object obj) {
		resultSet.setElementInRow(row, column, obj);
	}
	
	public void insertColumnName (int row, String name) {
		// shift all the elements up by one
		Object tempHold1 = resultSet.get(row).getElement(0);
		for (int i = 0; i < resultSet.get(row).size() - 1; i++) {
			//tempHold1 = resultSet.get(row).getElement(i);
			Object tempHold2 = resultSet.get(row).getElement(i + 1);
			resultSet.get(row).insert(i + 1, tempHold1);
			tempHold1 = tempHold2;
			if (i == resultSet.get(row).size() - 2) {
				resultSet.get(row).add(i + 1, tempHold1);
			}
		}
		resultSet.setElementInRow(row, 0, name);
	}
	
	public void addRow () {
		resultSet.addRow();
	}
	
	public void addEmptyRow () {
		resultSet.addEmptyRow();
	}
	
	public void deleteLastRow() {
		resultSet.deleteRow(resultSet.size() - 1);
	}
	
	public void addTableValue (Double num) {
		resultSet.get(1).add(num);
	}
	
	public void addTableValue (Integer num) {
		resultSet.get(1).add(num);
	}
	public void addTableValue (String str) {
		resultSet.get(1).add(str);
	}
	
//	2. For CREATE INDEX, Insert, Update, and Delete, a ResultSet that has one column and row,
//	whose value is “true” or “false”, indicating if the query was successful. Success for these queries means
//	that the query did not refer to any non-existent columns or tables.
	
	public void setTableBoolean (Boolean haSucceeded) {
		if (resultSet.get(0).size() >= 1) {
			resultSet.get(0).insert(0, haSucceeded);
		}
		else if (resultSet.get(0).size() < 1){
			resultSet.get(0).add(haSucceeded);
		}
	}

//	3. For CREATE TABLE, return an empty ResultSet with the columns that were just created.
	// add first row to resultSet.
	public void addFirstRow (RowOfTable row) {
		if (row != null) {
			for (int i = 0; i < row.size(); i++) {
				resultSet.addElementToRow(0, i, row.getElement(i));
			}
		}
	}
	
//	3. ResultSet: your result set class must include the names and data types of your columns, as well as the rows of
//	data that matched the query. Keep in mind that if the query only asked for a subset of the columns in a table,
//	then your result set must include only those columns specified in the query
	
// other private methods
	public void printResultSet () {
		for (int i = 0; i < resultSet.size(); i++) { // for each row
			System.out.print("|");
			for (int num = 0; num < resultSet.get(i).size(); num++) { // for each slot
				Object currSlot = resultSet.get(i).getElement(num);
				if (currSlot instanceof String) {
					String castedCurrSlot = (String) currSlot;
					System.out.print(currSlot);
				}
				// if in the first row, like created the table
				if (currSlot instanceof ColumnDescription) {
					ColumnDescription castedCurrSlot = (ColumnDescription) currSlot;
					System.out.print(castedCurrSlot.getColumnName());
				}
				if (currSlot instanceof PrimaryKey) {
					PrimaryKey castedCurrSlot = (PrimaryKey) currSlot;
					System.out.print(castedCurrSlot.getColumnName());
				}
				if (currSlot instanceof Boolean) {
					Boolean castedCurrSlot = (Boolean) currSlot;
					System.out.print(castedCurrSlot);
				}
				if (currSlot instanceof Double) {
					Double castedCurrSlot = (Double) currSlot;
					System.out.print(castedCurrSlot);
				}
				if (currSlot instanceof Integer) {
					Integer castedCurrSlot = (Integer) currSlot;
					System.out.print(castedCurrSlot);
				}
				System.out.print("|");
			}
			System.out.println();
		}
	}
	public void resetResultSet () {
		resultSet = null;
		resultSet = new Table();
	}

	public RowOfTable get(int rowNum) {
		return resultSet.get(rowNum);
	}

}
