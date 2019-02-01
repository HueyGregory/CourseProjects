package edu.yu.oats.oatsdb.dbms.v1a;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.yu.oats.oatsdb.dbms.ClientNotInTxException;
import edu.yu.oats.oatsdb.dbms.ConfigurableDBMS;
import edu.yu.oats.oatsdb.dbms.NotSupportedException;
import edu.yu.oats.oatsdb.dbms.OATSDBType;
import edu.yu.oats.oatsdb.dbms.RollbackException;
import edu.yu.oats.oatsdb.dbms.SystemException;
import edu.yu.oats.oatsdb.dbms.Tx;
import edu.yu.oats.oatsdb.dbms.TxCompletionStatus;
import edu.yu.oats.oatsdb.dbms.TxMgr;
import edu.yu.oats.oatsdb.dbms.TxStatus;

public class ProxyDBMSwithTxMgr <K, V> implements ConfigurableDBMS, TxMgr {
//	Instance; 
	
	private final static Logger logger = LogManager.getLogger(ProxyDBMSwithTxMgr.class);
	volatile GlobalTables globalTables = GlobalTables.factory();
	DBMSImpl dbmsImpl;
	
	public ProxyDBMSwithTxMgr() {
		try {
			this.dbmsImpl = (DBMSImpl) OATSDBType.dbmsFactory(OATSDBType.V1a);
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void begin() throws NotSupportedException, SystemException {
		logger.debug("Entered begin()");
	//	localTablesThread = dbmsImpl.getLocalThread();
		try {
			if (LocalThreadInfo.getLocalTxInfoThread() == null) LocalThreadInfo.setLocalTxInfoThread(new TxInfo());
			else if (LocalThreadInfo.getLocalTxInfoThread().getCompletionStatus() == TxCompletionStatus.NOT_COMPLETED) throw new NotSupportedException("Already in a transaction. Thread name is: " + Thread.currentThread().getName());
			LocalThreadInfo.getLocalTxInfoThread().setCurrentTxStatus(TxStatus.ACTIVE);
			BeginGetFromFile.Instance.startGetFromFile();
		} catch (NotSupportedException e) { 
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SystemException("Problem with txInfo and LocalThreadInfo");
		}
		logger.debug("Exiting begin()");
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public void commit() throws RollbackException, IllegalStateException, SystemException {
		logger.debug("Entered commit()");
		if (!(LocalThreadInfo.getLocalTxInfoThread().getCompletionStatus() == TxCompletionStatus.NOT_COMPLETED)) throw new IllegalStateException();
		// when commit, need to copy all the local tables to the global tables.
		//  first check that none of those maps are already in globalTables, and then addAll()
		LocalThreadInfo.getLocalTxInfoThread().setCurrentTxStatus(TxStatus.COMMITTING);
		if (LocalThreadInfo.getLocalTxInfoThread().isWillThrowSystemException()) {
			rollback();
			throw new SystemException("A RollbackException was thrown due to an issue earlier");
		}
		
		logger.debug("After set currentTxStatus to committing; Before the for-loop");
		
		// move all the map entries from the local repository to the global repository and release all the locks
		Iterator<MapEntryWrapper> localIterator = LocalThreadInfo.getLocalRepository().iterator();
		try {
			while(localIterator.hasNext()) {
				MapEntryWrapper<K,V> mapEntry = localIterator.next();
				mapEntry.commit();
			}

			logger.debug("Before resetLocalThreadInfo");
			CommitWriteToFile.Instance.commitToFile();
		}
		catch (Exception e) {
			e.printStackTrace();
			rollback();
			throw e;
		}
		finally {
			LocalThreadInfo.resetLocalThreadInfo();
			LocalThreadInfo.getLocalTxInfoThread().setCurrentTxStatus(TxStatus.COMMITTED);
			logger.debug("Exiting commit()");
		}
	}

	@Override
	public TxStatus getStatus() throws SystemException {
		logger.debug("Inside getStatus()");
		return LocalThreadInfo.getLocalTxInfoThread().getStatus();
	}

	@Override
	public Tx getTx() throws SystemException {
		logger.debug("Inside getTx()");
		try {
			return LocalThreadInfo.getLocalTxInfoThread();
		} catch (Exception e) {
			logger.error("Error: Inside getTx()");
			throw new SystemException("Problem in LocalThreadInfo");
		}
	}

	@Override
	public void rollback() throws IllegalStateException, SystemException {
		logger.debug("Entered rollback()");
		if (!(LocalThreadInfo.getLocalTxInfoThread().getCompletionStatus() == TxCompletionStatus.NOT_COMPLETED)) 
			throw new IllegalStateException();
		LocalThreadInfo.getLocalTxInfoThread().setCurrentTxStatus(TxStatus.ROLLING_BACK);
		
		// need to reverse any damage that may have been done
		// rollback the global repository to what it was before and release all the locks
		Iterator<MapEntryWrapper> localIterator = LocalThreadInfo.getLocalRepository().iterator();
		while(localIterator.hasNext()) {
			MapEntryWrapper<K,V> mapEntry = localIterator.next();
			mapEntry.rollback();
		}
		
		// need to rollback/delete any maps which were created but not committed.
		for(int i = 0; i < this.globalTables.size(); i++) {
			if(this.globalTables.get(i).isThreadWithCurrentLockOnMap()) {
				this.globalTables.remove(this.globalTables.get(i));
			}
		}
		LocalThreadInfo.resetLocalThreadInfo();
		LocalThreadInfo.getLocalTxInfoThread().setCurrentTxStatus(TxStatus.ROLLEDBACK);
		logger.debug("Exiting rollback()");
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	@Override
	public <K, V> Map<K, V> createMap(String name, Class<K> keyClass, Class<V> valueClass) throws ClientNotInTxException, IllegalArgumentException {
		logger.debug("createMap(" + name + ", " + keyClass + ", " + valueClass + ")");
		if (!(LocalThreadInfo.getLocalTxInfoThread().getCompletionStatus() == TxCompletionStatus.NOT_COMPLETED) || this.dbmsImpl == null) {
			logger.error("Client not in transaction.");
			logger.debug("exiting createMap(" + name + ", " + keyClass + ", " + valueClass + "); throwing a ClientNotInTxException()");
			throw new ClientNotInTxException("Id of current thread is: " + Thread.currentThread().getId());
		}
		synchronized(this.globalTables) {
			if (name == null || keyClass == null || valueClass == null || globalFindName(name) != null) {
				logger.error("Name already exists.");
				logger.debug("exiting createMap(" + name + ", " + keyClass + ", " + valueClass + "); throwing an IllegalArgumentException()");
				throw new IllegalArgumentException("Name already exists");
			}
			// the map does not exist anywhere
			// first create the map in the server and get a lock on the entire map until the Tx commits()
			logger.debug("[createMap] about to create a new proxy instance. name == " + name + ", keyClass == " + keyClass.getName() + "; valueClass == " + valueClass.getName());
			ProxyTable<K, V> newGlobalTable = new ProxyTable<K, V>(name, keyClass, valueClass);
			// put in a proxy here. Do not add to the main tables arraylist (perhaps should have a spearate tables arraylist in the proxy instance)
			// until commit/rollback in the transaction has been done.
			this.globalTables.add(newGlobalTable);
			logger.debug("exiting createMap(" + name + ", " + keyClass + ", " + valueClass + ")");
			return (ProxyTableInterface<K,V>) Proxy.newProxyInstance(ProxyTable.class.getClassLoader(), new Class[] {Map.class, ProxyTableInterface.class}, newGlobalTable);
		}
	}

	@SuppressWarnings("hiding")
	@Override
	public <K, V> Map<K, V> getMap(String name, Class<K> keyClass, Class<V> valueClass) throws ClientNotInTxException, NoSuchElementException, ClassCastException {
		logger.debug("getMap(" + name + ", " + keyClass + ", " + valueClass + ")");
		if (!(LocalThreadInfo.getLocalTxInfoThread().getCompletionStatus() == TxCompletionStatus.NOT_COMPLETED) || this.dbmsImpl == null) {
			logger.error("Client not in transaction.");
			LocalThreadInfo.getLocalTxInfoThread().setCurrentTxStatus(TxStatus.UNKNOWN);
			throw new ClientNotInTxException("Id of current thread is: " + Thread.currentThread().getId());
		}
		ProxyTableInterface<K, V> table = null;
		if ((table = (ProxyTableInterface<K, V>) globalFindName(name)) == null) {
			throw new NoSuchElementException("Id of current thread is: " + Thread.currentThread().getId());
		}
		// now check if the table already exists in the local repository. If it does, then do nothing
		if(!(table.getKeyClass().equals(keyClass) && table.getValueClass().equals(valueClass))) {
			throw new ClassCastException("Id of current thread is: " + Thread.currentThread().getId());
		}
		// the table is in the server, but not in the local repository.
		// Need to create a new table with the same name in the local repository, which will act as the shadow table for the real table in the server.		
		// Give a proxy of the table use (ProxyTable<K, V>) globalTables.get(index).copy(); (maybe also add that table to the localTablesThread).

		logger.debug("exiting getMap(); table is: name == " + table.getName() + ", keyClass == " + table.getKeyClass() + ", valueClass == " + table.getValueClass());

		return (ProxyTableInterface<K,V>) Proxy.newProxyInstance(ProxyTable.class.getClassLoader(), new Class[] {Map.class, ProxyTableInterface.class}, table);
	}
	
	private synchronized ProxyTableInterface<K,V> globalFindName (String name) {
		if (name == null) return null; 
		synchronized(this.globalTables) {
			logger.debug("Entered synchronized(globalTables) in globalFindName(" + name + ")");
			int size = this.globalTables.size();
			for (int i = 0; i < size; i++) {
				if (this.globalTables.get(i).getName().equals(name)) {
					logger.debug("Exiting synchronized(globalTables) in globalFindName(" + name + ")");
					return this.globalTables.get(i);
				}
			}
			logger.debug("Exiting synchronized(globalTables) in globalFindName(" + name + ")");
			return null;
		}
	}

	@Override
	public int getTxTimeoutInMillis() {
		return DBMSImpl.Instance.getTxTimeoutInMillis();
	}

	@Override
	public void setTxTimeoutInMillis(int numOfMillis) {
		DBMSImpl.Instance.setTxTimeoutInMillis(numOfMillis);
	}
	
/*	public static ProxyDBMSwithTxMgr factory () {
		ProxyDBMSwithTxMgr proxyDBMSwithTxMgr = null;
		try {
			Class<?> clazz = Class.forName("edu.yu.oats.oatsdb.dbms.v1a.ProxyDBMSwithTxMgr");
			Object instance = Enum.valueOf((Class<Enum>)clazz, "Instance");
			proxyDBMSwithTxMgr = ProxyDBMSwithTxMgr.class.cast(instance);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return proxyDBMSwithTxMgr;
		
	}
	*/
}
