import java.util.List;
import java.util.Map;

import interfaces.CaseConverter;
import interfaces.LineFilterer;
import interfaces.NonABCFilterer;
import interfaces.WordCounter;
import interfaces.WordFinder;

public class DocumentProcessor {
	
	private LineFilterer lineFilterer;
	private CaseConverter caseConverter;
	private WordFinder wordFinder;
	private NonABCFilterer nonABCFilterer;
	private WordCounter wordCounter;

	public DocumentProcessor(DocumentProcessorBuilder dPB) {
		lineFilterer = dPB.getLineFilterer();
		caseConverter = dPB.getCaseConverter();
		wordFinder = dPB.getWordFinder();
		nonABCFilterer = dPB.getNonABCFilterer();
		wordCounter = dPB.getWordCounter();
	}

	public Object process(String contentFromFile) {
		if(this.lineFilterer != null) {
			// this means that the command was "grep"
			contentFromFile = this.lineFilterer.process(contentFromFile);
		}
		if(this.caseConverter != null && this.wordFinder != null && this.nonABCFilterer != null && this.wordCounter != null) {
			// this means that the command was "wc"
			contentFromFile = this.caseConverter.process(contentFromFile);
			List<String> listOfWords = this.wordFinder.process(contentFromFile);
			listOfWords = this.nonABCFilterer.process(listOfWords);
			return this.wordCounter.process(listOfWords);
		}
		return contentFromFile;
		
	}

}
