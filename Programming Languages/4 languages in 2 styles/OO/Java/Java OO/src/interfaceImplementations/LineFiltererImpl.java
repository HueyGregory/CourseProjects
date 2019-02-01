package interfaceImplementations;

import interfaces.LineFilterer;

public class LineFiltererImpl implements LineFilterer {
	
	String stringToSearchFor;
	
	public LineFiltererImpl() {
		
	}

	@Override
	public void setStringToSearchFor(String string) {
		this.stringToSearchFor = string;
	}

	@Override
	public String process(String contentOfDoc) {
		String[] splitString = contentOfDoc.split("\\\n");
		String matchingLines = "";
		for (int i = 0; i < splitString.length; i++) {
			if(splitString[i].toLowerCase().contains(this.stringToSearchFor.toLowerCase())) {
				matchingLines += splitString[i] + "\n";
			}
		}
		return matchingLines;
	}


}
