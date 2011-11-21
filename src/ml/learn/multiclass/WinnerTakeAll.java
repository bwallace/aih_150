package ml.learn.multiclass;

import ml.extraction.Lexicon;
import ml.instance.FeatureReal;
import ml.instance.Instance;
import ml.instance.Prediction;
import ml.learn.Learner;
import ml.learn.PredictionScore;

/**
 * The NetworkLearner form where the evaluation function is such that the prototype with
 * the largest score represents the prediction.  Should have error checking to enforce
 * that we have PredictionScore implemented.
 * 
 * @author ksmall
 */
public abstract class WinnerTakeAll extends NetworkLearner {

	/**
	 * The full constructor
	 * 
	 * @param learner	the base learner
	 * @param targets	targets for which learners are generated
	 * @param labels	the label lexicon (to get integers associated with labels)
	 */
	public WinnerTakeAll(Learner learner, String[] targets, Lexicon labels) {
		super(learner);
		addClasses(targets, labels);
		/*
		for (int i = 0; i < targets.length; i++)
			addClass(labels.get(targets[i]));
		*/
	}	

	/**
	 * Constructor which assumes all labels in the LabelLexicon will have a single
	 * prototype in the network.
	 * 
	 * @param learner	the base learner
	 * @param labels	the LabelLexicon describing the output space
	 */
	public WinnerTakeAll(Learner learner, Lexicon labels) {
		//this(learner, (String[]) labels.description.keySet().toArray(new String[0]), labels);
		super(learner);
		addClasses(labels);
	}

	public WinnerTakeAll(Learner learner) {
		super(learner);
	}
	
	/* probably not necessary as inheritance will handle
	public WinnerTakeAll(WinnerTakeAll learner) {
		super(learner);
	}
	*/
	
	public void addClasses(String[] targets, Lexicon labels) {
		for (int i = 0; i < targets.length; i++)
			addClass(labels.get(null, targets[i]));		
	}
		
	/**
	 * Returns the scores associated with each learner in the network with respect to the
	 * given instance.  
	 * 
	 * @param example	the input instance to be scored
	 * @return	the resulting scores
	 */
	public Prediction evaluate(Instance example) {
		Prediction result = new Prediction();
		//Iterator<Integer> it = network.keySet().iterator();
		//while (it.hasNext()) {
		for (Integer identifier : network.keySet()) {
			//Feature label = (Feature) it.next();
			Learner learner = (Learner) network.get(identifier);
			result.addScore(new FeatureReal(identifier, ((PredictionScore) learner).score(example)));
		}
		return result;
	}
	
	public Prediction trainEvaluate(Instance example) {
		Prediction result = new Prediction();
		//Iterator<Integer> it = network.keySet().iterator();
		//while (it.hasNext()) {
		for (Integer identifier : network.keySet()) {
		//Feature label = (Feature) it.next();
			Learner learner = (Learner) network.get(identifier);
			result.addScore(new FeatureReal(identifier, ((PredictionScore) learner).trainScore(example)));
		}
		return result;		
	}
	
	/**
	 * Returns the score associated with the winner.
	 * 
	 * @param example	the input example
	 * @return	the score of the winning prototype
	 */
	public double score(Instance example) {
		return evaluate(example).winner().strength();
	}
	
	public double trainScore(Instance example) {
		return trainEvaluate(example).winner().strength();
	}
	
	public void addClass(int id) {
		Learner algorithm = (Learner) baseLearner.copy();
		algorithm.setIdentifier(id);
		network.put(new Integer(id), algorithm);		
	}
	
	public WinnerTakeAll copy() {
		return (WinnerTakeAll) super.copy();
	}
	
	public WinnerTakeAll deepCopy() {
		return (WinnerTakeAll) super.deepCopy();
	}
}
