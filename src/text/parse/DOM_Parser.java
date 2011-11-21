package text.parse;

import java.io.File;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Parses XML documents, largely using the java supplied XML libraries.  This is 
 * very simple at this point, not even setting up a Validator.  Therefore, it is
 * very likely that this will be extended in the future including possibly more
 * sophisticated XML retrieval schemes, etc.
 * 
 * @author ksmall
 */
public class DOM_Parser {

	/** the parser */
	private DocumentBuilder parser;
	
	/**
	 * Constructor
	 */
	public DOM_Parser() {
		try {
			parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parses the desired file.
	 * 
	 * @param file	the name of the file to be parsed
	 * @return		the resulting java Document structure
	 */
	public Document parse(File file) {
		Document result = null;
		try {
			result = parser.parse(file);
		}	
		catch (Exception e) {
			e.printStackTrace();
		}	
		return result;
	}			

	/**
	 * Parses the file represented by the input String
	 * 
	 * @param fileName	the input file String
	 * @return			the resulting java Document structure
	 */
	public Document parse(String fileName) {
		return parse(new File(fileName));
	}
	
	public Document parse(Reader reader) {
		Document result = null;
		try {
			result = parser.parse(new InputSource(reader));
		}	
		catch (Exception e) {
			e.printStackTrace();
		}	
		return result;		
	}
}
