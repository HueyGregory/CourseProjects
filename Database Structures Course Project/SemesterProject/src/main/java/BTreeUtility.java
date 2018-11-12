import java.util.ArrayList;
import java.util.LinkedHashSet;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition.Operator;

public class BTreeUtility <K extends  Comparable <K>> {
	private BTree btree;
	private BTreeDatabase btreeDatabase;
	private Database theDatabase;
	private String tableName;
	private ArrayList<K> allKeys;
	private int height;
	private Node root;
	
	public BTreeUtility(BTree givenBtree) {
		height = givenBtree.getHeight();
		root = givenBtree.getRoot();
		tableName = givenBtree.getTableName();
		btree = givenBtree;
		btreeDatabase = new BTreeDatabase();
		theDatabase = new Database();
	}
	
	// somewhere else
	public LinkedHashSet<RowOfTable> get(Condition whereCondition) {
		// returns a list of all rows which fit the condition
		BTreeInCondition btreeInCond = new BTreeInCondition(tableName);
		Object listObj = btreeInCond.getListRows(whereCondition);
		if (listObj == null) {
			return null;
		}
		
		LinkedHashSet<RowOfTable> getRows = null;
		if (listObj instanceof LinkedHashSet) {
			getRows = (LinkedHashSet<RowOfTable>) listObj;
		}
		
		return getRows;
		
//		BTreeWhereCondition btreeCond = new BTreeWhereCondition(tableName);
//		ArrayList<RowOfTable> listRows = (ArrayList<RowOfTable>) btreeCond.getList(whereCondition);
//		if (listRows == null) {
//			return null;
//		}
////			ArrayList<RowOfTable> copyRows = new ArrayList<RowOfTable>(listRows.size());
////			// make a copy of all the rows, so that when the rows are removed from the tree, they won't be fully deleted.
////			for (RowOfTable row : listRows) {
////				RowOfTable rowCopy = new RowOfTable();
////				for (int index = 0; index < row.size(); index++) {
////					rowCopy.add(index, row.getElement(index));
////				}
////				copyRows.add(rowCopy);
////			}
////			deleteFromTree(listRows);
//		return listRows;
	}

	// somewhere else
	public void deleteFromTree (ArrayList<RowOfTable> rows) {
	// get the list of rows that conform to all the conditions
	// iterate over the list - for each row
		for (int i = 0; i < rows.size(); i++) {
			RowOfTable row = rows.get(i);
		//for (RowOfTable row : rows) {
			// iterate over the row - for each column
			for (int index = 0; index < row.size(); index++) {
				// if that column is not null
				if (row.getElement(index) != null) {
					String keyStr = null;
					if (row.getElement(index) instanceof String) {
						keyStr = (String) row.getElement(index);
					}
					String btreeStr = null;
					RowOfTable firstRow = theDatabase.findTable(tableName).get(0);
					if (firstRow.getElement(index) instanceof ColumnDescription) {
						ColumnDescription currElement = (ColumnDescription) firstRow.getElement(index);
						btreeStr = currElement.getColumnName();
					}
					else if (firstRow.getElement(index) instanceof PrimaryKey) {
						PrimaryKey currElement = (PrimaryKey) firstRow.getElement(index);
						btreeStr = currElement.getColumnName();
					}
					// call deleteRow in btree class while giving the value of that index in the row as the key and the row as the givenRow
					deleteRow(keyStr, btreeStr, row);
				}
			}
		}
	}
	
