package ml.learn.multiclass.online;

import java.util.Iterator;
import ml.extraction.Lexicon;
import ml.instance.Feature;
import ml.instance.Instance;
import ml.instance.Prediction;
import ml.instance.SingleInstance;
import ml.learn.online.LinearThresholdUnit;
import ml.learn.online.OnlineLearner;

// assume "conservative"
/**
 * Extends the winner take all prediction rule with the learning algorithm where each
 * multiclass example is projected into a higher dimensional problem via the Kesler
 * construction.  This method is described by:
 * 
 * citation
 * 
 * @author ksmall
 */
public class ConstraintClassification extends WinnerTakeAll {
	
	/** the required distance between the two highest scores for updates */
	protected double gamma = 0.0;
	
	/**
	 * the basic constructor
	 * 
	 * @param learner	the base learner
	 * @param labels	the label Lexicon which determines the output space
	 */
	public ConstraintClassification(LinearThresholdUnit learner, Lexicon labels) {
		super(learner, labels);
	}

	public ConstraintClassification(LinearThresholdUnit learner) {
		super(learner);
	}

	public ConstraintClassification(ConstraintClassification learner) {
		super(learner);
		this.gamma = learner.gamma;
	}
	
	/**
	 * Sets the required thickness between the highest two scores, which will determine
	 * is an update is necessary.
	 * 
	 * @param value	the new value of gamma
	 */
	public void setGamma(double value) {
		gamma = value;
	}

	public void train(Instance example) {
		//System.out.println(example);
		//System.out.println(trainEvaluate(example));
		train(example, trainEvaluate(example).winner());
	}
	
	public void train(Instance example, Feature prediction) {
		// not sure about visible with this
		/*
		for (Iterator it = network.keySet().iterator(); it.hasNext(); ) {
			((LinearThresholdUnit) network.get((Feature) it.next())).advanceTime();
		}
		*/
		for (Integer identifier : network.keySet())
			((OnlineLearner) network.get(identifier)).advanceTime();
		Feature one = ((SingleInstance) example).firstLabel();
		//System.out.println(example);
		if (!one.isVisible())
			return;
//		System.out.println(one + ", " + prediction);
		if (!prediction.equals(one))
			update(example, one, prediction);
		else if (gamma > 0.0) {   // should really verify gamma is working
			Prediction activations = trainEvaluate(example);
			activations.sort();
			for (Iterator<Feature> it = activations.iterator(); it.hasNext(); ) {
				Feature prototype = (Feature) it.next();
				if (prototype.equals(one) && it.hasNext()) {
					Feature second = (Feature) it.next();
					double margin = prototype.strength() - second.strength();
					if (margin < gamma)
						update(example, one, second);
				}
			}
		}
		// need to give command to "average" here
		/*
		for (Iterator it = network.keySet().iterator(); it.hasNext(); ) {
			LinearThresholdUnit ltu = (LinearThresholdUnit) network.get((Feature) it.next());
			ltu.average();
		}
		*/
	}
		
	/**
	 * Actually performs the "conservative" constraint classification update.
	 * 
	 * @param example		the current training instance
	 * @param correct		the correct labeling (to be promoted)
	 * @param prediction	the predicted labeling (to be demoted)
	 */
	protected void update(Instance example, Feature correct, Feature prediction) {
//		System.out.println("updating");
		((LinearThresholdUnit) network.get(correct.identifier())).promote(example);
		((LinearThresholdUnit) network.get(prediction.identifier())).demote(example);		
	}
	
	public ConstraintClassification copy() {
		ConstraintClassification copy = (ConstraintClassification) super.copy();
		copy.gamma = gamma;
		return copy;
	}
	
	public ConstraintClassification deepCopy() {
		ConstraintClassification copy = (ConstraintClassification) super.deepCopy();
		copy.gamma = gamma;
		return copy;
	}
}
