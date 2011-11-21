package ml.instance.structured;

import java.util.ArrayList;
import java.util.Iterator;

import shared.Copyable;

import ml.instance.Feature;
import ml.instance.FeatureReal;
import ml.instance.Prediction;
import ml.instance.Probability;
import ml.instance.SingleInstance;
import ml.inference.constraint.Constraint;

/**
 * StructuredAssignment is the output structure of an inference process for structured output
 * predictions.  Essentially, it holds the StructuredPrediction resulting from passing a 
 * StructuredInstance through a StructuredLearner and holds intermediate representations for
 * inference algorithms (i.e. partial assignments and such).  Therefore, this is heavily used
 * for search-based optimization, essentially representing a state of the search procedure. 
 * An important note is that, by default, only a pointer to the input StructuredPrediction is
 * held (this can be overcome with a copy or deepCopy - although I don't see why you would
 * want to).
 * 
 * @author ksmall
 */

public class StructuredAssignment implements Copyable<StructuredAssignment> {

	/** contains the predictions resulting from the inference process */
    protected FeatureReal[] assignments;
    /** contains the StructuredPredicion output from a StructuredLearner */
    protected StructuredPrediction predictions;
    /** a cached value of the global prediction score */
    protected double score;
    /** a cached value of the global prediction softmax score */
    protected double softmaxScore;
    /** the number of local variables which have been assigned an inferred value */
    protected int numAssigned;
    /** the number of local variables which were visible at construction */
    protected int numVisible;

    /**
     * Constructor.  Note that the input StruturedPrediction is not copied, but
     * only makes a reference assignment.
     * 
     * @param sp	the input StructuredPrediction
     */
    public StructuredAssignment(StructuredPrediction sp) {
    	predictions = sp;
    	assignments = new FeatureReal[predictions.size()];
    	score = Double.NEGATIVE_INFINITY;
    	softmaxScore = Double.NEGATIVE_INFINITY;
    	numAssigned = 0;
    	numVisible = 0;
    }

    /**
     * Given a StructuredInstance (which is presumed to have been used to generate the 
     * StructuredPrediction via a StructuredLearner), assignVisible makes assignments for
     * all local variables for which the true value is given.  Assumes that a local prediction
     * has a single true label (as throughout StructuredAssignment).
     * 
     * @param	si	the StructuredInstance associated with the input StructuredPrediction
     */
    public void assignVisible(StructuredInstance si) {
    	int counter = 0;
    	numVisible = 0;
    	for (SingleInstance instance : si) {
    		Feature label = instance.firstLabel();
    		if (label.isVisible()) {
    			if (assignments[counter] == null)  // overwriting assignment
    				numAssigned++;
    			assignments[counter] = new FeatureReal(label.identifier(), Double.POSITIVE_INFINITY);
    			numVisible++;
    		}
    		resetScores();
    		counter++;
    	}
    }

    /**
     * allVisible determines if all local variables are visible during construction
     * 
     * @return	{@code true} if all local variables are visible, else {@code false}
     */
    public boolean allVisible() {
    	return (numVisible == size());
    }
	    
    /**
     * Returns the assignment associated with the local variable at the specified index position
     * 
     * @param index	the specified index position
     * @return	the assignment at the specified index position
     */
    public FeatureReal get(int index) {
    	return assignments[index];
    }
    
    /**
     * A test for equality between two StructuredAssignment instances.  Note that this equality test
     * relies on equality between the Feature assignments at each position, which in turn is dependent 
     * exclusively on the identifier member elements associated with each Feature. 
     * 
     * @param sa	the StructuredAssignment to be tested against for equality
     * @return	{@code true} if the StructuredAssignment in equal, else {@code false}
     */
    public boolean equals(StructuredAssignment sa) {
    	if (assignments.length != sa.assignments.length)
    		return false;
    	else {
    		for (int i = 0; i < assignments.length; i++) {
    			if (((assignments[i] == null) && (sa.assignments[i] != null)) ||
    					((assignments[i] != null) && (sa.assignments[i] == null)))
    				return false;
    			if (!assignments[i].equals(sa.assignments[i]))
    				return false;
    		}
    	}
    	return true;
    }

