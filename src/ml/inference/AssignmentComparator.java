package ml.inference;

import java.util.Comparator;


import ml.instance.structured.StructuredAssignment;
import ml.utility.Globals;

/**
 * AssignmentComparator determines ordering of two StructuredAssignment based
 * on their scoring function.  Used primarily for searching during the
 * inference process.
 * 
 * @author ksmall
 */

public class AssignmentComparator implements Comparator<StructuredAssignment> {

	/** indicates whether the raw scores or softmax scores are used for comparison */
    protected boolean softmax;

    /**
     * Constructor
     * 
     * @param softmax	indicates whether raw scores or softmax scores are compared
     */
    public AssignmentComparator(boolean softmax) {
    	this.softmax = softmax;
    }

    /**
     * Compares two StructuredAssignment instances.  Returns a value less than 0 if the
     * score of o1 is numerically greater than o2 and vice versa (descending order).  Note
     * that ties (equality) are broken randomly for convenience, even though this is somewhat
     * against the standard Comparator semantics (could add a flag later).
     * 
     * @param	o1	the first StructuredAssignment to compare
     * @param	o2	the second StructuredAssignment to compare
     */
    public int compare(StructuredAssignment o1, StructuredAssignment o2) {
    	int result = 0;
    	if (softmax)
    		result = -Double.compare(o1.softmaxScore(), o2.softmaxScore());
    	else
    		result = -Double.compare(o1.score(), o2.score());
    	if (result == 0) {
    		if (Globals.randDouble() < 0.5)
    			result = -1;
    		else
    			result = 1;
    	}
    	return result;
    }
}
