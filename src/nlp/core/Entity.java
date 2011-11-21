package nlp.core;

import nlp.annotation.AnnotatedSentence;
import nlp.annotation.Annotation;

/**
 * Intended to represent named entities and such.  This is essentially a labeled segment.
 * 
 * @author ksmall
 */

public class Entity extends Segment implements Annotation<Entity>{

	/**
	 * The default constructor.
	 */
	public Entity() {
		this("", "");
	}

	/**
	 * Constructor which also initializes the {@code id} and {@code label} properties.
	 * 
	 * @param id
	 * @param label
	 */
	public Entity(String id, String label) {
		super(id);
		setProperty("label", label);
	}
	
	/**
	 * Constructor which also initializes the primary properties
	 * of an Entity ({@code id}, {@code label}, {@code start}, {@code end}).
	 * 
	 * @param id	the id associated with this Entity
	 * @param label	the label associated with this Entity
	 * @param start	the start index associated with this Entity
	 * @param end	the end index associated with this Entity
	 */
	public Entity(String id, String label, int start, int end) {
		super(id, start, end);
		setProperty("label", label);		
	}

	/*
	public Entity(Segment s) {
		super(s);
		setProperty("label", "entity");
	}
	*/
	
	public Entity(org.w3c.dom.Element e, AnnotatedSentence sentence, String args) {
		//String label = e.getFirstChild().getNodeValue().trim();
		String label = e.getElementsByTagName("label").item(0).getFirstChild().getNodeValue().trim();
		String id = e.getElementsByTagName("id").item(0).getFirstChild().getNodeValue().trim();
		int start = Integer.parseInt(e.getElementsByTagName("start").
				item(0).getFirstChild().getNodeValue().trim());
		int end = Integer.parseInt(e.getElementsByTagName("end").
				item(0).getFirstChild().getNodeValue().trim());
		setProperty("label", label);
		setID(id);
		setRange(start, end);
		for (int i = start; i <= end; i++)
			addBelow(sentence.getBelow(i));
	}
	
	public Entity addAnnotation(org.w3c.dom.Element e, AnnotatedSentence sentence, String args) {
		return new Entity(e, sentence, args);
	}
	
	public String getLabel() {
		return getProperty("label");
	}

	public Entity copy() {
		return (Entity) super.copy();
	}
	
	public Entity deepCopy() {
		return (Entity) super.deepCopy();
	}
}
