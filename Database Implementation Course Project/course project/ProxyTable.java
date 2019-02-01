package edu.yu.oats.oatsdb.dbms.v1a;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.yu.oats.oatsdb.dbms.ClientNotInTxException;
import edu.yu.oats.oatsdb.dbms.SystemException;
import edu.yu.oats.oatsdb.dbms.TxCompletionStatus;


public class ProxyTable <K, V> implements ProxyTableInterface<K, V>, Serializable {

	//private ConcurrentHashMap<K,V> hashMap;
	
	private final static Logger logger = LogManager.getLogger(ProxyTable.class);
	
	private String name;
	private final Class<K> keyClass;
	private final Class<V> valueClass;
	private ReentrantLock lockTable;	// used for when the table is first created but not yet committed.
	
	private ConcurrentHashMap<K, MapEntryWrapper<K, V>> mapEntries;
	private HashMap<K, V> keysToRemove;

	private ProxyTableInterface<K, V> original;

	private boolean alreadyBeenCommitted;
	
	public ProxyTable(String name, Class<K> keyClass, Class<V> valueClass) {
	//	super();
		logger.debug("Entered Constructor of ProxyTable(" + name + ", " + keyClass.getName() + ", " + valueClass.getName() + ")");
		this.lockTable = new ReentrantLock(true);
		UtilityMethods.acquireLock(this.lockTable);
		this.name = name;
		this.keyClass = keyClass;
		this.valueClass = valueClass;
		this.mapEntries = new ConcurrentHashMap<K, MapEntryWrapper<K, V>>();
		this.original = this;
		this.keysToRemove = new HashMap<K, V>();
		this.alreadyBeenCommitted = false;
		logger.debug("Created a new ProxyTable(" + name + ", " + keyClass.getName() + ", " + valueClass.getName() + ")");
	}
	
	public ProxyTable(ProxyTableInterface<K, V> globalTable) {
		logger.debug("Entered the constructor to create a new ProxyTable(" + globalTable.getName() + ", " + globalTable.getKeyClass().getName() + ", " + globalTable.getValueClass().getName() + ")");
		this.name = globalTable.getName();
		this.lockTable = new ReentrantLock(true);
		UtilityMethods.acquireLock(this.lockTable);
		this.keyClass = globalTable.getKeyClass();
		this.valueClass = globalTable.getValueClass();
		this.original = globalTable;
		this.mapEntries = new ConcurrentHashMap<K, MapEntryWrapper<K, V>>();
		this.keysToRemove = new HashMap<K, V>();
		this.alreadyBeenCommitted = false;
		logger.debug("Created a new ProxyTable(" + this.name + ", " + this.keyClass.getName() + ", " + this.valueClass.getName() + ")");
	}
	
	
	public V put(K key, V value) {
		logger.debug("v1a put(" + key + ", " + value + ") "
				+ "key.getClass() == " + key.getClass() + "; keyClass of map is " + this.keyClass);
		logger.debug("[ProxyTable - put(" + key + ", " + value + ")] Table id == " + this.hashCode() + "; original id == " + this.original.hashCode());
	//	UtilityMethods.checkSerializeValue(value);
		synchronized(this.mapEntries) {
			if (this.mapEntries.get(key) == null) {
				this.mapEntries.put(key, new MapEntryWrapper<K,V> (this, key, value));
				return null;
			}
			return this.mapEntries.get(key).putLocal(value);
		}
	}

	private void printAllMapEntries() {
		synchronized(this.mapEntries) {
			for (K key : this.mapEntries.keySet()) {
				System.out.println(key.toString());
				System.out.println(this.mapEntries.get(key).toString());
			}
		}
	}


	@Override
	public V get(Object key) {
		logger.debug("[ProxyTable - get(" + key + ")] Just before the method mapEntries.get(" + key + ").");
		logger.debug("[ProxyTable - get(" + key + ")] Table id == " + this.hashCode() + "; original id == " + this.original.hashCode());
		// if this thread has the lock, then just .get() the value from this table 
		// or else need to serialize the value and return the new value
		synchronized(this.mapEntries) {
			if(this.mapEntries.get(key) == null) {
				return null;
			}
			return this.mapEntries.get(key).getLocalValue();
		}
//		return serializeValue(mapEntries.get(key).getValue());
	}

	@Override
	public V remove(Object key) {
		logger.debug("[ProxyTable - remove()] Table id == " + this.hashCode());
		synchronized(this.mapEntries) {
			return this.mapEntries.get(key).removeLocal();
		}
	}
	
	public ByteArrayOutputStream writeTableToOutputStream() throws SystemException, IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
		synchronized(this.mapEntries) {
			Iterator<Map.Entry<K, MapEntryWrapper<K,V>>> mapEntriesIterator = this.mapEntries.entrySet().iterator();
			HashMap<K, MapEntryWrapper<K,V>> mapToSerialize = new HashMap<K, MapEntryWrapper<K,V>>(this.mapEntries.size());
			while(mapEntriesIterator.hasNext()) {
				Map.Entry<K, MapEntryWrapper<K,V>> mapEntry = mapEntriesIterator.next();
	//			MapEntryWrapper<K,V> mapEntryWrapper = mapEntry.getValue();
				if (mapEntry.getValue() == null) {
					// this means that another thread has created the key but has not committed yet.
					continue;
				}
				try {
					//Serialization of object
					mapToSerialize.put(mapEntry.getKey(), mapEntry.getValue());
			        logger.debug("Finished writing object to output stream");
			        
				} catch (Exception e) {
					logger.debug("throwing a system exception");
					e.printStackTrace();
					throw new SystemException("Could not serialize");
				}
			}
			out.writeObject(mapToSerialize);
		}
		logger.debug("Finished reading the object into ObjectOutputStream");
		return bos;
	}
	

	public String getName() {
		return this.name;
	}

	public Class<K> getKeyClass() {
		return this.keyClass;
	}

	public Class<V> getValueClass() {
		return this.valueClass;
	}
	
