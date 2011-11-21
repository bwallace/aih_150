package ml.instance;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import ml.utility.Globals;

import shared.Copyable;

/**
 * FeatureVector performs the basic operations on a set of features for use in
 * learning algorithms.  Should consider making a sparse and fixed size vector
 * version.
 * 
 * @author ksmall
 */
public class FeatureVector implements Copyable<FeatureVector>, Iterable<Feature> {

	/** the set of features */
    public ArrayList<Feature> features;
    /** should hidden variables be respected (for "active" user-interactions) */
    public boolean respectHidden;
    /** keeps sorting status so we don't repetitively re-sort (mostly for distances) */
    private byte sortStatus;
    
    /**
     * The primary FeatureVector constructor.  Creates an empty list for which 
     * features will be added.
     * 
     * @param respectHidden	indicates if learning algorithms respect the hidden 
     * 	status of specified variables
     */
    public FeatureVector(boolean respectHidden) {
    	features = new ArrayList<Feature>();
    	this.respectHidden = respectHidden;
    	this.sortStatus = Globals.UNSORTED;
    }

    /**
     * A constructor which assumes the hidden status is respected.
     */
    public FeatureVector() {
    	this(true);
    }

    /**
     * A constructor which also takes a Feature from which the vector is started.
     * 
     * @param respectHidden	indicates if learning algorithms respect the hidden 
     * 	status of specified variables
     * @param f	the initial feature
     */
    public FeatureVector(boolean respectHidden, Feature f) {
    	this(respectHidden);
    	addFeature(f);
    }
    
    /**
     * A constructor which takes an initial feature and assumes the hidden
     * status of features is respected
     * 
     * @param f	the initial feature
     */
    public FeatureVector(Feature f) {
    	this(true, f);
    }

    /**
     * The copy constructor
     * 
     * @param v		the FeatureVector to be copied
     */
    public FeatureVector(FeatureVector v) {
    	this(v.respectHidden);
    	for (Feature feature : v.features)
    		addFeature(feature.deepCopy());
    	this.sortStatus = v.sortStatus;
    }
    
    /**
     * Indicates the size of the FeatureVector, including hidden features.
     * 
     * @return	the number of Feature instances in the vector
     */
    public int size() {
    	return features.size();
    }

    /**
     * Clears the FeatureVector, resulting in an empty vector.
     */
    public void clear() {
    	features.clear();
    	this.sortStatus = Globals.UNSORTED;
    }

    /**
     * Adds the specified Feature to the FeatureVector.
     *  
     * @param f	the Feature to be added
     * TODO: create global parameter that requires adding elements in order
     */
    public void addFeature(Feature f) {
    	features.add(f);
    	sortStatus = Globals.UNSORTED;
    }

    /**
     * Adds the input FeatureVector to the current FeatureVector
     * 
     * @param v	the FeatureVector to be added
     */
    public void addFeatures(FeatureVector v) {
    	features.addAll(v.features);
    	sortStatus = Globals.UNSORTED;
    }

    /*
    public void pruneFeatures(HashSet pruneSet) {
    	ArrayList result = new ArrayList();
    	for (Iterator it = iterator(); it.hasNext(); ) {
    		Feature f = (Feature) it.next();
    		if (!pruneSet.contains((f.identifier)))
    			result.add(f);
    	}
    	result.trimToSize();
    	features = result;
    }
    */
    
    /**
     * An iterator for the Feature instances in the FeatureVector
     * 
     * @return	the Feature iterator
     */
    public Iterator<Feature> iterator() {
    	return features.iterator();
    }

    /**
     * Returns the specified Feature in the Feature vector as defined by
     * Feature equality (identifier) or null if the Feature does not exist.
     * 
     * @param f	the Feature to be retrieved
     * @return	the Feature if it exists, else null
     * 
     * TODO: Should check if id-sorted and do a binary search here.
     */
    public Feature get(Feature f) {
    	for (Feature current : features) {
    		if (f.equals(current))
    			return current;    		
    	}
    	return null;
    }
    
    /**
     * Returns the Feature located at the specified index.  Note that this
     * currently doesn't have error checking for ArrayOutOfBounds.
     * 
     * @param index	the specified index
     * @return	the Feature at the specified position
     * TODO: add error checking for array out of bounds if necessary
     */
    public Feature get(int index) {
    	return features.get(index);
    }

    /**
     * The dot product between two FeatureVector instances.  Used when algorithm
     * work in the dual space.
     * 
     * @param v	the FeatureVector for which the dot product is computed with
     * @return	the value of the dot product
     * TODO: actually implement this, including sorting enhancements (but could be a distance also)
     */
    /*
    public double dot(FeatureVector v) {
    	double result = 0.0;
    	HashMap x = new HashMap(size(), 1.0f);
    	for (Iterator it = iterator(); it.hasNext(); ) {
    		Feature f = (Feature) it.next();
    		if (!respectHidden || f.isVisible())
    			x.put(f, new Double(f.strength));
    	}
    	for (Iterator it = v.iterator(); it.hasNext(); ) {
    		Feature f = (Feature) it.next();
    		if ((!respectHidden || f.isVisible()) && x.containsKey(f))
    			result += ((Double) x.get(f)).doubleValue() * f.strength;
    	}
    	return result;
    }
     */
    
