package edu.yu.oats.oatsdb.dbms.v1a;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.yu.oats.oatsdb.dbms.SystemException;

public enum BeginGetFromFile {
	Instance;
	
	private final static Logger logger = LogManager.getLogger(BeginGetFromFile.class);
	
	public static int getMetaDataFileBit() throws SystemException, IOException {
		Path path = Paths.get(UtilityMethods.getMetaDataFileName());
		if (!Files.exists(path)) {
			return -1;
		}
		if (!isCorrupted(path)) {
			logger.error("MetaData File was corrupted");
			throw new SystemException("MetaData File was corrupted");
		}
		byte[] numbers = Files.readAllBytes(path);
		for (int i = 0; i < numbers.length; i++) {
			logger.debug("byte #{} is: {}; but after using .toUnsignedInt() we get {}", i, numbers[i], Byte.toString(numbers[i]));
		}
		switch(numbers[numbers.length - 1]) {
		case 49:
			return 1;
		case 50: 
			return 2;
		default:
			logger.debug("Was not 1 or 2");
			return 0;
		}
	}
	
	private static boolean isCorrupted(Path path) {
		if (!Files.isReadable(path) || !Files.isWritable(path)) 
			return false;
		return true;
	}

	private <K, V> void addToServerTables(int version) throws SystemException {
		logger.debug("Entered addToServerTables; version == " + version);
		ArrayList<ProxyTable<K,V>> proxyTables = new ArrayList<ProxyTable<K,V>>();
		Path path = Paths.get(UtilityMethods.getRootDirectory() + "Version " + version + " directory");
		try (Stream<Path> pathsInDirectory = Files.walk(path)) {
			pathsInDirectory.filter(pathForFilter -> pathForFilter.toFile().isFile())
			.forEach(path1 -> {
				try {
					logger.debug("path1.toString(): " + path1.toString());
					String thePath = path1.toString();
					logger.debug("thePath: " + thePath);
					FileInputStream fi = new FileInputStream(thePath);
					ObjectInputStream in = new ObjectInputStream(fi);
					HashMap<K, MapEntryWrapper<K, V>> hashMap = (HashMap<K, MapEntryWrapper<K, V>>) in.readObject();
					ProxyTable currentTable = getTableOfMap(hashMap);
					logger.debug("currentTable is: " + currentTable);
					proxyTables.add(currentTable);
					for (MapEntryWrapper<K, V> mapEntryWrapper : hashMap.values()) {
						System.out.println(mapEntryWrapper.getLocalValue());
					}
					fi.close();
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Error when trying to deserialize", e);
				}
			});
		}	catch (Exception e) {
			throw new SystemException("From outside: Error with trying to get and read objects from files", e);
		}
		GlobalTables.Instance.addAll(proxyTables);
		logger.debug("Exiting addToServerTables; version == " + version);
	}
	
	private <K, V> ProxyTable getTableOfMap(HashMap<K, MapEntryWrapper<K, V>> hashMap) throws SystemException {
		ProxyTable currentTable = null;
		for(K key : hashMap.keySet()) {
			if (hashMap.get(key).getTable() != currentTable && currentTable != null) {
				throw new SystemException("The file must have gotten corrupted");
			}
			currentTable = hashMap.get(key).getTable();
		}
		return currentTable;
	}

	public boolean startGetFromFile() throws SystemException {
		if (GlobalTables.Instance.size() > 0) {
			// only need to bring in from disk when the system crashed or is starting up anew now.
			return false;
		}
		try {
			int version = -1;
			synchronized(GlobalTables.Instance) {
				if((version = getMetaDataFileBit()) == -1)
					return false;
				addToServerTables(version);
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Something went wrong");
			throw new SystemException("Something went wrong when tried to load server tables into memory from disk");
		}
		return true;
		
	}

}
