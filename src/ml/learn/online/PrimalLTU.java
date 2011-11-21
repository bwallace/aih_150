package ml.learn.online;

import java.io.PrintWriter;

import ml.instance.Instance;
import ml.instance.SingleInstance;
import ml.learn.SparseWeightVector;

/**
 * An abstract class for LinearThresholdUnit algorithms that maintain the hypothesis
 * by updating the weight vector directly (e.g. Perceptron, Winnow, ALMA, etc.).
 * 
 * @author ksmall
 */
public abstract class PrimalLTU extends LinearThresholdUnit {

	/** the weight vector, which represents the learning algorithm hypothesis */
    protected SparseWeightVector w;

    protected PrimalLTU(int identifier, int iterations, boolean shuffle,
    		double threshold, double positiveGamma, double negativeGamma) {
    	super(identifier, iterations, shuffle, threshold, positiveGamma, negativeGamma);
    	w = new SparseWeightVector();
    }
    
    /**
     * Default constructor
     */
    protected PrimalLTU() {
    	super();
    	w = new SparseWeightVector();
    }

    protected PrimalLTU(PrimalLTU ltu) {
    	//super(ltu.identifier, ltu.iterations, ltu.shuffle, ltu.threshold, ltu.positiveGamma, ltu.negativeGamma);
    	super(ltu);
    	w = ltu.w.deepCopy();
    }
    
    /**
     * Returns the dot product of the weight vector with a given Instance.
     * 
     * @param example	the Instance which will be used to calculate the dot product with
     * 						the weight vector
     * @return	the resulting dot product
     */
    public double score(Instance example) {
    	return w.dot(((SingleInstance) example).features);
    }
    
    public double trainScore(Instance example) {
    	return score(example);
    }
    
    public void reset() {
    	time = 0;
    	w = new SparseWeightVector();
    }
    
    /*
    public double predictScore(Instance example) {
    	return score(example);
    }
    */
    
	public void xmlBody(PrintWriter out) {
		super.xmlBody(out);
		out.println("<Weight>");
		w.xml(out);
		out.println("</Weight>");
	}

    /**
     * A String representation of the weight vector.
     * 
     * @return	the weight vector String representation
     */
    public String toString() {
    	return w.toString();
    }

    public PrimalLTU copy() {
    	PrimalLTU copy = (PrimalLTU) clone();
    	copy.w = this.w.copy();
    	return copy;
    }

    public PrimalLTU deepCopy() {
    	PrimalLTU copy = (PrimalLTU) clone();
    	copy.w = this.w.deepCopy();
		return copy;
    }
}
