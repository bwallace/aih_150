package ml.learn.structured.online;

import java.io.PrintWriter;
import java.util.Iterator;

import ml.inference.Inference;
import ml.instance.Instance;
import ml.instance.structured.StructuredAssignment;
import ml.instance.structured.StructuredInstance;
import ml.instance.structured.StructuredPrediction;
import ml.learn.Learner;
import ml.learn.structured.StructuredLearner;
import ml.sample.RandomAccessSample;
import ml.sample.Sample;

/**
 * Similar to StructuredLearner, except allows the learner to be
 * trained with a single StructuredInstance at a time.
 * 
 * @author ksmall
 */
// TODO: not sure about second train prototype (save this for another day)
public abstract class OnlineStructuredLearner extends StructuredLearner {  //implements Online {

	/** number of iterations through the data */
	protected int iterations;
	/** indicates if the data should be shuffled between iterations */
	protected boolean shuffle;
	
    /**
     * Standard constructor, which also sets the inference mechanism.
     * 
     * @param inference	the inference mechanism
     */
    public OnlineStructuredLearner(Inference inference) {
    	super(inference);
    }

    /**
     * Constructor which also adds a single learner and sets the inference mechanism.
     * 
     * @param learner	the local learning algorithm
     * @param inference	the inference mechanism
     */
    public OnlineStructuredLearner(Learner learner, Inference inference) {
		super(learner, inference);
	}

    /**
     * Copy constructor (deep copy).
     * 
     * @param learner	the OnlineStructuredLearner to be copied
     */
    public OnlineStructuredLearner(OnlineStructuredLearner learner) {
    	super(learner);
    	this.iterations = learner.iterations;
    	this.shuffle = learner.shuffle;
    }
    
    /**
     * Sets parameters of online learning protocol.
     * 
     * @param iterations	number of iterations through the data
     * @param shuffle		whether the data should be shuffled between iterations
     */
	public void setProtocol(int iterations, boolean shuffle) {
		this.iterations = iterations;
		this.shuffle = shuffle;
	}

	/**
	 * Trains the learning algorithm based on a single StructuredInstance
	 * 
	 * @param example	the input instance
	 */
	public abstract void train(StructuredInstance example);
	
    public void train(Sample<Instance> examples) {
    	StructuredInstance instance = null;
    	for (int i = 0; i < iterations; i++) {
    		examples.reset();
    		while ((instance = (StructuredInstance) examples.next()) != null)
    			train(instance);
    	}
    	for (Learner learner : this)
    		learner.finish();
    }
	
    public void train(RandomAccessSample<Instance> examples) {
    	StructuredInstance instance = null;
    	for (int i = 0; i < iterations; i++) {
    		if (shuffle)
    			examples.permute();
    		else
    			examples.reset();
    		while ((instance = (StructuredInstance) examples.next()) != null) {
    			//System.out.println(instance);
    			train(instance);
    		}
    	}
    	for (Learner learner : this)
    		learner.finish();
    }

    /**
     * TODO
     * 
     * @param example
     * @param assignVisible
     * @return	the resulting StructuredAssignment
     */
    public StructuredAssignment trainEvaluate(StructuredInstance example, boolean assignVisible) {
    	//System.out.println(inference);
    	return inference.infer(example, getTrainEvaluations(example), assignVisible);
    }

    /**
     * TODO
     * 
     * @param example
     * @return the resulting StructuredAssignment
     */
    public StructuredAssignment trainEvaluate(StructuredInstance example) {
    	return trainEvaluate(example, false);
    }

    /**
     * TODO
     * 
     * @param example
     * @return	the resulting StructuredPrediction
     */
    public StructuredPrediction getTrainEvaluations(StructuredInstance example) {
    	StructuredPrediction result = new StructuredPrediction();
    	Learner learner = null;
    	Iterator<Learner> itLearner = iterator();
    	//System.out.println("size: " + learners.size());
    	if (learners.size() == 1)
    		learner = (Learner) itLearner.next();
    	for (Instance instance : example) {
    		if (learners.size() > 1)
    			learner = (Learner) itLearner.next();
    		result.addPrediction(learner.trainEvaluate(instance));
    	}
    	result.trim();
    	return result;
    }
        
	public void xmlBody(PrintWriter out) {
		super.xmlBody(out);
		out.println("<iterations>" + identifier + "</iterations>");
		out.println("<shuffle>" + shuffle + "</shuffle>");
	}

    
    public OnlineStructuredLearner copy() {
    	OnlineStructuredLearner copy = (OnlineStructuredLearner) super.copy();
    	copy.iterations = iterations;
    	copy.shuffle = shuffle;
    	return copy;
    }
    
    public OnlineStructuredLearner deepCopy() {
    	OnlineStructuredLearner copy = (OnlineStructuredLearner) super.deepCopy();
    	copy.iterations = iterations;
    	copy.shuffle = shuffle;
    	return copy;    	
    }
}
