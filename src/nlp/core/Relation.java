package nlp.core;

import nlp.annotation.AnnotatedSentence;
import nlp.annotation.Annotation;

import org.w3c.dom.NodeList;


/**
 * Represents a Relation between a set of Entity items.
 * 
 * @author ksmall
 */

public class Relation extends Element implements Annotation<Relation> {
	
	/**
	 * The default constructor.
	 */
	public Relation() {
		this("", "");
	}

	/**
	 * Constructor that also sets the value of {@code id} and {@code label}
	 * 
	 * @param id	the id associated with this Relation
	 * @param label	the label associated with this Relation
	 */
	public Relation(String id, String label) {
		super(id);
		setProperty("label", label);
	}

	public Relation(org.w3c.dom.Element e, AnnotatedSentence sentence, String args) {
		//String label = e.getFirstChild().getNodeValue().trim();
		String label = e.getElementsByTagName("label").item(0).getFirstChild().getNodeValue().trim(); 
		String id = e.getElementsByTagName("id").item(0).getFirstChild().getNodeValue().trim();
		setProperty("label", label);
		setID(id);
		NodeList arguments = e.getElementsByTagName("arg");
 		for (int i = 0; i < arguments.getLength(); i++) {
 			String argID = ((org.w3c.dom.Element) arguments.item(i)).getFirstChild().getNodeValue().trim();
 			for (Element argument : sentence.getAnnotation(args)) {
 				if (argument.getID().equals(argID))
 					addBelow(argument);
 			}
 		}
	}
	
	public Relation addAnnotation(org.w3c.dom.Element e, AnnotatedSentence sentence, String args) {
		return new Relation(e, sentence, args);
	}

	// id = R0 because this is for negative relation examples
	public Relation(String label, Entity e0, Entity e1) {
		this(label, "R0");
		addEntity(e0);
		addEntity(e1);
	}
	
	/**
	 * {@inheritDoc} Returns {@code false} as I am presently not sure what to do here.
	 * Two options could be within any Entity in the Relation or within the largest
	 * Entity span.
	 */
	public boolean isInside(int i) {
		return false;
	}
	
	public String getLabel() {
		return getProperty("label");
	}
	
	/**
	 * Adds an Entity to the Relation.  Note that the Entity items must be added in
	 * the order that they will be used for retrieval.
	 * 
	 * @param entity	the Entity to be added
	 */
	public void addEntity(Entity entity) {
		addBelow(entity);
	}
	
	/**
	 * Retrieves the Entity in the order they were added to the relation.
	 * 
	 * @param index	the order by which the Entity items were added
	 * @return	the specified Entity
	 */
	public Entity getArgument(int index) {
		return (Entity) below.get(index);
	}

	public Relation copy() {
		return (Relation) super.copy();
	}
	
	public Relation deepCopy() {
		return (Relation) super.deepCopy();
	}
}
