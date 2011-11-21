package ml.learn;

import java.io.OutputStream;
import java.io.PrintWriter;

import shared.Copyable;
import text.output.PrintXML;
import text.output.XML_Writer;

import ml.instance.Instance;
import ml.instance.Prediction;
import ml.loss.LossFunction;
//import ml.sample.Costing;
import ml.sample.Sample;

/**
 * The basic (abstract) Learner class.  A learner is defined here as a parameterized
 * functional form that sets the parameters using data and can make prediction on
 * future data.  The only requirements are that learners derived from this class
 * can train in batch mode and evaluate new instances.
 * 
 * @author ksmall
 */

public abstract class Learner implements Cloneable, Copyable<Learner>, PrintXML {

	/** 
	 * An identifier for the Learner; note  by default, this is set to 1 meaning
	 * a positive classification for a binary classification.  However, this will
	 * generally be reset or not matter depending on the learning algorithm.
	 */
    protected int identifier;
    protected XML_Writer xml;

    protected Learner(int identifier) {
    	this.identifier = identifier;
    	xml = new XML_Writer(this);
    }
    
    protected Learner() {
    	this(1);
    }
    
    protected Learner(Learner learner) {
    	this(learner.identifier);
    }
    
    /** 
     * Sets the identifier
     * 
     * @param identifier	the new identifier value	
     */
    public void setIdentifier(int identifier) {
    	this.identifier = identifier;
    }

    /**
     * Trains the learner parameters using the provided data Sample
     * 
     * @param examples	the Sample of examples for training
     */
    public abstract void train(Sample<Instance> examples);
        
    /**
     * Evaluate a new Instance based on the learned function
     * 
     * @param example	the Instance to be evaluated
     * @return	the resulting Prediction
     */
    public abstract Prediction evaluate(Instance example);
    
    /**
     * Evaluate a new Instance based on the learned function relative to
     * the hypothesis used when making predictions used for training
     * updates (think averaged Perceptron)
     * 
     * @param example	the Instance to be evaluated
     * @return	the resulting Prediction
     */
    public Prediction trainEvaluate(Instance example) {
    	return evaluate(example);
    }
    
    /**
     * Evaluates a hypothesis relative to a testset.
     * 
     * @param testSample	the testing set
     * @param loss			the loss function for evaluation
     * @return				the cumulative loss
     */
    public abstract double evaluate(Sample<Instance> testSample, LossFunction loss);
     
    /**
     * Generally, we will do nothing; useful for averaged Perceptron and such.  Could be
     * thought of as wrapping up the algorithm for testing after training with multiple
     * data samples.
     *
     */
    public void finish() { ; }

    
    /**
     * Resets the learned parameters of the learning algorithm.  Intended for
     * running multiple experiments and such.
     */
	public abstract void reset();

	public void xml(OutputStream out) {
		xml.xml(out);
	}

	public String xml() {
		return xml.xml();
	}

	public void xml(PrintWriter out) {
		out.println("<Learner>");
		out.println("<type>" + getClass().getName() + "</type>");
		xmlBody(out);
		out.println("</Learner>");
	}

	public void xmlBody(PrintWriter out) {
		out.println("<identifier>" + identifier + "</identifier>");
	}

	
    /* will most certainly want to put these back in */
    //public abstract void write(PrintWriter out);
    //public abstract void read(BufferedReader in);
	
    /*
    public void write() {
    	write(new PrintWriter(System.out, true));
    }
    
    public void write(File out) {
    	FileOutputStream output = null;
    	try {
			output = new FileOutputStream(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	write(new PrintWriter(output, true));
    	try {
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    */
    
    /**
     * returns a clone of the Learner
     */
    public Object clone() {
    	Learner clone = null;
    	try {
    		clone = (Learner) super.clone();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	return clone;
    }
    
    public Learner copy() {
    	return (Learner) clone();
    }
    
    public Learner deepCopy() {
    	return copy();
    }
}
