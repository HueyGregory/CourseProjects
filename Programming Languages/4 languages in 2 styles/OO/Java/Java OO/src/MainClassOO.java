import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import interfaceImplementations.CaseConverterImpl;
import interfaceImplementations.LineFiltererImpl;
import interfaceImplementations.NonABCFiltererImpl;
import interfaceImplementations.WordCounterImpl;
import interfaceImplementations.WordFinderImpl;
import interfaces.CaseConverter;
import interfaces.LineFilterer;
import interfaces.NonABCFilterer;
import interfaces.WordCounter;
import interfaces.WordFinder;

public class MainClassOO {
		
	private MainClassOO(final String[] args) {
		Object[] objectsForDPB = parseCommands(args);
		String stringDoc = parseArguments(args, objectsForDPB);
		String contentFromFile = readFromFile(stringDoc);
		
		DocumentProcessorBuilder DPB = DPBFactory(objectsForDPB);
		
		DocumentProcessor DP = build(DPB);
		Object result = DP.process(contentFromFile);
		if (result instanceof String) {
			System.out.println((String) result); 
		}
		else if (result instanceof Map) {
			Map<String, Integer> mapResult = (Map<String, Integer>) result;
			for (Map.Entry<String, Integer> entry : mapResult.entrySet()) {
				System.out.println(entry.getKey() + " " + entry.getValue());
			}
		}
	}

	// called from constructor
	private DocumentProcessorBuilder DPBFactory(Object[] objectsForDPB) {
		DocumentProcessorBuilder DPB = new DocumentProcessorBuilder();
		DPB.setLineFilterer((LineFilterer) objectsForDPB[0]);
		DPB.setCaseConverter((CaseConverter) objectsForDPB[1]);
		DPB.setWordFinder((WordFinder) objectsForDPB[2]);
		DPB.setNonABCFilterer((NonABCFilterer) objectsForDPB[3]);
		DPB.setWordCounter((WordCounter) objectsForDPB[4]);
		return DPB;
	}

	// called from constructor
	private String readFromFile(String stringDoc) {
		File file = new File(stringDoc);
		String allLines = "";
		try (BufferedReader br = new BufferedReader(new FileReader(file));) {
			String tempString;
			while ((tempString = br.readLine()) != null) {
				allLines += tempString + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem with reading from the file", e);
		} 
		return allLines;
	}

	// called from constructor
	private DocumentProcessor build(DocumentProcessorBuilder dPB) {
		return new DocumentProcessor(dPB);
	}

	// called from constructor
	private String parseArguments(String[] args, Object[] classObjects) {
		if (classObjects[0] != null) {
			// this means that the "grep" command has been used
			((LineFilterer) classObjects[0]).setStringToSearchFor(args[1]);
			return args[2];
		}
		else {
			// this means that only the "wc" command has been used
			return args[1];
		}
	}

	// called from constructor
	private Object[] parseCommands(String[] args) {
		String secondCommand = null;
		if (args.length > 4) {
			secondCommand = args[4];
		}
		else if (args.length < 2){
			throw new IllegalArgumentException();
		}
		return fillObjectsToReturn(args[0], secondCommand);
	}

	private Object[] fillObjectsToReturn(String stringArgsZero, String argsWC) {
		LineFilterer lineFilterer = null;
		CaseConverter caseConverter = null;
		NonABCFilterer nonABCFilterer = null;
		WordCounter wordCounter = null;
		WordFinder wordFinder = null;
		
		if (stringArgsZero.equals("grep")) {
			lineFilterer = new LineFiltererImpl();
		}
		if (stringArgsZero.equals("wc") || (argsWC != null && argsWC.equals("wc"))) {
			caseConverter = new CaseConverterImpl();
			wordFinder = new WordFinderImpl();
			nonABCFilterer = new NonABCFiltererImpl();
			wordCounter = new WordCounterImpl();
		}
		else if (!(stringArgsZero.equals("grep"))){
			throw new IllegalArgumentException();
		}
		return new Object[] { lineFilterer, caseConverter, wordFinder, nonABCFilterer, wordCounter };
	}

	public static void main(final String[] args) {
	//	printArgs(args);
		new MainClassOO(args);
	}

	private static void printArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
		}
	}

}
