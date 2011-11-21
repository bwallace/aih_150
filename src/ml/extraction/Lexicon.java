package ml.extraction;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;

import text.output.PrintXML;
import text.output.XML_Writer;

/**
 * A class which associates generated Feature instances with specific identifiers.
 * Used to analyze Feature types and reconstruct final predictions.
 * 
 * @author ksmall
 */
public class Lexicon implements Iterable<String>, PrintXML {

	/** the String/LexiconEntry pairs */
    public HashMap<String, LexiconEntry> description;
    /** the default feature identifier number (if none is specified).  Note that we do not
     * perform any error checking for duplicates
     */
    protected int nextValue; // = 0;
	/** an XML_Writer for writing configuration files */
    protected XML_Writer xml;
    
    /**
     * the default constructor
     */
    public Lexicon() {
    	description = new HashMap<String, LexiconEntry>();
    	xml = new XML_Writer(this);
    	nextValue = 0;
    }

    /**
     * Associates the given feature description with an integer identifier by
     * creating a new LexiconEntry
     * 
     * @param source	the source Extractor which generated this entry
     * @param key		the feature description
     * @param id		the feature identifier
     * @return			{@code true} if a new feature was created
     */
    public boolean add(String source, String key, int id) {
    	if (!containsKey(key))
    		return add(source, key, new LexiconEntry(id, 0));
    	return false;
    }
    
    /**
     * Associates the given feature description with the next available identifier
     * by creating a new LexiconEntry
     * 
     * @param source	the source Extractor which generated this entry
     * @param key		the feature description
     * @return			{@code true} if a new feature was created
     */
    public boolean add(String source, String key) {
    	if (add(source, key, nextValue)) {
    		nextValue++;
    		return true;
    	}
    	return false;
    }
    
    /**
     * Associates the given feature description with the input LexiconEntry
     * 
     * @param source	the source Extractor which generated this entry
     * @param key		the feature description
     * @param entry		a LexiconEntry
     * @return			{@code true}
     */
    public boolean add(String source, String key, LexiconEntry entry) {
		description.put(key, entry);
		return true;
    }

    /**
     * Tests if the Lexicon contains the given feature description
     * 
     * @param key	the feature description
     * @return		{@code true} if the feature description is in the Lexicon, else {@code false}
     */
    public boolean containsKey(String key) {
    	return description.containsKey(key);
    }
    
    /**
     * Returns the size of the Lexicon in terms of the number of feature descriptions
     * 
     * @return	the size of the Lexicon
     */
    public int size() {
    	return description.size();
    }
    
    /**
     * Returns the identifier associated with the given feature description.  As a side effect,
     * this generates a new LexiconEntry if the description does not exist in the Lexicon or
     * augments the LexiconEntry counter if it does exist and the addCount flag is set.
     * 
     * @param source	the source Extractor which generated this entry
     * @param key		the feature description
     * @param addCount	determines whether the LexiconEntry counter should be incremented if
     * 					the key already exists
     * @return			the resulting identifier
     */    
    public int get(String source, String key, boolean addCount) {
    	if (containsKey(key)) {
    		LexiconEntry entry = (LexiconEntry) description.get(key);
    		if (addCount)
    			entry.count++;
    		return entry.id;
    	}
    	else {
    		description.put(key, new LexiconEntry(nextValue, 1));
    		nextValue++;
    		return (nextValue - 1);
    	}
    }
    
    /**
     * Returns the identifier associated with the given feature description.  As a side effect,
     * this generates a new LexiconEntry if the description does not exist in the Lexicon or
     * augments the LexiconEntry counter if it does exist.
     * 
     * @param source	the source Extractor which generated this entry
     * @param key
     * @return the resulting identifier
     */
    public int get(String source, String key) {
    	return get(source, key, true);
    }
        
    public Iterator<String> iterator() {
    	return description.keySet().iterator();
    }
    
    // assumes added in order
    public String[] invertArray() {
    	String[] result = new String[description.size()];
    	for (String key : this)
    		result[get(null, key, false)] = key;
    	return result;
    }
    
    public HashMap<LexiconEntry, String> invert() {
    	HashMap<LexiconEntry, String> result = new HashMap<LexiconEntry, String>();
    	for (String key : this)
    		result.put(description.get(key), key);
    	return result;
    }
           
	public void xml(OutputStream out) {
		xml.xml(out);
	}

	public String xml() {
		return xml.xml();
	}
    
	public void xml(PrintWriter out) {
		out.println("<Lexicon>");
    	for (String key : this) {
    		LexiconEntry entry = description.get(key);
    		out.print("<entry>");
    		out.print(" <key>" + key + "</key>");
    		out.print(" <id>" + entry.id + "</id>");
    		out.print(" <count>" + entry.count + "</count>");
    		out.println(" </entry>");
    	}
		out.println("</Lexicon>");
	}
}
