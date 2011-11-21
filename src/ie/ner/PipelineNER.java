package ie.ner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import ml.instance.structured.StructuredAssignment;
import ml.instance.structured.StructuredInstance;
import ml.learn.multiclass.NetworkLearner;
import ml.sample.Sample;
import nlp.annotation.AnnotatedSentence;
import nlp.annotation.LearnableSentenceAnnotator;
import nlp.annotation.PipelineSentenceAnnotator;
import nlp.core.Element;
import nlp.core.Segment;
import nlp.extraction.sequence.EntityInstanceGenerator;

/**
 * An implementation of a two-stage named entity recognition system.  First, the sentence is segmentation using the
 * BIO formalism.  Secondly, the resulting segments are classified into the specified output space.
 * 
 * @author ksmall
 */
public class PipelineNER extends PipelineSentenceAnnotator {
	
	/**
	 * Constructor
	 * 
	 * @param labelTag		the segmentation configuration tag
	 * @param localLearner	the learning algorthm for local predictions
	 * @param Evaluation_Labels	the entity labels actually used for evaluation
	 */
	public PipelineNER(String labelTag, NetworkLearner localLearner, HashSet<String> Evaluation_Labels) {
		super(labelTag, localLearner, Evaluation_Labels, "entity");
		generator = new EntityInstanceGenerator(localGenerator, "entity");
		precision = Double.NEGATIVE_INFINITY;
		recall = Double.NEGATIVE_INFINITY;
		this.Evaluation_Labels = Evaluation_Labels;
	}
	
	public void addAnnotator(LearnableSentenceAnnotator annotator) {
		previous =  annotator;
		testGenerator = new EntityInstanceGenerator(localGenerator, previous.tag());
	}
			
	public AnnotatedSentence annotate(StructuredInstance sentence) {
		StructuredAssignment predicted = learner.evaluate(sentence);
		AnnotatedSentence s = (AnnotatedSentence) sentence.label;
		ArrayList<Element> segment = s.getAnnotation(previous.tag());
		if (segment != null) {
			for (int i = 0; i < segment.size(); i++) {
				Segment seg = (Segment) segment.get(i);
				String label = labels[predicted.get(i).identifier()].split("=")[1];
				s.addPredictedEntity(labelTag, "e" + i, label, seg.getPropertyInteger("start"), seg.getPropertyInteger("end"));
			}
		}
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
						if (e1.getLabel().equals(e2.getLabel()) &&
								(e1.getProperty("start").equals(e2.getProperty("start"))) &&
								(e1.getProperty("end").equals(e2.getProperty("end"))))
							thisCorrect++;
					}
					thisGold++;
				}
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
}
