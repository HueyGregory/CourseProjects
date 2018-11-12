import java.util.ArrayList;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;

public class Database {
	// The main Database will be static
	// use an Arraylist to hold a list of all table objects.
	private static ArrayList<Table> theDatabase = new ArrayList<Table>();
	// consists of Table classes which is a doubly linkedList holding rowOfTables (doubly linkedList of a RowOfTable object)
	// method:
		// find table(s) with given name.
	//public Database () { }
	public Table findTable (String tableName) {
		for (Table table: theDatabase) {
			if (table.getName().equals(tableName)) {
				return table;
			}
		}
		return null;
	}
	
	public void addTable (Table table) {
		theDatabase.add(table);
	}

	public int size() {
		return theDatabase.size();
	}
	
	public void reset() {
		theDatabase = null;
		theDatabase = new ArrayList<Table>();
	}
	
	public void printDatabase () {
		for (int table = 0; table < theDatabase.size(); table++) { // for each table
			System.out.println("\n\t" + theDatabase.get(table).getName());
			for (int i = 0; i < theDatabase.get(table).size(); i++) { // for each row
				System.out.print("|");
				for (int num = 0; num < theDatabase.get(table).get(i).size(); num++) {// for each slot
					Object currSlot = theDatabase.get(table).get(i).getElement(num);
					if (currSlot == null) {
						System.out.print("null");
					}
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
	}
}
