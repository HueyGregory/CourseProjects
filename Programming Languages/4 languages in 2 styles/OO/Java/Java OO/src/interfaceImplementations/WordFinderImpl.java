package interfaceImplementations;

import java.util.ArrayList;
import java.util.List;

import interfaces.WordFinder;

// split the text into individual words  
public class WordFinderImpl implements WordFinder {

	@Override
	public List<String> process(String contentFromFile) {
		List<String> listToReturn = new ArrayList<String>();
		String[] splitString = contentFromFile.split(" ");
		for (int i = 0; i < splitString.length; i++) {
			listToReturn.add(splitString[i]);
		}
		return listToReturn;
	}

}
