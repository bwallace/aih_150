package ml.extraction;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;

import text.output.PrintXML;
import text.output.XML_Writer;
import text.UtilityXML;
import ml.instance.FeatureVector;

/**
 * The base (abstract) Extractor class is used to extract FeatureVector instances
 * from an arbitrary structure.  
 * 
 * @author ksmall
 */
public abstract class Extractor implements Cloneable, PrintXML {

	/** a description for the Extractor */
    public String identifier;
	/** an XML_Writer for writing configuration files */
    public XML_Writer xml;

    /**
     * the default constructor
     * 
     * @param identifier	the Extractor description
     */
    public Extractor(String identifier) {
    	this.identifier = new String(identifier);
    	xml = new XML_Writer(this);
    }

    /**
     * an instance factory for generating instances from xml specifications
     * 
     * @param specification	the xml specification
     * @return	a new Extractor instance based upon the specification
     */
    public static Extractor newInstance(org.w3c.dom.Element specification) {
    	String type = UtilityXML.getNodeValue(specification, "type");
		try {
			Class<?> extractor = Class.forName(type);
			Constructor<?> constructor = 
				extractor.getConstructor(new Class[] { Class.forName("org.w3c.dom.Element") });
			return (Extractor) constructor.newInstance(specification);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
        
    /**
     * Extracts a FeatureVector from the given object
     * 
     * @param source	the source extractor
     * @param o			the given Object
     * @param lexicon	a Lexicon for which integers are associated with Feature descriptions
     * @return	the extracted FeatureVector
     */
    public abstract FeatureVector extract(String source, Object o, Lexicon lexicon);
    
    public FeatureVector extract(Object o, Lexicon lexicon) {
    	return extract(toString(), o, lexicon);
    }
    
    /**
     * Extracts a FeatureVector from the given object, allowing for a prefix and suffix to allow 
     * different types of extractors to be noted (i.e. Token, Sequence, Relation) and provide
     * unique strings.  By default, just returns the regular (non-wrapped) extraction.
     * 
     * @param source	the source extractor
     * @param o			the given Object
     * @param lexicon	a Lexicon for which integers are associated with Feature descriptions
     * @param prefix	the prefix for the resulting extraction
     * @param suffix	the suffix for the resulting extraction
     * @return			the extracted FeatureVector
     */
    public FeatureVector wrapExtract(String source, Object o, Lexicon lexicon, String prefix, String suffix) {
    	return extract(source, o, lexicon);
    }

    public FeatureVector wrapExtract(Object o, Lexicon lexicon, String prefix, String suffix) {
    	return wrapExtract(toString(), o, lexicon, prefix, suffix);
    }
    
    public String toString() {
    	return identifier;
    }
    
    public Object clone() {
    	Extractor clone = null;
    	try {
    		clone = (Extractor) super.clone();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	return clone;
    }

	public void xml(OutputStream out) {
		xml.xml(out);
	}

	public String xml() {
		return xml.xml();
	}
    
	/**
	 * Prints the xml body extractor.  Note that this is empty for the
	 * abstract class Extractor.
	 * 
	 * @param out	the output PrintWriter
	 */
	public void xmlBody(PrintWriter out) { ; }
	
	public void xml(PrintWriter out) {
		out.println("<Extractor>");
		out.println("<type>" + getClass().getName() + "</type>");
		xmlBody(out);
		out.println("</Extractor>");
	}
	
	public int hashCode() {
		return toString().hashCode();
	}
}
