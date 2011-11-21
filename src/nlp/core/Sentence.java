package nlp.core;

import java.util.ArrayList;
import java.util.Iterator;


import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * A Sentence is intended to hold a vector of Tokens.  It also has some convenient 
 * properties associated with converting from XML to the NLP graph representation.
 * TODO id in constructor?
 * 
 * @author ksmall
 */

public class Sentence extends nlp.core.Element {

	/**
	 * Extracts a sentence from a String representation.
	 * 
	 * @param sentence	the (space separated) sentence
	 */
	public Sentence(String sentence) {
		String[] tSplit = sentence.split("\\s+|\\/|'|-");
		for (int i = 0; i < tSplit.length; i++) {
			Token t = new Token(tSplit[i]);
			t.setProperty("position", Integer.toString(i));
			//System.out.println("here " + t);
			addBelow(t);
		}
	}

	/**
	 * Extracts a sentence from the XML convention for the NLP graph representation.
	 * 
	 * @param sentence	the XML sentence
	 */
	public Sentence(Element sentence) {
		this(sentence.getFirstChild().getNodeValue().trim());
	}
	
	/**
	 * Extracts a sentence from the XML convention for the NLP graph representation and
	 * adds any property annotation for each Token as specified by the keys in in
	 * given ArrayList.
	 * 
	 * @param sentence		the XML sentence
	 * @param properties	an ArrayList of property keys in the XML file
	 */
	public Sentence(Element sentence, ArrayList<String> properties) {
		this(sentence);
		for (String property : properties)
			setProperty(sentence, property);
	}
	
	/**
	 * Adds the stated property from the XMl file to each Token.  Presently,
	 * there is no error checking to ensure matched lengths.
	 * 
	 * @param sentence	the XML sentence
	 * @param key		the property key
	 */
	public void setProperty(Element sentence, String key) {
		NodeList property = sentence.getElementsByTagName(key);
		String[] values = property.item(0).getFirstChild().getNodeValue().trim().split("\\s+");
		Iterator<nlp.core.Element> token = below.iterator();
		for (int i = 0; i < values.length; i++)
			token.next().setProperty(key, values[i]);
	}
	
	/**
	 * @return	an array of the Token items comprising this Sentence
	 */
	public Token[] getTokens() {
		Token[] result = new Token[length()];
		for (int i = 0; i < result.length; i++)
			result[i] = (Token) getBelow(i);
		return result;
	}

	/**
	 * @return	a String representation for the Tokens in this Sentence
	 */
	public String getTokenString() {
		Token[] tokens = getTokens();
		String result = new String();
		for (int i = 0; i < tokens.length; i++) {
			result += tokens[i].getText();
			if (i < tokens.length - 1)
				result += " ";
		}
		return result;
	}
	
	/**
	 * {@inheritDoc} In this case, we simply indicate whether the index is
	 * less than the size of this Sentence.
	 */
	public boolean isInside(int index) {
		return index < length();
	}
	
	/**
	 * @return	the length of this Sentence
	 */
	public int length() {
		return below.size();
	}

	/**
	 * {@inheritDoc} In this case, just returns the word "sentence".
	 */
	public String getLabel() {
		return new String("sentence");
	}
	
	public String toString() {
		return new String("text:[" + getTokenString() + "]");
	}
	
	public Sentence copy() {
		return (Sentence) super.copy();
	}
	
	public Sentence deepCopy() {
		return (Sentence) super.deepCopy();
	}
}
