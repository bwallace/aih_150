package ml.extraction;

import java.io.PrintWriter;
import ml.extraction.Extractor;
import ml.extraction.Lexicon;
import ml.instance.Feature;
import ml.instance.FeatureVector;

/**
 * Used simply to add an active feature corresponding to a bias element for
 * linear classifier algorithms.
 * 
 * @author ksmall
 */
public class BiasElement extends Extractor {

	public static int id = Integer.MAX_VALUE;
	
	/** the constructor */
    public BiasElement() {
    	super("BiasElement");
    }

    public BiasElement(org.w3c.dom.Element specification) {
    	this();
    }
    
    /** Extractor returns a single Feature associated with the bias element. */
    public FeatureVector extract(String source, Object o, Lexicon lexicon) {
    	lexicon.add(source, toString(), id); // Integer.MAX_VALUE);
    	return new FeatureVector(new Feature(lexicon.get(toString(), identifier))); // to add counts
    }
    
	// do nothing
	public void xmlBody(PrintWriter out) { }
	
	public String toString() {
		return identifier;
	}
}
