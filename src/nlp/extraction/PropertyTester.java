package nlp.extraction;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.w3c.dom.NodeList;

import text.UtilityXML;


import nlp.core.Token;
import nlp.extraction.filter.TextFilter;

/**
 * Tests if a property is true for the given target.
 * 
 * @author ksmall
 */
// ".*[A-Z].*" is any caps
public class PropertyTester extends PropertyExtractor {

	private Pattern pattern;
	public boolean printValue;  // this seems an inane option
	public boolean printFalse;
	
	public PropertyTester(String property, ArrayList<TextFilter> filters, boolean printFilters, 
			String pattern, boolean printValue, boolean printFalse) {
		super(property, filters, printFilters);
		this.identifier = "PropertyExtractor";
		this.pattern = Pattern.compile(pattern);
		this.printValue = printValue;
		this.printFalse = printFalse;
	}
	
	public PropertyTester(String property, String pattern, boolean printValue, boolean printFalse) {
		super(property);
		this.identifier = "PropertyExtractor";
		this.pattern = Pattern.compile(pattern);
		this.printValue = printValue;
		this.printFalse = printFalse;		
	}
	
	public PropertyTester(ArrayList<TextFilter> filters, String pattern) {
		this("text", filters, true, pattern, false, false);
	}
	
	public PropertyTester(String property, boolean printFilters, String pattern) {
		this(property, new ArrayList<TextFilter>(), printFilters, pattern, false, false);
	}
	
	public PropertyTester(String property, String pattern) {
		this(property, true, pattern);
	}
	
	public PropertyTester(boolean printFilters, String pattern) {
		this("text", printFilters, pattern);
	}
	
	public PropertyTester(String pattern, boolean printFalse) {
		this("text", new ArrayList<TextFilter>(), true, pattern, false, printFalse);
	}
	
	public PropertyTester(String pattern) {
		this("text", pattern);
	}
	
	public PropertyTester(org.w3c.dom.Element extractor) {
		this(UtilityXML.getNodeValue(extractor, "property"), new ArrayList<TextFilter>(),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "printFilters")),
				UtilityXML.getNodeValue(extractor, "pattern"),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "printValue")),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "printFalse")));
		NodeList nodes = extractor.getElementsByTagName("filters");
		NodeList filters = ((org.w3c.dom.Element) nodes.item(0)).getElementsByTagName("TextFilter");
		for (int i = 0; i < filters.getLength(); i++) {
			addFilter(TextFilter.decodeXML((org.w3c.dom.Element) filters.item(i)));
		}
	}
	
	public String pattern() {
		return pattern.toString();
	}
	
	//	 for the purposes of '*' in colloc, never used here
	public String key(Object o, Object target) {
    	Token token = (Token) o;
    	String pValue = token.getProperty(property);
    	
    	if (filters != null) {
    		for (TextFilter filter : filters)
    			pValue = filter.filter(pValue);
    	}
    	
		String result = new String(pattern.pattern());
		if (printFilters && (filterString.length() > 0))
			result += "{" + filterString + "}";
		result += "(" + property;
		if (printValue)
			result += "," + pValue;
		result += ")";
		if (printFalse)
			return result + "=" + pattern.matcher(pValue).matches();
		else if (pattern.matcher(pValue).matches())
			return result;
		return null;
	}
	
	// PATTERN{filterString}(property,value) - could have =false (might need to change method) 
	public String toString() {
		String result = new String(pattern.pattern());
		if (printFilters && (filterString.length() > 0))
			result += "{" + filterString + "}";
		result += "(" + property;
		if (printValue)
			result += ",<String>";
		result += ")";
		if (printFalse)
			result += "=<boolean>";
		return result;
	}
	
	public void xml(PrintWriter out) {
		out.println("<Extractor>");
		//out.println("<type>PropertyTester</type>");
		out.println("<type>" + getClass().getName() + "</type>");
		out.println("<property>" + property + "</property>");
		out.println("<pattern>" + pattern.pattern() + "</pattern>");
		out.println("<filters>");
		for (TextFilter filter : filters)
			filter.xml(out);
		out.println("</filters>");
		out.println("<printFilters>" + printFilters + "</printFilters>");
		out.println("<printValue>" + printValue + "</printValue>");
		out.println("<printFalse>" + printFalse + "</printFalse>");		
		out.println("</Extractor>");
	}
}
