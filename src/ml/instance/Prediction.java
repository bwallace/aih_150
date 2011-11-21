package ml.instance;

import java.util.Iterator;

import shared.Copyable;


/**
 * A Prediction is the output structure for a learning algorithm when given an
 * {@link Instance}.  Also, LossFunction uses as input both Instance instances and
 * Prediction instances to determine loss.
 * 
 * @author ksmall
 */

public class Prediction implements Copyable<Prediction>, Iterable<Feature> {

	/** the vector of labels with their corresponding activations */
    public FeatureVector scoreVector;

    /** default constructor which assumes that all of the Feature elements
     * are visible (as it was made by the classifier)
     */
    public Prediction() {
    	scoreVector = new FeatureVector(false);
    }

    /** 
     * Constructor with a single Feature mostly used for binary predictions 
     * 
     * @param f		the input Feature
     */
    public Prediction(Feature f) {
    	this();
    	addScore(f);
    }

    /**
     * The copy constructor
     * 
     * @param p		the Prediction to be copied
     */
    public Prediction(Prediction p) {
    	this();
    	scoreVector = p.scoreVector.deepCopy();
    }

    
    /**
     * adds a score associated with a specific label to the Prediction 
     * 
     * @param f	the score to be added	 
     */
    public void addScore(Feature f) {
    	scoreVector.addFeature(f);
    }
    
    /**
     * adds a set of scores to the Prediction
     * 
     * @param f	the FeatureVector of scores to be added
     */
    public void addScores(FeatureVector f) {
    	scoreVector.addFeatures(f);
    }
    
    /**
     * Instantiates an iterator over the Prediction scores
     * 
     * @return	the score vector iterator
     */
    public Iterator<Feature> iterator() {
    	return scoreVector.iterator();
    }

    /**
     * The number of scores in the prediction FeatureVector
     * 
     * @return	the number of predictions
     */
    public int size() {
    	return scoreVector.size();
    }
    
    /**
     * Returns the highest valued score in the prediction FeatureVector
     * 
     * @return	the highest valued prediction label
     */
    public Feature winner() {
    	return scoreVector.strengthMax();
    }

    /**
     * Returns the specified label if it exists in the prediction FeatureVector
     * 
     * @param f	the label to be retrieved
     * @return	the specified label if it exists, else {@code null}
     */
    public Feature get(Feature f) {
    	return scoreVector.get(f);
    }
    
    /**
     * Sorts the prediction FeatureVector by prediction scores (strength)
     */
    public void sort() {
    	scoreVector.strengthSort();
    }

    // notice sort first if we want this thing ordered (think about again)
    /**
     * Takes the current prediction FeatureVector and uses the softmax
     * calculation  to convert it to a vector of Probability instances
     */
    public void softmax() {
    	FeatureVector newVector = new FeatureVector();
    	double softmaxD = 0.0;
    	for (Feature f : this) {
    		double softmaxN = Math.exp(f.strength());
    		softmaxD += softmaxN;
    		newVector.addFeature(new Probability(f, softmaxN));
    	}
    	scoreVector.clear();
    	for (Feature p : newVector)
    		scoreVector.addFeature(new Probability(p, ((Probability) p).probability / softmaxD));
    }

    // the "linear probability" type of normalize
    public void normalize() {
    	FeatureVector newVector = new FeatureVector();
    	double sum = 0.0;
    	for (Feature f : this) {
    		sum += f.strength();
    		newVector.addFeature(new Probability(f, f.strength()));
    	}
    	scoreVector.clear();
    	for (Feature p : newVector)
    		scoreVector.addFeature(new Probability(p, ((Probability) p).probability / sum));    	
    }
    
    // assumes single label multiclass classification
    // active() added for binary (assume always positive for multiclass)
    // probably turn this into ZeroOneLoss for examples sake
    
    /**
     * Assumes single label classification and indicates if the Prediction
     * for a given instance is correct or not.  Note that this should be
     * reimplemented with the advent of general loss functions.
     * 
     * @deprecated	should be using the loss function paradigm (or at least add here)
     * @param example	the example to be tested for prediction correctness
     * @return	{@code true} if the prediction is correct, else {@code false}
     * 
     * TODO: should probably have a calculate loss function or something like this
     */
    /*
    public boolean classifies(Instance example) {
    	Feature correctLabel = example.firstLabel();
    	Feature predictLabel = winner();
    	return correctLabel.equals(predictLabel) && 
    		(correctLabel.isPositive() == predictLabel.isPositive());
    }
	*/
    
    // swaps maxValue with a different value (sort of WTA assumption)
    // sort is sort of inefficient, but few predictions
    /*
    public void addNoise(double rate) {
    	if (Globals.randDouble() < rate) {
	    // System.out.println("flipped");
    		sort();
    		Feature winner = scoreVector.get(0);
    		Feature swap = scoreVector.get(Globals.randInt(scoreVector.size() - 1) + 1);
    		int temp = winner.identifier;
    		winner.identifier = swap.identifier;
    		swap.identifier = temp;
    	}
    }
     */
    
    /**
     * a String representation of the Prediction
     */
    public String toString() {
    	return scoreVector.toString();
    }

    /**
     * Changes the polarity of a BinaryLabel into a positive/negative valued
     * score.  This is used for combining predictions for an ensemble.
     * 
     * TODO: check this over before activating
     */
    /*
    public void removePolarity() {
    	for (Iterator<Feature> it = iterator(); it.hasNext(); ) {
    		BinaryLabel f = (BinaryLabel) it.next();
    		if (!f.isPositive()) {
    			f.strength *= -1.0;
    			f.positive = true;
    		}
    	}
    }
    */
    
	public Prediction copy() {
		Prediction copy = new Prediction();
		copy.scoreVector = scoreVector.copy();
		return copy;
	}

	public Prediction deepCopy() {
		return new Prediction(this);
	}
}
