package ml.instance;

import shared.Copyable;

/**
 * The Feature class.
 * 
 * @author ksmall
 */

public class Feature implements Cloneable, Copyable<Feature> {

	/**
	 * Used to uniquely identify this specific Feature in the Lexicon
	 */
    protected int identifier;
    /**
     * Determines if the value of this Feature is visible to a learning algorithm
     */
    protected boolean visible;

    /**
     * The primary constructor
     * 
     * @param identifier  	the feature identifier
     * @param visible		the feature visibility
     */
    public Feature(int identifier, boolean visible) {
    	this.identifier = identifier;
    	this.visible = visible;
    }

    /**
     * A constructor which assumes by default that the Feature is visible
     * 
     * @param identifier	the feature identifier
     */
    public Feature(int identifier) {
    	this(identifier, true);
    }

    /**
     * A constructor utilizing the String form.
     * 
     * @param s		String of the form {@code identifier} with a 
     * 				trailing {@code *} if the particular Feature is hidden
     */
    public Feature(String s) {
    	int visiblePos = s.indexOf('*');
    	if (visiblePos == -1) {
    		this.identifier = Integer.parseInt(s);
    		this.visible = true;
    	}
    	else {
    		this.identifier = Integer.parseInt(s.substring(0, visiblePos));
    		this.visible = false;
    	}
    }

    /**
     * The copy constructor
     * 
     * @param f		the Feature to be copied
     */
    public Feature(Feature f) {
    	this.identifier = f.identifier;
    	this.visible = f.visible;
    }
    
    /**
     * Returns the identifier associated with this Feature
     * 
     * @return	the identifier
     */
    public int identifier() {
    	return identifier;
    }

    /**
     * Returns the strength associated with this Feature
     * 
     * @return	the strength
     */
    public double strength() {
    	return 1.0;
    }
    
    /**
     * Used to test equality between two Feature instances.  Equality is determined
     * exclusively by the {@code identifier} values.
     * 
     * @param o		the {@code Feature} instance to be compared for equality
     * @return 		a boolean value of {@code true} if equal, {@code false} otherwise
     */
    public boolean equals(Object o) {
    	return identifier == ((Feature) o).identifier;
    }
    
    /**
     * Returns the polarity of this Feature; used for Label instances
     *
     * @return		{@code true}
     */
    public boolean isPositive() {
    	return true;
    }

    /**
     * Sets the visibility value
     * 
     * @param visible	the new value for the visibility
     */
    public void setVisible(boolean visible) {
    	this.visible = visible;
    }

    /**
     * Returns the visibility value
     * 
     * @return		{@code true} if this Feature is visible, else {@code false}
     */
    public boolean isVisible() {
    	return visible;
    }

    /**
     * Returns a String representation of the form {@code identifier} 
     * followed by an {@code *} if this Feature instance is hidden
     * 
     * @return		the {@code String} representation of this Feature
     */
    public String toString() {
    	String result = Integer.toString(identifier);
    	if (!visible)
    		result += "*";
    	return result;
    }
    
    /**
     * returns a clone of this instance
     * 
     *  @return		a clone of this instance
     */
    public Object clone() {
    	Feature clone = null;
    	try {
    		clone = (Feature) super.clone();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	return clone;
    }
  
    public Feature copy() {
    	return new Feature(this);
    }

	public Feature deepCopy() {
		return copy();
	}
}
