package ml.instance;

/**
 * Extends the FeatureReal class to include polarity for binary classification problems.
 * Assume real-valued features as there generally won't be a lot of labels.
 * 
 * @author ksmall
 */

public class BinaryLabel extends FeatureReal {

	/**
	 * States the polarity of the given label
	 */
    protected boolean positive;
    
    /**
     * The primary constructor
     * 
     * @param positive		the label polarity
     * @param identifier	the label identifier
     * @param strength		the label strength
     * @param visible		the feature visibility
     */
    public BinaryLabel(boolean positive, int identifier, double strength, boolean visible) {
    	super(identifier, strength, visible);
    	this.positive = positive;
    }

    /**
     * A constructor which assumes that the label is visible (for the standard supervised
     * learning setting)
     * @param positive		the label polarity
     * @param identifier	the label identifier
     * @param strength		the label strength
     */
    public BinaryLabel(boolean positive, int identifier, double strength) {
    	super(identifier, strength);
    	this.positive = positive;
    }
    
    /**
     * A constructor which assumes that the label is visible and the strength is 1.0 
     * (the standard binary supervised learning setting)
     * 
     * @param positive		the label polarity
     * @param identifier	the label identifier
     */
    public BinaryLabel(boolean positive, int identifier) {
    	this(positive, identifier, 1.0);
    }

    /**
     * A constructor which takes a label and converts it to a binary label (adds polarity).
     * This is primarily used by one versus all learning to generate negative instances.
     * 
     * @param positive	the polarity of the label
     * @param f			the label feature
     */
    public BinaryLabel(boolean positive, FeatureReal f) {
    	super(f);
    	this.positive = positive;
    }

    /**
     * The copy constructor
     * 
     * @param b		the BinaryLabel to be copied
     */
    public BinaryLabel(BinaryLabel b) {
    	this(b.positive, b.identifier, b.strength, b.visible);
    }
    
    /**
     * Returns the polarity status
     * 
     * @return {@code true} if the label is positive, else {@code false}
     */
    public boolean isPositive() {
    	return positive;
    }

    /**
     * Returns a {@code String} representation of the same form as Feature with 
     * the addition of a preceding {@code !} if the label is negative
     * 
     * @return		the {@code String} representation of this Feature
     */
    public String toString() {
    	String result = new String();
    	if (!positive)
    		result += "!";
    	result += super.toString();
    	return result;
    }
    
    public BinaryLabel copy() {
    	return new BinaryLabel(this);
    }
    
	public BinaryLabel deepCopy() {
		return copy();
	}
}
