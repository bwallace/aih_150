package nlp.extraction.filter;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import text.output.PrintXML;
import text.UtilityXML;

/**
 * Filters text for the purpose of feature extraction.
 * 
 * @author ksmall
 *
 */
public abstract class TextFilter implements PrintXML {

	protected String name;
	
	public TextFilter(String name) {
		this.name = name;
	}

	public static TextFilter decodeXML(org.w3c.dom.Element filter) {
		String type = UtilityXML.getNodeValue(filter, "type");
		//System.out.println(type + "," + printFilters);
		if (type.equals("Lowercase"))
			return new Lowercase();
		else if (type.equals("Prefix"))
			return new Prefix(filter);
		else if (type.equals("Suffix"))
			return new Suffix(filter);
		return null;
	}
	
	public String toString() {
		return name;
	}
	
	public abstract String filter(String s);
	
	public void xml(OutputStream out) {
		xml(new PrintWriter(out, true));	
	}

	public String xml() {
		StringWriter writer = new StringWriter();
		xml(new PrintWriter(writer));
		return writer.toString().trim();
	}
}
