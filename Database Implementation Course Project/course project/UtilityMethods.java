package edu.yu.oats.oatsdb.dbms.v1a;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.yu.oats.oatsdb.dbms.ClientTxRolledBackException;
import edu.yu.oats.oatsdb.dbms.SystemException;

public enum UtilityMethods {
	Instance;
	
	private final static Logger logger = LogManager.getLogger(UtilityMethods.class);
	//private static String absolutePathToFiles;
	
	private volatile static AtomicBoolean numOfDirectory;
	
	private final static String rootDirectory = "Disk Directory" + File.separator;
	private final static String metaDataFileName = rootDirectory + "YMetaDataFileW";

	public static <K, V> void acquireLock(ReentrantLock theLock) {
		logger.debug("[UtilityMethods - acquireLock()] Entered acquireLock for lock: " + theLock.toString());
		if (theLock.isHeldByCurrentThread()) {
			logger.debug("[UtilityMethods - acquireLock()] lock: " + theLock.toString() + " is already held by this thread: " + Thread.currentThread().getName());
			return;
		}
		boolean gotLock = true;
		try {
			logger.debug("just before theLock.tryLock(" + DBMSImpl.Instance.getTxTimeoutInMillis() + ")");
			gotLock = theLock.tryLock(DBMSImpl.Instance.getTxTimeoutInMillis(), TimeUnit.MILLISECONDS);
			logger.debug("just after .tryLock(); theLock: " + theLock.toString());
		} catch (InterruptedException e) {
//			gotLock = lock.tryLock();
			e.printStackTrace();
			throw new ClientTxRolledBackException("Error: Waited too long inside acquireLock(); theLock: " + theLock.toString());
		}
		if (!gotLock) {
			logger.debug("Thread timed out while trying to acquire lock, so calling rollbackTx()");
			ProxyDBMSwithTxMgr<K,V> rollbackClass = new ProxyDBMSwithTxMgr<K,V>();
			try {
				rollbackClass.rollback();
			} catch (IllegalStateException | SystemException e1) {
				e1.printStackTrace();
				throw new RuntimeException();
			}
			logger.debug("after rollbackTx()");
			ClientTxRolledBackException e = new ClientTxRolledBackException(null);
			e.printStackTrace();
			throw e;
		}
		else if (gotLock) {
			logger.debug("just before addLock(lock)");
			LocalThreadInfo.getLocalTxInfoThread().addLock(theLock);
			logger.debug("after addLock(lock)");
		}
	}

	public static <V> void checkSerializeValue(V value) {
		// check if the value is serializable
		if (!(value instanceof Serializable)) {
			LocalThreadInfo.getLocalTxInfoThread().setWillThrowSystemException(true);
			logger.debug("value is not serializable. Logged to throw a system exception later.");
		}
		else {
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
		        ObjectOutputStream out = new ObjectOutputStream(bos);
		        out.writeObject(value);
			} catch (@SuppressWarnings("unused") Exception e) {
				logger.debug("value is not serializable. Logged to throw a system exception later.");
				LocalThreadInfo.getLocalTxInfoThread().setWillThrowSystemException(true);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <V> V serializeValue(V value) throws SystemException {
		logger.debug("Entered serialize value(" + value + ")");
		V copied = value;
		if (value == null) {
			return null;
		}
		try {
			//Serialization of object
		
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        ObjectOutputStream out = new ObjectOutputStream(bos);
	        out.writeObject(value);
	 
	        logger.debug("Finished writing object to output stream");
	        
	        //De-serialization of object
	        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
	        ObjectInputStream in = new ObjectInputStream(bis);
			copied = (V) in.readObject();
		} catch (Exception e) {
			logger.debug("throwing a system exception");
			e.printStackTrace();
			throw new SystemException("Could not serialize");
		}
		logger.debug("Finished reading the object into copied: " + copied);
        return copied;
	}

	public static int getNumOfDirectory() {
		if (numOfDirectory == null) {
			switchNumOfDirectory();
		}
		synchronized (numOfDirectory) {	
			return numOfDirectory.get() ? 1 : 2;
		}
	}

	public static void switchNumOfDirectory() {
		if (numOfDirectory == null) {
			numOfDirectory = new AtomicBoolean();
			synchronized(numOfDirectory) {
				numOfDirectory.set(getValueFromFile());
				return;
			}
		}
		synchronized(numOfDirectory) {
			numOfDirectory.set(!numOfDirectory.get());			
		}
	}
	
	private static boolean getValueFromFile() {
		try {
			if(BeginGetFromFile.getMetaDataFileBit() == 2)
				return true;
		} catch (SystemException | IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String getDirectoryToWriteTo() {
		return rootDirectory + "Version " + getNumOfDirectory() + " directory" + File.separator;
	}
	
	public static String getMetaDataFileName() {
		return metaDataFileName;
	}
	
	public static String getRootDirectory() {
		return rootDirectory;
	}
	
/*	public static String getAbsolutePathToFiles() {
		return absolutePathToFiles;
	}
	
	public static String setAbsolutePathToFiles(String newPath) {
		return absolutePathToFiles = newPath;
	}*/
}
