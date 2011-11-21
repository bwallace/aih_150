package ml.inference;

import java.util.ArrayList;

import shared.Copyable;
import ml.instance.structured.StructuredAssignment;
import ml.instance.structured.StructuredInstance;
import ml.instance.structured.StructuredPrediction;

/**
 * The basic (abstract) Inference class.  An inference algorithm starts with a 
 * StructuredPrediction and returns a StructuredAssignment, which is a valid
 * structured output.
 * 
 * @author ksmall
 */

public abstract class Inference implements Cloneable, Copyable<Inference> {

	/** indicates if the softmax values (true) or raw values (false) are used */
    protected boolean softmax;

    /**
     * Full constructor
     * 
     * @param softmax	determines whether softmax or raw classifier scores are used
     */
    public Inference(boolean softmax) {
    	this.softmax = softmax;
    }

    /** default constructor assumes that softmax values are used */
    public Inference() {
    	this(true);
    }

    /**
     * Takes a StructuredInstance (to get visibility information in partial assignments)
     * and a StruturedPrediction, returning the top-k assignments consistent with the
     * structural information.  Note for some inference mechanisms (such as integer linear
     * programming), this will return only one solution.  Also note that
     * if no consistent output values are found, this should return {@code null}.
     * 
     *  @param	si	the input StructuredInstance
     *  @param	sp	the input StructuredPrediction (output from a StructuredLearner)
     *  @param	assignVisible	start by making assignments of visible local predictions
     *  @param	k	the number of top-k assignments desired 
     *  @return	the top k scoring StructuredAssignment instances consistent with the constraints
     */
    public abstract ArrayList<StructuredAssignment> infer(StructuredInstance si,
		    StructuredPrediction sp, boolean assignVisible, int k);

    /**
     * Assumes that any known local predictions are not assigned before the inference algorithm
     * begins (for top-k version) as these are generally assumed visible in supervised settings. 
     * 
     *  @param	si	the input StructuredInstance
     *  @param	sp	the input StructuredPrediction (output from a StructuredLearner)
     *  @param	k	the number of top-k assignments desired
     *  @return	the top k scoring StructuredAssignment instances 
     */
    public ArrayList<StructuredAssignment> infer(StructuredInstance si, StructuredPrediction sp, int k) {
    	return infer(si, sp, false, k);
    }
    
    /**
     * Takes a StructuredInstance (to get visibility information in partial assignments)
     * and a StruturedPrediction, returning the highest scoring assignment consistent with the
     * structural information.  Note that it will often be wise to overload this function to 
     * take advantage of efficiencies not required when maintaining a top-k list.  Also note that
     * if no consistent output values are found, this should return {@code null}.
     * 
     *  @param	si	the input StructuredInstance
     *  @param	sp	the input StructuredPrediction (output from a StructuredLearner)
     *  @param	assignVisible	start by making assignments of visible local predictions
     *  @return	the top scoring StructuredAssignment
     */
    public StructuredAssignment infer(StructuredInstance si, StructuredPrediction sp, 
    		boolean assignVisible) {
    	ArrayList<StructuredAssignment> result = infer(si, sp, assignVisible, 1);
    	if (result == null)
    		return null;
    	return result.get(0);
    }

    /**
     * Assumes that any known local predictions are not assigned before the inference algorithm
     * begins (for single optimal solution version). 
     * 
     *  @param	si	the input StructuredInstance
     *  @param	sp	the input StructuredPrediction (output from a StructuredLearner)
     *  @return	the top scoring StructuredAssignment
     */
    public StructuredAssignment infer(StructuredInstance si, StructuredPrediction sp) {
    	return infer(si, sp, false);
    }
    
    public Object clone() {
    	Inference clone = null;
    	try {
    		clone = (Inference) super.clone();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	return clone;
    }
    
    public Inference copy() {
    	return (Inference) clone();
    }
    
    public Inference deepCopy() {
    	return copy();
    }
}
