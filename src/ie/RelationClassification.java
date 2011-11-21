package ie;

import ie.relations.RelationClassifier;

import java.util.HashMap;
import java.util.HashSet;

import text.output.StandardPrintWriter;

import ml.utility.Globals;

public class RelationClassification {

	public static void main(String[] args) {

		Globals.seedRNG(459849822L);
		
		RelationClassifier ner = new RelationClassifier("predictedRelation", RelationLabels());
		//EntityClassifier ner = new EntityClassifier("entity");
		ner.setProtocol(4, true);
		ner.train("src/ie/data/trainCorp.xml", new HashMap<String,Integer>(), new StandardPrintWriter(System.out));
		// TODO think about F1 in this situation again
		ner.evaluate("src/ie/data/testCorp.xml", "relation");
		
		UtilityIE.F1(ner.precision(), ner.recall(), true);
	}
	
	public static HashSet<String> RelationLabels() {
		HashSet<String> RelationLabelSet = new HashSet<String>();
		RelationLabelSet.add(new String("OrgBased_In"));
		RelationLabelSet.add(new String("Work_For"));
		RelationLabelSet.add(new String("Located_In"));
		RelationLabelSet.add(new String("Live_In"));
		RelationLabelSet.add(new String("Kill"));
		return RelationLabelSet;
	}
}
