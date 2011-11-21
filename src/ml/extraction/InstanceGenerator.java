package ml.extraction;

import java.util.ArrayList;
import java.util.Iterator;
import ml.extraction.Lexicon;
import ml.instance.FeatureVector;
import ml.instance.Instance;
import ml.instance.SingleInstance;

/**
 * Uses a set of FeatureExtractor and LabelExtractor instances to generate an example
 * to be used for training.  As a side effect, the InstanceGenerator adds the generated
 * features and labels to global Lexicon objects which are used to reassemble predictions
 * and understand the relevant features.
 * 
 * @author ksmall
 */
public class InstanceGenerator implements Generator<Instance> {

	public ExtractID idExtractor;
	/** the list of Extractor instances associated with feature extraction */
    public ArrayList<Extractor> featureExtractors;
    /** the list of Extractor instances associated with label extraction */
    public ArrayList<Extractor> labelExtractors;
    // sequential later
    /** the Lexicon of extracted Feature instances */
    public Lexicon featureLexicon;
    /** the Lexicon of extracted Label instances */
    public Lexicon labelLexicon;
    /** the default  numbering associated with the Instance objects extracted */
    protected int nextValue = 1;  // used only if none is provided

    /**
     * the constructor
     * 
     * @param featureLexicon	a Lexicon to associate generated features with integers
     * @param labelLexicon		a Lexicon to associate generated labels with integers
     */
    public InstanceGenerator(Lexicon featureLexicon, Lexicon labelLexicon) {
    	featureExtractors = new ArrayList<Extractor>();
    	labelExtractors = new ArrayList<Extractor>();
    	idExtractor = null;
    	this.featureLexicon = featureLexicon;
    	this.labelLexicon = labelLexicon;
    }

    /**
     * Adds a feature generating function (FGF) to the list of feature extractors
     * 
     * @param fgf	the feature extractor to be added
     */
    public void addFGF(Extractor fgf) {
    	featureExtractors.add(fgf);
    }

    /**
     * Removes a feature generating function (FGF) to the list of feature extractors
     * 
     * @param fgf	the feature extractor to be removed
     */    
    public void removeFGF(Extractor fgf) {
    	featureExtractors.remove(fgf);
    }
    
    /**
     * Adds a label generating function (LGF) to the list of label extractors
     * 
     * @param lgf	the label extractor to be added
     */
    public void addLGF(Extractor lgf) {
    	labelExtractors.add(lgf);
    }

    /**
     * Generates an Instance using the current Extractor objects given an object in 
     * its native form and an id to be associated with the generated instsance.
     * 
     * @param o		the object for which an Instance to be generated
     * @param id	the id associated with the object for input; note that while
     * 				assigning two Instances the same id will likely not break the
     * 				system, it defeats the purpose of assigning them ids.  This issue
     * 				is left to the code calling this function.  If you don't want to 
     * 				handle this, use the other version of generate.
     * @return		the generated Instance
     */
    public SingleInstance generate(Object o, String id) {
    	SingleInstance result = new SingleInstance(id);
    	for (Extractor extractor : featureExtractors) {
    		//System.out.println(extractor);
    		FeatureVector features = extractor.extract(o, featureLexicon);
    		result.addFeatures(features);
    	}
    	for (Extractor extractor : labelExtractors)
    		result.addLabels(extractor.extract(o, labelLexicon));
    	result.trim();
    	return result;
    }
    
    
    /**
     * Generates an Instance using the current Extractor objects given an object in 
     * its native form.  The id for the newly generated Instance will be consecutive numbers.
     * 
     * @param o		the object for which an Instance to be generated
     * @return		the generated Instance
     */
    public Instance generate(Object o) {
    	return generate(o, (idExtractor == null) ? (new Integer(nextValue++)).toString() :
    		idExtractor.extract(o));
    }
    
    /**
     * a String representation of the InstanceGenerator which lists the
     * feature and label extractor objects.
     */
    public String toString() {
    	String result = new String("--- Feature Extractors ---\n");
    	for (Extractor extractor : featureExtractors)
    		result += extractor + "\n";
    	result += "--- Label Extractors ---\n";
    	for (Iterator<Extractor> it = labelExtractors.iterator(); it.hasNext();) {
    		result += it.next();    	
    		if (it.hasNext())
    			result += "\n";
    	}
    	return result;
    }
}
