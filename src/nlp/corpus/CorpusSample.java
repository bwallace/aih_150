package nlp.corpus;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import org.w3c.dom.Element;

import text.parse.DOM_Parser;

import nlp.annotation.AnnotatedSentence;
import nlp.extraction.NLP_InstanceGenerator;

import ml.instance.structured.StructuredInstance;
import ml.sample.Sample;

/**
 * Generates a Sample from the specified xml file.
 * 
 * @author ksmall
 *
 */
// TODO - see what Parser means for this class
public class CorpusSample implements Sample<StructuredInstance> {

	protected String fileName;
	protected BufferedReader reader;
	protected DOM_Parser parser;
	protected NLP_InstanceGenerator generator;
	
	// requirement is that it begins and ends with <sentence> tags on their own line
	public CorpusSample(String fileName, NLP_InstanceGenerator generator) {
		this.fileName = fileName;
		try {
			reader = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		parser = new DOM_Parser();
		this.generator = generator;
	}

	public StructuredInstance next() {
		//System.out.println("cs:next()");
		String entry = nextEntry();
		if (entry == null)
			return null;
		return generate(entry);
	}

	public String nextEntry() {
		//System.out.println("cs:nextEntry()");
		String line = null;
		StringBuffer entry = new StringBuffer();
		boolean complete = false;
		try {
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				line = line.replace("^\\s+", "");
//				if ((entry.length() == 0) && (!line.equalsIgnoreCase("<sentence>")))
				if ((entry.length() == 0) && (!line.equalsIgnoreCase("<document>")))
				continue;
				entry.append(line);
//				if (line.equalsIgnoreCase("</sentence>")) {
				if (line.equalsIgnoreCase("</document>")) {
				complete = true;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (complete) {
			return entry.toString().trim();
		}
		return null;		
	}
	
	public AnnotatedSentence parse(String s) {
		//System.out.println("cs:parse()");
		Element xml = parser.parse(new StringReader(s)).getDocumentElement();
		AnnotatedSentence sentence = new AnnotatedSentence((Element) xml.getElementsByTagName("text").item(0));			
		sentence.addEntities(xml, "localization");
		sentence.addRelations(xml, "relation", "localization");
//		System.out.println(xml.getElementsByTagName("docid").item(0).getTextContent());
		sentence.setID(xml.getElementsByTagName("docid").item(0).getTextContent());
		/*
		AnnotatedSentence sentence = new AnnotatedSentence(xml);			
		sentence.addEntities(xml, "entity");
		sentence.addRelations(xml, "relation", "entity");
		*/
		//System.out.println(sentence);
		return sentence;
	}
		
	public StructuredInstance generate(String entry) {
		//System.out.println("cs:generate()");
		return generator.generate(parse(entry));
	}
		
	// I still don't know of a better way to rewind files that are not random access
	public void reset() {
		try {
			reader.close();
			reader = new BufferedReader(new FileReader(fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