    /**
     * Scales the FeatureVector by the specified scalar.
     * 
     * @param scalar	the specified scalar
     */
    public void scale(double scalar) {
    	ArrayList<Feature> scaled = new ArrayList<Feature>(features.size());
    	for (Feature f : features)
    		scaled.add(new FeatureReal(f.identifier(), f.strength() * scalar, f.isVisible()));
    	features = scaled;
    }
    
    /**
     * Trims the FeatureVector to size (in an effort to save space).
     */
    public void trim() {
    	features.trimToSize();
    }
    
    /**
     * a String representation of the FeatureVector
     */
    public String toString() {
    	String result = new String();
    	if (features.size() > 0) {
    		Iterator<Feature> it = iterator();
    		result += it.next();
    		while (it.hasNext())
    			result += ", " + it.next();
    	}
    	return result;
    }

    /**
     * Sorts the FeatureVector by identifier order.
     */
    public void idSort() {
    	Collections.sort(features, new idComparator());
    	sortStatus = Globals.ID_SORTED;
    }

    /**
     * Sorts the FeatureVector by strength order (used for predictions).
     */
    public void strengthSort() {
    	Collections.sort(features, new strengthComparator(respectHidden));
    	sortStatus = Globals.STRENGTH_SORTED;
    }

    /**
     * Reverses the order of the FeatureVector.  No idea why I would want to
     * do this.
     */
    public void reverse() {
    	Collections.reverse(features);
    }

    /**
     * Returns the maximum strength valued Feature.
     * @return	the maximum strength valued Feature
     */
    public Feature strengthMax() {
    	return Collections.min(features, new strengthComparator(respectHidden));
    }

    /**
     * Returns the minimum strength valued Feature.
     * @return	the minimum strength valued Feature
     */
    public Feature strengthMin() {
    	return Collections.max(features, new strengthComparator(respectHidden));
    }
    
    /**
     * Calculates the p-norm of the FeatureVector.
     * 
     * @param p	the specified norm
     * @return	the value of the p-norm
     */
    public double norm(double p) {
    	double result = 0.0;
    	for (Feature feature : features) {
    		if (!respectHidden || feature.isVisible())
    			result += Math.pow(Math.abs(feature.strength()), p);    		
    	}
    	result = Math.pow(result, (1.0 / p));
    	return result;
    }

    /**
     * Calculates the 2-norm of the FeatureVector
     * 
     * @return	the value of the 2-norm
     */
    public double norm() {
    	return norm(2);
    }
    
    /**
     * Takes a FeatureVector and scalar, returning a new scaled FeatureVector.
     * This is widely used for additive online learning algorithms (Perceptron).
     * Assumes that any scaling results in a FeatureReal vector.
     * 
     * @param scalar	the value used to scale the input FeatureVector
     * @param v	the FeatureVector to be scaled
     * @return	a scaled FeatureVector
     */
    public static FeatureVector staticScale(double scalar, FeatureVector v) {
    	FeatureVector result = new FeatureVector(v.respectHidden);
    	for (Feature f : v.features)
    		result.addFeature(new FeatureReal(f.identifier(), f.strength() * scalar, f.isVisible()));
    	return result;
    }
    
    /**
     * Used to sort Features in ascending order of Feature identifiers.
     */
    public class idComparator implements Comparator<Feature> {
    	public int compare(Feature f1, Feature f2) {
    		return f1.identifier() - f2.identifier();
    	}
    }
    
    /**
     * Used to sort Features in descending order by Feature strength values
     */
    public class strengthComparator implements Comparator<Feature> {

    	/**	respect hidden status of Features */
    	public boolean visibility;
	
    	/**
    	 * the comparator constructor
    	 * 
    	 * @param visibility	indicates if Feature hidden status is respected
    	 */
    	public strengthComparator(boolean visibility) {
    		this.visibility = visibility;
    	}
	
    	// if not visible, is less than (not selected)
    	/**
    	 * Actually performs comparison.  Note that hidden features are
    	 * considered to have minimum values.
    	 */
    	public int compare(Feature f1, Feature f2) {
    		if (!visibility || (f1.isVisible() && f2.isVisible()))
    			return -Double.compare(f1.strength(), f2.strength());
    		else if (f1.isVisible() && !f2.isVisible())
    			return Integer.MAX_VALUE;
    		else if (!f1.isVisible() && f2.isVisible())
    			return Integer.MIN_VALUE;
    		else // (!f1.visible() && !f2.visible())
    			return 0;
    	}
    }
    
	public FeatureVector copy() {
		FeatureVector result = new FeatureVector(respectHidden);
		result.addFeatures(this);
		result.sortStatus = sortStatus;
		return result;
	}

	public FeatureVector deepCopy() {
		return new FeatureVector(this);
	}	
}
