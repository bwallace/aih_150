package nlp.extraction.relations;

import java.io.PrintWriter;

import text.UtilityXML;
import ml.extraction.Extractor;
import ml.extraction.Lexicon;
import ml.instance.FeatureVector;
import nlp.core.Entity;
import nlp.core.Relation;
import nlp.extraction.PropertyExtractor;
import nlp.extraction.sequence.SequenceContextExtractor;
import nlp.extraction.sequence.SequenceWindowExtractor;

// TODO - note that there is no context extractor
public class RelationEntityWindowExtractor extends SequenceWindowExtractor {
	
	protected boolean printLocation;
	protected int extractionMode;
	
	public RelationEntityWindowExtractor(PropertyExtractor extractor, int leftOffset,
				int rightOffset, int featureMode, boolean multiStar, 
				boolean targetLength, boolean markSequence,
				int extractionMode, boolean printLocation) {
		super(extractor, leftOffset, rightOffset, featureMode, multiStar, targetLength, markSequence);
		this.printLocation = printLocation;
		this.extractionMode = extractionMode;
	}
	
	public RelationEntityWindowExtractor(PropertyExtractor extractor, int leftOffset, int rightOffset) {
		 this(extractor, leftOffset, rightOffset, 3, true, false, false, 2, true);
	}

	public RelationEntityWindowExtractor(org.w3c.dom.Element extractor) {
		this((PropertyExtractor) Extractor.newInstance(UtilityXML.getElement(extractor, "Extractor")),
				Integer.parseInt(UtilityXML.getNodeValue(extractor, "leftOffset")),
				Integer.parseInt(UtilityXML.getNodeValue(extractor, "rightOffset")),
				Integer.parseInt(UtilityXML.getNodeValue(extractor, "featureMode")),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "multiStar")),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "targetLength")),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "markSequence")),
				Integer.parseInt(UtilityXML.getNodeValue(extractor, "extractionMode")),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "printLocation")));
	}
		
	public FeatureVector extract(Object o, Lexicon lexicon) {
		//System.out.println("Here");
		Relation r = (Relation) o;	// could force this more elegantly (but unnecessary)
		Entity arg0 = r.getArgument(0);
		Entity arg1 = r.getArgument(1);
		FeatureVector result = new FeatureVector();
		for (int i = leftOffset; i <= rightOffset; i++) {
			//System.out.println(extractor.size());
			//if (rightOffset - i + 1 >= extractor.length() )
			// error checking in ContextExtractor
			if (i == 0)
				continue;
			SequenceContextExtractor ex = new SequenceContextExtractor(extractor, 
					i, featureMode, multiStar, targetLength, markSequence);
			//result.addFeatures(ex.extract(o, lexicon));
			if ((extractionMode == 0) || (extractionMode == 2) || (extractionMode == 3)) {
				if (printLocation)
					result.addFeatures(ex.wrapExtract(arg0, lexicon, "l(", ")"));
				else if (extractionMode == 3)
					result.addFeatures(ex.wrapExtract(arg0, lexicon, "e(", ")"));
				else
					result.addFeatures(ex.extract(arg0, lexicon));
			}
			if ((extractionMode == 1) || (extractionMode == 2) || (extractionMode == 3)) {
				if (printLocation)
					result.addFeatures(ex.wrapExtract(arg1, lexicon, "r(", ")"));
				else if (extractionMode == 3)
					result.addFeatures(ex.wrapExtract(arg1, lexicon, "e(", ")"));
				else
					result.addFeatures(ex.extract(arg1, lexicon));
			}
		}				
    	return result;
	}

	// not sure what this means (and if it actually does anything)
	public FeatureVector wrapExtract(Object o, Lexicon lexicon, String prefix, String suffix) {
		/*
		Relation r = (Relation) o;	// could force this more elegantly (but unnecessary)
		Entity arg0 = r.getArg(0);
		Entity arg1 = r.getArg(1);		
		*/
		FeatureVector result = new FeatureVector();

		for (int i = leftOffset; i <= rightOffset; i++) {
			//System.out.println(extractor.size());
			//if (rightOffset - i + 1 >= extractor.length() )
			// error checking in ContextExtractor
			if (i == 0)
				continue;
			RelationEntityExtractor ex = new RelationEntityExtractor(new SequenceContextExtractor(extractor, 
					i, featureMode, multiStar, targetLength, markSequence));
			result.addFeatures(ex.extract(o, lexicon));
/*
			SequenceContextExtractor ex = new SequenceContextExtractor(extractor, i, featureMode, multiStar, 
					targetLength, markSequence);
			if ((extractionMode == 0) || (extractionMode == 2) || (extractionMode == 3)) {
				if (printLocation)
					result.addFeatures(ex.wrapExtract(arg0, lexicon, prefix + "left(", ")" + suffix));
				else if (extractionMode == 3)
					result.addFeatures(ex.wrapExtract(arg0, lexicon, prefix + "ent(", ")" + suffix));
				else
					result.addFeatures(ex.wrapExtract(arg0, lexicon, prefix, suffix));
			}
			if ((extractionMode == 1) || (extractionMode == 2) || (extractionMode == 3)) {
				if (printLocation)
					result.addFeatures(ex.wrapExtract(arg1, lexicon, prefix + "right(", ")" + suffix));
				else if (extractionMode == 3)
					result.addFeatures(ex.wrapExtract(arg1, lexicon, prefix + "ent(", ")" + suffix));
				else
					result.addFeatures(ex.wrapExtract(arg1, lexicon, prefix, suffix));
			}
*/
		}
		return result;
	}	
	
	public void xml(PrintWriter out) {
		out.println("<Extractor>");
		out.println("   <type>" + getClass().getName() + "</type>");
		out.println("   <leftOffset>" + leftOffset + "</leftOffset>");
		out.println("   <rightOffset>" + rightOffset + "</rightOffset>");
		out.println("   <featureMode>" + featureMode + "</featureMode>");
		out.println("   <extractionMode>" + extractionMode + "</extractionMode>");
		out.println("   <markSequence>" + markSequence + "</markSequence>");
		out.println("   <printLocation>" + printLocation + "</printLocation>");
		// TODO -- think more about multiStar and targetLength
		out.println("<multiStar>" + multiStar + "</multiStar>");
		out.println("<targetLength>" + targetLength + "</targetLength>");
		extractor.xml(out);
		out.println("</Extractor>");
	}	
	
	public String toString() {
		String result = new String("RelationEntityWindow(");
		if (extractionMode == RelationEntityExtractor.LEFT)
			result += "left,";
		else if (extractionMode == RelationEntityExtractor.RIGHT)
			result += "right,";
		else if (extractionMode == RelationEntityExtractor.BOTH)
			result += "both,";
		result += leftOffset + "," + rightOffset + "):";
		if (printLocation)
			result += "l/r(";
		if (featureMode != NONE)
			result += "[";
		if (featureMode == DIRECTION) 
			result += "+/-,";
		else if (featureMode == LOCATION)	
			result +=  "#,";
		result += extractor;
		if (featureMode != NONE)
			result += "]";
		if (printLocation)
			result += ")";
		return result;// + "}";
	}

}
