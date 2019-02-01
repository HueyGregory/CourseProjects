package interfaces;

public interface LineFilterer {

	public void setStringToSearchFor(final String string);
	public String process (String contentOfDoc);
}
