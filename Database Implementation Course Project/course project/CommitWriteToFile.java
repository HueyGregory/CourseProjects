package edu.yu.oats.oatsdb.dbms.v1a;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Random;

import edu.yu.oats.oatsdb.dbms.SystemException;

public enum CommitWriteToFile {
	Instance;
	// no need to worry about a rollback because the server tables in memory would have already been completely updated.
	private static Random rand = new Random();
	
	private static void switchMetaDataFileBit() throws IOException {
		// or perhaps append to file so that won't be a problem if the program crashes during the bit switch
		// and then truncate the file during a begin()
	//	new File(UtilityMethods.getMetaDataFileName()).createNewFile();
		String whatToAppend = "" + UtilityMethods.getNumOfDirectory();
		Path path = Paths.get(UtilityMethods.getMetaDataFileName());
		if(!Files.exists(path)) {
			new File(UtilityMethods.getMetaDataFileName()).createNewFile();
		}
		if (rand.nextInt(100) == 1) {
			// refresh the file
			Files.write(path, whatToAppend.getBytes());
		}
		else {
			Files.write(path, whatToAppend.getBytes(), StandardOpenOption.APPEND);
		}
		UtilityMethods.switchNumOfDirectory();
	}
	
	private static void writeTableToFile(ProxyTable<?, ?> table) throws SystemException, IOException {
	//	https://stackoverflow.com/questions/2836646/java-serializable-object-to-byte-array
		ByteArrayOutputStream bos = table.writeTableToOutputStream();		
		
		Path path = Paths.get(UtilityMethods.getDirectoryToWriteTo() + table.getName());
	//	Path path = Paths.get(UtilityMethods.getNumOfDirectory() + table.getName());
		Files.write(path, bos.toByteArray());
		
	}

	public synchronized void commitToFile() throws SystemException {
		// commit this thread/transaction's tables to the file
		synchronized (GlobalTables.Instance) {
			List<ProxyTable> listOfProxyTablesToWrite = GlobalTables.Instance.getListOfGlobalTablesToSerialize();
			try {
				if (!Files.isDirectory(Paths.get(UtilityMethods.getRootDirectory() + "Version 1 directory"))) {
					System.out.println((new File(UtilityMethods.getRootDirectory() + "Version 1 directory")).mkdirs());
				}
				if (!Files.isDirectory(Paths.get(UtilityMethods.getRootDirectory() + "Version 2 directory"))) {
					System.out.println((new File(UtilityMethods.getRootDirectory() + "Version 2 directory")).mkdirs());
				}
				for (ProxyTable<?, ?> table : listOfProxyTablesToWrite) {
					writeTableToFile(table);
				}
				switchMetaDataFileBit();
			} catch (IOException e) {
				e.printStackTrace();
				throw new SystemException("Rethrow IOException", e);
			}
		}
	}
	
	static {
		(new File(UtilityMethods.getRootDirectory() + "Version 1 directory")).mkdirs();
		(new File(UtilityMethods.getRootDirectory() + "Version 2 directory")).mkdirs();
	}

}
