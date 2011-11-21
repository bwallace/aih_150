package nlp.extraction.sequence;

import java.io.PrintWriter;
import java.util.Iterator;

import text.UtilityXML;

import nlp.core.Element;
import nlp.core.Segment;
import nlp.core.Token;
import nlp.extraction.PropertyExtractor;
import ml.extraction.Extractor;
import ml.extraction.Lexicon;
import ml.instance.FeatureVector;

/**
 * A conveneince class for applying an extractor to a set of elements.
 * 
 * @author ksmall
 */
public class SetExtractor extends Extractor {

	public Extractor extractor;
	public boolean markSequence;
	
	public SetExtractor(Extractor extractor, boolean markSequence) {
		super("SetExtractor");
		this.extractor = extractor;
		this.markSequence = markSequence;
	}

	public SetExtractor(Extractor extractor) {
		this(extractor, false);
	}
		
	public SetExtractor(org.w3c.dom.Element extractor) {
		this((PropertyExtractor) Extractor.newInstance(UtilityXML.getElement(extractor, "Extractor")),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "markSequence")));
	}
	
	public FeatureVector extract(String source, Object o, Lexicon lexicon) {
		Segment s = (Segment) o;	// could force this more elegantly (would need sequence superclass for Entity
    	FeatureVector result = new FeatureVector();
    	for (Iterator<Element> it = s.belowIterator(); it.hasNext(); ) {
    		if (markSequence)
    			result.addFeatures(extractor.wrapExtract(source, (Token) it.next(), lexicon, "seq(", ")"));
    		else
    			result.addFeatures(extractor.extract(source, (Token) it.next(), lexicon));
    	}
    	return result;
	}

	// TODO: this was changed 2/17 to accommodate markSequence
	public FeatureVector wrapExtract(String source, Object o, Lexicon lexicon, String prefix, String suffix) {
		Segment s = (Segment) o;	// could force this more elegantly (would need sequence superclass for Entity
    	FeatureVector result = new FeatureVector();
    	for (Iterator<Element> it = s.belowIterator(); it.hasNext(); ) {
			//result.addFeatures(extractor.wrapExtract((Token) it.next(), lexicon, prefix, suffix));
			if (markSequence)
				result.addFeatures(extractor.wrapExtract(source, (Token) it.next(), lexicon, prefix + "seq(", ")" + suffix));
			else
				//result.addFeatures(extractor.wrapExtract((Token) it.next(), lexicon, "seq(", ")"));
				result.addFeatures(extractor.wrapExtract(source, (Token) it.next(), lexicon, prefix, suffix));
    	}
    	return result;
	}
	
	public String toString() {
		String result = new String("Set[");
		if (markSequence)
			result += "seq(";
		result += extractor;
		if (markSequence)
			result += ")";
		return result + "]";
	}
	
	public void xml(PrintWriter out) {
		out.println("<Extractor>");
		out.println("<type>" + getClass().getName() + "</type>");
		out.println("<markSequence>" + markSequence + "</markSequence>");
		extractor.xml(out);
		out.println("</Extractor>");
	}
}
