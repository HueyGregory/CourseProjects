import java.util.ArrayList;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition.Operator;

// implementation of a BTree
// most probably consisting of multiple Node instances
public class BTree<K extends  Comparable <K>, V> {
	// should be Generic
	
	private int height;
	private Node root;
	private int numOfNodes;
	private int sizeOfNodes;
	private NodeSlot newEntry;
	//private Integer nodeIndex; // by methods findNodeIndex() and put()
	private String indexName;
	private String columnName;
	private String tableName;
	private ArrayList<K> allKeys;
	
	private BTreeDatabase btreeDatabase;
	private Database theDatabase;
	
	public BTree(String givenTableName, int numOfRows, String givenIndexName, String givenColumnName) throws Exception {
		height = 0;
		sizeOfNodes = calcSizeNode(numOfRows);
		root = new Node(sizeOfNodes);
		btreeDatabase = new BTreeDatabase();
		theDatabase = new Database();
		newEntry = new NodeSlot(null, null);
		indexName = givenIndexName;
		columnName = givenColumnName;
		tableName = givenTableName;
		allKeys = new ArrayList<K>();
		//final K sentinel = (K) getLargestString(); // -Double.MAX_VALUE is the lowest Integer, String, Double, and Boolean
		final K sentinel = getSentinel();
		//K sentinel = (K) String.valueOf(-Double.MAX_VALUE);
		this.put(sentinel, null);
	}

	
	// if the column is of type Integer or Decimal, then need to use a different value for sentinel
	private K getSentinel() throws Exception {
		// need to get column type
		K sentinel = null;
		Table theTable = theDatabase.findTable(tableName);
		RowOfTable firstRow = theTable.get(0);
		for (int i = 0; i < firstRow.size(); i++) {
			Object objSlot = firstRow.getElement(i);
			String colType = null;
			if (objSlot instanceof ColumnDescription) {
				ColumnDescription currSlot = (ColumnDescription) objSlot;
				if (currSlot.getColumnName().equals(columnName)) {
					colType = currSlot.getColumnType().toString();
				}
			}
			else if (objSlot instanceof PrimaryKey) {
				PrimaryKey currSlot = (PrimaryKey) objSlot;
				if (currSlot.getColumnName().equals(columnName)) {
					colType = currSlot.getColumnType().toString();
				}
			}
			if (colType != null) {
				try {
					switch (colType) {
					case "INT":
					case "DECIMAL":
						sentinel = (K) String.valueOf(-Double.MAX_VALUE);
						break;
					case "BOOLEAN":
					case "VARCHAR":
						Character char2 = 0;
						String value2 = Character.toString(char2);
						sentinel = (K) value2;
						break;
						default: 
					}
				}
				catch (Exception e) {
					throw e;
				}
				return sentinel;
			}
		}
		// if get here, then must mean that the column does not exist, so throw exception
		Exception e = new Exception();
		throw e;
	}
	
	
	private int calcSizeNode(int numOfRows) {
		return 4;
	}
	
