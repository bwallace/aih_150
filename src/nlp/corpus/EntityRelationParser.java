package nlp.corpus;

import java.io.IOException;
import java.io.StringReader;

import org.w3c.dom.Element;

import nlp.annotation.AnnotatedSentence;
import nlp.extraction.NLP_InstanceGenerator;
import text.parse.DOM_Parser;
import ml.extraction.Generator;
import ml.instance.Instance;
import ml.sample.Parser;

public class EntityRelationParser extends Parser<Instance> {

	protected DOM_Parser parser;
	
	public EntityRelationParser(NLP_InstanceGenerator generator) {
		super(generator);
		this.parser = new DOM_Parser();
	}
	
	public EntityRelationParser(Generator<Instance> generator) {
		super(generator);
	}
	
	public Instance next() {
		String line = null;
		StringBuffer entry = new StringBuffer();
		//boolean complete = false;
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
					//System.out.println(entry.toString());
					return generate(entry.toString().trim());
					//entry = new StringBuffer();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// note that this is specifically for the relation extraction problem
	// TODO: this should be abstract with this function being abstract
	public Instance generate(String entry) {
		Element xml = parser.parse(new StringReader(entry)).getDocumentElement();
		AnnotatedSentence sentence = new AnnotatedSentence((Element) xml.getElementsByTagName("text").item(0));			
		sentence.addEntities(xml, "localization");
		sentence.addRelations(xml, "relation", "localization");
		sentence.setID(xml.getElementsByTagName("docid").item(0).getTextContent());
		/*
		AnnotatedSentence sentence = new AnnotatedSentence(xml);			
		sentence.addEntities(xml, "entity");
		sentence.addRelations(xml, "relation", "entity");
		*/
//		System.out.println(sentence);
		return generator.generate(sentence);
	}
}
