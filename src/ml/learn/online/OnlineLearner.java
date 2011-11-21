package ml.learn.online;

import java.io.PrintWriter;

import ml.sample.RandomAccessSample;
import ml.sample.Sample;
import ml.utility.Globals;
import ml.instance.Feature;
import ml.instance.Instance;
import ml.learn.Learner;
import ml.loss.LossFunction;

/**
 * An abstract class for online learners, which are distinguished by their ability
 * to train one example at a time such that the hypothesis is updated for each example
 * presented.
 * TODO	do we really want SingleInstance here?
 * 
 * @author ksmall
 */
public abstract class OnlineLearner extends Learner implements Online {

	// doesn't write out iterations or shuffle as only used during training
	protected int iterations;
	protected boolean shuffle;
	protected int time;
	
	protected OnlineLearner(int identifier, int iterations, boolean shuffle) {
		super(identifier);
		setProtocol(iterations, shuffle);
		time = 0;
	}
	
	protected OnlineLearner(int iterations, boolean shuffle) {
		super();
		setProtocol(iterations, shuffle);
		time = 0;		
	}
	
	protected OnlineLearner() {
		this(1, false);
	}
	
	protected OnlineLearner(OnlineLearner l) {
		this(l.identifier, l.iterations, l.shuffle);
		this.time = l.time;
	}

	public void setProtocol(int iterations, boolean shuffle) {
		this.iterations = iterations;
		this.shuffle = shuffle;
	}
	
	public boolean shuffle() {
		return shuffle;
	}

	public int iterations() {
		return iterations;
	}
		
    /**
     * The training method where the feedback signal is determined by a specified 
     * prediction (which may be different from the prediction of the Learner)
     * 
     * @param example		the Instance presented for training
     * @param prediction	the Prediction to be used for determining training feedback
     */
    public void train(Instance example, Feature prediction) {
    	advanceTime();
    }
    
    public void advanceTime() {
    	time++;
    }

    // TODO probably make this abstract?
    public double quality() {
    	return Globals.randDouble();
    }
    
    /**
     * The standard training method for a single instance
     * 
     * @param example	the Instance presented for training
     */
    public void train(Instance example) {
    	train(example, null);
    }
    
    /**
     * Trains an online algorithm for an entire Sample by presenting each Instance in the
     * Sample once to the OnlineLearner.
     * 
     * @param examples	the Sample of examples for training
     */
    public void train(RandomAccessSample<Instance> examples) {
    	staticTrain(this, examples, iterations, shuffle);
    }
        
    public static void staticTrain(Learner learner, RandomAccessSample<Instance> examples, 
    		int iterations, boolean shuffle) {
    	Instance instance = null;
    	Online online = (Online) learner;
    	for (int i = 0; i < iterations; i++) {
    		if (shuffle)
    			examples.permute();
    		else
    			examples.reset();
    		while ((instance = examples.next()) != null)
    			online.train(instance);
    	}
   		learner.finish();    	
    }

    /**
     * Trains an online algorithm for an entire Sample by presenting each Instance in the
     * Sample once to the OnlineLearner.
     * 
     * @param examples	the Sample of examples for training
     */
    public void train(Sample<Instance> examples) {
    	staticTrain(this, examples, iterations);
    }
        
    public static void staticTrain(Learner learner, Sample<Instance> examples, int iterations) {
    	Instance instance = null;
    	Online online = (Online) learner;
    	for (int i = 0; i < iterations; i++) {
   			examples.reset();
    		while ((instance = (Instance) examples.next()) != null)
    			online.train(instance);
    	}
   		learner.finish();    	
    }

    
    public static double staticEvaluate(Learner learner, Sample<Instance> testSample, LossFunction loss) {
    	double result = 0.0;
    	Instance instance = null;
    	testSample.reset();
		while ((instance = (Instance) testSample.next()) != null) {
			result += loss.loss(instance, learner.evaluate(instance));
		}
		return result;    	
    }
    
    public double evaluate(Sample<Instance> testSample, LossFunction loss) {
    	return staticEvaluate(this, testSample, loss);
    }

	public void xmlBody(PrintWriter out) {
		super.xmlBody(out);
		out.println("<iterations>" + identifier + "</iterations>");
		out.println("<shuffle>" + shuffle + "</shuffle>");
	}
    
    /*
    public void reset() {
    	time = 1;
    }
    */
    /*
    public OnlineLearner copy() {
    	return (OnlineLearner) clone();
    }
    */
}