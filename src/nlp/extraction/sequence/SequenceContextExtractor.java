package nlp.extraction.sequence;

import java.io.PrintWriter;

import text.UtilityXML;

import ml.extraction.Extractor;
import nlp.core.Element;
import nlp.core.Sentence;
import nlp.core.Token;
import nlp.extraction.ContextExtractor;
import nlp.extraction.PropertyExtractor;

/**
 * Extracts features from the context surrounding a target sequence.
 * 
 * @author ksmall
 */
public class SequenceContextExtractor extends ContextExtractor {

	protected boolean multiStar;	 // means allowing multiple star locations (should avoid I think)
	protected boolean targetLength;  // true for Collocation and such, false (default) for Properties (going to enforce in GUI)
	public boolean markSequence;  // marks that this came from a sequence
	
	public SequenceContextExtractor(PropertyExtractor extractor, int offset, 
			int featureMode, boolean multiStar, boolean targetLength, boolean markSequence) {		
		super(extractor, offset, featureMode);
    	this.identifier = new String("SequenceContextExtractor");
		this.multiStar = multiStar;
		this.targetLength = targetLength;
		this.markSequence = markSequence;
	}

	// by default, assume we use multistar mode
	public SequenceContextExtractor(PropertyExtractor extractor, int offset, int featureMode) {
		this(extractor, offset, featureMode, true, false, false);
	}
	
	public SequenceContextExtractor(PropertyExtractor extractor, int offset) {
		this(extractor, offset, LOCATION);
	}
	
	public SequenceContextExtractor(PropertyExtractor extractor, int offset, boolean multiStar) {
		this(extractor, offset, LOCATION, multiStar, false, false);
	}
	
	public SequenceContextExtractor(org.w3c.dom.Element extractor) {
		this((PropertyExtractor) Extractor.newInstance(UtilityXML.getElement(extractor, "Extractor")),
				Integer.parseInt(UtilityXML.getNodeValue(extractor, "offset")),
				Integer.parseInt(UtilityXML.getNodeValue(extractor, "featureMode")),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "multiStar")),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "targetLength")),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "markSequence")));
	}
	
	// don't include filters as this would be in the PropertyExtractor contained
	// the object here is a Sequence which is required to have a start, end, and Token pointers
	// returns null if outside sentence boundary
	// assume separate lexicons, so we don't  have to specify token or sequence
	public String key(Object o) {
		//System.out.println("HERE");
		Element current = (Element) o;
		int start = Integer.parseInt(current.getProperty("start"));
		int end = Integer.parseInt(current.getProperty("end"));
		Sentence s = ((Token) current.getBelow(0)).getSentence(); // assume only Tokens below
		int absolute = offset;
		String result = null;
		if (offset < 0) { // 0 is really undefined and should be error checked out
			absolute += start;
			if ((absolute < 0) || (absolute + extractor.length() - 1 >= s.length()))
				return null;
			result = extractor.key((Token) s.getBelow(absolute), current);
		}
		else {
			absolute += end;
			int front = absolute - extractor.length() + 1;
			if ((absolute >= s.length()) || (front < 0))
				return null;
			result = extractor.key((Token) s.getBelow(front), current);
			if (targetLength)
				offset = offset - extractor.length() + end - start + 1;
			else 
				offset = offset - extractor.length() + 1;
		}		
		if (!multiStar)
			result = result.replaceFirst("\\*(,\\*)+", "*");
		else if (featureMode == OFFSET)
			result = "[" + result + "]";
		else if (featureMode == DIRECTION) {
			if (offset < 0)
				result = "[-," + result + "]";
			else if (offset > 0)
				result = "[+," + result + "]";
			else
				result = "[0," + result + "]";
		}
		else if (featureMode == LOCATION)
			result = "[" + offset + "," + result + "]";
		//System.out.println("h: " + result + "," + featureMode + "," + offset + "," + multiStar);
		if (markSequence)
			result = "seq(" + result + ")";
		return result;
	}
	
	public String toString() {
		String result = new String("SequenceContext");
		if (multiStar)
			result += "*";
		result += "(" + offset;
		if (markSequence)
			result += ",seq";
		result += "):";
		if (featureMode != NONE)
			result += "[";
		if (featureMode == DIRECTION) {
			if (offset < 0) 
				result += "-,";
			else if (offset > 0)
				result += "+,";
			else
				result += "0,";
		}
		else if (featureMode == LOCATION)	
			result += offset + ",";
		result += extractor;
		if (featureMode != NONE)
			result += "]";
		//if (markSequence)
		//	result += ")";
		return result;// + "}";
	}
	
	public void xml(PrintWriter out) {
		out.println("<Extractor>");
		out.println("<type>" + getClass().getName() + "</type>");
		out.println("<offset>" + offset + "</offset>");
		out.println("<featureMode>" + featureMode + "</featureMode>");
		out.println("<multiStar>" + multiStar + "</multiStar>");
		out.println("<targetLength>" + targetLength + "</targetLength>");
		out.println("<markSequence>" + markSequence + "</markSequence>");
		extractor.xml(out);
		out.println("</Extractor>");
	}
}
