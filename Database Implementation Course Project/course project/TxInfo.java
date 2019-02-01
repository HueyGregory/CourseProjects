package edu.yu.oats.oatsdb.dbms.v1a;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.yu.oats.oatsdb.dbms.SystemException;
import edu.yu.oats.oatsdb.dbms.Tx;
import edu.yu.oats.oatsdb.dbms.TxCompletionStatus;
import edu.yu.oats.oatsdb.dbms.TxStatus;

public class TxInfo implements Tx {

	private final static Logger logger = LogManager.getLogger(TxInfo.class);

	private volatile boolean InTransaction;
	// move the thread local to this TxInfo class
	private TxStatus currentTxStatus = TxStatus.UNKNOWN;
	private TxCompletionStatus currentTxCompletionStatus = null;
	private boolean willThrowSystemException;
	private ArrayList<ReentrantLock> locksHeld;
	
	public TxInfo() {
		this.locksHeld = new ArrayList<ReentrantLock>();
		this.willThrowSystemException = false;
	}

	public void setCurrentTxStatus(TxStatus currentTxStatus) {
		this.currentTxStatus = currentTxStatus;
	}

	@Override
	public TxCompletionStatus getCompletionStatus() {
		logger.debug("Before completion status: " + this.currentTxCompletionStatus + "; Current TxStatus: " + this.currentTxStatus);
		this.currentTxCompletionStatus = TxCompletionStatus.updateTxStatus(this.currentTxCompletionStatus, this.currentTxStatus);
		logger.debug("Changed completion status to: " + this.currentTxCompletionStatus);
		return this.currentTxCompletionStatus;
	}

	@Override
	public TxStatus getStatus() throws SystemException {
		return this.currentTxStatus;
	}

	public boolean isWillThrowSystemException() {
		return this.willThrowSystemException;
	}

	public void setWillThrowSystemException(boolean willThrowSystemException) {
		this.willThrowSystemException = willThrowSystemException;
	}

	public void releaseAllLocks() {
		logger.debug("Entered releaseAllLocks() of thread: " + Thread.currentThread().getName());
		for (ReentrantLock lock : this.locksHeld) {
			while (lock.getHoldCount() > 0) {
				lock.unlock();
			}
		}
		this.locksHeld = new ArrayList<ReentrantLock>();
		logger.debug("Exiting releaseAllLocks() of thread: " + Thread.currentThread().getName());
	}

	public void removeLock(ReentrantLock lock) {
		this.locksHeld.remove(lock);
	}

	public ArrayList<ReentrantLock> getAllLocksFromLocalRepository() {
		return this.locksHeld;
	}

	public void addLock(ReentrantLock lock) {
		this.locksHeld.add(lock);
	}
}