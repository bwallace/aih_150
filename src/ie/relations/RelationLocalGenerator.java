package ie.relations;

import nlp.extraction.PropertyExtractor;
import nlp.extraction.filter.Lowercase;
import nlp.extraction.relations.RelationEntityExtractor;
import nlp.extraction.relations.RelationEntityLengthExtractor;
import nlp.extraction.relations.RelationEntityWindowExtractor;
import nlp.extraction.relations.RelationLabelExtractor;
import nlp.extraction.relations.RelationLengthExtractor;
import nlp.extraction.sequence.SetExtractor;
import ml.extraction.BiasElement;
import ml.extraction.InstanceGenerator;
import ml.extraction.Lexicon;

public class RelationLocalGenerator extends InstanceGenerator {

	public RelationLocalGenerator(Lexicon featureLexicon, Lexicon labelLexicon) {
		super(featureLexicon, labelLexicon);

		PropertyExtractor lowercase = new PropertyExtractor("text");
		lowercase.addFilter(new Lowercase());
		
		// the labels (in this case, true) of the entities labels
		PropertyExtractor eLabel = new PropertyExtractor("label");
		RelationEntityExtractor entityLabels = new RelationEntityExtractor(eLabel, 3, true);
		addFGF(entityLabels);
		
		// the text of the entities (currently doesn't mark locations)
		/*
		SequenceTokenExtractor eText = new SequenceTokenExtractor("text");
		eText.addFilter(new Lowercase());
		RelationEntityExtractor entityText = new RelationEntityExtractor(eText, 2, false);		
		localGenerator.addFGF(entityText);
		*/
		
		// the words of the entities (currently doesn't mark locations)
		SetExtractor eWords = new SetExtractor(lowercase);
		RelationEntityExtractor entityWords = new RelationEntityExtractor(eWords, 3, false);
		addFGF(entityWords);
		
		RelationEntityWindowExtractor window = new RelationEntityWindowExtractor(lowercase, -2, 2);
		addFGF(window);
		
		RelationEntityLengthExtractor entityLength = new RelationEntityLengthExtractor();
		addFGF(entityLength);
		
		RelationLengthExtractor relationLength = new RelationLengthExtractor();
		addFGF(relationLength);
		
		addFGF(new BiasElement());
		
		RelationLabelExtractor label = new RelationLabelExtractor(true, "e");
		
		addLGF(label);	
	}
	

}
