package nlp.extraction.sequence;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.NodeList;

import text.UtilityXML;

import nlp.core.Element;
import nlp.core.Segment;
import nlp.core.Token;
import nlp.extraction.PropertyExtractor;
import nlp.extraction.filter.TextFilter;

/**
 * Extracts a sequence of properties from a sequence.
 * 
 * @author ksmall
 */
// extracts properties of the tokens in the sequence (explicitly joint features - create new class for "convenience" type)
public class SequencePropertyExtractor extends PropertyExtractor {
	
	public boolean markSequence;
	
	public SequencePropertyExtractor(String property, ArrayList<TextFilter> filters, boolean printFilters, boolean markSequence) {
		super(property, filters, printFilters);
		this.identifier = new String("SequencePropertyExtractor");
		this.markSequence = markSequence;
	}

	public SequencePropertyExtractor(String property, boolean markSequence) {
		this(property, new ArrayList<TextFilter>(), true, markSequence);		
	}
	
	public SequencePropertyExtractor(String property) {
		this(property, new ArrayList<TextFilter>(), true, false);
	}
	
	public SequencePropertyExtractor(org.w3c.dom.Element extractor) {
		this(UtilityXML.getNodeValue(extractor, "property"), new ArrayList<TextFilter>(),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "printFilters")),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "markSequence")));
		NodeList nodes = extractor.getElementsByTagName("filters");
		NodeList filters = ((org.w3c.dom.Element) nodes.item(0)).getElementsByTagName("TextFilter");
		for (int i = 0; i < filters.getLength(); i++) {
			addFilter(TextFilter.decodeXML((org.w3c.dom.Element) filters.item(i)));
		}
	}

	
	// this is a bit strange, but don't worry about for now
	public int length() {
		return 1;
	}
	
	// for the purposes of '*' in colloc, never used here (not sure why this is said)
	// assumes Tokens are directly and exclusively underneath Entities
	public String key(Object o, Object target) {
		Segment s = (Segment) o;	// could force this more elegantly (would need sequence superclass for Entity
		ArrayList<String> tokens = new ArrayList<String>();
		for (Iterator<Element> it = s.belowIterator(); it.hasNext(); ) {
			Token t = (Token) it.next();		// may not be necessary to typecast here
	    	String value = t.getProperty(property);
	    	
	    	if (filters != null) {
	    		for (Iterator<TextFilter> jt = filters.iterator(); jt.hasNext(); )
	    			value = jt.next().filter(value);
	    	}

	    	if (value != null)
	    		tokens.add(value);
		}
    	
    	if (tokens.size() > 0) {
    		String result = new String(property);
    		if (printFilters && (filterString.length() > 0))
    			result += "{" + filterString + "}";
    		result += "=[";
    		for (Iterator<String> it = tokens.iterator(); it.hasNext(); ) {
    			result += it.next();
    			if (it.hasNext())
    				result += ",";
    		}
    		result += "]";
    		if (markSequence)
    			result = "seq(" + result + ")";
    		return result;
    	}
    	return null;
    }

	public String toString() {
		String result = new String("Sequence[" + property);
		if (printFilters && (filterString.length() > 0))
			result += "{" + filterString + "}";
		//return result + "=<<ARG0[String]>>]";
		return result + "]";
	}
	
	public void xml(PrintWriter out) {
		out.println("<Extractor>");
		out.println("<type>" + getClass().getName() + "</type>");
		out.println("<property>" + property + "</property>");
		out.println("<markSequence>" + markSequence + "</markSequence>");
		out.println("<filters>");
		for (TextFilter filter : filters)
			filter.xml(out);
		out.println("</filters>");
		out.println("<printFilters>" + printFilters + "</printFilters>");
		out.println("</Extractor>");
	}
}
