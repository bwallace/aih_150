package ie;

import java.util.HashMap;
import java.util.HashSet;

import server.Server;
import text.output.StandardPrintWriter;
import nlp.extraction.BIO_Extractor;
import nlp.extraction.CollocationExtractor;
import nlp.extraction.MembershipTester;
import nlp.extraction.PropertyExtractor;
import nlp.extraction.PropertyTester;
import nlp.extraction.UtilityExtraction;
import nlp.extraction.WindowExtractor;
import nlp.extraction.filter.Lowercase;
import nlp.extraction.sequence.SequenceLengthExtractor;
import nlp.extraction.sequence.SequencePropertyExtractor;
import nlp.extraction.sequence.SequenceWindowExtractor;
import nlp.extraction.sequence.SetExtractor;

import ie.segmentation.BIO;
import ml.extraction.BiasElement;
import ml.learn.multiclass.NetworkLearner;
import ml.learn.multiclass.online.ConstraintClassification;
import ml.learn.online.AveragedPerceptron;
import ml.utility.Globals;
import ie.ner.PipelineNER;
import ie.ner.ProcessNER;

/**
 * Implementation of a two-stage named entity extraction system which first extracts entity segments and then
 * classifies them into {Peop, Loc, Org}.
 * 
 * Some command line arguments:
 * <ul>
 * <li>-train &lt;file&gt; : an xml file of the training data (with a specific format) [default: src/ie/data/trainCorp.xml]
 * <li>-test &lt;file&gt;	 : an xml file of the testing data [default: src/ie/data/testCorp.xml]
 * <li>-noshuffle : will remove the shuffling of data between rounds of training the structured Perceptron
 * <li>-rounds : specifies the rounds of training [default: 4]
 * <li>-server &lt;port&gt; : specifies the port number if this is run in server mode
 * </ul>
 * 
 * @author ksmall
 */
public class PipelinedNER {
	
	public static void main(String[] args) {

		/** seeds the random number generator, such that experiments are repeatable */
		Globals.seedRNG(459849822L);

		/** default command line parameters */
		String train = "src/ie/data/trainCorp.xml";
		String test = "src/ie/data/testCorp.xml";
		boolean shuffle = true;
		boolean evaluate = true;
		int rounds = 4;
		int port = Integer.MIN_VALUE;  // sentinel meaning "not in server mode"
		
		/** no error checking here for the command line */
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-train"))
				train = args[i+1];
			else if (args[i].equals("-test"))
				test = args[i+1];
			else if (args[i].equals("-noshuffle"))
				shuffle = false;
			else if (args[i].equals("-noevaluate"))
				evaluate = false;
			else if (args[i].equals("-rounds"))
				port = Integer.parseInt(args[i+1]);
			else if (args[i].equals("-server")) {
				port = Integer.parseInt(args[i+1]);
				evaluate = false;
			}
		}
		
		/** a regularized averaged Perceptron for the local classifiers */
		NetworkLearner learner = new ConstraintClassification(new AveragedPerceptron());
		((ConstraintClassification) learner).setGamma(0.5);
				
		/** the segmentation algorithm */
		BIO bio = new BIO("predictedSegmentation", learner.deepCopy());
		addGenerators(bio);		// populate feature extractors for segmentation
		bio.setProtocol(rounds, shuffle);

		/** the named entity classification algorithm */
		PipelineNER ner = new PipelineNER("predictedEntity", learner.deepCopy(), NER_Labels());
		addGenerators(ner);		// populate feature extractors for ner
		ner.setProtocol(rounds, shuffle);
		ner.addAnnotator(bio);	// adding the previous stage of the pipeline
		// train the model
		ner.train(train, new HashMap<String,Integer>(), new StandardPrintWriter(System.out));
		
		
		/** evaluate algorithm on test data */
		if (evaluate) {
			/** evaluating segmentation */
			bio.evaluate(test, "entity");
			UtilityIE.F1(bio.precision(), bio.recall(), true);
			/** evaluating ner */
			ner.evaluate("src/ie/data/testCorp.xml", "entity");
			UtilityIE.F1(ner.precision(), ner.recall(), true);
		}
		
