package edu.yu.oats.oatsdb.dbms.v1a;

import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public interface ProxyTableInterface<K, V> extends Map<K,V>, InvocationHandler {

	public String getName();

	public Class<K> getKeyClass();

	public Class<V> getValueClass();

	public ProxyTableInterface<K, V> getServerTable();

//	public Semaphore getSemaphoreFromLocalRepository(K key);
	
	public boolean isThreadWithCurrentLockOnMap();
	public boolean isThreadThatLockedThisKey(Object key);
//	public Semaphore getSemaphoreOfKey(K key);
//	public boolean isThreadThatCreatedTheMap();

//	public HashMap<K, Semaphore> get_KeyToSemaphores_Map();

	public ConcurrentHashMap<K, MapEntryWrapper<K, V>> getMapEntries();


}
