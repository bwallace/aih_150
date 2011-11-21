package nlp.annotation;

//import retired.CorpusRandomAccessSample;
import java.util.HashMap;

import ml.extraction.ExplanationLexicon;
import ml.extraction.Extractor;
import ml.extraction.InstanceGenerator;
import ml.extraction.Lexicon;
import ml.instance.Instance;
import ml.instance.structured.StructuredInstance;
import ml.interactive.sample.IFSC.InteractiveEvent;
import ml.interactive.sample.IFSC.InteractiveSample;
import ml.learn.multiclass.NetworkLearner;
import ml.learn.structured.online.OnlineStructuredLearner;
import ml.sample.Parser;
import nlp.corpus.CorpusSample;
import nlp.corpus.EntityRelationParser;
import nlp.extraction.NLP_InstanceGenerator;
import text.output.GeneralPrintWriter;
import text.output.StandardPrintWriter;

/**
 * Implements an annotator which has a model of annotation learned from data.
 * 
 * @author ksmall
 */
public abstract class LearnableSentenceAnnotator implements Annotator<AnnotatedSentence> {

	/** the lexicon of extracted labels */
	public Lexicon labelLexicon;
	/** the lexicon of extracted features */
	public ExplanationLexicon featureLexicon;

	protected InstanceGenerator localGenerator;
	/** the feature generation functions */
	protected NLP_InstanceGenerator generator;
	
	/** the local learning algorithm for individual predictions */
	public NetworkLearner localLearner;
	/** the structured learning algorithm */
	public OnlineStructuredLearner learner;
	/** the training corpus */
	//public CorpusRandomAccessSample training;
	//public ArrayListSample<Instance, Event<Instance>> training;
	protected InteractiveSample trainingData;
	
	/** the label for the annotation which we are learning */
	protected String labelTag;
	
	/** the number of rounds of training */
	protected int iterations;
	/** if the training data should be shuffled between rounds of training */
	protected boolean shuffle;
	
	/** for partial re-training */
	protected boolean locked;
	
	/**
	 * Primary constructor
	 * 
	 * @param labelTag		the tag used to specify the type of annotation
	 * @param localLearner	the learning algorithm used for local predictions
	 */
	public LearnableSentenceAnnotator(String labelTag, NetworkLearner localLearner) {
		labelLexicon = new Lexicon();
		featureLexicon = new ExplanationLexicon();  // assume we want explanations (since it doesn't cost much)
		localGenerator = new InstanceGenerator(featureLexicon, labelLexicon);
		this.labelTag = labelTag;
		this.localLearner = localLearner;
		setProtocol(1, false);
		locked = false;
	}
			
	/**
	 * Adds a feature generation function
	 * 
	 * @param fgf	the feature generation function to be added
	 */
	public void addFGF(Extractor fgf) {
		localGenerator.addFGF(fgf);
	}

	/**
	 * Adds a label generation function
	 * 
	 * @param lgf	the label generation function to be added
	 */
	public void addLGF(Extractor lgf) {
		localGenerator.addLGF(lgf);
	}
		
	/**
	 * Generates training data from the specified file
	 * 
	 * @param file	the input file
	 */
	public void generateTraining(String file) {
		generateTraining(file, new HashMap<String,Integer>(), new StandardPrintWriter(System.out));
	}

	/**
	 * Generates training data from the specified file (note GeneralPrintWriter is for Console)
	 * 
	 * @param file	the input file
	 * @param out	the PrintWriter to send update messages
	 */
	// TODO -- note this was made for EntityRelationParser
	//public void generateTraining(String file, PrintWriter out) {
	public void generateTraining(String file, HashMap<String,Integer> savedStatus, GeneralPrintWriter out) {
		out.println("Extracting " + getClass().getName());
		//training = new CorpusRandomAccessSample(file, generator);
		Parser<Instance> parser = new EntityRelationParser(generator);
		//training = new ArrayListSample<Instance, Event<Instance>>(file, parser);
		trainingData = new InteractiveSample(file, savedStatus, parser);
		configureLearner();
		learner.setProtocol(iterations, shuffle);		
	}
	
	public void generateTraining(InteractiveSample data, GeneralPrintWriter out) {
		out.println("Extracting " + getClass().getName());
		//training = new CorpusRandomAccessSample(file, generator);
		//Parser<Instance> parser = new EntityRelationParser(generator);
		for (InteractiveEvent event : data) {
			AnnotatedSentence s = (AnnotatedSentence) ((StructuredInstance) event.outcome).label;
			s.removeAnnotation(labelTag);
			StructuredInstance si = generator.generate(s);
			event.outcome = si;
		}
	}
	
