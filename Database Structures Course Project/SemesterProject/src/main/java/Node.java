import java.util.ArrayList;

// a node in the BTree
public class Node<V> {

	// should be Generic
	private int nodeSize;
	private int entryCount;
	private Node root;
	
	// perhaps implementation as an array of NodeSlot objects
	private NodeSlot[] node;
	// each key will have a value paired with it.
	// in Internal node cases, that value will be another node
	// keep track of the number of objects put into the node
	
	public Node(int initialSize) {
		nodeSize = initialSize;
		node = new NodeSlot[nodeSize];
		entryCount = 0;
	}
	
	public NodeSlot[] getEntireNode() {
		return node;
	}
	
	public NodeSlot getEntry(int location) {
		return node[location];
	}
	
	public void add(NodeSlot newEntry) {
		node[entryCount] = newEntry;
//		String givenKey = newEntry.getKey();
//		Object givenValue = newEntry.getValue();
//		node[entryCount].setKey(givenKey);
//		node[entryCount].setValue(givenValue);
		entryCount++;
	}
	
	public void addRow(int slotIndex, V newRow) {
		if (node[slotIndex].getValue() instanceof ArrayList) {
			ArrayList<V> rows = (ArrayList<V>) node[slotIndex].getValue();
			rows.add(newRow);
		}
	}
	
	// size()
	public int getNodeSize() {
		return nodeSize;
	}
	
	public int getEntryCount() {
		return entryCount;
	}

	public void insert(int slotIndex, NodeSlot newEntry) {
		node[slotIndex] = newEntry;
//		String givenKey = newEntry.getKey();
//		Object givenValue = newEntry.getValue();
//		node[slotIndex].setKey(givenKey);
//		node[slotIndex].setValue(givenValue);
		entryCount++;
	}

	// used in split method in BTree
	public void deleteSlot(int i) {
		node[i] = null;
		if (i != entryCount) {
			// need to shift everything above the i index down by one
			for (int num = i; num < this.entryCount; num++) {
				if (num == this.nodeSize - 1) {
					node[num] = null;
					break;
				}
				node[num] = node[num + 1];
				
			}
		}
		entryCount--;
	}

	public void decrementEntryCount() {
		entryCount--;
	}
	
	
}
