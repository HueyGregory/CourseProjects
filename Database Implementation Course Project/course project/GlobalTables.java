package edu.yu.oats.oatsdb.dbms.v1a;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.yu.oats.oatsdb.dbms.SystemException;

// a Thread-safe class which contains the global tables used by all the local Threads.
public enum GlobalTables {
	Instance;
	
	@SuppressWarnings("rawtypes")
	// convert the globalTables implementation to be one of a hashmap with the name of the various tables being the keys, 
	// and the values will be the tables themselves (just like the setup with the local repositories.
	
	private volatile List<ProxyTable> globalTables = Collections.synchronizedList(new ArrayList<ProxyTable>());
	private final static Logger logger = LogManager.getLogger(GlobalTables.class);
	
	public <K, V> void add (ProxyTable<K, V> table) {
		synchronized (this.globalTables) {
			logger.debug("Entered synchronized(globalTables) in add(" + table.getName() + ")");
			this.globalTables.add(table);
			logger.debug("Exiting synchronized(globalTables) in add(" + table.getName() + ")");
		}
	}
	
	// called when committed and now getting from the disk
	public <K, V> void addAll(ArrayList<ProxyTable<K,V>> arrayList) {
		synchronized(this.globalTables) {
			logger.debug("Entered synchronized(globalTables) in addAll()");
			for (ProxyTable<K, V> table : arrayList) {
				if(table == null) { 
					logger.debug("table was null");
					continue;
				}
				logger.debug("Adding " + table.getName() + " to the globalTables");
				add(table);
			}
			logger.debug("Exiting synchronized(globalTables) in addAll()");
		}
	}
	
	public <K, V> ProxyTableInterface<?, ?> remove (ProxyTable<K, V> table) {
		synchronized(this.globalTables) {
			logger.debug("Entered synchronized(globalTables) in remove(" + table.getName() + ")");
			for (int i = 0; i < this.globalTables.size(); i++) {
				if (table == this.globalTables.get(i)) {
					logger.debug("removed from globalTables: table name == " + table.getName() + "; keyClass == " + table.getKeyClass() + "; value class == " + table.getValueClass());
					logger.debug("Exiting synchronized(globalTables)");
					return this.globalTables.remove(i);
				}
			}
			logger.debug("Exiting synchronized(globalTables) in remove(" + table.getName() + ")");
			return null;
		}
	} 
	
	@SuppressWarnings("unchecked")
	public <K, V> ProxyTable<K,V> get (int index) {
		ProxyTable<K,V> table = null;
		synchronized(this.globalTables) {
			logger.debug("Entered synchronized(globalTables) in get(" + index + ")");
			table = this.globalTables.get(index);
			logger.debug("Exiting synchronized(globalTables) in get(" + table.getName() + ")");
		}
		return table;
	}
	
	public int size() {
		synchronized(this.globalTables) {
			logger.debug("Inside synchronized(globalTables) in size");
			return this.globalTables.size();
		}
	}

	public <K, V> boolean contains (ProxyTable<K, V> table) {
		synchronized(this.globalTables) {
			logger.debug("Entered synchronized(globalTables) in contains(" + table.getName() + ")");
			for (int i = 0; i < this.globalTables.size(); i++) {
				if (table.getName().equals(this.globalTables.get(i).getName())) {
					logger.debug("Exiting synchronized(globalTables) in contains(" + table.getName() + ")");
					return true;
				}
			}
			logger.debug("Exiting synchronized(globalTables) in contains(" + table.getName() + ")");
			return false;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static GlobalTables factory () {
		GlobalTables globalTables = null;
		try {
			Class<?> clazz = Class.forName("edu.yu.oats.oatsdb.dbms.v1a.GlobalTables");
			@SuppressWarnings("unchecked")
			Object instance = Enum.valueOf((Class<Enum>)clazz, "Instance");
			globalTables = GlobalTables.class.cast(instance);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return globalTables;
		
	}

	// used when serializing the tables and writing them to disk
	public List<ProxyTable> getListOfGlobalTablesToSerialize() throws SystemException {
		List<ProxyTable> list = Collections.synchronizedList(new ArrayList<ProxyTable>());
		synchronized(this.globalTables) {
			for (ProxyTable table : this.globalTables) {
				if(table.alreadyBeenCommittedOnce() || table.isThreadWithCurrentLockOnMap()) {
					list.add(UtilityMethods.serializeValue(table));
				}
			}
		}
		return list;
	}

	public void clear() {
		synchronized(this.globalTables) {
			this.globalTables = Collections.synchronizedList(new ArrayList<ProxyTable>());
		}
	}
	
}
