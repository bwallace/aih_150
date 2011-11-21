package nlp.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import shared.Copyable;

/**
 * This is something very similar to Mark's graph representation of language
 * (although developed independently -- and earlier for some components).  Each
 * NLP Element vertex has a set of properties (only which of an "id" is predefined)
 * and is assumed to have a location in another structure (which will lead to other
 * predefined properties for inherited classes).  Furthermore, each Element can link above
 * or below to other Element structures (above meaning more general and below being more specific).
 * For example, a sentence would point to tokens and entities below, while entities may also
 * point above to relations (and tokens will probably point above to entities).  Another assumption
 * is that construction will generally be "from above" meaning that sentences would add tokens.
 * While it can also be done in the other direction, addBelow automatically adds the other 
 * direction.
 * TODO an example of usage
 * 
 * 
 * @author ksmall
 */
public abstract class Element implements Cloneable, Copyable<Element> {

	/** the properties associated with this vertex */
	protected HashMap<String, String> properties;
	/** a list of the vertices which exist directly above this level in the hierarchy */
	protected ArrayList<Element> above;
	/** a list of the vertices which exist directly below this level in the hierarchy */
	protected ArrayList<Element> below;

	/**
	 * Constructor
	 */
	public Element() {
		properties = new HashMap<String, String>();
		above = new ArrayList<Element>();
		below = new ArrayList<Element>();
	}

	/**
	 * Constructor which also sets the id
	 * 
	 * @param id	the (generally unique) identifier of this element
	 */
	public Element(String id) {
		this();
		setID(id);
	}
	
	/**
	 * Copy constructor (deepCopy)
	 * 
	 * @param e	Element to be copied
	 */
	public Element(Element e) {
		this();
		for (String key : e.properties.keySet())
			properties.put(new String(key), new String(e.properties.get(key)));
		for (Element element : e.above)
			above.add(element.deepCopy());
		for (Element element : e.below)
			below.add(element.deepCopy());
	}
	
	/**
	 * Determines equality for two elements as determined by their (generally unique) identifiers.
	 * 
	 * @param o	the Element which this Element is compared to for equality
	 * @return	{@code true} if the two Elements have the same identifier, else {@code false}
	 */
	public boolean equals(Object o) {
		return getID().equals(((Element) o).getID());
	}
	
	/**
	 * Indicates whether this Element instance "covers" the specified position.  For example, 
	 * tokens have a single position, entities have a range of positions, etc.
	 * 
	 * @param position	the position being checked
	 * @return	{@code true} if the position is in this Element, else {@code false}
	 */
	public abstract boolean isInside(int position);
	
	/**
	 * Sets a property to the stated value.
	 * 
	 * @param property	the property name
	 * @param value		the new value of the property
	 */
	public void setProperty(String property, String value) {
		properties.put(property, value);
	}

	/**
	 * Returns the value associated with the specified property
	 * 
	 * @param property	the specified property
	 * @return	the value associated with the specified property
	 */
	public String getProperty(String property) {
		return properties.get(property);
	}

	/**
	 * Returns the integer value associated with the specified property
	 * 
	 * @param property	the specified property
	 * @return	the integer value associated with the specified property
	 */
	public Integer getPropertyInteger(String property) {
		String result = getProperty(property);
		return (result != null) ? Integer.parseInt(result) : null;
	}

	
	/**
	 * Sets the ID value for this instantiated vertex
	 * 
	 * @param id	identifier of this vertex
	 */
	public void setID(String id) {
		setProperty("id", id);
	}
		
	/**
	 * Returns the ID value for this vertex
	 * 
	 * @return	the identifier of this vertex
	 */
	public String getID() {
		return getProperty("id"); 
	}
		
	/**
	 * Returns the label associated with this Element
	 * 	
	 * @return	the label of this Element
	 */
	public abstract String getLabel();
	
	/**
	 * Adds a pointer to the specified Element below this Element in the hierarchy.  Note
	 * that since we assume the construction of the graph is from most general (Sentence)
	 * to most specific (Token), this also adds a pointer from the Element being added to
	 * {@code this} structure.
	 * 
	 * @param e	the Element below to be pointed to
	 */
	public void addBelow(Element e) {
		e.addAbove(this);
		//System.out.println(e);
		below.add(e);
	}

	/**
	 * Adds a pointer to the specified Element below this Element in the graph hierarchy.
	 * 
	 * @param e	 the Element above to be pointed to
	 */
	public void addAbove(Element e) {
		above.add(e);
	}

	/**
	 * Removes the specified Element above this Element in the hierarchy.  Note that 
	 * this depends on the definition of equality defined in this class.
	 * 
	 * @param e	the specified Element to be removed
	 * @param backpointer	{@code true} means the backpointer should also be removed
	 */
	public void removeAbove(Element e, boolean backpointer) {
		if (backpointer)
			e.removeBelow(this, false);
		above.remove(e);
	}