    public boolean exclusivelyGlobal() {
    	int counter = 0;
    	for (Iterator<Prediction> it = predictionIterator(); it.hasNext(); ) {
    		if (it.next().winner().identifier() != get(counter++).identifier())
    			return true;
    	}
    	return false;
    }
    
    // 0 means locally learnable
    public int exclusivelyGlobalDistance() {
    	int result = 0;
    	int counter = 0;
    	for (Iterator<Prediction> it = predictionIterator(); it.hasNext(); ) {
    		if (it.next().winner().identifier() != get(counter++).identifier())
    			result++;
    	}    	
    	return result;
    }
    
    // if not same length, assume both from beginning, then different
    /*
    public int HammingLoss(StructuredAssignment sa) {
    	int min = Math.min(assignments.length, sa.assignments.length);
    	int max = Math.max(assignments.length, sa.assignments.length);
    	int result = max - min;
    	for (int i = 0; i < min; i++) {
    		if (((assignments[i] == null) && (sa.assignments[i] != null)) ||
    				((assignments[i] != null) && (sa.assignments[i] == null)) ||
    				(!assignments[i].equals(sa.assignments[i])))
    			result++;
    	}
    	return result;
    }
    */
   
    /**
     * Returns an iterator over the underlying StructuredPrediction instance.
     */
    public Iterator<Prediction> predictionIterator() {
    	return predictions.iterator();
    }

    public void set(int index, FeatureReal value) {
    	assignments[index] = value;
    	numAssigned++;
    }
    
    /**
     * Returns an ArrayList containing all of the predictions of the local variable at the
     * specified index which are consistent with the structural constraint as determined 
     * by the variables which have already been assigned at other positions.
     * 
     * @param index			the specified index which to expand
     * @param constraint	the constraint which the expansion must be consistent with
     * @return	the ArrayList of consistent expansions of the variable at the specified position
     */
    public ArrayList<StructuredAssignment> expand(int index, Constraint constraint) {
    	if (assignments[index] != null)
    		return null;
    	ArrayList<StructuredAssignment> result = new ArrayList<StructuredAssignment>();
    	for (Feature prediction : predictions.get(index)) {
    		StructuredAssignment sa = this.copyState();
    		sa.assignments[index] = (FeatureReal) prediction;
    		sa.resetScores();
    		sa.numAssigned++;
    		if ((constraint == null) || (constraint.valid(sa)))
    			result.add(sa);
    	}
    	return result;
    }

    /**
     * Expands the local variable at the specified position under the assumption
     * that there are no constraints on this variable.
     * 
     * @param index	the specified index which to expand
     * @return	the ArrayList of expansions of the variable at the specified position
     */
    public ArrayList<StructuredAssignment> expand(int index) { 
    	return expand(index, null); 
    }

    /**
     * Expands the first unexpanded local variables from left to right such that
     * the expansion is consistent with the specified constraint.
     * 
     * @param constraint	the constraint which the expansion must be consistent with
     * @return	the ArrayList of consistent expansions at the first unexplored position
     */
    public ArrayList<StructuredAssignment> expand(Constraint constraint) {
    	for (int i = 0; i < assignments.length; i++) {
    		if (assignments[i] == null)
    			return expand(i, constraint);
    	}
    	return null;
    }

    /**
     * Expands the first unexpanded local variables from left to right 
     * under the assumption that there are no constraints on this variable.
	 *
     * @return	the ArrayList of expansions at the first unexplored position
     */
    public ArrayList<StructuredAssignment> expand() { return expand(null); }

    /**
     * Calculates the sum of raw scores of the assigned local prediction variables.
     * Note that this caches the value such that multiple calls will not require 
     * recalculation.
     * 
     * @return	the global score based on raw activation values
     */
    public double score() {
    	if (score == Double.NEGATIVE_INFINITY)
    		score = calculateScore(false);
    	return score;
    }

