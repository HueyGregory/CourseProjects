package interfaceImplementations;

import java.util.ArrayList;
import java.util.List;

import interfaces.NonABCFilterer;

// strip out all non-alphabetic characters. Eliminate any “words” that are just white space
public class NonABCFiltererImpl implements NonABCFilterer {

	@Override
	public List<String> process(List<String> originalListOfWords) {
		List<String> newListOfWords = new ArrayList<String>(originalListOfWords.size());
		for (String word : originalListOfWords) {
			String newWord = "";
			for (int i = 0; i < word.length(); i++) {
				if (Character.isAlphabetic(word.charAt(i))) {
					newWord += word.charAt(i);
				}
			}
			if (newWord.equals("")) {
				continue;
			}
			newListOfWords.add(newWord);
		}
		return newListOfWords;
	}

}