	/**
	 * Removes the specified Element above this Element in the hierarchy, assuming
	 * that the backpointer should also be removed.
	 * 
	 * @param e	the specified Element to be removed
	 */
	public void removeAbove(Element e) {
		removeAbove(e, true);
	}
	
	/**
	 * Removes the specified Element below this Element in the hierarchy.  Note that 
	 * this depends on the definition of equality defined in this class.
	 * 
	 * @param e	the specified Element to be removed
	 * @param backpointer	{@code true} means the backpointer should also be removed
	 */
	public void removeBelow(Element e, boolean backpointer) {
		if (backpointer)
			e.removeAbove(this, false);
		below.remove(e);
	}
	
	/**
	 * Removes the specified Element below this Element in the hierarchy, assuming
	 * that the backpointer should also be removed.
	 * 
	 * @param e	the specified Element to be removed
	 */
	public void removeBelow(Element e) {
		removeBelow(e, true);
	}
	
	/**
	 * Removes everything below this Element in the hierarchy (including backpointers).
	 */
	public void removeBelow() {
		for (Element element : below)
			element.removeAbove(this, false);
		below = new ArrayList<Element>();
	}

	/**
	 * Removes everything above this Element in the hierarchy (including backpointers).
	 */
	public void removeAbove() {
		for (Element element : above)
			element.removeBelow(this, false);
		above = new ArrayList<Element>();
	}
	
	/**
	 * Removes all edges from this Element in the graph (including backpointers).
	 */
	public void remove() {
		removeAbove();
		removeBelow();
	}
	
	/**
	 * Returns the Element located below this Element at the specified position in the 
	 * ArrayList.  Note that this is useful for specific cases, but unless done 
	 * externally, this structured is not intended to support ordering of the 
	 * graph structure.
	 * 
	 * @param i	the specified position
	 * @return	the element located below at the specified position
	 */
	public Element getBelow(int i) {
		return below.get(i);
	}

	// TODO: look at this more closely, as it was commented out for a long time (uncommented for using with Entity extraction)
	public Iterator<Element> belowIterator() {
		return below.iterator();
	}
	
	/*
	naming convention needs to be specified (or have ArrayLists become Hashes of ArrayLists with keys)
	need an ArrayList version of this
	*/
	
	/**
	 * Returns the first element above this element in the hierarchy 
	 * with a class containing the specified suffix.  While this relies 
	 * on a naming convention to be established, this is very useful in cases 
	 * of a single entity being the root of many types of annotation
	 * (e.g. sentences).  Returns {@code null} if no matching Element is found.
	 * TODO: Do we want an ArrayList version?
	 * 
	 * @param classSuffix	the suffix of the class name that will be matched
	 * @return	the first Element above this Element with the specified class name suffix
	 */
	public Element getFirstAbove(String classSuffix) {
		for (Element element : above) {
			if (element.getClass().getName().endsWith(classSuffix))
				return element; 
		}
		return null;
	}
	
	public String toString() {
		String result = "this[";
		for (Iterator<String> it = properties.keySet().iterator(); it.hasNext(); ) {
			String key = (String) it.next();
			result += key + ":" + getProperty(key);
			if (it.hasNext())
				result += " ";
		}
		if ((above != null) && (above.size() > 0)) {
			result += "] above:[";
			for (Iterator<Element> it = above.iterator(); it.hasNext(); ) {
				result += ((Element) it.next()).getID();
				if (it.hasNext())
					result += " ";
			}
		}
		if ((below != null) && (below.size() > 0)) {
			result += "] below:[";
			for (Iterator<Element> it = below.iterator(); it.hasNext(); ) {
				result += ((Element) it.next()).getID();
				if (it.hasNext())
					result += " ";
			}
		}
		return result + "]";
	}
	
    /**
     * Returns a clone of this instance
     * 
     *  @return		a clone of this instance
     */
    public Object clone() {    // may need a deep clone or check this more thoroughly or whatever
    	Element clone = null;
    	try {
    		clone = (Element) super.clone();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	return clone;
    }
	
	public Element copy() {
		Element copy = (Element) this.clone();
		for (String key : properties.keySet())
			copy.properties.put(key, properties.get(key));
		for (Element element : above)
			copy.above.add(element);
		for (Element element : below)
			copy.below.add(element);
		return copy;
	}
	
	public Element deepCopy() {
		Element copy = (Element) this.clone();
		for (String key : properties.keySet())
			copy.properties.put(new String(key), new String(properties.get(key)));
		for (Element element : above)
			copy.above.add(element.deepCopy());
		for (Element element : below)
			copy.below.add(element.deepCopy());
		return copy;
	}
}