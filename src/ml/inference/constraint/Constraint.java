package ml.inference.constraint;


import ml.instance.structured.StructuredAssignment;

/**
 * Used to define constraints for ensuring structural coherence.
 * 
 * @author ksmall
 */
public abstract class Constraint {

    public boolean softmax;

    public Constraint(boolean softmax) {
    	this.softmax = softmax;
    }
    
    public Constraint() { 
    	this(true); 
    }

    public abstract boolean valid(StructuredAssignment example);
}