	public String getIndexName() {
		return indexName;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Node getRoot() {
		return root;
	}
	
	public ArrayList<V> get(K key) {
		System.out.println("Using the BTREE");
		return this.get(this.root, key, this.height);
	}
	private ArrayList<V> get(Node currentNode, K key, int height) {
		//Node entries = new Node(currentNode.getNodeSize());
		//entries = currentNode;
		//NodeSlot[] entries = currentNode.getEntireNode();
		
		//current node is external (i.e. height == 0)
		if (height == 0) {
			for (int j = 0; j < currentNode.getEntryCount(); j++) {
				if(key.equals(currentNode.getEntry(j).getKey())) {
					//found desired key. Return its value
					//return (String)entries.getEntry(j).getValue();
					return (ArrayList) currentNode.getEntry(j).getValue();
				}
			}
			//didn't find the key
			return null;
		}
		//current node is internal (height > 0)
		else {
			for (int j = 0; j < currentNode.getEntryCount(); j++) {
				//if (we are at the last key in this node OR the key we
				//are looking for is less than the next key, i.e. the
				//desired key must be in the subtree below the current entry),
				//then recurse into the current entry’s child
				if (j + 1 == currentNode.getEntryCount() || (key.compareTo((K) currentNode.getEntry(j + 1).getKey()) < 0)) {
					return this.get((Node)currentNode.getEntry(j).getValue(), key, height - 1);
				}
			}
			//didn't find the key
			return null;
		}
	}
	
	public ArrayList<K> getAllKeys() {
		allKeys = new ArrayList<K>();
		return this.getAllKeys(this.root, this.height);
	}
	
	private ArrayList<K> getAllKeys(Node currentNode, int height) {
		//allKeys = new ArrayList<String>();
		//current node is external (i.e. height == 0)
		if (height == 0) {
			for (int j = 0; j < currentNode.getEntryCount(); j++) {
				//Return all keys
				allKeys.add((K) currentNode.getEntry(j).getKey());

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
					this.getAllKeys(nodeChild, height - 1);
				}
			}
		}
		return allKeys;
	}
	
	
	public void put(K key, V val) {
		System.out.println("Using the BTREE");
		Node newNode = this.put(this.root, key, val, this.height);
		//this.n++;
		if(newNode == null) {
			return;
		}
		//split the root:
		//Create a new node to be the root.
		//Set the old root to be new root's first entry.
		//Set the node returned from the call to put to be new root's second entry
		Node newRoot = new Node(this.sizeOfNodes);
		newRoot.add(new NodeSlot(this.root.getEntry(0).getKey(), this.root));
		//newRoot.entries[0] = new NodeSlot(this.root.get(0).getKey(), null, this.root);
		//newRoot.add(new NodeSlot(this.root.getEntry(0).getKey(), null));
		//newRoot.entries[1] = new Entry(newNode.entries[0].key, null, newNode);
		newRoot.add(new NodeSlot(newNode.getEntry(0).getKey(), newNode));
		this.root = newRoot;
		//a split at the root always increases the tree height by 1
		this.height++;
	}
	
	
	private Node put(Node currentNode, K key, V val, int height) {
		Integer nodeIndex;
		ArrayList<V> row = new ArrayList();
		row.add(val);
		NodeSlot newEntry = new NodeSlot(key, row);
		//NodeSlot newEntry = new NodeSlot(key, val);
		
		//external node
		if (height == 0) {
			//find index in currentNode’s entry[] to insert new entry
			for (nodeIndex = 0; nodeIndex < currentNode.getEntryCount(); nodeIndex++) {
				if (key.equals(currentNode.getEntry(nodeIndex).getKey())) {
					currentNode.addRow(nodeIndex, val);
					return null;
				}
				if (key.compareTo((K) currentNode.getEntry(nodeIndex).getKey()) < 0) {
					//currentNode.insert(j, newEntry);
					break;
				}
			}
		}
		else {
			//find index in node entry array to insert the new entry
			for (nodeIndex = 0; nodeIndex < currentNode.getEntryCount(); nodeIndex++) {
				//if (we are at the last key in this node OR the key we
				//are looking for is less than the next key, i.e. the
				//desired key must be added to the subtree below the current entry),
				//then do a recursive call to put on the current entry’s child
				if ((nodeIndex + 1 == currentNode.getEntryCount()) || ((key.compareTo((K) currentNode.getEntry(nodeIndex + 1).getKey()) < 0))) {
					//increment j (j++) after the call so that a new entry created by a split
					//will be inserted in the next slot
					Node newNode = this.put((Node) currentNode.getEntry(nodeIndex++).getValue(), key, val, height - 1);
					if (newNode == null) {
						return null;
					}
					//if the call to put returned a node, it means I need to add a new entry to
					//the current node
					newEntry.setKey((String) newNode.getEntry(0).getKey());
					newEntry.setValue(newNode);
					break;
				}
			}	
		}
		
		//management
		//shift entries over one place to make room for new entry
		for (int i = currentNode.getEntryCount(); i > nodeIndex; i--) {
			currentNode.insert(i, currentNode.getEntry(i - 1));
			currentNode.decrementEntryCount(); // cancels out all the incrementing of entryCount everytime insert();
		}
		//add new entry
		currentNode.insert(nodeIndex, newEntry);
		//currentNode.decrementEntryCount();
		if (currentNode.getEntryCount() < this.sizeOfNodes /* BTree.MAX*/) {
			//no structural changes needed in the tree
			//so just return null
			return null;
		}
		else {
			//will have to create new entry in the parent due
			//to the split, so return the new node, which is
			//the node for which the new entry will be created
			return this.split(currentNode);
		}
	}
	
	// split node in half
	private Node split(Node currentNode) {
		Node newNode = new Node(this.sizeOfNodes);
		//by changing currentNode.entryCount, we will treat any value
		//at index higher than the new currentNode.entryCount as if
		//it doesn't exist
		//copy top half of currentNode into newNode
		
		for (int j = 0; j <= this.sizeOfNodes / 2; j++)	{
			//newNode.insert(j, currentNode.getEntry(this.sizeOfNodes / 2 + j));
			if (currentNode.getEntry(this.sizeOfNodes/ 2) == null) {
				break; // means already retrieved all the possible nodes, so break
			}
			newNode.insert(j, currentNode.getEntry(this.sizeOfNodes / 2));
			// delete those nodeSlots from currentNode
			//currentNode.deleteSlot(this.sizeOfNodes / 2 + j);
			currentNode.deleteSlot(this.sizeOfNodes / 2);
		}
		return newNode;
	}


	public void delete (K key) {
		put(key, null);
	}
	
}
