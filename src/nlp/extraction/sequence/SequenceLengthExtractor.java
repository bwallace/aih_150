package nlp.extraction.sequence;

import java.io.PrintWriter;

import nlp.core.Segment;
import nlp.extraction.PropertyExtractor;

/**
 * Extracts the length of a sequence of items.
 * 
 * @author ksmall
 */
public class SequenceLengthExtractor extends PropertyExtractor {

	public SequenceLengthExtractor() {
		super("null");
		this.identifier = new String("SequenceLengthExtractor");
	}
	
	public SequenceLengthExtractor(org.w3c.dom.Element extractor) {
		this();
	}

	
	// assumes Segment
	public String key(Object o, Object target) {
		Segment s = (Segment) o;
		int length = Integer.parseInt(s.getProperty("end")) -
			Integer.parseInt(s.getProperty("start")) + 1;
    	if (length >= 0) 
    		return new String("SequenceLength=" + length);
    	return null;
    }
	
	public String toString() {
		//return new String("SequenceLength=<ARG0[int]>");
		return new String("SequenceLength");
	}
	
	public void xml(PrintWriter out) {
		out.println("<Extractor>");
		out.println("   <type>" + getClass().getName() + "</type>");
		out.println("</Extractor>");
	}	
}
