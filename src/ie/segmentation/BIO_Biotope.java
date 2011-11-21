package ie.segmentation;

import java.util.ArrayList;
import java.util.Iterator;


import ml.inference.BeamSearch;
import ml.inference.Inference;
import ml.inference.constraint.BIOConstraint;
import ml.instance.structured.StructuredAssignment;
import ml.instance.structured.StructuredInstance;
import ml.learn.multiclass.NetworkLearner;
import ml.learn.structured.online.InferenceBasedTraining;
import ml.sample.Sample;
import nlp.annotation.AnnotatedSentence;
import nlp.annotation.LearnableSentenceAnnotator;
import nlp.core.Element;
import nlp.core.Segment;
import nlp.corpus.CorpusSample;
import nlp.extraction.SentenceInstanceGenerator;

/**
 * Implements a learnable model of BIO segmentation for annotation.
 * 
 * @author ksmall
 *
 */
public class BIO_Biotope extends LearnableSentenceAnnotator {

	/** the labels for segmentation - automatically generated after extraction */
	protected String[] BIO_Labels;
	/** the precision on a given dataset */
	protected double precision;
	/** the recall on a given dataset */
	protected double recall;
	
	/**
	 * Constructor
	 * 
	 * @param labelTag		the segmentation configuration tag
	 * @param localLearner	the learning algorthm for local predictions
	 */
	public BIO_Biotope(String labelTag, NetworkLearner localLearner) {
		super(labelTag, localLearner);
		generator = new SentenceInstanceGenerator(localGenerator);
		precision = Double.NEGATIVE_INFINITY;
		recall = Double.NEGATIVE_INFINITY;
	}
	
	public void configureLearner() {
		BIO_Labels = labelLexicon.invertArray();
//		for (int i = 0; i < BIO_Labels.length; i++)
//			System.out.println(BIO_Labels[i]);
		localLearner.addClasses(labelLexicon);
		Inference inference = new BeamSearch(new BIOConstraint(BIO_Labels), 25);
		learner = new InferenceBasedTraining(localLearner, inference);
	}

	public void annotate(AnnotatedSentence element) {
		StructuredInstance instance = generator.generate(element);
		annotate(instance);
	}
	
	public AnnotatedSentence annotate(StructuredInstance sentence) {
		StructuredAssignment predicted = learner.evaluate(sentence);
		//System.out.println(predicted);
		AnnotatedSentence s = (AnnotatedSentence) sentence.label;
		//System.out.println(s);
		boolean inside = false;
		int start = 0; 
		int end = 0;
		for (int i = 0; i < predicted.size(); i++) {
			String l = BIO_Labels[predicted.get(i).identifier()];
			//System.out.println(predicted.get(i).identifier() + ", " + l);
			if (l.split("=")[1].equals("B")) {
				if (inside)
					s.addPredictedSegment(labelTag, start, end);
				start = i;
				end = i;
				inside = true;
			}
			else if (l.split("=")[1].equals("I")) {
				end = i;
			}
			else { // if O
				if (inside)
					s.addPredictedSegment(labelTag, start, end);
				inside = false;
			}
		}
		if (inside)
			s.addPredictedSegment(labelTag, start, end);
		//System.out.println(s);
		return s;
	}

	/**
	 * Evaluates the current trained model on the input data Sample.
	 * 
	 * @param data		the input data Sample
	 * @param trueTag	the specification of the annotation label
	 */
	public void evaluate(Sample<StructuredInstance> data, String trueTag) {
		int predicted = 0;
		int correct = 0;
		int gold = 0;
		
		StructuredInstance si = null;
		data.reset();
		
		while ((si = (StructuredInstance) data.next()) != null) {
			//int thisPredicted = 0;
			annotate(si);
			//System.out.println(si.label);
			int thisCorrect = 0;
			int thisGold = 0;
			ArrayList<Element> trueEntities = ((AnnotatedSentence) si.label).getAnnotation(trueTag);
			ArrayList<Element> predictedEntities = ((AnnotatedSentence) si.label).getAnnotation(labelTag);
			if (predictedEntities != null) {
				for (Iterator<Element> it = trueEntities.iterator(); it.hasNext(); ) {
					Segment e1 = (Segment) it.next();
					for (Iterator<Element> jt = predictedEntities.iterator(); jt.hasNext(); ) {
						Segment e2 = (Segment) jt.next();
						// this is where we would get F1*
						if ((e1.getProperty("start").equals(e2.getProperty("start"))) &&
							(e1.getProperty("end").equals(e2.getProperty("end"))))
							thisCorrect++;
					}
					thisGold++;
				}
				/*
				for (Iterator<Element> it = predictedEntities.iterator(); it.hasNext(); ) {
					Segment e1 = (Segment) it.next();
					for (Iterator<Element> jt = trueEntities.iterator(); jt.hasNext(); ) {
						Entity e2 = (Entity) jt.next();
						if ((e1.getProperty("start").equals(e2.getProperty("start"))) &&
							(e1.getProperty("end").equals(e2.getProperty("end"))))
							thisCorrect++;
					}
					thisPredicted++;
				}
				*/
				gold += thisGold;
				correct += thisCorrect;
				predicted += predictedEntities.size();
			}
			else {
				gold += trueEntities.size();
			}
		}
		precision = (double) correct / predicted;
		recall = (double) correct / gold;
	}
		
	/**
	 * Evaluates the current trained model on the Sample derived from the input file.
	 * 
	 * @param fileName	the input file
	 * @param trueLabel	the specification of the annotation label
	 */
	public void evaluate(String fileName, String trueLabel) {
		CorpusSample testing = new CorpusSample(fileName, generator());
		evaluate(testing, trueLabel);
	}
	
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

	public void annotate(String fileName) {
		CorpusSample data = new CorpusSample(fileName, generator());
		StructuredInstance i = null;
		while ((i = data.next()) != null) {
			annotate(i);
			ArrayList<Element> predictedEntities = ((AnnotatedSentence) i.label).getAnnotation("predictedSegmentation");
			// the label actually holds the AnnotatedSentence object
			System.out.println(((Element) i.label).getID());
			System.out.println(i.label);
//			System.out.println(((Element) (i.label)).getProperty("text"));
			String[] text = ((AnnotatedSentence) i.label).getTokenString().split("\\s+|\\/|'|-");
			int[] start = new int[text.length];
			int[] end = new int[text.length];
			// needed to handle annotation standard of the shared task
			int counter = 0;
			for (int k = 0; k < text.length; k++) {
				int prefix = text[k].length() - text[k].replaceAll("^\\W*", "").length();
				int suffix = text[k].length() - text[k].replaceAll("\\W+\\d+(,\\d+)*\\W+$", "").length();
				start[k] = counter + prefix;
				end[k] = counter + text[k].length() - suffix;
				counter += (text[k].length() + 1);
			}
			int j = 1;
			if (predictedEntities != null) {
				for (Element e : predictedEntities) {
					System.out.print("T" + j + "\t");
					int sI = e.getPropertyInteger("start");
					int eI = e.getPropertyInteger("end");
					System.out.print(e.getLabel() + " " + start[sI] + " " + end[eI] + "\t"); 
					for (int x = sI; x < eI; x++)
						System.out.print(text[x] + " ");
					System.out.println(text[eI]);
					j++;
				}
			}
		}
	}
}
