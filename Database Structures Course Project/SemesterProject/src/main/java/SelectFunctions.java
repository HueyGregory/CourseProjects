import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.math.BigDecimal;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription.DataType;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery.FunctionInstance;

public class SelectFunctions {

	//private Database theDatabase;
	private Table theTable;
	private Table origTable;
	//private ArrayList<FunctionInstance> theFunctions;
	//private String theColumnName;
	private Integer colIndex;
	private String dataType;	// holds the datatype of the specific column so that all the values in the slots in the column may be converted
	private boolean distinct;
	private ResultSet resultSet;
	//private String functionName;
	
	public SelectFunctions(ArrayList<FunctionInstance> givenFunctions, Table givenTable) throws Exception {
		//theDatabase = new Database();
		origTable = givenTable;
		resultSet = new ResultSet();
		// since the resultSet will only be returning one thing, like average or sum, so reset the resultSet
		resultSet.resetResultSet();
		//theFunctions = givenFunctions;
		getFields(givenFunctions);
	}
	
	private void getFields(ArrayList<FunctionInstance> givenFunctions) throws Exception {
		// need to accomodate multiple functions
		for (int i = 0; i < givenFunctions.size(); i++) {
			// reset to the original table, which is not changing if distinct is true
			theTable = origTable;
			FunctionInstance function = givenFunctions.get(i);
			//FunctionInstance function = givenFunctions.get(0);
			String theColumnName = function.column.getColumnName();
			// need to create a local variable to hold the col name and type for the first row of the resultSet
			String colNameAndType = theColumnName;
			colIndex = findIndex(theTable, theColumnName);
			if (colIndex == null) {
				// error - means we have the wrong column
				Exception e = new Exception();
				throw e;
			}
			Object tempObject = theTable.get(0).getElement(colIndex);
			if(tempObject instanceof ColumnDescription) {
				ColumnDescription tempElement = (ColumnDescription) tempObject;
				DataType tempDataType = tempElement.getColumnType();
				if (tempDataType != null) {
					dataType = tempDataType.toString();
				}
			}
			else if (tempObject instanceof PrimaryKey) {
				PrimaryKey tempElement = (PrimaryKey) tempObject;
				DataType tempDataType = tempElement.getColumnType();
				if (tempDataType != null) {
					dataType = tempDataType.toString();
				}
			}
			colNameAndType += " " + dataType;
			if (i == 0) {
				resultSet.addEmptyRow();
			}
			resultSet.addElement(0, i, colNameAndType);
			distinct = function.isDistinct;
			String functionName = function.function.toString();
			mainPart(functionName);
		}
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
	
	private void mainPart(String functionName) {
		// find which function method to proceed to by using a switch statement.
		switch (functionName) {
		case "AVG":
			average();
			break;
		case "COUNT":
			count();
			break;
		case "MAX":
			maxValue();
			break;
		case "MIN":
			minValue();
			break;
		case "SUM":
			sum();
			break;
		}
			
	}
		// (sixth row - ) functions - each one will probably be its own method.
			//  NOTE: SUM does not apply to VARCHAR or Boolean columns. MIN and MAX do not apply to Boolean columns.
			// average of all values in a column
	private void average () {
		double sum = 0; // need to be conscious of decimal values
		int num = 0; // count the number of numbers added to sum 
		RuntimeException exception = new RuntimeException();
		if (dataType.equals("BOOLEAN") || dataType.equals("VARCHAR")) {
			throw exception;
		}
		checkDistinct();
		// iterate through the rows of the table
		for (int i = 1; i < theTable.size(); i++) {
			// get the slot from the ith row and the colIndex column
			Object tempObject = theTable.get(i).getElement(colIndex);
			if(tempObject == null) {
				continue;
			}
			if (tempObject instanceof String) {
				String tempString = (String) tempObject;
				// now convert the tempString to either an Int, decimal, varChar (String), or boolean based on global variable "dataType" to start the calculations.
				switch (dataType) {
				case "INT":
					// convert the value to whatever type it really is.
					sum += Integer.valueOf(tempString);
					break;
				case "DECIMAL":
					sum += Double.parseDouble(tempString);
					break;
				case "BOOLEAN":
				case "VARCHAR":
					throw exception;
				}
				num++;
			}
			
		}
		resultSet.addTableValue(sum/num);
	}
			
	// number of values in a column
	private void count() {
		checkDistinct();
		resultSet.addTableValue(theTable.size() - 1);
	}		
				
				
	// greatest of all values in a column
	// NOTE: MIN and MAX do not apply to Boolean columns.
	private void maxValue () {
		checkDistinct();
		switch (dataType) {
		case "INT":
			maxValInt();
			break;
		case "DECIMAL":
			maxValDec();
			break;
		case "VARCHAR":
			maxValStr();
			break;
		case "BOOLEAN":
			RuntimeException e = new RuntimeException();
			throw e;
		}
	}
	
	private void maxValInt () {
		Integer greatest = Integer.MIN_VALUE;
		for (int i = 1; i < theTable.size(); i++) {
			// get the slot from the ith row and the colIndex column
			Object tempObject = theTable.get(i).getElement(colIndex);
			if(tempObject == null) {
				continue;
			}
			if (tempObject instanceof String) {
				String tempString = (String) tempObject;	
				// convert the value to whatever type it really is and compare with greatest
				Integer currNum = Integer.valueOf(tempString);
				if (currNum > greatest) {
					greatest = currNum;
				}
			}
		}
		resultSet.addTableValue(greatest);
	}
	private void maxValDec () {
		Double greatest = -Double.MAX_VALUE; // Double.MIN_VALUE is the lowest absolute positive double, so in order to get the lowest double need to flip the sign of the max double value
		for (int i = 1; i < theTable.size(); i++) {
			// get the slot from the ith row and the colIndex column
			Object tempObject = theTable.get(i).getElement(colIndex);
			if(tempObject == null) {
				continue;
			}
			if (tempObject instanceof String) {
				String tempString = (String) tempObject;	
				// convert the value to whatever type it really is and compare with greatest
				Double currNum = Double.valueOf(tempString);
				if (currNum > greatest) {
					greatest = currNum;
				}
			}
		}
		resultSet.addTableValue(greatest);
	}

	private void maxValStr () {
		String greatest = "";
		String tempString = "";
		for (int i = 1; i < theTable.size(); i++) {
			// get the slot from the ith row and the colIndex column
			Object tempObject = theTable.get(i).getElement(colIndex);
			if(tempObject == null) {
				continue;
			}
			if (tempObject instanceof String) {
				tempString = (String) tempObject;	
				// convert the value to whatever type it really is and compare with greatest
				if (tempString.compareTo(greatest) > 0) {
					greatest = tempString;
				}
			}
		}
		resultSet.addTableValue(greatest);
	}
			
			// smallest of all values in a column
	// NOTE: MIN and MAX do not apply to Boolean columns.
		private void minValue () {
			checkDistinct();
			switch (dataType) {
			case "INT":
				minValInt();
				break;
			case "DECIMAL":
				minValDec();
				break;
			case "VARCHAR":
				minValStr();
				break;
			case "BOOLEAN":
				RuntimeException e = new RuntimeException();
				throw e;
			}
		}
		
		private void minValInt () {
			Integer smallest = Integer.MAX_VALUE;
			for (int i = 1; i < theTable.size(); i++) {
				// get the slot from the ith row and the colIndex column
				Object tempObject = theTable.get(i).getElement(colIndex);
				if(tempObject == null) {
					continue;
				}
				if (tempObject instanceof String) {
					String tempString = (String) tempObject;	
					// convert the value to whatever type it really is and compare with greatest
					Integer currNum = Integer.valueOf(tempString);
					if (currNum < smallest) {
						smallest = currNum;
					}
				}
			}
			resultSet.addTableValue(smallest);
		}
		private void minValDec () {
			Double smallest = Double.MAX_VALUE; // Double.MIN_VALUE is the lowest absolute positive double, so in order to get the lowest double need to flip the sign of the max double value
			for (int i = 1; i < theTable.size(); i++) {
				// get the slot from the ith row and the colIndex column
				Object tempObject = theTable.get(i).getElement(colIndex);
				if(tempObject == null) {
					continue;
				}
				if (tempObject instanceof String) {
					String tempString = (String) tempObject;	
					// convert the value to whatever type it really is and compare with greatest
					Double currNum = Double.valueOf(tempString);
					if (currNum < smallest) {
						smallest = currNum;
					}
				}
			}
			resultSet.addTableValue(smallest);
		}

		private void minValStr () {
			String smallest = "";
			String tempString = "";
			for (int i = 1; i < theTable.size(); i++) {
				// get the slot from the ith row and the colIndex column
				Object tempObject = theTable.get(i).getElement(colIndex);
				if(tempObject == null) {
					continue;
				}
				if (tempObject instanceof String) {
					tempString = (String) tempObject;	
					// convert the value to whatever type it really is and compare with greatest
					if ((tempString.compareTo(smallest) < 0) || (smallest.equals(""))) {
						smallest = tempString;
					}
				}
			}
			resultSet.addTableValue(smallest);
		}
				
	// sum of all values in a column
	// NOTE: SUM does not apply to VARCHAR or Boolean columns.
	private void sum () {
		double sum = 0; // need to be conscious of decimal values
		RuntimeException exception = new RuntimeException();
		if (dataType.equals("BOOLEAN") || dataType.equals("VARCHAR")) {
			throw exception;
		}
		checkDistinct();
		// iterate through the rows of the table
		for (int i = 1; i < theTable.size(); i++) {
			// get the slot from the ith row and the colIndex column
			Object tempObject = theTable.get(i).getElement(colIndex);
			if(tempObject == null) {
				continue;
			}
			if (tempObject instanceof String) {
				String tempString = (String) tempObject;
				// now convert the tempString to either an Int, decimal based on global variable "dataType" to start the calculations.
				switch (dataType) {
				case "INT":
					// convert the value to whatever type it really is.
					sum += Integer.valueOf(tempString);
					break;
				case "DECIMAL":
					sum += Double.parseDouble(tempString);
					break;
				case "VARCHAR":
				case "BOOLEAN":
					throw exception;
				}
			}
			
		}
		resultSet.addTableValue(sum);
	}

	private void checkDistinct () {
		if(distinct) {
			// move everything over to a linkedhashset
			LinkedHashSet distinctSet = new LinkedHashSet(); // use a LinkedHashSet to maintain order and no duplicates
			for (int i = 1; i < theTable.size(); i++) {
				// get the slot from the ith row and the colIndex column
				Object tempObject = theTable.get(i).getElement(colIndex);
				if(tempObject == null) {
					continue;
				}
				if (tempObject instanceof String) {
					if (distinct) { // if only counting the distinct numbers
						// need to get the values
						String tempString = (String) tempObject;
						// now convert the tempString to either an Int, decimal, varChar (String), or boolean based on global variable "dataType" to start the calculations.
						switch (dataType) {
						case "INT":
							// convert the value to whatever type it really is.
							distinctSet.add(Integer.valueOf(tempString));
							break;
						case "DECIMAL":
							distinctSet.add(Double.parseDouble(tempString));
							break;
						case "BOOLEAN":
							distinctSet.add(Boolean.parseBoolean(tempString));
							break;
						case "VARCHAR":
							distinctSet.add(tempString);
							break;
						}
					}
				}
				
			}	
			// then move everything from the linkedhashset to the global variable, theTable
			Table newTable = new Table();
			newTable.addFirstRow(theTable.get(0).size());
			//for (int hashSetIndex = 0; hashSetIndex < distinctSet.size(); hashSetIndex++) {
				//newTable.addFirstRow(theTable.get(hashSetIndex + 1).size());
			int tableIndex = 0;
				for(Object element : distinctSet) {
					// convert the element to a string
					tableIndex++;
					newTable.setElementInRow(tableIndex, colIndex, element.toString());
					newTable.addFirstRow(theTable.get(tableIndex).size());
				}	
			newTable.deleteRow(newTable.size() - 1);
			theTable = newTable;
		}
	}
}
	
