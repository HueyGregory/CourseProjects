package interfaceImplementations;

import interfaces.CaseConverter;

//  put everything in lowercase
public class CaseConverterImpl implements CaseConverter {

	@Override
	public String process(String contentFromFile) {
		return contentFromFile.toLowerCase();
	}

}
