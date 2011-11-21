package text.output;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * An interface for items that have a xml writer function.
 * 
 * @author ksmall
 */

public interface PrintXML {
	
	/**
	 * Writes a XML representation to the input PrintWriter 
	 * 
	 * @param out	the output Writer (note that a StringWriter can just be used for testing)
	 */
	public void xml(PrintWriter out);
	
	public void xml(OutputStream out);
	
	public String xml();
}
