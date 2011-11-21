package nlp.extraction.relations;

import java.io.PrintWriter;
import text.UtilityXML;
import nlp.core.Entity;
import nlp.core.Relation;
import ml.extraction.Extractor;
import ml.extraction.Lexicon;
import ml.instance.FeatureVector;

// this handles directionality, etc.
public class RelationEntityExtractor extends Extractor {

	protected boolean printLocation;
	protected int extractionMode;  // 0 = left, 1 = right, 2 = both, 3 = both w/entity
	protected Extractor extractor; // must be Extractor due to context/window
	
	public static int LEFT = 0;
	public static int RIGHT = 1;
	public static int BOTH = 2;
	public static int BOTH_E = 3;
	
	public RelationEntityExtractor(Extractor extractor, int extractionMode, boolean printLocation) {
		super("RelationEntityExtractor");
		this.extractor = extractor;
		this.extractionMode = extractionMode;
		this.printLocation = printLocation;
	}
	
	public RelationEntityExtractor(Extractor extractor) {
		this(extractor, 2, true);
	}

	public RelationEntityExtractor(org.w3c.dom.Element extractor) {
		this(Extractor.newInstance(UtilityXML.getElement(extractor, "Extractor")), 
				Integer.parseInt(UtilityXML.getNodeValue(extractor, "extractionMode")),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "printLocation")));
	}
	
	public FeatureVector extract(String source, Object o, Lexicon lexicon) {
		Relation r = (Relation) o;	// could force this more elegantly (but unnecessary)
		Entity arg0 = r.getArgument(0);
		Entity arg1 = r.getArgument(1);
		
		FeatureVector result = new FeatureVector();
		
		if ((extractionMode == 0) || (extractionMode == 2) || (extractionMode == 3)) {
			if (printLocation)
				result.addFeatures(extractor.wrapExtract(source, arg0, lexicon, "l(", ")"));
			else if (extractionMode == 3)
				result.addFeatures(extractor.wrapExtract(source, arg0, lexicon, "e(", ")"));
			else
				result.addFeatures(extractor.extract(source, arg0, lexicon));
		}
		if ((extractionMode == 1) || (extractionMode == 2) || (extractionMode == 3)) {
			if (printLocation)
				result.addFeatures(extractor.wrapExtract(source, arg1, lexicon, "r(", ")"));
			else if (extractionMode == 3)
				result.addFeatures(extractor.wrapExtract(source, arg1, lexicon, "e(", ")"));
			else
				result.addFeatures(extractor.extract(source, arg1, lexicon));
		}
		
		return result;
	}
	
	// don't see why I would need it, but extractWrap would be necessary to implement (as per SetTokenExtractor)
	// 3/18/08 - this still needs to be thought about
	public FeatureVector wrapExtract(String source, Object o, Lexicon lexicon, String prefix, String suffix) {
		Relation r = (Relation) o;	// could force this more elegantly (but unnecessary)
		Entity arg0 = r.getArgument(0);
		Entity arg1 = r.getArgument(1);
		
		FeatureVector result = new FeatureVector();
		
		if ((extractionMode == 0) || (extractionMode == 2) || (extractionMode == 3)) {
			if (printLocation)
				result.addFeatures(extractor.wrapExtract(source, arg0, lexicon, prefix + "l(", ")" + suffix));
			else if (extractionMode == 3)
				result.addFeatures(extractor.wrapExtract(source, arg0, lexicon, prefix + "e(", ")" + suffix));
			else
				result.addFeatures(extractor.wrapExtract(source, arg0, lexicon, prefix, suffix));
		}
		if ((extractionMode == 1) || (extractionMode == 2) || (extractionMode == 3)) {
			if (printLocation)
				result.addFeatures(extractor.wrapExtract(source, arg1, lexicon, prefix + "r(", ")" + suffix));
			else if (extractionMode == 3)
				result.addFeatures(extractor.wrapExtract(source, arg1, lexicon, prefix + "e(", ")" + suffix));
			else
				result.addFeatures(extractor.wrapExtract(source, arg1, lexicon, prefix, suffix));
		}
		
		return result;
	}

	public void xml(PrintWriter out) {
		out.println("<Extractor>");
		out.println("   <type>" + getClass().getName() + "</type>");
		out.println("   <extractionMode>" + extractionMode + "</extractionMode>");
		out.println("   <printLocation>" + printLocation + "</printLocation>");
		extractor.xml(out);
		out.println("</Extractor>");
	}
		
	public String toString() {
		String result = new String("RelationEntity(");
		if (extractionMode == RelationEntityExtractor.LEFT)
			result += "left";
		else if (extractionMode == RelationEntityExtractor.RIGHT)
			result += "right";
		else if (extractionMode == RelationEntityExtractor.BOTH)
			result += "both";
		result += "):";
		if (printLocation)
			result += "l/r(";
		result += extractor;
		if (printLocation)
			result += ")";
		return result;// + "}";
	}

}
