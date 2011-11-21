package nlp.extraction.filter;

import java.io.PrintWriter;

import text.UtilityXML;

/**
 * Returns a suffix of the specified length from the target item.
 * 
 * @author ksmall
 */
public class Suffix extends TextFilter {

	public int length;
	
	public Suffix(int length) {
		super("suf");
		this.length = length;
	}
	
	public Suffix(org.w3c.dom.Element extractor) {
		this(Integer.parseInt(UtilityXML.getNodeValue(extractor, "length")));
	}
	
	public String toString() {
		return name + "(" + length + ")";
	}
	
	public String filter(String s) {
		if (s.length() < length)
			return null;
		return s.substring(s.length() - length);
	}

	public void xml(PrintWriter out) {
		out.println("<TextFilter>");
		out.println("<type>Suffix</type>");
		out.println("<length>" + length + "</length>");
		out.println("</TextFilter>");
	}
}
