import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery.OrderBy;

public class SelectSort {

	private Database theDatabase;
	private OrderBy[] orderArray;
	private boolean asc;
	private boolean dsc;
	private Integer colIndex;

	public SelectSort(OrderBy[] givenOrderArray) {
		theDatabase = new Database();
		orderArray = givenOrderArray;
	}
	
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
		return null;
	}
	
		// (first row -) sort the table in the orders specified in orderArray
	public Table sortTable (Table givenTable) {
		// get the table
		//Table theTable = theDatabase.findTable(givenTableName);
		RowOfTable firstRow = givenTable.get(0);
		// iterate over orderArray - need to sort each column based on the requirements provided in orderArray
		//NEED TO CHANGE TO ACCOMMODATE MULTIPLE ORDERS IN WHICH NEED TO ORDER EACH EQUAL CATEGORY BASED ON ANOTHER ORDER. EX. IF 4 ROWS ARE SENIORS, THEN ORDER THOSE 4 SENIORS BASED ON THEIR GPA
		//for (int i = 0; i < orderArray.length; i++) {
		for (int i = orderArray.length - 1; i >= 0; i--) { // start from the last condition to subsort each category. The first condition sorts the subcategories in general	
			String tempColName = orderArray[i].getColumnID().getColumnName();
			asc = false;
			dsc = false;
			asc = orderArray[i].isAscending();
			dsc = orderArray[i].isDescending();
			colIndex = findIndex(givenTable, tempColName);			
			// sort using mergeSort, which means to break down all the rows of the table into individual components (i.e. individual rows) and then sort using sortMerge
			if (colIndex != null) {
				givenTable = sort(givenTable);
			}
			if (colIndex == null) {
				throwExcept();
			}
		// use the methods listed below
		}
		return givenTable;
	}
	
	private void throwExcept () {
		try {
			ResultSet resultSet = new ResultSet();
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
			throw e;
			//System.out.println("test2");
//			try {
//				throw e;
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//				System.out.println("test3");
//			}
		}
	}
	
	private Table sort (Table givenTable) {
		Table tempTable = new Table();
		tempTable.deleteRow(tempTable.size() - 1); // delete the first row or else will have space management problems later by insertRow()
		sort(givenTable, tempTable, 0, givenTable.size() - 1);
		return givenTable;
	}
	
	// mergesort givenTable using an additional table, tempTable
	private void sort(Table givenTable, Table tempTable, int low, int high) {
		if (high <= low) {
			return; // means all done
		}
		int mid = low + (high - low)/2;
		
		//recursively: reduce sub-arrays to length 1, merge up
		sort(givenTable, tempTable, low, mid);
		sort(givenTable, tempTable, mid + 1, high);
		merge(givenTable, tempTable, low, mid, high);
	}		
		
	// Given two sorted half-Tables givenTable.get(low) to givenTable.get(mid) and 
	// givenTable.get(mid+1) to givenTable.get(high), replace with sorted table givenTable from low to high.
	
	private void merge(Table givenTable, Table tempTable, int low, int mid, int hi) {
		for (int row = low; (row <= hi) && (row < givenTable.size() - 1); row++) { // copy to tempTable
			tempTable = insertRow(tempTable, givenTable, row, row + 1);
		
		}
		//tempTable.deleteRow(tempTable.size() - 1);
		
		// merge back to givenTable
		int left = low;
		int right = mid + 1;
		for (int current = low; current <= hi; current++) {
			if (left > mid) { //left half exhausted
				if ((!tempTable.isTableEmpty()) && (tempTable.size() > right)) {
					 if ((!tempTable.get(right).isEmpty()) && (tempTable.get(right).size() > colIndex)) {
						givenTable = insertRow(givenTable, tempTable, current + 1, right); // copy value from the right
						right++;
					 }
				}
			}
			else if (right > hi) { //right half exhausted
				givenTable = insertRow(givenTable, tempTable, current + 1, left); // copy value from the left
				left++;
			}
			//neither exhausted - copy lower value
			else if ((!tempTable.isTableEmpty()) && (tempTable.size() > right)) {
				 if ((!tempTable.get(right).isEmpty()) && (tempTable.get(right).size() > colIndex)) {
					Object rightObj = tempTable.get(right).getElement(colIndex);
					if (rightObj == null) {
						//right++;
						rightObj = Double.toString(-Double.MAX_VALUE);
					}
					if (rightObj instanceof String) {
						String rightStr = (String) rightObj;
						Object leftObj = tempTable.get(left).getElement(colIndex);
						if (leftObj == null) {
							//left++;
							leftObj = Double.toString(-Double.MAX_VALUE);
						}
						if (leftObj instanceof String) {
							String leftStr = (String) leftObj;
							Integer sum = null;
							try { 
								Double rightDouble = Double.valueOf(rightStr);
								Double leftDouble = Double.valueOf(leftStr);
								if (rightDouble < leftDouble) {
									sum = -1;
								}
								else if (rightDouble >= leftDouble) {
									sum = 1;
								}
							}
							catch (Exception e) {
								sum = rightStr.compareTo(leftStr);
							}
							if (asc) {
								if (sum < 0) {
									givenTable = insertRow(givenTable, tempTable, current + 1, right);
									right++;
									if (!(tempTable.size() > right)) {
										 right++;
									 }
								}
								else {
									givenTable = insertRow(givenTable, tempTable, current + 1, left);
									left++;
								}
							}
							else if (dsc) {
								if (sum > 0) {
									givenTable = insertRow(givenTable, tempTable, current + 1, right);
									right++;
									if (!(tempTable.size() > right)) {
										 right++;
									 }
								}
								else {
									givenTable = insertRow(givenTable, tempTable, current + 1, left);
									left++;
								}
							}
						}
					}
				}
			}
		}
	}

	private Table insertRow (Table newTable, Table originalTable, int rowOfNew, int rowOfOrig) {
		if (rowOfNew == newTable.size()) { // need to put some sort of flag which will send to a setElementInRow instead of addElementToRow
			Integer sizeofRowOrig = originalTable.get(rowOfOrig).size();
			newTable.addFirstRow(sizeofRowOrig);
		} 		
		for (int i = 0; i < originalTable.get(rowOfOrig).size(); i++) {
			//MAYBE SHOULD PUT IN: if (tempTable.get(row).size() == givenTable.get(row).size()) {
			Object element = originalTable.get(rowOfOrig).getElement(i);
			newTable.setElementInRow(rowOfNew, i, element);
		}
		return newTable;
	}
	
	
}
