package nlp.extraction;

import nlp.core.Sentence;
import ml.extraction.Generator;
import ml.extraction.InstanceGenerator;
import ml.instance.Instance;
import ml.instance.structured.StructuredInstance;

public abstract class NLP_InstanceGenerator implements Generator<Instance> {

	/** the local InstanceGenerator for each Instance in the StructuredInstance */
	protected InstanceGenerator generator;
	/** the id of the StructuredInstance if none is specified - use in most cases */
    protected int nextValue = 1;  // used only if none is provided
    
    /**
     * the constructor
     * 
     * @param generator	the InstanceGenerator to be used at each sequence position
     */
	public NLP_InstanceGenerator(InstanceGenerator generator) {
		this.generator = generator;
	}

	/**
	 * Generates the structured instance
	 * 
	 * @param o		the sequence of Objects from which the local instances are generated
	 * @param id	the id associated with the generated StructuredInstance
	 * @return		the resulting generated StructuredInstance
	 */
	public abstract StructuredInstance generate(Sentence o, String id);
	
	/**
	 * Generates the structured instance with the default numbering scheme.
	 * 
	 * @param o	the sequence of Objects from which the local instances are generated
	 * @return	the resulting generated StructuredInstance
	 */
	public StructuredInstance generate(Sentence o) {
	    return generate(o, (new Integer(nextValue++)).toString());
	}	
	
	// TODO - this is eventually going to have to be expanded to documents, etc.
	public StructuredInstance generate(Object o) {
		return generate((Sentence) o);
	}
}
