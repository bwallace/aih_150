package ml.learn.online;

import java.io.PrintWriter;
import ml.instance.FeatureVector;
import ml.instance.Instance;
import ml.instance.SingleInstance;

/**
 * A Perceptron implementation (should add a citation - and more of a description).
 * 
 * @author ksmall
 */

public class Perceptron extends PrimalLTU {
    
	/** the learning rate for promotions */
    protected double promotionRate;
    /** the learning rate for demotions */
    protected double demotionRate;
    
    /**
     * the fully expressive constructor
     * 
     * @param promotionRate	the learning rate for promotions
     * @param demotionRate	the learning rate for demotions
     * @param threshold		the threshold parameter
     */
    public Perceptron(int identifier, int iterations, boolean shuffle,
    		double threshold, double positiveGamma, double negativeGamma,
    		double promotionRate, double demotionRate) {
    	super(identifier, iterations, shuffle, threshold, positiveGamma, negativeGamma);
    	this.promotionRate = promotionRate;
    	this.demotionRate = demotionRate;
    }

    public Perceptron(double promotionRate, double demotionRate, double threshold) {
    	super();
    	setThreshold(threshold);
    	this.promotionRate = promotionRate;
    	this.demotionRate = demotionRate;
    }
    
    /**
     * The constructor which assumes symmetric learning rate
     * 
     * @param learningRate	the learning rate for promotions and demotions
     * @param threshold		the threshold paramter
     */
    public Perceptron(double learningRate, double threshold) { 
    	this(learningRate, learningRate, threshold);
    }

    /**
     * Constructor which assumes symmetric learning rate and threshold = 0.0
     * 
     * @param learningRate	learning rate for promotions and demotions
     */
    public Perceptron(double learningRate) {
    	this(learningRate, 0.0);
    }
    
    /**
     * The default constructor; symmetric learning rate = 0.1, threshold = 0.0
     */
    public Perceptron() { this(0.1, 0.0); }
    
    public Perceptron(Perceptron p) { 
    	super(p);
    	this.promotionRate = p.promotionRate;
    	this.demotionRate = p.demotionRate;    	
    }
    
    /**
     * Constructor which read the Percpetron paramters in from a file reader
     * 
     * @param in	the file reader which contains the Perceptron
     */
    //public Perceptron(BufferedReader in) { read(in); }

    /**
     * Promotes the weight vector (false negative) for the given example
     * 
     * @param example	the given example
     */
    public void promote(Instance example) {
    	//System.out.println("Promoting " + identifier);
    	//System.out.println("b: " + w);
    	w.add(FeatureVector.staticScale(promotionRate, ((SingleInstance) example).features));
    	//System.out.println("a: " + w);
    }

    /**
     * Demotes the weight vector (false positive) for the given example
     * 
     * @param example	the given example
     */
    public void demote(Instance example) {
    	//System.out.println("Demoting " + identifier);
    	//System.out.println("b: " + w);
    	w.add(FeatureVector.staticScale(-demotionRate, ((SingleInstance) example).features));
    	//System.out.println("a: " + w);
    }

	public void xmlBody(PrintWriter out) {
		super.xmlBody(out);
		out.println("<promotionRate>" + promotionRate + "</promotionRate>");
		out.println("<demotionRate>" + demotionRate + "</demotionRate>");
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
    }
    */
    
    public Perceptron copy() {
    	Perceptron copy = (Perceptron) clone();
    	copy.w = this.w.copy();
    	return copy;
    }
    
    public Perceptron deepCopy() {
    	return new Perceptron(this);
    }
}
