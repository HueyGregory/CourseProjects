import java.util.ArrayList;

public class RowOfTable<E> {
	// single row of table
	// implementation using an arraylist
	private ArrayList row;
	//protected String columnDescription;
	
	// create a new row object
	public RowOfTable() {
		row = new ArrayList();
	}
	
	// create a new row object of a specific size
	public RowOfTable(int length) {
		row = new ArrayList(length);
		for (int i = 0; i < length; i++) {
			row.add(null);
		}
	}
	// get the columnDescription - is it the SSNUM column, or first names, etc.
	
	
	// get the size of the arraylist
	public int size() {
		return row.size();
	}

	// check if the arraylist is empty
	public boolean isEmpty() {
		return row.isEmpty();
	}
	
	// add an element to the end of the arraylist
	//public abstract void add (E element);
	public void add (E element) {
		row.add(element);
	}
	
	public void add (int index, E element) {
		row.add(index, element);
	}
	
	// set an element in the arraylist to null
	// can not delete the element because then mess up the slot index pairing for all elements of a type,
	// for example, a last name will be moved into the first name slot
	public void setSlotToNull (int index) {
		row.set(index, null);
	}
	
	public Object getElement (int index) {
		return row.get(index);
	}
	
	// insert a given element into a given slot
	public void insert (int index, E element) {
		row.set(index, element);
	}
	
	
	
	// public methods
		// insert
			// slot
			// fields
		// set
			// slot
			// fields
}
