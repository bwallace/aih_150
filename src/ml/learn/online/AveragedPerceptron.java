package ml.learn.online;

import java.io.PrintWriter;

import ml.instance.Feature;
import ml.instance.FeatureVector;
import ml.instance.Instance;
import ml.instance.SingleInstance;
import ml.learn.SparseWeightVector;

/**
 * An  AveragedPerceptron implementation (should add a citation).
 * 
 * @author ksmall
 */

// should add normalization
// currently the second dumbest implementation possible
public class AveragedPerceptron extends Perceptron {
    
    /** the final hypothesis **/
    protected SparseWeightVector s;
    
    /**
     * the fully expressive constructor
     * 
     * @param promotionRate	the learning rate for promotions
     * @param demotionRate	the learning rate for demotions
     * @param threshold			the threshold parameter
     */
    public AveragedPerceptron(int identifier, int iterations, boolean shuffle,
    		double threshold, double positiveGamma, double negativeGamma,
    		double promotionRate, double demotionRate) {
    	super(identifier, iterations, shuffle, threshold, positiveGamma, negativeGamma,
    			promotionRate, demotionRate);
    	s = new SparseWeightVector();
    }
    
    public AveragedPerceptron(double promotionRate, double demotionRate, double threshold) {
    	super(promotionRate, demotionRate, threshold);
    	s = new SparseWeightVector();
    	//last_time = 0;
    }
    
    /**
     * The constructor which assumes symmetric learning rate
     * 
     * @param learningRate	the learning rate for promotions and demotions
     * @param threshold		the threshold parameter
     */
    public AveragedPerceptron(double learningRate, double threshold) { 
    	this(learningRate, learningRate, threshold);
    }
   
    /**
     * The default constructor; symmetric learning rate = 0.1, threshold = 0.0
     */
    public AveragedPerceptron() { this(0.1, 0.0); }
    
    /**
     * Constructor which assumes symmetric learning rate and threshold = 0.0
     * 
     * @param learningRate	learning rate for promotions and demotions
     */
    public AveragedPerceptron(double learningRate) { 
    	this(learningRate, 0.0); 
    }
    
    public AveragedPerceptron(AveragedPerceptron p) {
    	super(p);
    	this.s = p.s.deepCopy();
    }
    
    /**
     * Constructor which read the Perceptron parameters in from a file reader
     * 
     * @param in	the file reader which contains the Perceptron
     */
    //public AveragedPerceptron(BufferedReader in) { read(in); }

    // overloading LTU class
    public double score(Instance example) {
    	return s.dot(((SingleInstance) example).features);
    }
    
    public double trainScore(Instance example) {
    	return super.score(example);
    }
    
    public void reset() {
    	super.reset();
    	s = new SparseWeightVector();
    }
    
    public String toString() {
    	return super.toString() + "\n" + s.toString();
    }
    
    // save clone for the end
    
    public void train(Instance example, Feature prediction) {
    	super.train(example, prediction);
    }
           
    // scaling for the purpose of softmax
    public void finish() {
    	//s.scale(1.0 / time);
    	//double diff = (double) time - last_time + 1.0;  // since time didn't advance as train not called
    	//last_time = time;
    	//s.add(w, diff);
    	//System.out.println("finishing averaged Perceptron");
    	s.scale(-1.0);
    	s.add(w, (double) time + 1.0);
    	s.scale(1.0 / (time + 1.0));
    	//s = w;
    }
    
    
    /**
     * Promotes the weight vector (false negative) for the given example
     * 
     * @param example	the given example
     */
    
    public void promote(Instance example) {
    	//double diff = (double) time - last_time;
    	//last_time = time;
    	//w.add(FeatureVector.staticScale(promotionRate, example.features));
    	s.add(FeatureVector.staticScale((double) time * promotionRate, ((SingleInstance) example).features));
    	super.promote(example);
    }
 
    /**
     * Demotes the weight vector (false positive) for the given example
     * 
     * @param example	the given example
     */
    public void demote(Instance example) {
    	//double diff = (double) time - last_time;
    	//last_time = time;
    	s.add(FeatureVector.staticScale(-(double) time * demotionRate, ((SingleInstance) example).features));
    	//s.add(w, diff);
    	super.demote(example);
    }

	public void xmlBody(PrintWriter out) {
		super.xmlBody(out);
		out.println("<FinalWeight>");
		s.xml(out);
		out.println("</FinalWeight>");
	}
    
    /**
     * Writes the Perceptron parameters to a file
     * 
     * @param out	the writer used to write out the Perceptron parameters
     */
    /*
    public void write(PrintWriter out) {
    	out.println(identifier + " " + promotionRate + " " + demotionRate + " " +
    			threshold + " " + positiveGamma + " " + negativeGamma);
    	w.write(out);
    	s.write(out);
    }
	*/
    
    /**
     * Reads the Perceptron parameters in from a file
     * 
     * @param in	the file reader where the Perceptron parameters are located
     */
    /*
    public void read(BufferedReader in) {
    	String line = new String();
    	try {
    		line = in.readLine();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}

    	String[] fields = line.split("\\s+");
    	identifier = Integer.parseInt(fields[0]);
    	promotionRate = Double.parseDouble(fields[1]);
        demotionRate = Double.parseDouble(fields[2]);
    	threshold = Double.parseDouble(fields[3]);
        positiveGamma = Double.parseDouble(fields[4]);
        negativeGamma = Double.parseDouble(fields[5]);
        w = new SparseWeightVector(in);
        s = new SparseWeightVector(in);
    }
    */
    
    public AveragedPerceptron copy() {
    	AveragedPerceptron copy = (AveragedPerceptron) super.copy();
    	copy.s = this.s.copy();
    	return copy;
    }
    
    public AveragedPerceptron deepCopy() {
    	return new AveragedPerceptron(this);
    }
}
