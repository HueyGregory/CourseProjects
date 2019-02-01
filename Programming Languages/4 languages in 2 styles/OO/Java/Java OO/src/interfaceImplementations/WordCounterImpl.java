package interfaceImplementations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interfaces.WordCounter;

//  produce a set of unique words and the number of times they each appear 
public class WordCounterImpl implements WordCounter {

	@Override
	public Map<String, Integer> process(List<String> listOfWords) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (String word : listOfWords) {
			if (!map.containsKey(word)) {
				map.put(word, 1);
			}
			else if (map.containsKey(word)) {
				map.put(word, map.get(word) + 1);
			}
		}
		return map;
	}

}
