package ml.learn.structured;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import ml.instance.Instance;
import ml.instance.Prediction;
import ml.inference.AllMaximum;
import ml.inference.Inference;
import ml.instance.structured.StructuredAssignment;
import ml.instance.structured.StructuredInstance;
import ml.instance.structured.StructuredPrediction;
import ml.learn.Learner;
import ml.loss.LossFunction;
import ml.sample.Sample;

/**
 * A StructuredLearner takes a set of StructuredInstance items, uses them to make
 * a StructuredPrediction (a vector of local predictions) and then uses an Inference
 * procedure to combine these local predictions into a StructuredAssignment that
 * is consistent with the structural constraints of the output space.
 * TODO also generate the case where constraints are specified during runtime (not until needed,
 * 		and probably only for evaluation)
 * 
 * @author ksmall
 */

public abstract class StructuredLearner extends Learner implements Iterable<Learner> {
    
	/**
	 * A vector of local learners.  Note that the two modes of operation are either a
	 * fixed vector size (where there is a local Learner for each position) or
	 * a single Learner is used for all positions (allows for variable length
	 * StructuredInstance items).
	 */
    protected ArrayList<Learner> learners;
    /** the Inference procedure (which also includes constraints) */
    protected Inference inference;

    /**
     * The default constructor.  Assumes all output configurations are valid.
     */
    public StructuredLearner() {
    	this(new AllMaximum());
    }
    
    /**
     * Constructor which also sets the inference mechanism.
     * 
     * @param inference	the inference mechanism
     */
    public StructuredLearner(Inference inference) {
    	super();	// doesn't really matter as we don't use the identifier
    	learners = new ArrayList<Learner>();
    	setInference(inference);
    }
    
    /**
     * Constructor which also adds a single learner and sets the inference mechanism.
     * 
     * @param learner	the local learning algorithm
     * @param inference	the inference mechanism
     */
    public StructuredLearner(Learner learner, Inference inference) {
    	this(inference);
    	addLearner(learner);
    }
    
    /**
     * Copy constructor (deep copy).
     * 
     * @param learner	the StructuredLearner to be copied
     */
    public StructuredLearner(StructuredLearner learner) {
    	this(learner.inference.deepCopy());
    	for (Learner l : learner)
    		addLearner(l.deepCopy());
    }
    
    /**
     * Adds a learning algorithm to the local learners.  Note that in the case where
     * a learner is being set for each position, the learners must be added in order.
     * 
     * @param learner	the local learner to be added
     */
    public void addLearner(Learner learner) {
    	learners.add(learner);
    }
    
    /**
     * Sets the inference mechanism.
     * 
     * @param inference	the inference mechanism
     */
    public void setInference(Inference inference) {
    	this.inference = inference;
    }
        
    public abstract void train(Sample<Instance> examples);

    /**
     * Generates a StructuredAssignment for the specified StructuredInstance, meaning
     * the resulting output will have a valid structure.
     * 
     * @param example		the input StucturedInstance to be evaluated
     * @param assignVisible	if {@code true}, known local output variables will be assigned
     * 		before inference starts (used for "partial" active learning)
     * @return	the resulting StructuredAssignment
     */
    public StructuredAssignment evaluate(StructuredInstance example, boolean assignVisible) {
    	return inference.infer(example, getEvaluations(example), assignVisible);
    }
    
    /**
     * Assumes that local output variables are not assigned before inference as this is
     * generally the case for supervised learning.
     * 
     * @param example	the input StructuredInstance to be evaluated
     * @return	the resulting StructuredAssignment
     */
    public StructuredAssignment evaluate(StructuredInstance example) {
    	return evaluate(example, false);
    }
            
    /**
     * Generates a set of the top k scoring StructuredAssignment items (if supported by the 
     * inference  mechanism and possible) for the specified StructuredInstance, meaning
     * the resulting output will have a valid structure.
     * 
     * @param example		the input StucturedInstance to be evaluated
     * @param assignVisible	if {@code true}, known local output variables will be assigned
     * 		before inference starts (used for "partial" active learning)
     * @param k	the number of output items desired
     * @return	the resulting StructuredAssignment
     */
    public ArrayList<StructuredAssignment> evaluate(StructuredInstance example, 
    		boolean assignVisible, int k) {
    	return inference.infer(example, getEvaluations(example), assignVisible, k);
    }

    /**
     * Assumes that local output variables are not assigned before inference as this is
     * generally the case for supervised learning.
     * 
     * @param example	the input StructuredInstance to be evaluated
     * @param k	the number of output items desired
     * @return	the resulting StructuredAssignment
     */
    public ArrayList<StructuredAssignment> evaluate(StructuredInstance example, int k) {
    	return inference.infer(example, getEvaluations(example), false, k);
    }
    
    public double evaluate(Sample<Instance> testSample, LossFunction  loss) {
    	double result = 0.0;
    	StructuredInstance instance = null;
    	testSample.reset();
		while ((instance = (StructuredInstance) testSample.next()) != null) {
			result += loss.loss(instance, evaluate(instance));
		}
		return result;
    }
    
    /**
     * Takes a StructuredInstance and returns the local predictions.  Should be
     * thought of as the step before inference ensures a valid output structure
     * (which is exactly how it is actually used).
     * 
     * @param example
     * @return	the resulting local predictions
     */
    public StructuredPrediction getEvaluations(StructuredInstance example) {
    	StructuredPrediction result = new StructuredPrediction();
    	Learner learner = null;
    	Iterator<Learner> itLearner = iterator();
    	if (learners.size() == 1)
    		learner = (Learner) itLearner.next();
    	for (Instance instance : example) {
    		if (learners.size() > 1)
    			learner = (Learner) itLearner.next();
    		result.addPrediction(learner.evaluate(instance));
    	}
    	result.trim();
    	return result;
    }
    
    /**
     * {@inheritDoc} Just returns the Prediction resulting from the first
     * local learner.
     */
    public Prediction evaluate(Instance example) {
    	return evaluate(example, 0);
    }

    /**
     * Returns the Prediction resulting from the local learning at the
     * specified position.  If there is only one local learner, it is
     * used, but otherwise, there is no error checking.  There was a 
     * claim that this was actually used somewhere, but I am not so 
     * sure.
     * 
     * @param example	the (non-structured) example to evaluate
     * @param index		the index of the local learner to use for evaluation
     * @return			the resulting Prediction
     */
    public Prediction evaluate(Instance example, int index) {
    	System.out.println("using this");
    	Learner learner = null;
    	if (learners.size() == 1)
    		learner = (Learner) learners.get(0);
    	else
    		learner = (Learner) learners.get(index);
    	return learner.evaluate(example);
    }
        
    public Iterator<Learner> iterator() {
    	return learners.iterator();
    }
    
    public void reset() {
    	for (Learner learner : this)
    		learner.reset();
    }
    
	public void xmlBody(PrintWriter out) {
		super.xmlBody(out);
		for (Learner learner : this) {
			out.println("<LocalLearner>");
			learner.xmlBody(out);
			out.println("</LocalLearner>");
		}
	}
    
    public StructuredLearner copy() {
    	StructuredLearner copy = (StructuredLearner) super.clone();
    	copy.inference = inference.copy();
        for (Learner learner : this)
        	copy.addLearner(learner.copy());
        return copy;
    }
    
    public StructuredLearner deepCopy() {
    	StructuredLearner copy = (StructuredLearner) super.clone();
    	copy.inference = inference.deepCopy();
        for (Learner learner : this)
        	copy.addLearner(learner.deepCopy());
        return copy;
    }
}
