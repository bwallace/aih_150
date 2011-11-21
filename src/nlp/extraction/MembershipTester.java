package nlp.extraction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.NodeList;


import shared.IntPair;
import text.UtilityXML;
import nlp.core.Sentence;
import nlp.core.Token;
import nlp.extraction.filter.TextFilter;

/**
 * An extractor that tests if the target element is the member of a given list.
 * 
 * @author ksmall
 *
 */
public class MembershipTester extends PropertyExtractor {

	protected String listName;
	protected HashMap<String, ArrayList<IntPair>> phrases;
	protected ArrayList<String[]> multiwords;
	protected ArrayList<TextFilter> sourceFilters;
	protected int counter;   // start at 0 in constructor
	public boolean printFalse;
	public boolean printValue;
	protected String sFilterString;
	public ArrayList<String> fileName;  // assumes one filename for gui purposes
	
	public MembershipTester(String property, ArrayList<TextFilter> sourceFilters,
			ArrayList<TextFilter> filters, boolean printFilters, boolean printValue, 
			boolean printFalse, String listName) {
		super(property, filters, printFilters);
		this.identifier = new String("MembershipTester");
		this.sourceFilters = sourceFilters;
		this.listName = listName;
		phrases = new HashMap<String, ArrayList<IntPair>>();
		multiwords = new ArrayList<String[]>();
		this.printFalse = printFalse;
		this.printValue = printValue;
		this.sFilterString = new String();
		fileName = new ArrayList<String>();
		counter = 0;
	}

	public MembershipTester(String property, String listName, boolean printValue, boolean printFalse) {
		this(property, new ArrayList<TextFilter>(), new ArrayList<TextFilter>(), 
				true, printValue, printFalse, listName);
	}
	
	public MembershipTester(String property, String listName) {
		this(property, new ArrayList<TextFilter>(), new ArrayList<TextFilter>(), true, false, false, listName);
	}
	
	public MembershipTester(String listName) {
		this("text", listName);
	}

