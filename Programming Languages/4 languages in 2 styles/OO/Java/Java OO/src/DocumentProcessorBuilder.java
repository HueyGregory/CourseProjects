import interfaces.CaseConverter;
import interfaces.LineFilterer;
import interfaces.NonABCFilterer;
import interfaces.WordCounter;
import interfaces.WordFinder;

public class DocumentProcessorBuilder {
	
	private LineFilterer lineFilterer;
	private CaseConverter caseConverter;
	private WordFinder wordFinder;
	private NonABCFilterer nonABCFilterer;
	private WordCounter wordCounter;

	public DocumentProcessorBuilder() {
		
	}

	public LineFilterer getLineFilterer() {
		return lineFilterer;
	}

	public void setLineFilterer(LineFilterer lineFilterer) {
		this.lineFilterer = lineFilterer;
	}

	public CaseConverter getCaseConverter() {
		return caseConverter;
	}

	public void setCaseConverter(CaseConverter caseConverter) {
		this.caseConverter = caseConverter;
	}

	public WordFinder getWordFinder() {
		return wordFinder;
	}

	public void setWordFinder(WordFinder wordFinder) {
		this.wordFinder = wordFinder;
	}

	public NonABCFilterer getNonABCFilterer() {
		return nonABCFilterer;
	}

	public void setNonABCFilterer(NonABCFilterer nonABCFilterer) {
		this.nonABCFilterer = nonABCFilterer;
	}

	public WordCounter getWordCounter() {
		return wordCounter;
	}

	public void setWordCounter(WordCounter wordCounter) {
		this.wordCounter = wordCounter;
	}
	
}
