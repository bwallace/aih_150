package nlp.extraction.sequence;

import java.io.PrintWriter;

import text.UtilityXML;

import nlp.extraction.PropertyExtractor;
import nlp.extraction.WindowExtractor;
import ml.extraction.Extractor;
import ml.extraction.Lexicon;
import ml.instance.FeatureVector;

/**
 * Extracts features from a window surrounding a sequence of items (such as segmented text).
 * 
 * @author ksmall
 */
public class SequenceWindowExtractor extends WindowExtractor {
	
	protected boolean multiStar;
	protected boolean targetLength;
	public boolean markSequence;
	
	public SequenceWindowExtractor(PropertyExtractor extractor, int leftOffset,
				int rightOffset, int featureMode, boolean multiStar, 
				boolean targetLength, boolean markSequence) {
		super(extractor, leftOffset, rightOffset, featureMode);
		this.identifier = new String("SequenceWindowExtractor");
		this.targetLength = targetLength;
		this.markSequence = markSequence;
		this.multiStar = multiStar;
	}
	
	public SequenceWindowExtractor(PropertyExtractor extractor, int leftOffset, int rightOffset) {
		 this(extractor, leftOffset, rightOffset, LOCATION, true, false, false);
	}

	public SequenceWindowExtractor(org.w3c.dom.Element extractor) {
		this((PropertyExtractor) Extractor.newInstance(UtilityXML.getElement(extractor, "Extractor")),
				Integer.parseInt(UtilityXML.getNodeValue(extractor, "leftOffset")),
				Integer.parseInt(UtilityXML.getNodeValue(extractor, "rightOffset")),
				Integer.parseInt(UtilityXML.getNodeValue(extractor, "featureMode")),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "multiStar")),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "targetLength")),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "markSequence")));
	}
	
	public FeatureVector extract(Object o, Lexicon lexicon) {
    	FeatureVector result = new FeatureVector();
		for (int i = leftOffset; i <= rightOffset; i++) {
			if (i == 0)
				continue;
			result.addFeatures(new SequenceContextExtractor(extractor, i, featureMode, multiStar, 
					targetLength, markSequence).extract(o, lexicon));
		}				
    	return result;
	}
	
	public FeatureVector wrapExtract(Object o, Lexicon lexicon, String prefix, String suffix) {
    	FeatureVector result = new FeatureVector();
		for (int i = leftOffset; i <= rightOffset; i++) {
			if (i == 0)
				continue;
			result.addFeatures(new SequenceContextExtractor(extractor, i, featureMode, multiStar, 
					targetLength, markSequence).wrapExtract(o, lexicon, prefix, suffix));
		}				
    	return result;
	}
	
	public String toString() {
		String result = new String("SequenceWindow");
		if (multiStar)
			result += "*";
		result += "(" + leftOffset + "," + rightOffset + "){";
		if (markSequence)
			result += "seq(";
		if (featureMode != NONE)
			result += "[";
		if (featureMode == DIRECTION)
			result += "+/-,";
		else if (featureMode == LOCATION)	
			result +=  "#,";
		result += extractor;
		if (featureMode != NONE)
			result += "]";
		if (markSequence)
			result += ")";
		return result + "}";
	}
		
	public void xml(PrintWriter out) {
		out.println("<Extractor>");
		out.println("<type>" + getClass().getName() + "</type>");
		out.println("<leftOffset>" + leftOffset + "</leftOffset>");
		out.println("<rightOffset>" + rightOffset + "</rightOffset>");
		out.println("<featureMode>" + featureMode + "</featureMode>");
		out.println("<multiStar>" + multiStar + "</multiStar>");
		out.println("<targetLength>" + targetLength + "</targetLength>");
		out.println("<markSequence>" + markSequence + "</markSequence>");
		extractor.xml(out);
		out.println("</Extractor>");
	}
}