	public MembershipTester(org.w3c.dom.Element extractor) {
		this(UtilityXML.getNodeValue(extractor, "property"), 
				new ArrayList<TextFilter>(), new ArrayList<TextFilter>(),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "printFilters")),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "printValue")),
				Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "printFalse")),
				UtilityXML.getNodeValue(extractor, "listName"));
		NodeList nodes = extractor.getElementsByTagName("sourceFilters");
		NodeList filters = ((org.w3c.dom.Element) nodes.item(0)).getElementsByTagName("TextFilter");
		for (int i = 0; i < filters.getLength(); i++) {
			addSourceFilter(TextFilter.decodeXML((org.w3c.dom.Element) filters.item(i)));
		}
		nodes = extractor.getElementsByTagName("targetFilters");
		filters = ((org.w3c.dom.Element) nodes.item(0)).getElementsByTagName("TextFilter");
		for (int i = 0; i < filters.getLength(); i++) {
			addTargetFilter(TextFilter.decodeXML((org.w3c.dom.Element) filters.item(i)));
		}
	}
	
	public String name() {
		return listName;
	}
	
	public void addSourceFilter(TextFilter filter) {
		sourceFilters.add(filter);
		if (sFilterString.length() > 0)
			sFilterString += ",";
		sFilterString += filter;
	}
	
	public void addTargetFilter(TextFilter filter) {
		addFilter(filter);
	}


	public Iterator<TextFilter> sourceFilters() {
		return sourceFilters.iterator();
	}

	public Iterator<TextFilter> targetFilters() {
		return filters();
	}
	
	public void addFile(String filename) {
		// reader;
		fileName.add(filename);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				addPhrase(line);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addPhrase(String phrase) {
		String[] words = phrase.split("\\s+");
		for (int i = 0; i < words.length; i++) {
			for (TextFilter filter : sourceFilters)
				words[i] = filter.filter(words[i]);
		}
		if (words.length == 1)
			phrases.put(words[0], null);
		else {
			multiwords.add(words);
			for (int i = 0; i < words.length; i++) {
				if (phrases.containsKey(words[i])) {
					ArrayList<IntPair> occurrences = phrases.get(words[i]);
					if (occurrences != null)
						occurrences.add(new IntPair(counter, i));
				}
				else {
					ArrayList<IntPair> occurrences = new ArrayList<IntPair>();
					occurrences.add(new IntPair(counter, i));	
					phrases.put(words[i], occurrences);
				}
			}
			counter++;
		}
	}
	
	// TODO token assumption is too strong
	// based off key for purposes of semantic inclusion (and exclusion)
	public boolean testMembership(Token token) {
		String pValue = token.getProperty(property);

		if (filters != null) {
    		for (TextFilter filter : filters)
    			pValue = filter.filter(pValue);
    	}

    	//boolean result = false;
    	if (phrases.containsKey(pValue)) {
    		if (phrases.get(pValue) == null)
    			return true;
    		else
    			return hasMembership(pValue, token.getSentence(), 
        				Integer.parseInt(token.getProperty("position")));
    	}
    	return false;
	}
	
	public String key(Object o, Object target) {
    	Token token = (Token) o;
    	String pValue = token.getProperty(property);
    	
    	if (filters != null) {
    		for (TextFilter filter : filters)
    			pValue = filter.filter(pValue);
    	}

    	boolean result = false;
    	if (phrases.containsKey(pValue)) {
    		if (phrases.get(pValue) == null)
    			result = true;
    		else
    			result = hasMembership(pValue, token.getSentence(), 
        				Integer.parseInt(token.getProperty("position")));
    	}
    	else
    		result = false;
    	
    	
    	String resultString = new String(listName);
		if (printFilters && (filterString.length() > 0))
			resultString += "{" + sFilterString + "|" + filterString + "}";
		resultString += "(" + property;
		if (printValue)
			resultString += "=" + pValue;
		resultString += ")";
		if (printFalse)
			return resultString + "=" + result;
		else if (result)
			return resultString;
		else
			return null;
	}
	
	// at this point, we know it is a multiword where one word was a match (tests for remainder?)
	public boolean hasMembership(String token, Sentence sentence, int position) {
		//System.out.println("testing full");
		ArrayList<IntPair> occurrences = phrases.get(token);
		//for (Iterator it = occurrences.iterator(); it.hasNext(); ) {
		for (IntPair occurrence : occurrences) {
			//IntPair occurrence = (IntPair) it.next();
			//System.out.println("checking " + occurrence);
			String[] phrase = (String[]) multiwords.get(occurrence.first);
			int relativePos = occurrence.second;  // this is the relative position
			if (position - relativePos < 0)  // runs out of left sentence
				continue;  
			if (position + phrase.length - relativePos > sentence.length()) // runs out of right sentence
				continue;
			boolean result = true;
			for (int i = 0; i < phrase.length; i++) {
				if (i != relativePos) {
					String t = sentence.getBelow(position - relativePos + i).getProperty(property);
					if (filters != null) {
						//for (Iterator jt = filters.iterator(); jt.hasNext(); )
						for (TextFilter filter : filters)
							t = filter.filter(t);
					}
					if (!t.equals(phrase[i]))
						result = false;
				}
			}
			if (result)
				return true;
		}
		return false;  // none of the occurrences were true;
	}
	
	// should the filterString be added?
	public String toString() {
    	String result = new String(listName);
		if (printFilters && (filterString.length() > 0))
			result += "{" + sFilterString + "|" + filterString + "}";
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
		out.println("<type>MembershipTester</type>");
		out.println("<property>" + property + "</property>");
		out.println("<listName>" + listName + "</listName>");
		out.println("<fileName>");
		for (String file : fileName)
			out.println("<file>" + file + "</file>");
		// TODO verify this
		out.println("</fileName>");
		out.println("<sourceFilters>");
		for (TextFilter filter : sourceFilters)
			filter.xml(out);
		out.println("</sourceFilters>");
		out.println("<targetFilters>");
		for (TextFilter filter : filters)
			filter.xml(out);
		out.println("</targetFilters>");
		out.println("<printFilters>" + printFilters + "</printFilters>");
		out.println("<printValue>" + printValue + "</printValue>");
		out.println("<printFalse>" + printFalse + "</printFalse>");		
		out.println("</Extractor>");
	}
}
