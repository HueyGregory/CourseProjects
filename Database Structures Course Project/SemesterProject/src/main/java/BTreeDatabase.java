import java.util.ArrayList;

public class BTreeDatabase {

	// The main Database will be static
	// use an Arraylist to hold a list of all table objects.
	private static ArrayList<BTree> BTreeDatabase = new ArrayList<BTree>();
	// consists of Table classes which is a doubly linkedList holding rowOfTables (doubly linkedList of a RowOfTable object)
	// method:
		// find table(s) with given name.
	//public Database () { }
	//private static BTree BTreeDatabase = new BTree(1);
	
	public BTree findBTree (String tableName, String indexName) {
		for (BTree btree: BTreeDatabase) {
			//if (btree.getIndexName().equals(indexName)) {
			if (btree.getColumnName().equals(indexName) && btree.getTableName().equals(tableName)) {
				return btree;
			}
		}
		
		return null;
	}
	
	public void resetDatabase () {
		BTreeDatabase = null;
		BTreeDatabase = new ArrayList<BTree>();
	}
	
	public ArrayList<BTree> getAllBTrees(String tableName) {
		ArrayList<BTree> allBTrees = new ArrayList<BTree>();
		for (BTree btree : BTreeDatabase) {
			if (btree.getTableName().equals(tableName)) {
				allBTrees.add(btree);
			}
		}
		return allBTrees;
	}
	
	public void addBTree (BTree btree) {
		BTreeDatabase.add(btree);
	}

	public int size() {
		return BTreeDatabase.size();
	}
	
	public void printBtreeDatabase () {
		for (BTree btree : BTreeDatabase) {
			System.out.print("\nTable Name = " + btree.getTableName());
			System.out.println(";\tColumn Name = " + btree.getColumnName());
			ArrayList<String> keys = btree.getAllKeys();
			if (keys == null) {
				System.out.println("null");
				continue;
			}
			for (String key : keys) {
				System.out.println("\nkey = " + key);
				ArrayList<RowOfTable> rows = btree.get(key);
				if (rows == null) {
					System.out.println("null");
					continue;
				}
				for (RowOfTable row : rows) {
					System.out.print("|");
					for (int i = 0; i < row.size(); i++) {
						//System.out.print("|");
						System.out.print(row.getElement(i));
						System.out.print("|");
					}
					System.out.println();
				}
			}
			System.out.println();
		}
	}

	public void delete(String tableName, String indexName) {
		// TODO Auto-generated method stub
		for (int tree = 0; tree < BTreeDatabase.size(); tree++) {
			BTree btree = BTreeDatabase.get(tree);
			if (btree.getIndexName().equals(indexName) && btree.getTableName().equals(tableName)) {
				BTreeDatabase.remove(tree);
				return;
			}
		}
	}
}
