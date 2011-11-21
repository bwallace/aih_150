package ml.learn;

//import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.PrintWriter;
//import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import shared.Copyable;
import text.output.PrintXML;
import text.output.XML_Writer;
import ml.instance.Feature;
import ml.instance.FeatureVector;

/**
 * A sparse weight vector implementation.  A few semantic (but not interesting) subtleties to Copyable.
 * TODO: fix String issues
 * 
 * @author ksmall
 */

public class SparseWeightVector implements Copyable<SparseWeightVector>, Iterable<Integer>, PrintXML {

	/** the identifiers associated with a given weight value */
    private HashMap<Integer, Double> identifiers;
    protected XML_Writer xml;
    
    /**
     * Default constructor
     */
    public SparseWeightVector() {
    	identifiers = new HashMap<Integer, Double>();
    }

    /**
     * The copy constructor
     * 
     * @param v		the SparseWeightVector to be copied
     */
    public SparseWeightVector(SparseWeightVector v) {
    	this();
    	identifiers.putAll(v.identifiers);
    }

    
    /**
     * Constructor for use with file i/o
     * 
     * @param in	the input reader
     */
    //public SparseWeightVector(BufferedReader in) { read(in); }

    /**
     * Indicates the number of weights in the vector
     * 
     * @return	the size of the vector
     */
    public int size() {
    	return identifiers.size();
    }
    
    /**
     * Computes the dot product of this weight vector with a specified FeatureVector
     * 
     * @param vector	the vector for which the weight vector dot product is calculated
     * @return			the resulting dot product
     */
    public double dot(FeatureVector vector) {
    	double result = 0.0;
    	for (Feature f : vector) {
    		Integer id = new Integer(f.identifier());
    		if ((!vector.respectHidden || f.isVisible()) && (identifiers.containsKey(id)))
    			result += identifiers.get(id).doubleValue() * f.strength();
    	// creates small random values (made for additive algorithms) - not currently
	    /*
	    else {
		double wInit = Globals.randDouble() * 0.0002 - 0.0001;
		//System.out.println("wInit(" + f + "): " + wInit);
		identifiers.put(f, new Double(wInit));
		result += wInit * f.strength;
	    }
	    */
    	}
    	return result;
    }
    
    /**
     * Adds a FeatureVector to the current sparse weight vector (for Perceptron-like
     * learning algorithms)
     * 
     * @param vector	the input feature vector to be added to the weight vector
     */
    public void add(FeatureVector vector, double scale) {
    	for (Feature f : vector) {
    		Integer id = new Integer(f.identifier());
    		if (!vector.respectHidden || f.isVisible()) {
    			double weight = f.strength() * scale;
    			if (identifiers.containsKey(id))
    				weight += identifiers.get(id).doubleValue();
    			identifiers.put(id, new Double(weight));
    		}
    	}
    }
    
    public void add(FeatureVector vector) {
    	add(vector, 1.0);
    }
    
    public void put(int id, double weight) {
    	identifiers.put(id, weight);
    }
    
    // why are we adding SparseWeightVectors? (it looks like for AveragedPerceptron)
    // this won't work for sum (for now)
    public void add(SparseWeightVector vector, double scale) {
    	for (Integer id : vector) {
    		double w = vector.identifiers.get(id).doubleValue() * scale;
    		if (identifiers.containsKey(id))
    			w += identifiers.get(id).doubleValue();
    		identifiers.put(id, new Double(w));
    	}
    }
    
    public void add(SparseWeightVector vector) {
    	add(vector, 1.0);
    }
    
    /**
     * Returns an iterator for the sparse weight vector
     * 
     * @return	the weight vector iterator
     */
    public Iterator<Integer> iterator() {
    	return identifiers.keySet().iterator();
    }

	public void xml(OutputStream out) {
		xml.xml(out);
	}

	public String xml() {
		return xml.xml();
	}

	public void xml(PrintWriter out) {
		out.println("<SparseWeightVector>");
		ArrayList<Integer> keys = new ArrayList<Integer>(identifiers.keySet());
		Collections.sort(keys);
		for (Integer id : keys)
			out.println("   <id>" + id + "</id><value>" + identifiers.get(id) + "</value>");
		out.println("</SparseWeightVector>");
	}
    
