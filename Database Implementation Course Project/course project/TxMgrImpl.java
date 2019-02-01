package edu.yu.oats.oatsdb.dbms.v1a;

import java.lang.reflect.Proxy;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.yu.oats.oatsdb.dbms.DBMS;
import edu.yu.oats.oatsdb.dbms.NotSupportedException;
import edu.yu.oats.oatsdb.dbms.RollbackException;
import edu.yu.oats.oatsdb.dbms.SystemException;
import edu.yu.oats.oatsdb.dbms.Tx;
import edu.yu.oats.oatsdb.dbms.TxCompletionStatus;
import edu.yu.oats.oatsdb.dbms.TxMgr;
import edu.yu.oats.oatsdb.dbms.TxStatus;

public enum TxMgrImpl implements TxMgr {
	Instance;
	
	private final static Logger logger = LogManager.getLogger(TxMgrImpl.class);
	ThreadLocal<TxMgr> localProxyThread = new ThreadLocal<TxMgr>() {
		@SuppressWarnings("rawtypes")
		protected TxMgr initialValue () {
			return (TxMgr) Proxy.newProxyInstance(TxMgrImpl.class.getClassLoader(), new Class[] {DBMS.class, Map.class, TxMgr.class}, new ProxyHandler(new ProxyDBMSwithTxMgr()));
		}
	};
	//ProxyDBMSwithTxMgr proxyDBMSwithTxMgr = new ProxyDBMSwithTxMgr();

	public void begin() throws NotSupportedException, SystemException {
		logger.debug("Entered begin() wth thread name: " + Thread.currentThread().getName() + " ; thread id: " + Thread.currentThread().getId());
		this.localProxyThread.get().begin();
	}

	public synchronized void commit() throws RollbackException, IllegalStateException, SystemException {
		this.localProxyThread.get().commit();
		if (((LocalThreadInfo.getLocalTxInfoThread().getCompletionStatus() == TxCompletionStatus.COMMITTED) && (LocalThreadInfo.getLocalTxInfoThread().getStatus() == TxStatus.COMMITTED)) || ((LocalThreadInfo.getLocalTxInfoThread().getCompletionStatus() == TxCompletionStatus.ROLLEDBACK) && (LocalThreadInfo.getLocalTxInfoThread().getStatus() == TxStatus.ROLLEDBACK))) {
			LocalThreadInfo.getLocalTxInfoThread().setCurrentTxStatus(TxStatus.NO_TRANSACTION);
		}
		else {
			LocalThreadInfo.getLocalTxInfoThread().setCurrentTxStatus(TxStatus.COMMITTED);
			LocalThreadInfo.getLocalTxInfoThread().getCompletionStatus();
			LocalThreadInfo.getLocalTxInfoThread().setCurrentTxStatus(TxStatus.NO_TRANSACTION);
		}
		
	}

	public void rollback() throws IllegalStateException, SystemException {
		this.localProxyThread.get().rollback();
		if ((LocalThreadInfo.getLocalTxInfoThread().getCompletionStatus() == TxCompletionStatus.ROLLEDBACK) && (LocalThreadInfo.getLocalTxInfoThread().getStatus() == TxStatus.ROLLEDBACK)) {
			LocalThreadInfo.getLocalTxInfoThread().setCurrentTxStatus(TxStatus.NO_TRANSACTION);	
		}
		else {
			LocalThreadInfo.getLocalTxInfoThread().setCurrentTxStatus(TxStatus.ROLLEDBACK);
			LocalThreadInfo.getLocalTxInfoThread().getCompletionStatus();
			LocalThreadInfo.getLocalTxInfoThread().setCurrentTxStatus(TxStatus.NO_TRANSACTION);
		}
	}

	public Tx getTx() throws SystemException {
		return this.localProxyThread.get().getTx();
	}

	public TxStatus getStatus() throws SystemException {
		return this.localProxyThread.get().getStatus();
	}
// Implement TxMgr interface
}
