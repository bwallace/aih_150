package nlp.extraction.filter;

import java.io.PrintWriter;

/**
 * Returns a lowercase version of the target item.
 * 
 * @author ksmall
 */
public class Lowercase extends TextFilter {

	public Lowercase() {
		super("lc");
	}
	
	public String filter(String s) {
		return s.toLowerCase();
	}

	public void xml(PrintWriter out) {
		out.println("<TextFilter>");
		out.println("<type>Lowercase</type>");
		out.println("</TextFilter>");
	}

}
