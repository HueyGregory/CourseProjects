import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnValuePair;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription.DataType;

public class ValidColumnDataType {
	//private static Database theDatabase; 	// static database which holds all the tables
	private Table theTable; 	// the table desired by the given tableName in the query
	private RowOfTable firstRow; 	// first row of the table which holds all the column descriptions
	private Integer fractLength;
	private Integer wholeNumLength;
	private Integer varCharLength;
	private DataType currColType;
	private Object theSlot;

	public ValidColumnDataType(Table givenTable) {
		theTable = givenTable;
		firstRow = theTable.get(0);
		// place column type into local variable
		fractLength = Integer.MAX_VALUE;
		wholeNumLength = Integer.MAX_VALUE;
		varCharLength = Integer.MAX_VALUE;
		DataType currColType = null;
		
	}
		
	public boolean isDataValid (int index, ColumnValuePair givenColVal) {
		
		getFields(index); // get the values of the fields
		if (currColType == null) {
			// do not stick the value into that column and just return false to move on to the next column.
			return false;
		}
		
		String currColName = currColType.toString();
		// place the value of givenColVal into a local variable of type String
		String value = givenColVal.getValue();
		if (value.equals("NULL")) {
			return true;
		}
		return checkData(currColName, value);
	}
	
	
	// get the information about the data in types and size of data in the column from the first row
	private void getFields(int index) {
		theSlot = firstRow.getElement(index); // place the slot indicated by the index into a local variable
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
	
	private void getFieldsColDescrip (ColumnDescription currElement) {
		// place column type into local variable
		currColType = currElement.getColumnType();
		if (currElement.getFractionLength() != 0) {
			fractLength = currElement.getFractionLength();
		}
		if (currElement.getWholeNumberLength() != 0) {
			wholeNumLength = currElement.getWholeNumberLength();
		}
		if (currElement.getVarCharLength() != 0) {
			varCharLength = currElement.getVarCharLength();
		}
	}
	
	private void getFieldsPrimKey (PrimaryKey currElement) {
		// place column type into local variable
		currColType = currElement.getColumnType();
		if (currElement.getFractionLength() != 0) {
			fractLength = currElement.getFractionLength();
		}
		if (currElement.getWholeNumberLength() != 0) {
			wholeNumLength = currElement.getWholeNumberLength();
		}
		if (currElement.getVarCharLength() != 0) {
			varCharLength = currElement.getVarCharLength();
		}
	}
		
	private Boolean checkData(String givenColType, String value) {
		// use switch statement to then get to the location of the method to test the value of the givenObject against the column type
		try {
			switch (givenColType) {
			case "INT":
				// test the value for compliance to valueof Integer
				Integer theNum = Integer.valueOf(value);
				if (theNum.toString().length() > wholeNumLength) {
					return false;
				}
				break;
			case "DECIMAL":
				Double theDouble = Double.valueOf(value);
				String[] splitter = theDouble.toString().split("\\.");
				// splitter[0].length();   // Before Decimal Count
				// splitter[1].length();   // After  Decimal Count
				if ((splitter[0].length() > wholeNumLength) || (splitter[1].length() > fractLength)) {
					return false;
				}
				break;
			case "BOOLEAN":
				Boolean isGood = Boolean.parseBoolean(value);
				if (!isGood && (!(value.equalsIgnoreCase("false")))) {
					Exception e = new Exception();
					throw e;
				}
				break;
			case "VARCHAR":
				if (!(((value.indexOf('\'') == 0) && (value.lastIndexOf('\'') == value.length() - 1)) || ((value.indexOf('"') == 0) && (value.lastIndexOf('"') == value.length() - 1)))) {
					return false;
				}
				
				if (value.length() - 2 > varCharLength) { // subtract the value's length by 2 because the two sides are quotation marks
					return false;
				}
				//return true;
			}
		}
		// use an exception to return false
		catch (Exception e) {
			return false;
		}
		return true;
		
	}
	
}
