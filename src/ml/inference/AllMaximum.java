package ml.inference;

import java.util.ArrayList;

import ml.instance.structured.StructuredAssignment;
import ml.instance.structured.StructuredInstance;
import ml.instance.structured.StructuredPrediction;

/**
 * AllMaximum simply returns the maximum local scoring prediction for each variable, assuming that
 * all possible global predictions are valid (thereby not really being a structured prediction).
 * Mostly used for cases when several predictions have to be made in a pipeline and such.
 * 
 * @author ksmall
 */

public class AllMaximum extends Inference {

	/**
	 * Constructor
	 * 
	 * @param softmax	indicates whether raw scores or softmax scores are used
	 */
    public AllMaximum(boolean softmax) {
    	super(softmax);
    }
    
    /**
     * Constructor which assumes softmax scores are used
     */
    public AllMaximum() {
    	this(true);
    }

    /**
     * Takes a StructuredInstance (to get visibility information in partial assignments)
     * and a StruturedPrediction, returning the top assignment based exclusively on local
     * predictions.
     * 
     *  @param	si	the input StructuredInstance
     *  @param	sp	the input StructuredPrediction (output from a StructuredLearner)
     *  @param	assignVisible	start by making assignments of visible local predictions
     */
    public StructuredAssignment infer(StructuredInstance si, StructuredPrediction sp,
    		boolean assignVisible) {
    	/*
    	System.out.println("si=" + si);
    	System.out.println("sp=" + sp);
    	*/
    	if (softmax)
    		sp.softmax();
    	StructuredAssignment result = StructuredAssignment.max(sp);
    	if (assignVisible)
    		result.assignVisible(si);
    	result.score();
    	return result;
    }
    
    /**
     * Takes a StructuredInstance (to get visibility information in partial assignments)
     * and a StruturedPrediction, returning the top assignment based exclusively on local
     * predictions.  Note that this will return only one solution.
     * 
     *  @param	si	the input StructuredInstance
     *  @param	sp	the input StructuredPrediction (output from a StructuredLearner)
     *  @param	assignVisible	start by making assignments of visible local predictions
     *  @param	k	the number of top-k assignments desired 
     */
    public ArrayList<StructuredAssignment> infer(StructuredInstance si, StructuredPrediction sp, 
			   boolean assignVisible, int k) {
    	ArrayList<StructuredAssignment> result = new ArrayList<StructuredAssignment>();
    	result.add(infer(si, sp, assignVisible));
    	return result;
    }
    
    public AllMaximum copy() {
    	return (AllMaximum) clone();
    }
}
