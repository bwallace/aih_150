package nlp.extraction.sequence;

import java.util.ArrayList;
import java.util.Iterator;

import ml.extraction.InstanceGenerator;
import ml.instance.structured.StructuredInstance;
import nlp.annotation.AnnotatedSentence;
import nlp.core.Element;
import nlp.core.Sentence;
import nlp.extraction.NLP_InstanceGenerator;

/**
 * Generates a structured instance composed of segments of text in a sentence.
 * 
 * @author ksmall
 */
public class EntityInstanceGenerator extends NLP_InstanceGenerator {

	protected String name;
	
	 /**
     * the constructor
     * 
     * @param generator	the InstanceGenerator to be used at each sequence position
     */
	public EntityInstanceGenerator(InstanceGenerator generator, String name) {
		super(generator);
		this.name = name;
	}
	
	public StructuredInstance generate(Sentence o, String id) {
		StructuredInstance result = new StructuredInstance(id);
		result.setLabel(o);
		ArrayList<Element> pEntities = ((AnnotatedSentence) o).getAnnotation(name);
		if (pEntities == null)
			//return null;
			return result;
		int position = 0;
		for (Iterator<Element> it = pEntities.iterator(); it.hasNext(); ) {
			//Entity e = new Entity((Segment) it.next());  // a bit hacky
			result.addInstance(generator.generate(it.next(), new Integer(position++).toString()));
		}
		result.trim();
		return result;
	}
}
