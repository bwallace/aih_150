package ie.relations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import ml.instance.structured.StructuredAssignment;
import ml.instance.structured.StructuredInstance;
import ml.learn.multiclass.NetworkLearner;
import ml.learn.multiclass.online.ConstraintClassification;
import ml.learn.online.AveragedPerceptron;
import ml.learn.structured.online.OnlineLocalStructured;
import ml.sample.Sample;
import nlp.annotation.AnnotatedSentence;
import nlp.annotation.LearnableSentenceAnnotator;
import nlp.core.Element;
import nlp.core.Entity;
import nlp.core.Relation;
import nlp.corpus.CorpusSample;
import nlp.extraction.relations.RelationInstanceGenerator;
import nlp.predict.EvaluatePR;

public class RelationClassifier extends LearnableSentenceAnnotator implements EvaluatePR {

	protected String[] Labels;  // initialized in configure
	protected double precision;
	protected double recall;
	
	protected HashSet<String> Evaluation_Labels;
	/*
	public EntityClassifier(String labelTag, NetworkLearner localLearner, LocalGeneratorBIO localGenerator) {
		super(labelTag, localLearner); //, localGenerator);
		generator = new EntityInstanceGenerator(localGenerator, "entity");
		precision = Double.NEGATIVE_INFINITY;
		recall = Double.NEGATIVE_INFINITY;
	}
	*/

	public RelationClassifier(String labelTag, NetworkLearner localLearner, HashSet<String> Evaluation_Labels) {
		super(labelTag, localLearner);
		precision = Double.NEGATIVE_INFINITY;
		recall = Double.NEGATIVE_INFINITY;
		generator = new RelationInstanceGenerator(new RelationLocalGenerator(featureLexicon, labelLexicon), "entity");
		this.Evaluation_Labels = Evaluation_Labels;
	}
	
	// not very general at all, but useful
	// wonder if gamma not being set is making the performance difference (is being copied)
	public RelationClassifier(String labelTag, HashSet<String> Evaluation_Labels) {
		this(labelTag, new ConstraintClassification(new AveragedPerceptron()), Evaluation_Labels);
		((ConstraintClassification) localLearner).setGamma(0.5);
	}

	public void configureLearner() {
		Labels = labelLexicon.invertArray();
		localLearner.addClasses(labelLexicon);
		//Inference inference = new BeamSearch(new BIOConstraint(BIO_Labels), 25);
		learner = new OnlineLocalStructured(localLearner);
	}

	// this one assumes extraction hasn't been performed yet
	public void annotate(AnnotatedSentence element) {
		StructuredInstance instance = generator.generate(element);
		annotate(instance);
	}
		
	// TODO (this assumes that the instance has already been labeled by segmentation)
	public AnnotatedSentence annotate(StructuredInstance sentence) {
		StructuredAssignment predicted = learner.evaluate(sentence);
		AnnotatedSentence s = (AnnotatedSentence) sentence.label;
		//System.out.println("Annotating: " + s);
		ArrayList<Element> entities = s.getAnnotation("entity");
		String outsideLabel = "0";
		
		int counter = 0;
		if (entities != null) {
			for (int i = 0; i < entities.size(); i++) {
				Entity e0 = (Entity) entities.get(i);
				for (int j = i + 1; j < entities.size(); j++) {
					Entity e1 = (Entity) entities.get(j);
				// very specific to certain formatting conventions
					String label = Labels[predicted.get(counter++).identifier()].split("=")[1];
					String[] fields = label.split("\\(");
					label = fields[0];
					//System.out.println(e0 + "," + e1 + ": " + labels[predicted.get(counter-1).identifier()]);
					if (label.equals(outsideLabel))
						continue;
					else if (fields[1].equals("0,1)"))
						s.addPredictedRelation(labelTag, "r" + Integer.toString(counter - 1), label, e0, e1);
					else
						s.addPredictedRelation(labelTag, "r" + Integer.toString(counter - 1), label, e1, e0);
				}
			}
		}
		//System.out.println("Result: " + s);
		return s;
	}

	public void annotate(String fileName) {
		CorpusSample testing = new CorpusSample(fileName, generator());
		StructuredInstance si = null;
		testing.reset();
		
		while ((si = (StructuredInstance) testing.next()) != null) {
			//int thisPredicted = 0;
			System.out.println(annotate(si));
			//annotate(si);
		}
	}
	
	public void evaluate(String fileName, String trueLabel) {
		CorpusSample testing = new CorpusSample(fileName, generator());
		evaluate(testing, trueLabel);
	}
	
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
			int thisPredicted = 0;
			ArrayList<Element> trueRelations = ((AnnotatedSentence) si.label).getAnnotation(trueTag);
			ArrayList<Element> predictedRelations = ((AnnotatedSentence) si.label).getAnnotation(labelTag);
			if (predictedRelations != null) {
				for (Iterator<Element> it = trueRelations.iterator(); it.hasNext(); ) {
					Relation r1 = (Relation) it.next();
					Entity r1e0 = r1.getArgument(0);
					Entity r1e1 = r1.getArgument(1);
					if (Evaluation_Labels.contains(r1.getLabel())) {
						for (Iterator<Element> jt = predictedRelations.iterator(); jt.hasNext(); ) {
							Relation r2 = (Relation) jt.next();
							// assuming left and right here (as dictated by the labeler)
							Entity r2e0 = r2.getArgument(0);
							Entity r2e1 = r2.getArgument(1);
							// this is where we would do F1*
							if ((r1e0.getProperty("start").equals(r2e0.getProperty("start"))) &&
								(r1e0.getProperty("end").equals(r2e0.getProperty("end"))) &&
								(r1e1.getProperty("start").equals(r2e1.getProperty("start"))) &&
								(r1e1.getProperty("end").equals(r2e1.getProperty("end"))) &&
								(r1.getLabel().equals(r2.getLabel())))
								thisCorrect++;
						}
						thisPredicted++;
					}
					thisGold++;
				}
				gold += thisGold;
				correct += thisCorrect;
				predicted += thisPredicted;
			}
			else {
				gold += trueRelations.size();
			}
		}
		precision = (double) correct / predicted;
		recall = (double) correct / gold;
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
}
