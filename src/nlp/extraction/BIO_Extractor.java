package nlp.extraction;

import java.util.ArrayList;

import nlp.core.Entity;
import nlp.core.Token;
import nlp.extraction.filter.TextFilter;

/**
 * Extracts BIO labels for the purpose of segmentation.
 * 
 * @author ksmall
 */
public class BIO_Extractor extends PropertyExtractor {

	/** determines if the entity labels are kept {@code true} means entity extraction,
	 * {@code false} means segmentation
	 */
	protected boolean keepLabels;
	
	public BIO_Extractor(String property, ArrayList<TextFilter> filters, 
			boolean printFilters, boolean keepLabels) {
		super(property, filters, printFilters);
		this.keepLabels = keepLabels;
	}

	public BIO_Extractor(String property) {
		this(property, new ArrayList<TextFilter>(), true, true);
	}
	
	public BIO_Extractor(boolean keepLabels) {
		this("label", new ArrayList<TextFilter>(), true, keepLabels);
	}
	
	public BIO_Extractor() {
		this("label");
	}
	
	public String key(Object o, Object target) {
		Token t = (Token) o;  // this must be over Tokens
		int position = Integer.parseInt(t.getProperty("position"));
    	Entity e = t.getEntity();
    	String value = null;
    	if (e == null)
    		value = "O";
    	else {
        	value = e.getProperty(property);
    		int start = Integer.parseInt(e.getProperty("start"));
    	
    		if (filters != null) {
    			for (TextFilter filter : filters)
    				value = filter.filter(value);
    		}
    		
    		if (value != null) {
    			if (position == start) {
    				if (keepLabels)
    					value = "B-" + value;
    				else
    					value = "B";
    			}
    			else {
    				if (keepLabels)
    					value = "I-" + value;
    				else
    					value = "I";
    			}
    		}
    	}
    	
    	if (value == null)
    		value = "O";
    	String result = new String(property);
    	if (printFilters && (filterString.length() > 0))
    		result += "{" + filterString + "}";
    	return result + "=" + value;
    }
	
	public String toString() {
		String result = new String(property);
    	if (printFilters && (filterString.length() > 0))
    		result += "{" + filterString + "}=";
    	return result + "<<ARG0[String]>>";
	}
}
