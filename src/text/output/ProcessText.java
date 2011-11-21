package text.output;

import java.io.PrintWriter;

import org.xml.sax.InputSource;

/**
 * An interface used by xml.server.ServerThread which is expected to take as
 * input a parsed XML document and return a String which will be sent to the
 * server.  Note that ServerThread isn't thread safe (I think) at this time
 * and therefore, I would recommend only reading the XML document.
 * 
 * @author ksmall
 */

public interface ProcessText {
	
	/**
	 * Processes XML documents and outputs a String 
	 * 
	 * @param data		the input XML Document
	 * @param out		the output Writer (note that a StringWriter can just be used for testing)
	 * @throws Exception 
	 */
	public void process(InputSource data, PrintWriter out);
	
	// a factory in case state is maintained for servers (or anything multi-threaded)
	// note that this should also copy things read form files so they don't have to be re-read
	public ProcessText newInstance();
}
