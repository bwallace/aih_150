package nlp;

import java.util.regex.Pattern;

public class Globals {

	public static final String NO_RELATION_LABEL = "NO_RELATION";
	public static final String NO_RELATION_ID = "NO_RELATION";
	
	public static Pattern firstLetterCapitalized = Pattern.compile("[A-Z].*");
	//public static Pattern firstLetterCapitalized = Pattern.compile("[A-Z``].*");
	// assume word splitter handles remainder
	// probably going to have to do this more thoroughly at some point
	public static String[] defaultPunctuation = {".", "?", "!", 
		".''", "?''", "!''",	// normal quotes 
		".'''", "?'''", "!'''",	// nested quotes
		".'", "?'", "!'",		// single quotes
		".)", "?)", "!)"};		// parentheses
	//"---", ":"}; 
	public static String[] defaultStartQuotation = {"``"};

}
