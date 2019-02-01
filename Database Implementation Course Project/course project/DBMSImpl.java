package edu.yu.oats.oatsdb.dbms.v1a;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.yu.oats.oatsdb.dbms.ClientNotInTxException;
import edu.yu.oats.oatsdb.dbms.ConfigurablePersistentDBMS;
import edu.yu.oats.oatsdb.dbms.TxMgr;

public enum DBMSImpl implements ConfigurablePersistentDBMS {
	Instance;
	
	private final static Logger logger = LogManager.getLogger(DBMSImpl.class);

	// list of maps
	@SuppressWarnings("rawtypes")
	// have to make this concurrent and move to a different class (build the GlobalTables class)
	
	ThreadLocal<ConfigurablePersistentDBMS> localProxyThread = new ThreadLocal<ConfigurablePersistentDBMS>() {
		int i = 0;
		public ConfigurablePersistentDBMS initialValue () {
			logger.debug("creating and returning a new proxy instance " + i++);
			return (ConfigurablePersistentDBMS) Proxy.newProxyInstance(DBMSImpl.class.getClassLoader(), new Class[] {ConfigurablePersistentDBMS.class, Map.class, TxMgr.class}, new ProxyHandler(new ProxyDBMSwithTxMgr()));
		}
	};
	
	static int timeoutAmount = 1000;
	
	 /** Retrieves the named map.  The map is "typed" by its key and value classes
	   * and this API provides a type-safe way for the map retrieval.
	   *
	   * @param name name associated with the map, cannot be empty
	   * @return the named map
	   * @param keyClass the type of the map's keys, cannot be null
	   * @param valueClass the type of the map's values, cannot be null
	   * @see #createMap
	   * @throws ClassCastException if specified key or value class doesn't match
	   * the corresponding classes of the actual map
	   * @throws NoSuchElementException if no map is associated with the specified
	   * name
	   * @throws ClientNotInTxException if client is not associated with a
	   * transaction.
	   */	
	public <K, V> Map<K, V> getMap(String name, Class<K> keyClass, Class<V> valueClass) {
		logger.debug("getMap(" + name + ", " + keyClass + ", " + valueClass + ")");
		if (name == null || keyClass == null || valueClass == null) throw new IllegalArgumentException();
		if (name.equals("")) throw new IllegalArgumentException();
		if (localProxyThread.get() == null) {
			localProxyThread.set((ConfigurablePersistentDBMS) Proxy.newProxyInstance(DBMSImpl.class.getClassLoader(), new Class[] {ConfigurablePersistentDBMS.class, Map.class, TxMgr.class}, new ProxyHandler(new ProxyDBMSwithTxMgr<Object, Object>())));
		}
		logger.debug("localProxyThread.get() returns: " + localProxyThread.get());
		return localProxyThread.get().getMap(name, keyClass, valueClass);
	}

	
	
	 /** Creates (and returns) a map, associates it with the specified name for
	   * subsequent retrieval.  The map is parameterized by the type of the key
	   * class and the type of the value class
	   *
	   * @param name names the map (for subsequent retrieval), cannot be empty
	   * @param keyClass the type of the map's keys, cannot be null
	   * @param valueClass the type of the map's values, cannot be null
	   * @return a parameterized map of the specified key and value types
	   * @throws IllegalArgumentException if name is already bound to another map
	   * @throws ClientNotInTxException if client is not associated with a
	   * transaction.
	   */	
	public <K, V> Map<K, V> createMap(String name, Class<K> keyClass, Class<V> valueClass) {
		logger.debug("createMap(" + name + ", " + keyClass + ", " + valueClass + ")");
		if (name == null || keyClass == null || valueClass == null)	throw new IllegalArgumentException();
		if (name.trim().isEmpty()) throw new IllegalArgumentException();
		if (localProxyThread.get() == null) {
			localProxyThread.set((ConfigurablePersistentDBMS) Proxy.newProxyInstance(DBMSImpl.class.getClassLoader(), new Class[] {ConfigurablePersistentDBMS.class, Map.class, TxMgr.class}, new ProxyHandler(new ProxyDBMSwithTxMgr<Object, Object>())));
		}
		return localProxyThread.get().createMap(name, keyClass, valueClass);
	}



	@Override
	public int getTxTimeoutInMillis() {
		logger.debug("getTxTimeoutInMillis()");
		return timeoutAmount;
	}



	@Override
	public void setTxTimeoutInMillis(int numOfMillis) {
		logger.debug("setTxTimeoutInMillis(" + numOfMillis + ")");
		if (numOfMillis <= 0) {
			throw new IllegalStateException("Number of milliseconds for timeout: " + numOfMillis);
		}
		timeoutAmount = numOfMillis;
	}


	/** Delete all files and directories associated with this DBMS instance from
	   * both disk and from main-memory.  Effectively resets the database.
	   *
	   * IMPORTANT: the effects of this API on existing transactions is undefined.
	   * This method should be invoked only when the system is quiescent.
	   */
	@Override
	public void clear() {
		GlobalTables.Instance.clear();
		try {
			deleteDirectory(Paths.get(UtilityMethods.getRootDirectory()));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem with clearing the directory", e);
		}
		(new File(UtilityMethods.getRootDirectory())).delete();
	}


	private void deleteDirectory(Path path) throws IOException {
		if (!Files.exists(path)) {
			return;
		}
		if(Files.isDirectory(path)) {
			DirectoryStream<Path> paths = Files.newDirectoryStream(path);
			Iterator<Path> iterateDirectory = paths.iterator();
			while(iterateDirectory.hasNext()) {
				Path path1 = iterateDirectory.next();
				if (Files.isDirectory(path1)) {
					deleteDirectory(path1);
				}
				else {
					Files.delete(path1);
				}
			}
			paths.close();
		}
		Files.delete(path);
	}
	
	
/** Returns the disk usage in MB of this DBMS instance.
	   *
	   * @return disk usage in MB for this DBMS
	   */
	@Override
	public double getDiskUsageInMB() {
		// idea and general strategy is taken from https://www.baeldung.com/java-folder-size
		AtomicLong totalSize = new AtomicLong(0);
		Stream<Path> paths;
		try {
			Files.walk(Paths.get(UtilityMethods.getRootDirectory()))
				.filter(path -> path.toFile().isFile())
				.forEach(path -> totalSize.addAndGet(path.toFile().length()));
		} catch (IOException e) {
			e.printStackTrace();
		//	throw new RuntimeException();
			return 0;
		}
		return totalSize.doubleValue()/1000000;
	}
	
	
	
}