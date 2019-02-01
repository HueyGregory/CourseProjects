package edu.yu.oats.oatsdb.dbms.v1a;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.yu.oats.oatsdb.dbms.SystemException;

public class MapEntryWrapper<K,V> implements Serializable {
	
	enum TO_DO_BY_COMMIT {
		PUT, REMOVE;
	}
	enum ROLLBACK_ON_ERROR {
		ROLLBACK, DO_NOT_ROLLBACK;
	}
	enum STATUS_DELETE_MAP_ENTRY_ON_ROLLBACK {
		DELETE, ALREADY_BEEN_COMMITTED_ONCE;
	}
	
	private final static Logger logger = LogManager.getLogger(MapEntryWrapper.class);
	
	private ProxyTable<K,V> fromTable;
	private K key;
	private V localValue;
	private V committedValue;
	private V rollbackValue;
	private ReentrantLock lock;
	private TO_DO_BY_COMMIT toDoByCommitOrOnRollbackError;
	private ROLLBACK_ON_ERROR rollbackOnError;
	private STATUS_DELETE_MAP_ENTRY_ON_ROLLBACK statusOFMapEntry;
	
	public MapEntryWrapper(ProxyTable<K,V> table, K key, V value) {
		logger.debug("Entered MapEntryWrapper with key: " + key + "; value: " + value + "; table: " + table.getName());
		this.fromTable = table;
		this.key = key;
		this.committedValue = null;
		this.rollbackValue = null;
		this.localValue = value;
		this.lock = new ReentrantLock(true);
		UtilityMethods.acquireLock(this.lock);
		LocalThreadInfo.addMapEntry(this);
		this.statusOFMapEntry = STATUS_DELETE_MAP_ENTRY_ON_ROLLBACK.DELETE;
		this.toDoByCommitOrOnRollbackError = TO_DO_BY_COMMIT.PUT;
		logger.debug("Exited MapEntryWrapper with key: " + key + "; value: " + value + "; table: " + table.getName());

		
	}
	
	public V putLocal(V value) {
		UtilityMethods.acquireLock(this.lock);
		V valueToReturn = this.localValue;
		this.rollbackValue = this.committedValue;
		this.localValue = value;
		LocalThreadInfo.addMapEntry(this);
		this.toDoByCommitOrOnRollbackError = TO_DO_BY_COMMIT.PUT;
		this.rollbackOnError = ROLLBACK_ON_ERROR.DO_NOT_ROLLBACK;
		return valueToReturn;
	}
	
	public V removeLocal() {
		UtilityMethods.acquireLock(this.lock);
		V valueToReturn = this.localValue;
		this.rollbackValue = this.committedValue;
		this.localValue = null;
		LocalThreadInfo.addMapEntry(this);
		this.toDoByCommitOrOnRollbackError = TO_DO_BY_COMMIT.REMOVE;
		this.rollbackOnError = ROLLBACK_ON_ERROR.DO_NOT_ROLLBACK;
		return valueToReturn;
	}
	
	
	public boolean commit() throws SystemException {
		if(!this.hasLock() || this.rollbackOnError == ROLLBACK_ON_ERROR.ROLLBACK) {
			return false;
		}
		// need to get things together in case of a rollback.
		this.rollbackValue = this.committedValue;
		if(this.toDoByCommitOrOnRollbackError == TO_DO_BY_COMMIT.REMOVE) {
			// reverse the local data in case of a rollback
			this.fromTable.commitRemove(this.key, this);
			this.toDoByCommitOrOnRollbackError = TO_DO_BY_COMMIT.PUT;
		}
		else if (this.toDoByCommitOrOnRollbackError == TO_DO_BY_COMMIT.PUT) {
			// reverse the local data in case of a rollback
			if (this.statusOFMapEntry == STATUS_DELETE_MAP_ENTRY_ON_ROLLBACK.DELETE) {
				this.toDoByCommitOrOnRollbackError = TO_DO_BY_COMMIT.REMOVE;
			}
			this.committedValue = UtilityMethods.serializeValue(this.localValue);
		}
		this.localValue = UtilityMethods.serializeValue(this.committedValue);
		this.rollbackOnError = ROLLBACK_ON_ERROR.ROLLBACK;
		this.statusOFMapEntry = STATUS_DELETE_MAP_ENTRY_ON_ROLLBACK.ALREADY_BEEN_COMMITTED_ONCE;
		this.fromTable.setAlreadyBeenCommittedOnce(true);
		return true;
	}
	
	public boolean rollback() throws SystemException {
		if(!this.hasLock()) {
			return false;
		}
		if (this.statusOFMapEntry == STATUS_DELETE_MAP_ENTRY_ON_ROLLBACK.DELETE) {
			this.fromTable.commitRemove(this.key, this);
			this.localValue = UtilityMethods.serializeValue(this.committedValue);
			return true;
		}
		else if(this.rollbackOnError == ROLLBACK_ON_ERROR.DO_NOT_ROLLBACK) {
			this.localValue = UtilityMethods.serializeValue(this.committedValue);
			return true;
		}
		else if(this.toDoByCommitOrOnRollbackError == TO_DO_BY_COMMIT.REMOVE) {
			this.fromTable.commitRemove(this.key, this);
			this.localValue = UtilityMethods.serializeValue(this.committedValue);
		}
		else if (this.toDoByCommitOrOnRollbackError == TO_DO_BY_COMMIT.PUT) {
			this.committedValue = UtilityMethods.serializeValue(this.rollbackValue);	
			this.localValue = UtilityMethods.serializeValue(this.committedValue);
		}
		else {
			throw new SystemException("Error occurred when trying to rollback");
		}
		return true;
	}

	public V getLocalValue() {
		logger.debug("entered getLocalValue()");
		UtilityMethods.acquireLock(this.lock);
		LocalThreadInfo.addMapEntry(this);
		return this.localValue;
	}

	boolean hasLock() {
		return this.lock.isHeldByCurrentThread();
	}
	
	public ProxyTable getTable() {
		return fromTable;
	}
	
}
