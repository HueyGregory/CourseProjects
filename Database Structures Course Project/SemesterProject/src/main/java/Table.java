import java.util.ArrayList;
import java.util.LinkedList;

public class Table <E> {
	// create one row of the Database
	// implementation is of generic 2D RowOfTable
	
	// fields
	// declare doublyLinkedList
	private LinkedList<RowOfTable> theDoublyLinkedList;
	// name of table
	private String tableName;
	// since the table is a doubly linked list, no set size to start off
	public Table () {
		// constructor
		// the table will be constructed as a doubly linked list of arraylists, so 
		/*
		 * [1][5][9][13] - an arraylist
		 * [2][6][10][14] - an arraylist
		 * [3][7][11][15] - an arraylist
		 * [4][8][12][16] - an arraylist
		 * 
		 * [1][2][3][4] - is a doubly linked list in which each slot, ex. [1], is holding an arraylist
		 */
		theDoublyLinkedList = new LinkedList<RowOfTable>();
		RowOfTable mainRow = new RowOfTable(); // special for the first row in order
			// so that all the fields could be held there for the rest of the column
		theDoublyLinkedList.add(mainRow); // will be the mainRow
			// mainRow = general information that will be listed in the table, like first name, last name, etc.
			// mainRow will be an arraylist // easier to go directly to a column once know the index
			// NOTE: the purpose of this first row acting as a "main" row is only by input into the database,
			// but not by other, like ResultSet, uses of this Table class.
	}
	
	// public methods
	// methods may use from the list interface:
		// isEmpty()
		// add (element)
		// add (int index, element)
		// remove (int index)
		// size()
		// get(int index)
			// specific RowOfTable
		// set (int index, element)
			// specific RowOfTable
	// Other methods may need to implement:
		// delete row
		// insert
		// replace - for the update query
			// delete and then insert
	
	public String getTableName() {
		return tableName;
	}
	
	
	// add element to slot in a row
	public void addElementToRow(int row, int index, E element) {
		theDoublyLinkedList.get(row).add(index, element);
	}
	
	// insert element to slot in a row
	public void setElementInRow(int row, int index, E element) {
		theDoublyLinkedList.get(row).insert(index, element);
	}
	
	public void addRow () {
		RowOfTable newRow = new RowOfTable(theDoublyLinkedList.get(0).size());
		theDoublyLinkedList.add(newRow);
	}
	
	public void addFirstRow (int size) {
		RowOfTable newRow = new RowOfTable(size);
		theDoublyLinkedList.add(newRow);
	}
	
	// size of theDoublyLinkedList - i.e. column
	public int size() {
		return theDoublyLinkedList.size();
	}
	
	// size of a given row
	public int sizeOfRow(int row) {
		return theDoublyLinkedList.get(row).size();
	}
	
	// get name of table
	public String getName() {
		return tableName;
	}
	
	// set name of table
	public void setName(String givenTableName) {
		tableName = givenTableName;
	}
	
	// check if the table is empty by checking if the doublyLinkedList is empty
	public boolean isTableEmpty () {
		return theDoublyLinkedList.isEmpty();
	}
	
	// check if the arraylist on a specific row is empty.
	public boolean isRowEmpty (int index) {
		return theDoublyLinkedList.get(index).isEmpty();
	}

	// get a row from the table.
	public RowOfTable get(int row) {
		return theDoublyLinkedList.get(row);
	}
	
	public RowOfTable getLastRow() {
		return theDoublyLinkedList.get(theDoublyLinkedList.size() - 1);
	}
	
	// delete a given row
	public void deleteRow(int row) {
		theDoublyLinkedList.remove(row);
	}
	
	private int largestRowSize () {
		int largestSize = 0;
		for (RowOfTable row : theDoublyLinkedList) {
			if (row.size() > largestSize) {
				largestSize = row.size();
			}
		}
		return largestSize;
	}
	
	public void resetTable () {
		theDoublyLinkedList = null;
		theDoublyLinkedList = new LinkedList<RowOfTable>();
		RowOfTable mainRow = new RowOfTable(); // special for the first row in order
			// so that all the fields could be held there for the rest of the column
		theDoublyLinkedList.add(mainRow); // will be the mainRow
	}

	public void addEmptyRow() {
		RowOfTable newRow = new RowOfTable(0);
		theDoublyLinkedList.add(newRow);
	}
}