		/** start server mode if specified */
		if (port > Integer.MIN_VALUE) {
			System.out.print("Starting server...");
			Server server = new Server(port, new ProcessNER(ner));
			System.out.println("done.");
			server.start();
		}
	}	
	
	/**
	 * Adds feature generation functions to the segmentation feature extractor.
	 * 
	 * @param bio	the segmentation annotator
	 */
	public static void addGenerators(BIO bio) {
		
		/** the target word (lowercase) */
		PropertyExtractor lowercase = new PropertyExtractor("text");
		lowercase.addFilter(new Lowercase());
		bio.addFGF(lowercase);
				
		/** tests if the first letter of a words is capitalized */
		PropertyTester startsCaps = new PropertyTester("[A-Z].*");		
		bio.addFGF(startsCaps);

		/** tests if the words surrounding the target word are capitalized */
		bio.addFGF(new WindowExtractor(startsCaps, -1, 1));
		
		/** the words (lowercase) in the [-3,2] window surrounding the target word */
		bio.addFGF(new WindowExtractor(lowercase, -3, 2));
		
		/** the bigrams within a [-2,2] window around the target word */
		CollocationExtractor bigram = new CollocationExtractor();
		bigram.add(lowercase);
		bigram.add(lowercase);
		bio.addFGF(new WindowExtractor(bigram, -2, 2));
	
		/** tests if the words within a [-2,-1] window of the target word are a known location */
		MembershipTester city = UtilityExtraction.getGazetteer("City", "src/ie/data/known_city.lst", true);
		bio.addFGF(new WindowExtractor(city, -2, -1));		 

		/** tests if the words within a [-2,-1] window of the target word are a known male name */
		MembershipTester male = UtilityExtraction.getGazetteer("Male", "src/ie/data/known_maleFirst.lst", false);
		bio.addFGF(male);
		bio.addFGF(new WindowExtractor(male, -2, -1));

		/** tests if the words within a [-2,-1] window of the target word are a known female name */
		MembershipTester female = UtilityExtraction.getGazetteer("Female", "src/ie/data/known_femaleFirst.lst", false);
		bio.addFGF(female);
		bio.addFGF(new WindowExtractor(female, -2, -1));
		
		/** tests if the target word is a month */
		MembershipTester months = UtilityExtraction.getGazetteer("Month", "src/ie/data/known_months.lst", false);
		bio.addFGF(months);
		
		/** the bias element */
		bio.addFGF(new BiasElement());
		
		/** the label generator function */
		bio.addLGF(new BIO_Extractor(false));
	}

	/**
	 * Adds feature generation functions to the named entity classifier feature extractor.
	 * 
	 * @param ner	the named entity annotator
	 */
	public static void addGenerators(PipelineNER ner) {
		
		/** extracts the lowercase words */
		PropertyExtractor lowercase = new PropertyExtractor("text");
		lowercase.addFilter(new Lowercase());
		
		/** extracts the length of the target segment */
		SequenceLengthExtractor length = new SequenceLengthExtractor();
		ner.addFGF(length);
		
		/** a concatenation of the words (lowercase) in the target segment */
		SequencePropertyExtractor entityText = new SequencePropertyExtractor("text");
		entityText.addFilter(new Lowercase());
		ner.addFGF(entityText);
		
		/** all of the words (lowercase) contained in the target segment */
		SetExtractor entityWords = new SetExtractor(lowercase);
		ner.addFGF(entityWords);
		
		/** tests if the words in the target segment belong to a list of known cities */
		MembershipTester city = UtilityExtraction.getGazetteer("City", "src/ie/data/known_city.lst", true);
		ner.addFGF(new SetExtractor(city));

		/** tests if the words in the target segment belong to a list of known male names */
		MembershipTester male = UtilityExtraction.getGazetteer("Male", "src/ie/data/known_maleFirst.lst", false);
		ner.addFGF(new SetExtractor(male));

		/** tests if the words in the target segment belong to a list of known female names */
		MembershipTester female = UtilityExtraction.getGazetteer("Female", "src/ie/data/known_femaleFirst.lst", false);
		ner.addFGF(new SetExtractor(female));
		
		/** extracts all of the words (lowercase) within [-2,2] window of the target segment */
		SequenceWindowExtractor window  = new SequenceWindowExtractor(lowercase, -2, 2);
		ner.addFGF(window);
			
		/** a bias element */
		ner.addFGF(new BiasElement());
		
		/** the label extractor */
		PropertyExtractor label = new PropertyExtractor("label");
		ner.addLGF(label);
	}
	
	/**
	 * A list of labels that we want to use for evaluation.
	 * 
	 * @return	the list of labels
	 */
	public static HashSet<String> NER_Labels() {
		HashSet<String> result = new HashSet<String>();
		result.add(new String("Loc"));
		result.add(new String("Peop"));
		result.add(new String("Org"));
		return result;
	}
}
