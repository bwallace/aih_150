package nlp.core;

import java.util.Iterator;



/**
 * A segment represents a contiguous set of indices.  This will be used as a base class
 * for chunking, NER, etc.
 * 
 * @author ksmall
 */

public class Segment extends Element {

	/**
	 * The default constructor
	 */
	public Segment() {
		this("");
	}
	
	/**
	 * Constructor which also initializes the {@code id} property
	 * 
	 * @param id	the id associated with this Segment
	 */
	public Segment(String id) {
		super(id);
		//setProperty("label", "entity");
	}
	
	/**
	 * Constructor which also initializes the primary properties
	 * of a segment ({@code id}, {@code start}, {@code end}).
	 * 
	 * @param id	the id associated with this Segment 
	 * @param start	the start index associated with this Segment
	 * @param end	the end index associated with this Segment
	 */
	public Segment(String id, int start, int end) {
		this(id);
		setRange(start, end);
	}

	/*
	public Segment(Segment s) {
		this(s.getID(), s.getPropertyInteger("start"), s.getPropertyInteger("end"));
	}
	*/
	
	/**
	 * Sets the ({@code start}, {@code end}) index range associated
	 * with this segment. 
	 * 	
	 * @param start	the {@code start} index
	 * @param end	the {@code end} index
	 */
	public void setRange(int start, int end) {
		setProperty("start", Integer.toString(start));
		setProperty("end", Integer.toString(end));
	}

	/**
	 * Indicates if the specified index is within the index range of this Segment.  
	 * 
	 * @param index	the specified index
	 * @return	{@code true} if the index is located within the window of this Segment, 
	 * 			else {@code false}
	 */
	public boolean isInside(int index) {
		/*
		Sentence s = (Sentence) getBelow(0).getFirstAbove("Sentence");
		return (s != null) && (Integer.parseInt(getProperty("start")) <= index) &&
				(Integer.parseInt(getProperty("end")) >= index);
		*/
		return (Integer.parseInt(getProperty("start")) <= index) &&
				(Integer.parseInt(getProperty("end")) >= index);
		
	}
	
	/**
	 * {@inheritDoc} Returns the word "segment".
	 */
	public String getLabel() {
		return new String("segment");
	}
	
	/**
	 * Adds a Token to this segment.  Also updates the start and end position 
	 * (if necessary) under the assumption that enough Token items will be
	 * added to complete the segment.  Note that this may (one day) be extended
	 * to anything that has a {@code position} property.
	 * 
	 * @param token	the Token to be added
	 */
	public void addToken(Token token) {
		addBelow(token);
		int position = Integer.parseInt(token.getProperty("position"));
		if (position < Integer.parseInt(getProperty("start")))
			setProperty("start", token.getProperty("position"));
		if (position > Integer.parseInt(getProperty("end")))
			setProperty("end", token.getProperty("position"));		
	}
	
	public String getTokenString() {
		String result = new String();
		for (Iterator<Element> it = belowIterator(); it.hasNext(); ) {
			result += it.next().getProperty("text");
			if (it.hasNext())
				result += " ";
		}
		return result;
	}
	
	public Segment copy() {
		return (Segment) super.copy();
	}
	
	public Segment deepCopy() {
		return (Segment) super.deepCopy();
	}	
}
