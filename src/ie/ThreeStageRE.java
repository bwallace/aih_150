package ie;

import ie.ner.PipelineNER;
import ie.relations.PipelineRE;
import ie.segmentation.BIO;

import java.util.HashMap;
import java.util.HashSet;

import text.output.StandardPrintWriter;

import ml.learn.multiclass.NetworkLearner;
import ml.learn.multiclass.online.ConstraintClassification;
import ml.learn.online.AveragedPerceptron;
import ml.utility.Globals;

public class ThreeStageRE {

	public static void main(String[] args) {

		Globals.seedRNG(459849822L);
		
		/** a regularized averaged Perceptron for the local classifiers */
		NetworkLearner learner = new ConstraintClassification(new AveragedPerceptron());
		((ConstraintClassification) learner).setGamma(0.5);

		/** the segmentation algorithm */
		BIO bio = new BIO("predictedSegmentation", learner.deepCopy());
		PipelinedNER.addGenerators(bio);		// populate feature extractors for segmentation
		bio.setProtocol(4, true);

		/** the named entity classification algorithm */
		PipelineNER ner = new PipelineNER("predictedEntity", learner.deepCopy(), NER_Labels());
		PipelinedNER.addGenerators(ner);		// populate feature extractors for ner
		ner.setProtocol(4, true);
		ner.addAnnotator(bio);	// adding the previous stage of the pipeline
		
		PipelineRE relations = new PipelineRE("predictedRelation", RelationLabels());
		relations.setProtocol(4, true);
		relations.addAnnotator(ner);
		relations.train("src/ie/data/trainCorp.xml", new HashMap<String,Integer>(), new StandardPrintWriter(System.out));
		relations.evaluate("src/ie/data/testCorp.xml", "relation");
		//relations.annotate("data/wikiDisastersWeakKMS.xml");
		
		UtilityIE.F1(relations.precision(), relations.recall(), true);
	}	
	
	public static HashSet<String> NER_Labels() {
		HashSet<String> result = new HashSet<String>();
		result.add(new String("Loc"));
		result.add(new String("Peop"));
		result.add(new String("Org"));
		return result;
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