    // used for ensemble learning
    /*
    public Prediction toBinaryPrediction() {
    	Prediction result = new Prediction();
    	for (Iterator it = iterator(); it.hasNext(); ) {
    		Feature f = (Feature) it.next(); //identifiers.get(it.next());
    		double score = ((Double) identifiers.get(f)).doubleValue();
    		//System.out.println("h: " + f + ", " + score);
    		//result.addFeature((Feature) identifiers.get(it.next()));
    		result.addScore(new BinaryLabel(score >= 0, f.identifier, Math.abs(score)));
    	}
    	return result;
    }
    */
    
    /**
     * Returns a String representation of the SparseWeightVector
     * 
     * @return	the resulting String representation
     */
    /*
    public String toString() {
    	Integer[] id = (Integer[]) identifiers.keySet().
	    	toArray(new Integer[identifiers.size()]);
	
    	Arrays.sort(id, new idComparator());
	
    	int biggest = 0;
	
    	for (int i = 0; i < id.length; i++) {
    		if (id[i].toString().length() > biggest)
    			biggest = id[i].toString().length();
    	}
    	if (biggest % 2 == 0)
    		biggest += 2;
    	else
    		biggest++;
	
    	String result = new String();
    	for (int i = 0; i < id.length; i++) {
    		result += id[i];
    		for (int j = 0; j + id[i].toString().length() < biggest; j++)
    			result += " ";
    		result += identifiers.get(id[i]) + "\n";
    	}
	
    	//result += "\n";
    	return result;
    }
    */
    
    /**
     * Writes the SparseWeightVector out to a file
     * 
     * @param out	the output file writer
     */
    /*
    public void write(PrintWriter out) {
    	Integer[] id = (Integer[]) identifiers.keySet().
	    	toArray(new Integer[identifiers.size()]);
	
    	Arrays.sort(id, new idComparator());
	
    	int biggest = 0;
	
    	for (int i = 0; i < id.length; i++) {
    		if (id[i].toString().length() > biggest)
    			biggest = id[i].toString().length();
    	}
    	if (biggest % 2 == 0)
    		biggest += 2;
    	else
    		biggest++;
	
    	for (int i = 0; i < id.length; i++) {
    		out.print(id[i]);
    		for (int j = 0; j + id[i].toString().length() < biggest; j++)
    			out.print(" ");
    		out.println(identifiers.get(id[i]));
    	}
	
    	out.println();
    }
    */
    
    /*
    public void write() {
    	write(new PrintWriter(System.out, true));
    }
    */
    
    /**
     * Reads a SparseWeightVector from a file reader
     * 
     * @param in	the input file reader
     */
    /*
    public void read(BufferedReader in) {
    	identifiers = new HashMap<Integer, Double>();
    	String line = new String();
    	try {
    		for (line = in.readLine(); line != null && !line.equals("");
    		line = in.readLine()) {
    			String identifier = line.substring(1, line.lastIndexOf("\""));
    			Double weight = new Double(line.substring(line.lastIndexOf(" ") + 1));
    			identifiers.put(Integer.parseInt(identifier), weight);
    		}
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    */
    
    public double norm(double p) {
    	double result = 0.0;
    	//for (Iterator it = identifiers.keySet().iterator(); it.hasNext(); ) {
    	for (Integer id : this)
    		//result += Math.pow(Math.abs(((Double) identifiers.get(it.next())).doubleValue()), p);
    		result += Math.pow(Math.abs(identifiers.get(id).doubleValue()), p);
    	return Math.pow(result, (1.0 / p));
    }

    // TODO recalculate sum
    public void scale(double scalar) {
    	for (Integer id : this) {
    		double value = scalar * identifiers.get(id).doubleValue();
    		identifiers.put(id, new Double(value));
    	}
    }

    // TODO recalculate sum
    /*
    public void scaleNB(double mp, double nm) {
    	for (Integer id : this) {
    		double value = Math.log((identifiers.get(id).doubleValue() + mp) / nm);
    		identifiers.put(id, new Double(value));
    	}
    }
    */
    
    public static SparseWeightVector staticScale(double scalar, SparseWeightVector v) {
    	SparseWeightVector result = v.deepCopy();
    	result.scale(scalar);
    	return result;
    }
        
    public class idComparator implements Comparator<Integer> {
    	public int compare(Integer i1, Integer i2) {
    		return i1.compareTo(i2);
    	}
    }
    
	public SparseWeightVector copy() {
		return new SparseWeightVector(this);
	}

	public SparseWeightVector deepCopy() {
		return copy();
	}
}
