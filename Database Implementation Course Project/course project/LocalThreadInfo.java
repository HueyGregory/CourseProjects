package edu.yu.oats.oatsdb.dbms.v1a;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.yu.oats.oatsdb.dbms.SystemException;

public enum LocalThreadInfo {
	Instance;
	
	private final static Logger logger = LogManager.getLogger(LocalThreadInfo.class);
	
	@SuppressWarnings("rawtypes")
	private static ThreadLocal<HashSet<MapEntryWrapper>> localMapEntriesThread = new ThreadLocal<HashSet<MapEntryWrapper>>() {
		public HashSet<MapEntryWrapper> initialValue() {
			return new HashSet<MapEntryWrapper>();
		}
	};

	private static ThreadLocal<TxInfo> localTxInfoThread = new ThreadLocal<TxInfo>() {
		//private TxInfo localTxInfo = new TxInfo(); 
		public TxInfo initialValue() {
			return new TxInfo();
		}
	};
	
	@SuppressWarnings("rawtypes")
	public static HashSet<MapEntryWrapper> getLocalRepository () {
		return localMapEntriesThread.get();
	}
	
	@SuppressWarnings("rawtypes")
	public static void resetLocalRepository() {
		localMapEntriesThread.set(new HashSet<MapEntryWrapper>());
	}	
	
	public static <K, V> void addMapEntry(MapEntryWrapper<K, V> mapEntry) {
//		MapEntryWrapper<K, V> mapEntry = new MapEntryWrapper<K, V>(table, key, value, lock);
		logger.debug("adding a new map entry to the local repository");
		localMapEntriesThread.get().add(mapEntry);
		logger.debug("added a new map entry to the local repository");
	}
	
/*	public static <K, V> Map<K,V> addNewTable(ProxyTableInterface<K, V> table) {
		logger.debug("adding a new ProxyLocalTable to the local repository");
	//	localTablesThread.get().put(table.getName(), (ProxyTableInterface<K,V>) Proxy.newProxyInstance(ProxyTable.class.getClassLoader(), new Class[] {Map.class, ProxyTableInterface.class}, table));
	//	localTablesThread.get().put(table.getName(), new ProxyTable<K, V>(table));
		localTablesThread.get().put(table.getName(), (ProxyTableInterface<K,V>) Proxy.newProxyInstance(ProxyTable.class.getClassLoader(), new Class[] {Map.class, ProxyTableInterface.class}, new ProxyTable<K, V>(table)));
		logger.debug("added a new ProxyLocalTable to the local repository");
		return localTablesThread.get().get(table.getName());
	}
	*/	
	
	public static TxInfo getLocalTxInfoThread() {
		return localTxInfoThread.get();
	}

	public static void setLocalTxInfoThread(TxInfo txInfo) {
		localTxInfoThread.set(txInfo);
	}
	
	public static void resetLocalThreadInfo() {
		logger.debug("Entered resetLocalThreadInfo");
		getLocalTxInfoThread().setWillThrowSystemException(false);
		resetLocalRepository();
		LocalThreadInfo.getLocalTxInfoThread().releaseAllLocks();
		logger.debug("Exiting resetLocalThreadInfo");
		
	}	

	@SuppressWarnings("rawtypes")
	public static LocalThreadInfo factory () {
		LocalThreadInfo localThreadInfo = null;
		try {
			Class<?> clazz = Class.forName("edu.yu.oats.oatsdb.dbms.v1a.LocalThreadInfo");
			@SuppressWarnings("unchecked")
			Object instance = Enum.valueOf((Class<Enum>)clazz, "Instance");
			localThreadInfo = LocalThreadInfo.class.cast(instance);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return localThreadInfo;
	}
}
		
