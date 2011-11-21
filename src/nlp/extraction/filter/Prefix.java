package nlp.extraction.filter;

import java.io.PrintWriter;
import text.UtilityXML;

/**
 * Returns a prefix of the specified length from the target item.
 * 
 * @author ksmall
 */
public class Prefix extends TextFilter {

	public int length;
	
	public Prefix(int length) {
		super("pre");
		this.length = length;
	}
	
	public Prefix(org.w3c.dom.Element extractor) {
		this(Integer.parseInt(UtilityXML.getNodeValue(extractor, "length")));
	}
	
	public String toString() {
		return name + "(" + length + ")";
	}
	
	public String filter(String s) {
		if (s.length() < length)
			return null;
		return s.substring(0, length);
	}

	public void xml(PrintWriter out) {
		out.println("<TextFilter>");
		out.println("<type>Prefix</type>");
		out.println("<length>" + length + "</length>");
		out.println("</TextFilter>");
	}

}
