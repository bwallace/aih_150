package nlp.semantics;

import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import nlp.semantics.SemanticSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

// TODO maybe add rank
public class SemanticDictionary extends DefaultHandler implements Iterable<SemanticSet> {
		
	protected HashMap<String, ArrayList<SetPointer>> dictionary;
	protected HashMap<String, SemanticSet> semanticSets;
	protected boolean lowercase;
	protected CharArrayWriter text;	
	protected String currentID;
	protected String currentScore;
	protected String currentPhrase;
	protected XMLReader reader;
	protected SemanticSet currentSet;
	
	public SemanticDictionary(boolean lowercase) {
		dictionary = new HashMap<String, ArrayList<SetPointer>>();
		semanticSets = new HashMap<String, SemanticSet>();
		this.lowercase = lowercase;
		text = new CharArrayWriter();
		try {
			reader = XMLReaderFactory.createXMLReader();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		reader.setContentHandler(this);
		reader.setErrorHandler(this);
	}
	
	public void readFile(String fileName) {
		try {
			reader.parse(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public ArrayList<SetPointer> get(String phrase) {
		return dictionary.get(phrase);
	}
	
	public SemanticSet lookup(String key) {
		return semanticSets.get(key);
	}
	
	public void startElement(String uri, String name, String qName, Attributes atts) {
		text.reset();
	}
	
	public void endElement(String uri, String name, String qName) {
		if (qName.equals("id")) {
			currentID = text();
		}
		else if (qName.equals("keys")) {
			//currentSet = new SemanticSet(text(), currentID);
			currentSet = new SemanticSet(currentID, text());
		}
		else if (qName.equals("phrase")) {
			currentPhrase = text();
			if (lowercase)
				currentPhrase = currentPhrase.toLowerCase();
		}
		else if (qName.equals("score")) {
			currentScore = text();
		}
		else if (qName.equals("count")) {
			String count =  text();
			/*
			SetPointer pointer = new SetPointer(currentID, currentScore);
			ArrayList<SetPointer> pointers = dictionary.get(currentPhrase);
			if (pointers == null)
				pointers = new ArrayList<SetPointer>();
			pointers.add(pointer);
			dictionary.put(currentPhrase, pointers);
			currentSet.add(new SemanticElement(currentPhrase, currentScore, count));
			*/
			currentSet.add(generateElement(currentID, currentPhrase, currentScore, count));
		}
		else if (qName.equals("set"))
			semanticSets.put(currentID, currentSet);
	}

	// intended to be external
	public void addSemanticSet(SemanticSet set) {
		String id = set.key;
		for (SemanticElement element : set) {
			String phrase = element.phrase;
			String score = element.score;
			String count = element.count;
			generateElement(id, phrase, score, count);
		}
		semanticSets.put(id, set);
	}
	
	public SemanticElement generateElement(String id, String phrase, String score, String count) {
		SetPointer pointer = new SetPointer(id, score);
		ArrayList<SetPointer> pointers = dictionary.get(phrase);
		if (pointers == null)
			pointers = new ArrayList<SetPointer>();
		pointers.add(pointer);
		dictionary.put(phrase, pointers);
		return new SemanticElement(phrase, score, count);
	}
	
	public void characters(char ch[], int start, int length) {
		text.write(ch, start, length);
	}
	
	public String text() {
		return text.toString().trim();
	}
	
	public void print() {
		System.out.println("<dictionary>");
		for (String phrase : dictionary.keySet()) {
			String printPhrase = phrase.replace("&", "&amp;");
			System.out.println("<entry>");
			System.out.println("   <phrase>" + printPhrase + "</phrase>");
			for (SetPointer pointer : dictionary.get(phrase))
				System.out.println(pointer);
			System.out.println("</entry>");
		}
		System.out.println("</dictionary>");
	}
	
	public class SetPointer {
		
		public String key;
		public double score;
		
		public SetPointer(String key, String score) {
			this.key = key;
			this.score = Double.parseDouble(score);
		}
		
		public String toString() {
			String result = new String("   <member>\n");
			result += "      <id>" + key + "</id\n";
			result += "      <score>" + score + "</score>\n";
			result += "   </member>";
			return result;
		}
	}
	
	public Iterator<SemanticSet> iterator() {
		return semanticSets.values().iterator();
	}
}
