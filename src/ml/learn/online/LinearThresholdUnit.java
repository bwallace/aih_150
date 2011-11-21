package ml.learn.online;

import java.io.PrintWriter;

import ml.instance.BinaryLabel;
import ml.instance.Feature;
import ml.instance.Instance;
import ml.instance.Prediction;
import ml.instance.SingleInstance;
import ml.learn.PredictionScore;

/**
 * The abstract class for the linear threshold unit (LTU) form of online learners.  This
 * class forms the basis for all forms of Perceptron and Winnow.
 * 
 * @author ksmall
 */

public abstract class LinearThresholdUnit extends OnlineLearner implements PredictionScore {
    
	/** the LTU threshold which determines positive or negative classification */
    protected double threshold;
    /** the positive side thickness of the separator used to determine updates */
    protected double positiveGamma;
    /** the negative side thickness of the separator used to determine updates */
    protected double negativeGamma;

    protected LinearThresholdUnit(int identifier, int iterations, boolean shuffle,
    		double threshold, double positiveGamma, double negativeGamma) {
    	super(identifier, iterations, shuffle);
    	this.threshold = threshold;
    	this.positiveGamma = positiveGamma;
    	this.negativeGamma = negativeGamma;    	
    }

    protected LinearThresholdUnit(double threshold, double positiveGamma, double negativeGamma) {
    	super();
    	this.threshold = threshold;
    	this.positiveGamma = positiveGamma;
    	this.negativeGamma = negativeGamma;    	    	
    }
    
    /**
     * The default constructor (all parameters set to 0.0)
     */
    protected LinearThresholdUnit() {
    	this(0.0, 0.0, 0.0);
    }
    
    protected LinearThresholdUnit(LinearThresholdUnit ltu) {
    	this(ltu.identifier, ltu.iterations, ltu.shuffle, ltu.threshold, ltu.positiveGamma, ltu.negativeGamma);
    }
    
    /**
     * Sets the value of the threshold
     * 
     * @param t	the new LTU threshold
     */
    public void setThreshold(double t) {
    	threshold = t;
    }

    /**
     * Sets the new positive and negative thickness necessary to not require updates
     * 
     * @param pG	the new positive thickness
     * @param nG	the new negative thickness
     */
    public void setGamma(double pG, double nG) {
    	positiveGamma = pG;
    	negativeGamma = nG;
    }

    // should think a bit about Gamma in this case
    // if the label isn't visible, we just don't train
    // don't consider negations in multiclass (not meant to handle)
    /**
     * The training implementation for a single Instance relative to a given
     * prediction.  If a prediction is not supplied, it is assumed that the
     * prediction of the learner is used.  Note that if the label is not 
     * visible, the Instance is just skipped and no training is performed.
     * 
     * @param example		the Instance presented for training
     * @param prediction	the prediction to be used for feedback
     */
    public void train(Instance example, Feature prediction) {
    	super.train(example, prediction);
    	Feature labelFeature = ((SingleInstance) example).firstLabel();
    	if (!labelFeature.isVisible())
    		return;

    	boolean label = labelFeature.isPositive();
    	
    	if (prediction == null)
    		prediction = trainEvaluate(example).winner();

    	/*
    	System.out.println("--------------");
    	System.out.println(example);
    	System.out.println(label + ", " + prediction);
    	 */

    	//System.out.println(labelFeature + "," + labelFeature.isPositive() + "," + prediction + "," + prediction.isPositive());
    	if (label != prediction.isPositive()) {  // an incorrect labeling
    		//System.out.println("making update");
    		if (label)
    			promote(example);
    		else 
    			demote(example);
    	}
    	else if (label && (prediction.strength() < positiveGamma))
    		promote(example);
    	else if (!label && (prediction.strength() < negativeGamma))
    		demote(example);
    }
    
    //public void average() { ; }
    
    // just return entire Prediction, regardless of input prediction
    /*
    public TrialInstance trainTrial(Instance example, Feature prediction) {
    	Prediction result = evaluate(example);
    	if (prediction == null)
    		train(example, result.winner());
    	else
    		train(example, prediction);
    	return new TrialInstance(example, result);
    }
    */
    
    /**
     * Evaluates a given Instance based on the current hypothesis of the Learner
     * 
     * @param example	the Instance to be evaluated
     * @return			the resulting Prediction (assumed binary)
     */
    public Prediction evaluate(Instance example) {
    	double score = score(example);
    	BinaryLabel label = new BinaryLabel(score >= threshold, identifier,
    										Math.abs(score - threshold));
    	return new Prediction(label);
    }
    
    public Prediction trainEvaluate(Instance example) {
    	double score = trainScore(example);
    	BinaryLabel label = new BinaryLabel(score >= threshold, identifier,
				Math.abs(score - threshold));
    	return new Prediction(label);
    }
    
    /**
     * The abstract method for LTU promotion (for false negatives)
     * 
     * @param example	the Instance for which the weight vector is to be promoted
     */
    public abstract void promote(Instance example);
    
    /**
     * The abstract method for LTU demotion (for false positives)
     * 
     * @param example	the Instance for which the weight vector is to be demoted
     */
    public abstract void demote(Instance example);

	public void xmlBody(PrintWriter out) {
		super.xmlBody(out);
		out.println("<threshold>" + threshold + "</threshold>");
		out.println("<positiveGamma>" + positiveGamma + "</positiveGamma>");
		out.println("<negativeGamma>" + negativeGamma + "</negativeGamma>");
	}
    
    // for adaptive rate algorithms
    /* 
    public abstract void promote(Instance example, double rate);
    public abstract void demote(Instance example, double rate);
    */

    /*
    public String toString() {
	return w.toString();
    }
    */
    public LinearThresholdUnit copy() {
    	return (LinearThresholdUnit) clone();
    }
}