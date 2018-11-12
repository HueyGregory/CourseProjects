import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;

// make sure the input into the column conforms to the column's type
public class ValidRowInsertion {
	private Table theTable;
	private boolean isUnique;
	private boolean hasDefault;
	private boolean isNotNull;
	private String defaultVal;
	private RowOfTable theRow;
	private int colIndex;	// colIndex == index of column currently dealing with in run()
	private String slotStr; // slotStr == the value of the slot that has just been inserted 
	private RowOfTable firstRow;

	public ValidRowInsertion (Table givenTable) {
		theTable = givenTable;
		
		
	}
	
	public Boolean run(RowOfTable givenRow, Integer[] indexArray) throws Exception {
		theRow = givenRow;
		firstRow = theTable.get(0);
		// iterate over first row of table
		//for (int index = 0; index < firstRow.size(); index++) {
		for (int i = 0; i < indexArray.length; i++) {
			int index = indexArray[i];
			colIndex = index;
			getFields(index);
			
			// cast the object in the colIndex of theRow to a String
			Object slotObj = theRow.getElement(colIndex);
			if(slotObj instanceof String) {
				slotStr = (String) slotObj;
			}
			
			if (slotObj != null) {
				if (slotStr.equals("NULL")) {
					slotStr = null;
					theRow.insert(colIndex, slotStr);
					if (isNotNull) {
						dealNotNull();
					}
					else {
						//theRow.insert(colIndex, slotStr);
					}
					continue;
				}
			}
			
			// if a column has been set to yes unique
			if (isUnique) {
				// call the unique method
				dealUnique();
			}
				
			// if a column has a default,
			if(hasDefault) {
				// call the default method
				dealDefault();
			}
			
			// if a column has not null,
			if(isNotNull) {
				// call the not-null method
				try {
					dealNotNull();
				} 
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;	
		
	}
	
	private void getFields(int index) {
		Object theSlot = firstRow.getElement(index); // place the slot indicated by the index into a local variable
		if (theSlot instanceof ColumnDescription) {
			ColumnDescription currElement = (ColumnDescription) theSlot;
			getFieldsColDescrip(currElement);
		}
		else if (theSlot instanceof PrimaryKey) {
			PrimaryKey currElement = (PrimaryKey) theSlot;
			getFieldsPrimKey(currElement);
		}
		else {
			// error!
		}
	}
	
	private void getFieldsColDescrip(ColumnDescription currElement) {
		isUnique = currElement.isUnique();
		hasDefault = currElement.getHasDefault();
		if (hasDefault) {
			defaultVal = currElement.getDefaultValue();
		}
		isNotNull = currElement.isNotNull();
	}
	
	private void getFieldsPrimKey(PrimaryKey currElement) {
		isUnique = currElement.isUnique();
		hasDefault = currElement.getHasDefault();
		if (hasDefault) {
			defaultVal = currElement.getDefaultValue();
		}
		isNotNull = currElement.isNotNull();
	}
	
	// if a column has been set to yes unique
	private void dealUnique () throws Exception {
		// iterate over the slots in all the rows with that column index
		for (int row = 1; row < theTable.size() - 1; row++) {
			// cast the object in that slot to a string
			Object tempObj = theTable.get(row).getElement(colIndex);
			if(tempObj == null) {
				if (slotStr == null) {
					Exception exception = new Exception();
					throw exception;
				}
				continue;	// no need to compare and can move on to the next row
			}
			if(slotStr == null) {
				if (tempObj == null) {
					Exception exception = new Exception();
					throw exception;
				}
				continue;	// no need to compare and can move on to the next row
			}
			String tempStr = "";
			if(tempObj instanceof String) {
				tempStr = (String) tempObj;
				// if one of those slots == slotVal
				if (slotStr.equals(tempStr) && (row != theTable.size() - 1)) {
					// need to change that slotStr to null and let the next if statement take care of if that column has a default or not-null
					slotStr = null;
					return; // return because nothing more to check
				}
			}
				
		}
		
		
	}
	
	// if a column has a default,
	private void dealDefault() {
		if (theRow.getElement(colIndex) == null)	{
			// go to the index of that column in theRow
			// check that the slot is not null - if the slot is null
			// insert the default into that slot
			theRow.insert(colIndex, defaultVal);
		}
			
	}
	
	// if a column has not null,
	private void dealNotNull() throws Exception {
		if (theRow.getElement(colIndex) == null)	{
			// go to the index of that column in the row
			// check that the slot is not null - if the slot is null
			if(!hasDefault) {
				Exception exception = new Exception();
				throw exception;
			}
			theRow.insert(colIndex, defaultVal);
		}
	}
	
}
