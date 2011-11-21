package ie;

import java.util.HashMap;

import text.output.StandardPrintWriter;
import nlp.extraction.BIO_Extractor;
import nlp.extraction.CollocationExtractor;
import nlp.extraction.MembershipTester;
import nlp.extraction.PropertyExtractor;
import nlp.extraction.PropertyTester;
import nlp.extraction.UtilityExtraction;
import nlp.extraction.WindowExtractor;
import nlp.extraction.filter.Lowercase;
import ie.segmentation.BIO;
import ml.extraction.BiasElement;
import ml.learn.multiclass.NetworkLearner;
import ml.learn.multiclass.online.ConstraintClassification;
import ml.learn.online.AveragedPerceptron;
import ml.utility.Globals;

/**
 * An implementation of a BIO segmentation system.
 * 
 * @author ksmall
 */
public class Segmentation {

	public static void main(String[] args) {

		/** seeds the random number generator, such that experiments are repeatable */
		Globals.seedRNG(459849822L);

		/** a regularized averaged Perceptron for the local classifiers */
		NetworkLearner learner = new ConstraintClassification(new AveragedPerceptron());
		((ConstraintClassification) learner).setGamma(0.5);
				
		/** the segmentation algorithm */
		BIO bio = new BIO("predictedSegmentation", learner);
		addGenerators(bio);			// add the feature extraction functions
		bio.setProtocol(4, true);
		// train the model
		bio.train("src/ie/data/trainCorp.xml", new HashMap<String,Integer>(), 
				new StandardPrintWriter(System.out));
		bio.evaluate("src/ie/data/testCorp.xml", "entity");		// test the model
		
		UtilityIE.F1(bio.precision(), bio.recall(), true);
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
}