	public void addToTree (ArrayList<RowOfTable> rows) {
		// get the list of rows that conform to all the conditions
		// iterate over the list - for each row
		for (RowOfTable row : rows) {
			// iterate over the row - for each column
			for (int index = 0; index < row.size(); index++) {
				// if that column is not null
				if (row.getElement(index) != null) {
					K key = null;
					String keyStr = null;
					if (row.getElement(index) instanceof String) {
						key = (K) row.getElement(index);
						keyStr = (String) row.getElement(index);
					}
					String btreeStr = null;
					RowOfTable firstRow = theDatabase.findTable(tableName).get(0);
					if (firstRow.getElement(index) instanceof ColumnDescription) {
						ColumnDescription currElement = (ColumnDescription) firstRow.getElement(index);
						btreeStr = currElement.getColumnName();
					}
					else if (firstRow.getElement(index) instanceof PrimaryKey) {
						PrimaryKey currElement = (PrimaryKey) firstRow.getElement(index);
						btreeStr = currElement.getColumnName();
					}
					// .put the row into the btree.
					BTree btree = btreeDatabase.findBTree(tableName, btreeStr);
					if (btree != null) {
						btree.put(key, row);
					}
				}
			}
		}
	}
	
	public void deleteRow(String key, String btreeColName, RowOfTable givenRow) {
		// .get the arraylist with the given key
		//boolean successful = false;
		try {
			BTree tempBtree = btreeDatabase.findBTree(tableName, btreeColName);
			ArrayList<RowOfTable> rows = tempBtree.get(key);
			// compare all the rows in the arraylist until find those that match
			int num = 0;
			for (RowOfTable row : rows) {
				// set those rows that match/equal the givenRow to null.
				if (row == givenRow) {
					rows.remove(num);
					//successful = true;
					break;
				}
				num++;
			}
			//return successful;
		}
		catch (NullPointerException e) {
			return;
		}
	}
	
	// somewhere else
		public ArrayList<K> getGoodKeys(Operator theOperator, String theKey) {
			allKeys = new ArrayList<K>();
			return this.getGoodKeys(this.root, this.height, theOperator, theKey);
		}
		
		private ArrayList<K> getGoodKeys(Node currentNode, int height, Operator theOperator, String theKey) {
			//allKeys = new ArrayList<String>();
			//current node is external (i.e. height == 0)
			if (height == 0) {
				for (int j = 0; j < currentNode.getEntryCount(); j++) {
					//Return all keys
					K key = (K) currentNode.getEntry(j).getKey();
					if (key != null) {
						if (compareStrings(key, theOperator, theKey)) {
							allKeys.add(key);
						}
					}

				}
			}
			//current node is internal (height > 0)
			else {
				for (int j = 0; j < currentNode.getEntryCount(); j++) {
					//if (we are at the last key in this node OR the key we
					//are looking for is less than the next key, i.e. the
					//desired key must be in the subtree below the current entry),
					//then recurse into the current entry’s child
					Object child = currentNode.getEntry(j).getValue();
					if (child instanceof Node) {
						Node nodeChild = (Node) child;
						//recursive(nodeChild, height - 1);
						this.getGoodKeys(nodeChild, height - 1, theOperator, theKey);
					}
				}
			}
			return allKeys;
		}
		
		private Boolean compareStrings (K givenKey, Operator theOperator, String theRight) {
			Boolean result = false;
			// problem with the operator .toString
			String temp = theOperator.toString().toLowerCase();
			String key = null;
			if (givenKey instanceof String ) {
				key = (String) givenKey;
			}
			switch (temp) {
			case "=": // equals
				result = (key.equals(theRight));
				break;
			case "<>": // not equals
				result = !(key.equals(theRight));
				break;
			case "<": // less than
				if (key.equals("NULL") || theRight.equals("NULL")) {
					break;
				}
				result = (Double.valueOf(key) < Double.valueOf(theRight));
				break;
			case "<=": // less than or equals
				if (key.equals("NULL") || theRight.equals("NULL")) {
					break;
				}
				result = (Double.valueOf(key) <= Double.valueOf(theRight));
				break;
			case ">": // greater than
				if (key.equals("NULL") || theRight.equals("NULL")) {
					break;
				}
				result = (Double.valueOf(key) > Double.valueOf(theRight));
				break;
			case ">=": // greater than or equals
				if (key.equals("NULL") || theRight.equals("NULL")) {
					break;
				}
				result = (Double.valueOf(key) >= Double.valueOf(theRight));
				break;
				// need to put in the other cases
			default:
				break;
			}
			return result;
		}
		
	
}
