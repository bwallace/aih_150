package nlp.annotation;

import java.util.HashMap;
import java.util.HashSet;

import text.output.GeneralPrintWriter;

import ml.instance.structured.StructuredInstance;
import ml.interactive.sample.IFSC.InteractiveSample;
import ml.learn.multiclass.NetworkLearner;
import ml.learn.structured.online.OnlineLocalStructured;
import ml.sample.Sample;
import nlp.corpus.PipelineSample;
import nlp.extraction.NLP_InstanceGenerator;
import nlp.predict.EvaluatePR;

/**
 * An annotator where the parameters of the model used for annotation are learned and the input
 * are other learned annotators.  Used to conveniently add previous annotators and enforce the correct
 * order of feature extraction and training.
 * 
 * @author ksmall
 *
 */
public abstract class PipelineSentenceAnnotator extends LearnableSentenceAnnotator implements EvaluatePR {

	protected String[] labels;  // initialized in configure
	protected HashSet<String> Evaluation_Labels;

	protected double precision;
	protected double recall;

	protected LearnableSentenceAnnotator previous;
	protected NLP_InstanceGenerator testGenerator;
		
	public PipelineSentenceAnnotator(String labelTag, NetworkLearner localLearner, 
			HashSet<String> Evaluation_Labels, String extractionLabel) {
		super(labelTag, localLearner);
		this.Evaluation_Labels = Evaluation_Labels;
		precision = Double.NEGATIVE_INFINITY;
		recall = Double.NEGATIVE_INFINITY;
		previous = null;
	}
	
	public void configureLearner() {
		labels = labelLexicon.invertArray();
		localLearner.addClasses(labelLexicon);
		learner = new OnlineLocalStructured(localLearner);
	}

	public abstract void addAnnotator(LearnableSentenceAnnotator annotator);
		
	public LearnableSentenceAnnotator previous() {
		return previous;
	}
	
	public StructuredInstance testGenerate(AnnotatedSentence sentence) {
		StructuredInstance p = previous.testGenerate(sentence);
		previous.annotate(p);
		return testGenerator.generate((AnnotatedSentence) p.label);
	}
	
	public abstract void evaluate(Sample<StructuredInstance> data, String trueTag);
		
	public void evaluate(String fileName, String trueLabel) {
		PipelineSample testing = new PipelineSample(fileName, this);
		evaluate(testing, trueLabel);
	}
	
	public void annotate(String fileName) {
		PipelineSample testing = new PipelineSample(fileName, this);
		annotate(testing);
	}
	
	/*
	public void train(String fileName) {
		previous.train(fileName);
		generateTraining(fileName);
		train();
	}
	*/
	
	// TODO -- propagate locked
	public void train(String fileName, HashMap<String,Integer> savedStatus, GeneralPrintWriter out) {
		previous.train(fileName, savedStatus, out);
		if (!locked) {
			generateTraining(fileName, savedStatus, out);
			out.println("Training " + getClass().getName());
			learner.train(trainingData);
		}
	}

	public void train(InteractiveSample data, GeneralPrintWriter out) {
		//System.out.println(previous);
		previous.train(data, out);
		if (!locked) {
			generateTraining(data, out);  // since it may be from the wrong pipeline stage
			out.println("Training " + getClass().getName());
			learner.train(data);
		}
	}
	
	// TODO -- fix this
	/*
	public void train(InteractiveSample data, GeneralPrintWriter out) {
		// nothing for a moment
	}
	*/
	
	public void reset() {
		previous.reset();
		if (!locked)
			super.reset();
	}
	
	/*
	public void clear() {
		previous.clear();
		super.clear();
	}
	*/
	
	public double precision(Sample<StructuredInstance> data, String trueLabel, String predictedLabel) {
		if (precision == Double.NEGATIVE_INFINITY)
			evaluate(data, trueLabel);
		return precision;
	}

	public double precision() {
		return precision;
	}
	
	public double recall(Sample<StructuredInstance> data, String trueLabel, String predictedLabel) {
		if (recall == Double.NEGATIVE_INFINITY)
			evaluate(data, trueLabel);
		return recall;
	}
	
	public double recall() {
		return recall;
	}	
	
	/*
	public void clearFeatures() {
		previous.clearFeatures();
		super.clearFeatures();
	}
	*/
}
