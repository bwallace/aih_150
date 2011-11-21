package nlp.extraction;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.NodeList;

import text.UtilityXML;

import ml.extraction.Extractor;
import nlp.core.Element;
import nlp.core.Sentence;
import nlp.core.Token;

/**
 * Generates an extractor for a sequence of items
 * 
 * @author ksmall
 */
// this means a sequence of extractions - see FEX manual for a better explanation
// (or add example here)
// TODO restrict input to not include Collocations?
public class CollocationExtractor extends PropertyExtractor implements Iterable<PropertyExtractor> {

	protected ArrayList<PropertyExtractor> extractors;
	public boolean starTarget;   // replaces target with * if true
	
	public CollocationExtractor(boolean starTarget) {
		super("CollocationExtractor");   // maybe think about later
		extractors = new ArrayList<PropertyExtractor>();
		this.starTarget = starTarget;
	}

	public CollocationExtractor() {
		this(false);
	}
	
	// keeping full Element path for clarity
	// don't do any validating for PropertyExtractor type
	// note that this will only return descendants
	public CollocationExtractor(org.w3c.dom.Element extractor) {
		this(Boolean.parseBoolean(UtilityXML.getNodeValue(extractor, "starTarget")));
		NodeList nodes = extractor.getElementsByTagName("Extractor");
		for (int i = 0; i < nodes.getLength(); i++)
			extractors.add((PropertyExtractor) Extractor.newInstance((org.w3c.dom.Element) nodes.item(i)));
	}
	
	// assume added in order
	public void add(PropertyExtractor extractor) {
		extractors.add(extractor);
		/*
		if (offset + 1 > size)  // shouldn't have to check this if in order
			size = offset + 1;
			*/
	}
	
	public int length() {
		return extractors.size();
	}
		
	// assumption here is that it wouldn't be sent if it wasn't possible to extract (no error checking)
	// assumed to be Token (and Token properties), at least for now
	// shouldn't allow recursive colloc until we know what the deal is
	public String key(Object o, Object target) {
		String result = new String("c(");
		Token current = (Token) o;
		int position = Integer.parseInt(current.getProperty("position"));
		Sentence s = (Sentence) current.getFirstAbove("Sentence");

		int counter = 0;
		for (Iterator<PropertyExtractor> it = extractors.iterator(); it.hasNext(); ) {
			PropertyExtractor extractor = (PropertyExtractor) it.next();
			int extractPos = position + counter;
			if (extractPos >= s.length())  // a bit of a hack
				return null;
			if (starTarget && (((Element) target).isInside(extractPos)))
				result += "*";
			else
				result += extractor.key(s.getBelow(extractPos));
			/*
			if (current != null)
				result += current;
			else
				break;
				*/
			if (it.hasNext())
				result += ",";
			counter++;
		}
		return result += ")";
	}

	public String toString() {
		String result = new String("Collocaton");
		if (starTarget)
			result += "*";
		result += "{";
		for (Iterator<PropertyExtractor> it = extractors.iterator(); it.hasNext(); ) {
			result += ((PropertyExtractor) it.next()).toString();
			if (it.hasNext())
				result += ",";
		}
		return result + "}";
	}

	public Iterator<PropertyExtractor> iterator() {
		return extractors.iterator();
	}
	
	public void xml(PrintWriter out) {
		out.println("<Extractor>");
		out.println("<type>" + getClass().getName() + "</type>");
		out.println("<starTarget>" + starTarget + "</starTarget>");
		out.println("<collocations>");
		for (PropertyExtractor extractor : extractors)
			extractor.xml(out);
		out.println("</collocations>");
		out.println("</Extractor>");
	}
}
