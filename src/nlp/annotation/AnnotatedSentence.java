package nlp.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

//import nlp.core.BinaryRelation;
import nlp.core.Entity;
//import nlp.core.Relation;
import nlp.core.BinaryRelation;
import nlp.core.Relation;
import nlp.core.Segment;
import nlp.core.Sentence;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The AnnotatedSentence is intended to augment the Sentence construct by also keeping
 * lists of all the annotation types that are added.  This allows a Sentence to be
 * composed exclusively of Tokens while keeping track of the annotations added to the
 * Tokens within this Sentence.  As this was primarily designed for Entity/Relation 
 * extraction, there are many convenience methods specifically for this task.  However, 
 * this can be easily generalized.
 * 
 * @author ksmall
 */

// TODO: eventually break this up into an AnnotatedSentence for different tasks
public class AnnotatedSentence extends Sentence {
	
	/**
	 * book keeping for all annotations on this specific Sentence
	 */
	protected HashMap<String, ArrayList<nlp.core.Element>> annotations;

	/**
	 * Primary constructor
	 * 
	 * @param sentence	the input sentence
	 */
	public AnnotatedSentence(org.w3c.dom.Element sentence) {
		super(sentence);
		annotations = new HashMap<String, ArrayList<nlp.core.Element>>();
	}
	
	/**
	 * Constructor for an input (space separated) String.  Primarily for demos and such.
	 * 
	 * @param sentence	the input sentence
	 */
	public AnnotatedSentence(String sentence) {
		super(sentence);
		annotations = new HashMap<String, ArrayList<nlp.core.Element>>();
	}
	
	/**
	 * Returns all Elements of a specified annotation type.
	 * 
	 * @param type	the type of annotation
	 * @return		all Elements of the specified annotation type
	 */
	public ArrayList<nlp.core.Element> getAnnotation(String type) {
		return annotations.get(type);
	}
	
	/**
	 * Adds the Entity items present in a given XML annotated Sentence.
	 * 
	 * @param sentence	the input sentence
	 * @param key		the key associated with Entity annotations
	 */
	public void addEntities(Element sentence, String key) {
		ArrayList<nlp.core.Element> entities = annotations.get(key);
		if (entities == null) {
			entities = new ArrayList<nlp.core.Element>();
			annotations.put(key, entities);
		}
		NodeList items = sentence.getElementsByTagName(key);
		for (int i = 0; i < items.getLength(); i++) {
			Element xml = (Element) items.item(i);
			entities.add(new Entity(xml, this, null));
		}	
	}

	/**
	 * Adds the Relation items present in a given XML annotated Sentence.
	 * 
	 * @param sentence	the input sentence
	 * @param key		the key associated with Relation annotations
	 */
	public void addRelations(Element sentence, String key, String eKey) {
		ArrayList<nlp.core.Element> relations = annotations.get(key);
		if (relations == null) {
			relations = new ArrayList<nlp.core.Element>();
			annotations.put(key, relations);
		}
		NodeList items = sentence.getElementsByTagName(key);
		for (int i = 0; i < items.getLength(); i++) {
			Element xml = (Element) items.item(i);
			relations.add(new Relation(xml, this, eKey));
		}	
	}
	
	/**
	 * Removes all annotations of a specified type.
	 * 
	 * @param type	the specified annotation type to be removed
	 */
	public void removeAnnotation(String type) {
		ArrayList<nlp.core.Element> temp = getAnnotation(type);
		if (temp != null) {
			for (nlp.core.Element element : temp)
				element.remove();
			annotations.remove(type);
		}
	}

	/**
	 * Adds the specified segment to the sentence.
	 * 
	 * @param tag	the segmentation tag
	 * @param start	the start index of the segment
	 * @param end	the end index of the segment
	 */
	public void addPredictedSegment(String tag, int start, int end) {
		ArrayList<nlp.core.Element> segmentation = getAnnotation(tag);
		if (segmentation == null) {
			segmentation = new ArrayList<nlp.core.Element>();
			annotations.put(tag, segmentation);
		}
		Segment segment = new Segment();
		segment.setRange(start, end);
		for (int i = start; i <= end; i++)
			segment.addBelow(getBelow(i));
		segmentation.add(segment);
	}
	
	/**
	 * Adds a predicted entity to the sentence.
	 * 
	 * @param tag	the entity specification tag
	 * @param id	the entity identifier (for coref and such)
	 * @param label	the label of the entity
	 * @param start	the start index of the entity
	 * @param end	the end index of the entity
	 */
	public void addPredictedEntity(String tag, String id, String label, int start, int end) {
		ArrayList<nlp.core.Element> entities = getAnnotation(tag);
		if (entities == null) {
			entities = new ArrayList<nlp.core.Element>();
			annotations.put(tag, entities);
		}
		Entity entity = new Entity(id, label, start, end);
		for (int i = start; i <= end; i++)
			entity.addBelow(getBelow(i));
		entities.add(entity);
	}
	
	public void addPredictedRelation(String tag, String id, String label, Entity e0, Entity e1) {
		ArrayList<nlp.core.Element> relations = getAnnotation(tag);
		if (relations == null) {
			relations = new ArrayList<nlp.core.Element>();
			annotations.put(tag, relations);
		}
		BinaryRelation relation = new BinaryRelation(id, label, e0, e1);
		relation.addBelow(e0);
		relation.addBelow(e1);
		relations.add(relation);
	}

	
	public String toString() {
		String result = super.toString() + " ";
		for (Iterator<String> it = annotations.keySet().iterator(); it.hasNext(); ) {
			String key = (String) it.next();
			result += key + ":[";
			ArrayList<nlp.core.Element> annotation = annotations.get(key);
			for (Iterator<nlp.core.Element> jt = annotation.iterator(); jt.hasNext(); ) {
				result += jt.next();
				if (jt.hasNext())
					result += "; ";
			}
			result += "]";
			if (it.hasNext())
				result += " ";
		}
		return result;
	}	

	// TODO: should implement copy functions for completeness, although likely not very useful
	/*
	public AnnotatedSentence copy() {
		return null;
	}
	
	public AnnotatedSentence deepCopy() {
		return null;
	}
	*/
}
