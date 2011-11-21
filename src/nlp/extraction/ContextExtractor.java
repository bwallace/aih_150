package nlp.extraction;

import java.io.PrintWriter;

import text.UtilityXML;

import nlp.core.Sentence;
import nlp.core.Token;
import ml.extraction.Extractor;
import ml.extraction.Lexicon;
import ml.instance.Feature;
import ml.instance.FeatureVector;

/**
 * Extracts items within a given context from the target.
 * 
 * @author ksmall
 */
public class ContextExtractor extends Extractor {

	protected PropertyExtractor extractor;
	protected int offset;
	protected int featureMode;
	/*
	 * featureMode is what is kept from the extraction
	 * 0 - all properties are treated equally
	 * 1 - marks as being an offset (not the target word)
	 * 2 - marks as being to the left or right of the target
	 * 3 - marks complete location information
	 */
	public final static int NONE = 0;
	public final static int OFFSET = 1;
	public final static int DIRECTION = 2;
	public final static int LOCATION = 3;
	
	public ContextExtractor(PropertyExtractor extractor, int offset, int featureMode) {
		super("ContextExtractor");
		this.extractor = extractor;
		this.offset = offset;
		this.featureMode = featureMode;
	}
	
	public ContextExtractor(PropertyExtractor extractor, int offset) {
		this(extractor, offset, LOCATION);  // by default print all info
	}
	
	// keeping full Element path for clarity
	// don't do any validating for PropertyExtractor type
	// note that this will only return descendants
	public ContextExtractor(org.w3c.dom.Element extractor) {
		this((PropertyExtractor) Extractor.newInstance(UtilityXML.getElement(extractor, "Extractor")),
				Integer.parseInt(UtilityXML.getNodeValue(extractor, "offset")),
				Integer.parseInt(UtilityXML.getNodeValue(extractor, "featureMode")));
	}
	
	public FeatureVector extract(String source, Object o, Lexicon lexicon) {
    	FeatureVector result = new FeatureVector();
    	String key = key(o);
    	if (key != null)
    		result.addFeature(new Feature(lexicon.get(source, key)));
    	return result;
	}
	
	public FeatureVector wrapExtract(String source, Object o, Lexicon lexicon, String prefix, String suffix) {
    	FeatureVector result = new FeatureVector();
    	String key = key(o);
    	if (key != null) {
    		key = prefix + key + suffix;
    		result.addFeature(new Feature(lexicon.get(source, key)));
    	}
    	return result;
	}
	
	// not quite sure when this is used
	public int offset() {
		return offset;
	}
	
	// don't include filters as this would be in the PropertyExtractor contained
	// the object here is a Token which is required to have a position property and Sentence pointer
	// returns null if outside sentence boundary
	// now also handles collocation
	public String key(Object o) {
		Token current = (Token) o;
		int position = Integer.parseInt(current.getProperty("position"));
		Sentence s = (Sentence) current.getSentence();
		int absolute = position + offset;
		//System.out.println(s);
		if ((absolute < 0) || (absolute >= s.length()))
			return null;
		String result = null;
		if (offset < 0) {
			if (absolute + extractor.length() - 1 >= s.length())
				return null;
			result = extractor.key((Token) s.getBelow(absolute), current);		
		}
		else {
			//System.out.println(absolute + ", " + extractor.length());
			int start = absolute - extractor.length() + 1;
			if (start < 0)
				return null;
			/*
			else if (start == position)
				return "*";
				*/
			result = extractor.key((Token) s.getBelow(start), current);
			offset = offset - extractor.length() + 1;
		}
		//System.out.println(extractor + "," + result);
		/*
		if (featureMode == 0)
			return result;
			*/
		if (result == null)
			return null;
		else if (featureMode == NONE)
			return result;
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
		else // LOCATION
			result = "[" + offset + "," + result + "]";
		return result;
	}

	public String toString() {
		String result = new String("Context(" + offset + "):");
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
		return result;// + "}";
	}

	/*
	protected PropertyExtractor extractor;
	protected int offset;
	protected int featureMode;
	 */
	
	public void xml(PrintWriter out) {
		out.println("<Extractor>");
		out.println("<type>" + getClass().getName() + "</type>");
		out.println("<offset>" + offset + "</offset>");
		out.println("<featureMode>" + featureMode + "</featureMode>");
		extractor.xml(out);
		out.println("</Extractor>");
	}
}