	/**
	 * used to configure the learning algorithm once feature extraction is completed
	 */
	public abstract void configureLearner();
	
	/**
	 * Annotates the sentence associated with the input StruturedInstance
	 * 
	 * @param instance	the input structured instance
	 * @return	the resulting AnnotatedSentence (with new annotations)
	 */
	public abstract AnnotatedSentence annotate(StructuredInstance instance);
	
	/**
	 * Set the protocol of the learning algorithm
	 * 
	 * @param iterations	the number of rounds of training
	 * @param shuffle		specifies if the training data should be shuffled between iterations
	 */
	public void setProtocol(int iterations, boolean shuffle) {
		this.iterations = iterations;
		this.shuffle = shuffle;
	}

	/**
	 * @return	the structured instance generator
	 */
	public NLP_InstanceGenerator generator() {
		return generator;
	}
	
	/**
	 * Generates a StructuredInstance based on the input AnnotatedSentence
	 * 
	 * @param s	the input AnnotatedSentence
	 * @return	the resulting StructuredInstance
	 */
	public StructuredInstance generate(AnnotatedSentence s) {
		return generator.generate(s);
	}
	
	public void annotate(AnnotatedSentence element) {
		StructuredInstance instance = testGenerate(element);
		annotate(instance);
	}

	/**
	 * Trains the annotation model using the given sample
	 * 
	 * @param training	the input Sample
	 */
	//public void train(ArrayListSample<Instance, Event<Instance>> training) {
	/*
	public void train(InteractiveSample training) {
		System.out.println("Training " + getClass().getName());
		if (!locked)
			learner.train(training);
	}
	*/
	
	/**
	 * Trains the annotation model.
	 */
	// TODO -- put this back
	/*
	public void train(GeneralPrintWriter out) {
		train(trainingData, out);
	}
	*/
	
	/**
	 * Trains the annotation model based on the input file.
	 * 
	 * @param fileName	the input file
	 */
	// TODO -- figure out what locked is all about (again)
	/*
	public void train(String fileName, GeneralPrintWriter out) {
		generateTraining(fileName);
		train(trainingData, out);
	}
	*/
	
	/**
	 * Trains the annotation model based on the input file.
	 * 
	 * @param fileName	the input file
	 * @param out	the PrintWriter to send update messages
	 */
	// TODO -- propagate locked
	public void train(String fileName, HashMap<String,Integer> done, GeneralPrintWriter out) {
		if (!locked) {
			generateTraining(fileName, done, out);
			out.println("Training " + getClass().getName());
			//System.out.println(training);
			//System.out.println(learner);
			learner.train(trainingData);
		}
//		System.out.println("here1:" + learner.xml());
	}

	// TODO - for reuse
	public void train(InteractiveSample data, GeneralPrintWriter out) {
		//System.out.println(learner.xml());
		//System.out.println(data.size());
		if (!locked) {
			generateTraining(data, out);
			out.println("Training " + getClass().getName());
			learner.train(data);
		}
//		System.out.println("here2");
	}
	
	/**
	 * @return 	the annotation type specification tag
	 */
	public String tag() {
		return labelTag;
	}

	/**
	 * Generates a testing instance.
	 * 
	 * @param sentence	the input AnnotatedSentence
	 * @return	the resulting StructuredInstance
	 */
	public StructuredInstance testGenerate(AnnotatedSentence sentence) {
		return generate(sentence);
	}
	
	/**
	 * Annotates the entire input corpus.
	 * 
	 * @param sample	the input corpus
	 */
	public void annotate(CorpusSample sample) {
		StructuredInstance si = null;
		sample.reset();
		while ((si = (StructuredInstance) sample.next()) != null)
			annotate(si);
	}
	
	/**
	 * Resets the learning algorithm
	 */
	public void reset() {
		if (!locked)
			learner.reset();
	}
	
	/**
	 * Clears the feature Lexicon, such that extraction is reset
	 */
	/*
	public void clear() {
		//labelLexicon = new Lexicon();
		featureLexicon = new ExplanationLexicon();		
	}
	*/
	
	public void lock(boolean state) {
		locked = state;
	}
	
	public InteractiveSample trainingData() {
		return trainingData;
	}
	
	/*
	public void clearFeatures() {
		if (!locked)
			featureLexicon = new ExplanationLexicon();
		//labelLexicon = new Lexicon();
	}
	*/
}
