package nlp.extraction;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.NodeList;

import text.UtilityXML;

import nlp.core.Element;
import nlp.extraction.filter.TextFilter;
import ml.extraction.Extractor;
import ml.extraction.Lexicon;
import ml.instance.Feature;
import ml.instance.FeatureVector;

/**
 * PropertyExtractor is used to create a Feature based on the given Property
 * in the NLP graph structure.  In addition to the property, filters can
 * also be added to alter the property according to the specification of the
 * TextFilter.  The primary use of this extractor is to generate Features
 * based on the text property (e.g. lexical features)
 * 
 * @author ksmall
 */
public class PropertyExtractor extends Extractor {

	protected String property;
	protected ArrayList<TextFilter> filters;
	protected boolean printFilters;
	protected String filterString;
	
	public PropertyExtractor(String property) {
		this(property, new ArrayList<TextFilter>(), true);
	}

	public Iterator<TextFilter> filters() {
		return filters.iterator();
	}
	
	public PropertyExtractor(String property, ArrayList<TextFilter> filters, boolean printFilters) {
		super("PropertyExtractor");
		this.property = property;
		this.filters = filters;
		this.printFilters = printFilters;
		this.filterString = new String();
		for (Iterator<TextFilter> it = filters.iterator(); it.hasNext(); ) {
			filterString += it.next();
			if (it.hasNext())
				filterString += ",";
		}
	}

	// keeping full Element path for clarity
	// don't do any validating for PropertyExtractor type
	public PropertyExtractor(org.w3c.dom.Element extractor) {
		this(UtilityXML.getNodeValue(extractor, "property"), new ArrayList<TextFilter>(),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "printFilters")));
		NodeList nodes = extractor.getElementsByTagName("filters");
		NodeList filters = ((org.w3c.dom.Element) nodes.item(0)).getElementsByTagName("TextFilter");
		for (int i = 0; i < filters.getLength(); i++) {
			addFilter(TextFilter.decodeXML((org.w3c.dom.Element) filters.item(i)));
		}
	}
	
	public void addFilter(TextFilter filter) {
		filters.add(filter);
		if (filterString.length() > 0)
			filterString += ",";
		filterString += filter;
	}

	public FeatureVector extract(String source, Object o, Lexicon lexicon) {
    	FeatureVector result = new FeatureVector();
    	String key = key(o);
    	if (key != null)
    		result.addFeature(new Feature(lexicon.get(source, key)));
    	return result;
	}

	public FeatureVector wrapExtract(String source, Object o, Lexicon lexicon, String prefix, String suffix) {
    	FeatureVector result = new FeatureVector();
    	String key = key(o);
    	if (key != null) {
    		key = prefix + key + suffix;
    		result.addFeature(new Feature(lexicon.get(source, key)));
    	}
    	return result;
	}
	
	public int length() {
		return 1;
	}
	
	// for the purposes of '*' in colloc, never used here
	public String key(Object o, Object target) {
		String value = ((Element) o).getProperty(property);
    	
    	if (filters != null) {
    		for (TextFilter filter : filters)
    			value = filter.filter(value);
    	}
    	
    	if (value != null) {
    		String result = new String(property);
    		if (printFilters && (filterString.length() > 0))
    			result += "{" + filterString + "}";
    		return result + "=" + value;
    	}
    	return null;
    }
	
	public String key(Object o) {
		return key(o, o);
	}

	public String toString() {
		String result = new String(property);
		if (printFilters && (filterString.length() > 0))
			result += "{" + filterString + "}";
		return result;// + "=<String>";		
	}

	public void xml(PrintWriter out) {
		out.println("<Extractor>");
		out.println("<type>" + getClass().getName() + "</type>");
		out.println("<property>" + property + "</property>");
		out.println("<filters>");
		for (TextFilter filter : filters)
			filter.xml(out);
		out.println("</filters>");
		out.println("<printFilters>" + printFilters + "</printFilters>");
		out.println("</Extractor>");
	}
}
