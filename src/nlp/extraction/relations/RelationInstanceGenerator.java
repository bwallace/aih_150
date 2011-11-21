package nlp.extraction.relations;

import java.util.ArrayList;
import java.util.HashMap;

import ml.extraction.InstanceGenerator;
import ml.instance.structured.StructuredInstance;
import nlp.annotation.AnnotatedSentence;
import nlp.core.Element;
import nlp.core.Entity;
import nlp.core.Relation;
import nlp.core.Sentence;
import nlp.extraction.NLP_InstanceGenerator;
import shared.IntPair;

public class RelationInstanceGenerator extends NLP_InstanceGenerator {

    protected String entityKey;
    
    /**
     * the constructor
     * 
     * @param generator	the InstanceGenerator to be used at each sequence position
     */
	public RelationInstanceGenerator(InstanceGenerator generator, String entityKey) {
		super(generator);
		this.entityKey = entityKey;
	}
	
	public RelationInstanceGenerator(InstanceGenerator generator) {
		this(generator, "entity");
	}

	/*
	public void setEntityKey(String s) {
		entityKey = new String(s);
	}
	*/
		
	/**
	 * Generates the structured instance
	 * 
	 * @param o		the sequence of Objects from which the local instances are generated
	 * @param id	the id associated with the generated StructuredInstance
	 * @return		the resulting generated StructuredInstance
	 */
	public StructuredInstance generate(Sentence o, String id) {
		StructuredInstance result = new StructuredInstance(id);
		result.setLabel(o);  // for AnnotatedSentence purposes
		String outsideLabel = new String("0");

		//System.out.println(o);
		ArrayList<Element> relations = ((AnnotatedSentence) o).getAnnotation("relation");
		ArrayList<Element> entities = ((AnnotatedSentence) o).getAnnotation(entityKey); 
		//System.out.println(relations);
		
		HashMap<IntPair, Relation> relationHash = new HashMap<IntPair, Relation>();
		for (Element relation : relations) {
			Relation r = (Relation) relation;
			IntPair pair = new IntPair(Integer.parseInt(r.getArgument(0).getID().substring(1)),
										Integer.parseInt(r.getArgument(1).getID().substring(1)));
			relationHash.put(pair, r);
		}
		int counter = 0;
		if (entities != null) {
			for (int i = 0; i < entities.size(); i++) {
				Entity e0 = (Entity) entities.get(i);
				for (int j = i + 1; j < entities.size(); j++) {
					Entity e1 = (Entity) entities.get(j);
					IntPair pair = new IntPair(Integer.parseInt(e0.getID().substring(1)),
							Integer.parseInt(e1.getID().substring(1)));
					Relation relation = (Relation) relationHash.get(pair);
					if (relation == null) {
						relation = new Relation(outsideLabel, e0, e1);
					}
					result.addInstance(generator.generate(relation, new Integer(counter++).toString()));
				}
			}
		}
		result.trim();
		return result;
	}
}