/*	public ConcurrentHashMap<K, WrapperValue<V>> getSerializedEntries() {
		return mapEntries;
	}
*/	
/*	public Semaphore getSemaphoreOfKey(K key) {
		return mapEntries.get(key).getLock();
	}
	
	public Semaphore getSemaphoreOfMap() {
		logger.debug("Entered getSemaphoreOfMap()");
		return this.sem;
	}
*/
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] arg2) throws Throwable {
		String allTheArgs = " Arguments: ";
		if (arg2 != null) {
			for (int i = 0; i < arg2.length; i++) { 
				allTheArgs += arg2[i] + "; ";
			}
		}
		logger.debug("Entered invoke() with Method: " + method.getName() + " and" + allTheArgs);

		if (!(LocalThreadInfo.getLocalTxInfoThread().getCompletionStatus() == TxCompletionStatus.NOT_COMPLETED)) {
			logger.debug("About to throw a ClientNotInTxException");
			throw new ClientNotInTxException("Current thread name is: " + Thread.currentThread().getName() + "; Thread id is: " + Thread.currentThread().getId());
		}
	//	synchronized(this) {
			logger.debug("Size of mapEntries == " + this.mapEntries.size());
	//		int i = 0;
	//		for (K key : mapEntries.keySet()) {
	//			logger.debug("index == " + i++ + "; key == " + key);
	//		}
	//	}
		
	//	logger.debug("[ProxyTable - invoke()] About to declare Object returningObject");
		Object returningObject = null;
		try {
			// lock the entire table when getting or putting entries. When dealing at the entry level, no lock on entire table
			//synchronized(this) {
			//	ThreadGetsSchemaOfTable();
			
		//		UtilityMethods.acquireLock(lockTable);
				logger.debug("method.invoke(); invoking method: " + method.getName() + "() with arguments: " + allTheArgs + "; number of keys to remove: " + this.keysToRemove.size());
		//		returningObject = method.invoke(this, arg2);
				returningObject = method.invoke(this.original, arg2);
				logger.debug("after method.invoke(); invoked method: " + method.getName() + "() with arguments: " + allTheArgs + "; number of keys to remove: " + this.keysToRemove.size());
		//		releaseLockOnMap();

		} catch (InvocationTargetException e) {
            throw e.getCause();		// From https://amitstechblog.wordpress.com/2011/07/24/java-proxies-and-undeclaredthrowableexception/
		} 
		return returningObject;
	}

/*	public boolean isThreadThatCreatedTheMap() {
		return this.lockTable.isHeldByCurrentThread();
	}
*/
	@Override
	public ProxyTableInterface<K,V> getServerTable() {
		return this.original;
	}

	@Override
	public int size() {
		return this.mapEntries.size();
	}

	@Override
	public boolean isEmpty() {
		return this.mapEntries.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.mapEntries.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.mapEntries.containsValue(value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (K key : m.keySet()) {
			this.put(key, m.get(key));
		}
	}

	@Override
	public void clear() {
		this.mapEntries.clear();
	}
	
	public String toString() {
		String returnString = this.name;
		if (this == this.original) {
			returnString += " server";
		}
		else {
			returnString += " proxy";
		}
		return returnString;
		
	}
	
	public int hashCode() {
		int returnInt = this.name.hashCode();
		if (this == this.original) {
			returnInt *= 1000;
		}
		return returnInt;
		
	}

	@Override
	public Set<K> keySet() {
		return this.mapEntries.keySet();
	}

	@Override
	public Collection<V> values() {
		Collection<V> values = new ArrayList<V>(this.mapEntries.size());
		for (MapEntryWrapper<K, V> value : this.mapEntries.values()) {
			if (value == null) {
				values.add(null);
			}
			else {
				values.add(value.getLocalValue());
			}
		}
		return values;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		HashMap<K,V> entrySetMap = new HashMap<K,V>();
		this.mapEntries.forEach((key, wrappedValue) -> entrySetMap.put(key, wrappedValue.getLocalValue()));
		return entrySetMap.entrySet();
	}


	@Override
	public ConcurrentHashMap<K, MapEntryWrapper<K, V>> getMapEntries() {
		return this.mapEntries;
	}


	@Override
	public boolean isThreadWithCurrentLockOnMap() {
		return (this.lockTable.isLocked() && this.lockTable.isHeldByCurrentThread());
	}
	
	public boolean alreadyBeenCommittedOnce() {
		return alreadyBeenCommitted;
	}
	
	/*public void getLockOnMap() {
		if(this.lockTable == null) {
			this.lockTable = new ReentrantLock(true);
		}
		UtilityMethods.acquireLock(this.lockTable);
	}
	
	public void releaseLockOnMap() {
		while(this.lockTable.getHoldCount() > 0) {
			this.lockTable.unlock();
		}
	}
	*/
	public boolean isThreadThatLockedThisKey(Object key) {
		synchronized (this.mapEntries) {
			if (this.mapEntries.get(key) == null) {
				logger.debug("The map entry is null for key: " + key);
				return false;
			}
			return this.mapEntries.get(key).hasLock();
		}
	}

	public void commitRemove(K key, MapEntryWrapper<K, V> mapEntryWrapper) {
		this.mapEntries.remove(key, mapEntryWrapper);
	}

	public void setAlreadyBeenCommittedOnce(boolean b) {
		this.alreadyBeenCommitted = b;
	}
	
}