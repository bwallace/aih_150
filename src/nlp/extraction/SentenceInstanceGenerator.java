package nlp.extraction;

import ml.extraction.InstanceGenerator;
import ml.instance.structured.StructuredInstance;
import nlp.core.Sentence;

/**
 * Generates a StructuredInstance from a given sentence.
 * 
 * @author ksmall
 */
public class SentenceInstanceGenerator extends NLP_InstanceGenerator {
    
    /**
     * the constructor
     * 
     * @param generator	the InstanceGenerator to be used at each sequence position
     */
	public SentenceInstanceGenerator(InstanceGenerator generator) {
		super(generator);
	}
	
	public StructuredInstance generate(Sentence o, String id) {
		StructuredInstance result = new StructuredInstance(id);
		result.setLabel(o);
		for (int i = 0; i < o.length(); i++) {
			result.addInstance(generator.generate(o.getBelow(i), 
					new Integer(i).toString()));
		}
		result.trim();
		return result;
	}
}
