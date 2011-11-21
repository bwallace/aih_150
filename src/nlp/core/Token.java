package nlp.core;


/**
 * Token is intended to represent a lexical element.  Therefore, it is assumed
 * (at least for the time being) that it is at the bottom of the NLP graph
 * representation hierarchy.  In general, Token elements will also have a position
 * property, which is used in combination with the text to generate a unique
 * element id if available.
 * 
 * @author ksmall
 */

public class Token extends Element {

	/**
	 * The default constructor
	 */
	public Token() {
		this("");
	}
		
	/**
	 * Constructor which also initializes the {@code text} property
	 * 
	 * @param text	the text associated with this Token
	 */
	public Token(String text) {
		super();
		below = null;
		setProperty("text", text);
	}

	/**
	 * Construction which also initializes the {@code text} and
	 * {@code position} properties.
	 * 
	 * @param text		the text associated with this Token
	 * @param position	the position associated with this Token
	 */
	public Token(String text, int position) {
		this(text);
		setProperty("position", Integer.toString(position));
	}
	
	/**
	 * Indicates if the Token is present at the specified index.  Note this first
	 * checks if the Token is the member of a Sentence (which can be viewed as
	 * a vector of Tokens).
	 * 
	 * @param index	the specified index
	 * @return	{@code true} if the Token is located at the specified index, 
	 * 			else {@code false}
	 */
	public boolean isInside(int index) {
		Integer position = getPropertyInteger("position");
		//return (getSentence() != null) && (position != null) && (position.intValue() == index);
		return (position != null) && (position.intValue() == index);
	}

	/**
	 * Returns the Sentence associated with this Token.  Note that this assumes
	 * that this Token is associated with a Single Sentence.
	 * 
	 * @return	the Sentence Element above this Token
	 */
	public Sentence getSentence() {
		return (Sentence) getFirstAbove("Sentence");
	}

	/**
	 * Returns the Entity associated with this Token.  Note that this assumes
	 * that this Token is associated with a Single Entity.
	 * 
	 * @return	the Entity Element above this Token
	 */
	public Entity getEntity() {
		return (Entity) getFirstAbove("Entity");
	}

	/**
	 * {@inheritDoc} Returns the text itself.
	 */
	public String getLabel() {
		return getText();
	}
	
	/**
	 * Retrieves the value associated with the text property.
	 * 
	 * @return	the resulting text (lexical value)
	 */
	public String getText() {
		return getProperty("text");
	}

	/**
	 * Returns an id of the form {@code text_position}, unless the
	 * position is not available, in which case the text is the
	 * id.
	 * 
	 * @return	the id associated with this Token
	 */
	public String getID() {
		/*
		Integer position = getPropertyInteger("position");
		return (position != null) ? (getText() + "_" + position) : getText();
		*/
		return getText();
	}
	
	public Token copy() {
		return (Token) super.copy();
	}
	
	public Token deepCopy() {
		return (Token) super.deepCopy();
	}

	/*
	public int compareTo(Token t) {
		return new Integer(getProperty("position")).compareTo(new Integer(t.getProperty("position")));
	}
	*/
}