    /**
     * Calculates the sum of probability scores of the assigned local prediction variables.
     * Note that this caches the value such that multiple calls will not require 
     * recalculation.
     * 
     * @return	the global score based on local probability values
     */
    public double softmaxScore() {
    	if (softmaxScore == Double.NEGATIVE_INFINITY) {
    		softmaxScore = calculateScore(true);
    	}
    	return softmaxScore;
    }
    
    /**
     * Calculates the global prediction score based on the sum of local prediction scores.
     * Note that this sum doesn't include scores for variables which have either not yet
     * been assigned values or were assigned values that were visible (known ahead of time).
     * 
     * @param softmax	{@code true} if probability values should be used, {@code false} if the
     * 					raw activation values should be used
     * @return	the global score
     */
    public double calculateScore(boolean softmax) {
    	double result = 0.0;
    	for (int i = 0; i < assignments.length; i++) {
    		if ((assignments[i] != null) && 
    				(assignments[i].strength() != Double.POSITIVE_INFINITY))  {
    			if (softmax)
    				result += ((Probability) assignments[i]).probability();
    			else
    				result += assignments[i].strength();    // argmax
    		}
    	}
    	return result;
    }
    
    /**
     * Clears the cached values of the local scores such that they will be recalculated
     * next time they are needed.  Note that expanding a local variable calls this function.
     */
    public void resetScores() {
    	score = Double.NEGATIVE_INFINITY;
    	softmaxScore = Double.NEGATIVE_INFINITY;
    }
    
    /**
     * Indicates if assignments have been made for all local predictions.
     * 
     * @return	{@code true} if all assignments have been made, else {@code false}
     */
    public boolean isComplete() {
    	return numAssigned == assignments.length;
    }

    /*
    // relplace by Hamming distance loss function
    public int numClassifies(StructuredInstance example) {
    	int index = 0;
    	int result = 0;
    	for (Iterator it = example.iterator(); it.hasNext(); ) {
	    // System.out.println(index + ", " + assignments[index]);
    		if ((assignments[index] == null) ||
    				(assignments[index].strength == Double.POSITIVE_INFINITY)) {
    			index++;
    			it.next();
    			continue;
    		}
    		Prediction prediction = new Prediction(assignments[index]);
    		if (prediction.classifies((Instance) it.next()))
    			result++;
    		index++;
    	}
    	return result;
    }
	*/

    /**
     * The total number of local variables
     * 
     * @return	the number of local variables
     */
    public int size() {
    	return predictions.size();
    }

    /**
     * The total number of local variables for which predictions are necessary (e.g. not
     * having known values)
     * 
     * @return	the number of necessary local predictions
     */
    public int predictionSize() {
    	return size() - numVisible;
    }

    /**
     * The underlying StructuredPrediction object used to make assignments
     * 
     * @return	the StructuredPrediction instance
     */
    public StructuredPrediction predictions() {
    	return predictions;
    }

    // TODO - check if better way, but at least comment
    public FeatureReal[] assignments() {
    	return assignments;
    }
    
    public String toString() {
    	String result = new String("[" + assignments[0]);
    	for (int i = 1; i < assignments.length; i++) 
    		result += ", " + assignments[i];
    	//result += ": " + score() + "]\n" + predictions;
    	result += ":";
    	if (score != Double.NEGATIVE_INFINITY)
    		result += " " + score();
    	if (softmaxScore != Double.NEGATIVE_INFINITY)
    		result += " " + softmaxScore();
    	result += "]";
    	return result;
    }
    
