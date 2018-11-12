import java.util.ArrayList;

// one single slot in a node in a BTree
public class NodeSlot<K, V> {

	// should be Generic
	
	// key field
	private K key;
	// value field
	private V value;
		// by an internal node, the value field will point to another node
		// by a leaf, the value field will hold an actual object value.
	
	public NodeSlot(K givenKey, V givenValue) {
		key = givenKey;
		value = givenValue;
	}
	
	// getter and setter methods
	public K getKey() {
		return key;
	}
	
	public void setKey(K givenKey) {
		key = givenKey;
	}
	
	public V getValue() {
		return value;
	}
	
	public void setValue(V givenValue) {
		value = givenValue;
	}
}
