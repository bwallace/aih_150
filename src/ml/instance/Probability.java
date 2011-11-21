package ml.instance;

/**
 * A Probability instance essentially just supplies a second floating point
 * value to Feature in order to hold a probability.  Note that this class
 * does not actually calculate probabilities as going from a vector of scores
 * to a probability is not (to the best of my knowledge) a solved problem.
 * 
 * @author ksmall
 */

public class Probability extends FeatureReal {

	/** the probability value */
    protected double probability;

    /**
     * a constructor which starts with a Feature and simply adds a Probability
     * 
     * @param f				the Feature
     * @param probability	the Probability score for f
     */
    public Probability(Feature f, double probability) {
    	super(f); 
    	this.probability = probability;
    }

    /**
     * The copy constructor
     * 
     * @param p		the Probability to be copied
     */
    public Probability(Probability p) {
    	super(p.identifier, p.strength, p.visible);
    	this.probability = p.probability;
    }
    
    /**
     * Returns the value of the Probability instance.
     * 
     * @return	the probability value
     */
    public double probability() {
    	return probability;
    }
    
    /**
     * Returns a String representation of the form {@code identifier(strength)} 
     * followed by an {@code *} if this Feature instance is hidden followed by
     * a {@code \{probability\}}.  
     * 
     * @return		the {@code String} representation of this Probability
     */
    public String toString() {
    	if (probability != 1.0)
    		return super.toString() + "{" + probability + "}";
    	else
    		return super.toString();
    }

    public Probability copy() {
    	return new Probability(this);
    }
    
	public Probability deepCopy() {
		return copy();
	}
}
