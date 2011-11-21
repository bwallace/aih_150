package nlp.extraction;

import java.io.PrintWriter;

import text.UtilityXML;
import ml.extraction.Extractor;
import ml.extraction.Lexicon;
import ml.instance.FeatureVector;

/**
 * Extractor used to generate features in a window around the target object.
 * 
 * @author ksmall
 */
public class WindowExtractor extends Extractor {

	protected PropertyExtractor extractor;
	protected int leftOffset;
	protected int rightOffset;
	protected int featureMode;

	public final static int NONE = 0;
	public final static int OFFSET = 1;
	public final static int DIRECTION = 2;
	public final static int LOCATION = 3;
	
	public WindowExtractor(PropertyExtractor extractor, int leftOffset,
			int rightOffset, int featureMode) {
		super("WindowExtractor");
  		this.extractor = extractor;
		this.leftOffset = leftOffset;
		this.rightOffset = rightOffset;
		this.featureMode = featureMode;
	}

	public WindowExtractor(PropertyExtractor extractor, int leftOffset, int rightOffset) {
		this(extractor, leftOffset, rightOffset, LOCATION);
	}
		
	// keeping full Element path for clarity
	// don't do any validating for PropertyExtractor type
	// note that this will only return descendants
	public WindowExtractor(org.w3c.dom.Element extractor) {
		this((PropertyExtractor) Extractor.newInstance(UtilityXML.getElement(extractor, "Extractor")),
				Integer.parseInt(UtilityXML.getNodeValue(extractor, "leftOffset")),
				Integer.parseInt(UtilityXML.getNodeValue(extractor, "rightOffset")),
				Integer.parseInt(UtilityXML.getNodeValue(extractor, "featureMode")));
	}

	
	public FeatureVector extract(String source, Object o, Lexicon lexicon) {
    	FeatureVector result = new FeatureVector();
		for (int i = leftOffset; i <= rightOffset; i++) {
			if (i == 0)
				continue;
			result.addFeatures(new ContextExtractor(extractor, i, featureMode).extract(source, o, lexicon));
		}				
    	return result;
	}

	public FeatureVector wrapExtract(String source, Object o, Lexicon lexicon, String prefix, String suffix) {
    	FeatureVector result = new FeatureVector();
		for (int i = leftOffset; i <= rightOffset; i++) {
			if (i == 0)
				continue;
			result.addFeatures(new ContextExtractor(extractor, i, featureMode).wrapExtract(source, o, lexicon, prefix, suffix));
		}				
    	return result;
	}
	
	public String toString() {
		String result = new String("Window(" + leftOffset + "," + rightOffset + "):");
		if (featureMode != NONE)
			result += "[";
		if (featureMode == DIRECTION) 
			result += "+/-,";
		else if (featureMode == LOCATION)	
			result +=  "#,";
		result += extractor;
		if (featureMode != NONE)
			result += "]";
		return result;// + "}";
	}

	public void xml(PrintWriter out) {
		out.println("<Extractor>");
		out.println("<type>" + getClass().getName() + "</type>");
		out.println("<leftOffset>" + leftOffset + "</leftOffset>");
		out.println("<rightOffset>" + rightOffset + "</rightOffset>");
		out.println("<featureMode>" + featureMode + "</featureMode>");
		extractor.xml(out);
		out.println("</Extractor>");
	}
}