    /**
     * Returns a StructuredAssignment reflecting the maximum local prediction at each position,
     * assuming that no constraints exist.
     * 
     * @param sp	the input StructuredPrediction
     * @return	the maximum local prediction at each position
     */
    public static StructuredAssignment max(StructuredPrediction sp) {
    	StructuredAssignment result = new StructuredAssignment(sp);
    	result.numAssigned = 0;
    	//for (Iterator it = sp.iterator(); it.hasNext(); )
    	for (Prediction prediction : sp)
    		result.assignments[result.numAssigned++] = new FeatureReal(prediction.winner());
    	//result.calculateScore();
    	return result;
    }

    // made for sequential learning bit
    public static StructuredAssignment max(StructuredPrediction sp, int lastIndex) {
    	StructuredAssignment result = new StructuredAssignment(sp);
    	result.numAssigned = 0;
    	//for (Iterator it = sp.iterator(); it.hasNext(); )
    	//for (Prediction prediction : sp)
    	for (int i = 0; i <= lastIndex; i++)
    		result.assignments[result.numAssigned++] = new FeatureReal(sp.get(i).winner());
    	//result.calculateScore();
    	return result;    	
    }
    
    // also sequential learning bit
    public void max(int firstIndex, int lastIndex) {
    	for (int i = firstIndex; i <= lastIndex; i++)
    		assignments[i] = new FeatureReal(predictions.get(i).winner());
    }
    
    /**
     * Returns a StructuredAssignment with the correct labeling as determined by the input
     * StructuredInstance.  Note that the input StructuredPrediction is used only to make
     * a valid StructuredAssignment structure and the scores associated with the true labels
     * are not included in the resulting structure.  Also, note that it is assumed that the
     * input StructuredPrediction was generated via the input StructuredInstance - therefore,
     * no error checking and such is performed.
     * 
     * @param example	the input StructuredInstance
     * @param sp		the input StructuredPrediction
     * @return	a StructuredAssignment based on the correct labeling
     */
    public static StructuredAssignment correctLabels(StructuredInstance example,
					       StructuredPrediction sp) {
    	StructuredAssignment result = new StructuredAssignment(sp);
    	result.numAssigned = 0;
    	Iterator<SingleInstance> instanceIterator = example.iterator();
    	for (Prediction prediction : sp) {
    		Feature label = ((SingleInstance) instanceIterator.next()).firstLabel();
    		result.assignments[result.numAssigned++] = new FeatureReal(prediction.get(label));
    	}
    	return result;
    }

	public StructuredAssignment copy() {
    	StructuredAssignment copy = new StructuredAssignment(predictions.copy());
    	copy.assignments = new FeatureReal[assignments.length];
    	for (int i = 0; i < assignments.length; i++)
    		copy.assignments[i] = assignments[i].copy();
    	copy.score = score;
    	copy.softmaxScore = softmaxScore;
    	copy.numAssigned = numAssigned;
    	copy.numVisible = numVisible;
    	return copy;		
	}

	public StructuredAssignment deepCopy() {
    	StructuredAssignment copy = new StructuredAssignment(predictions.deepCopy());
    	copy.assignments = new FeatureReal[assignments.length];
    	for (int i = 0; i < assignments.length; i++)
    		copy.assignments[i] = assignments[i].deepCopy();
    	copy.score = score;
    	copy.softmaxScore = softmaxScore;
    	copy.numAssigned = numAssigned;
    	copy.numVisible = numVisible;
    	return copy;		
	}
	
	/**
	 * Basically a shallow copy with the exception that the pointer to the StructuredPrediction
	 * is simply copied on the surface.  Since many StructuredAssignment instances in searching and
	 * similar inference mechanisms will not change the underlying StructuredPrediction, this should
	 * result in significant memory savings.
	 * 	
	 * @return	the copy with only a surface copy of the input StructuredPrediction
	 */
	public StructuredAssignment copyState() {
    	StructuredAssignment copy = new StructuredAssignment(predictions);
    	copy.assignments = (FeatureReal[]) assignments.clone();
    	copy.score = score;
    	copy.softmaxScore = softmaxScore;
    	copy.numAssigned = numAssigned;
    	copy.numVisible = numVisible;
    	return copy;
	}
}
	
