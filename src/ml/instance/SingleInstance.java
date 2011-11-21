package ml.instance;

import java.util.Iterator;

/**
 * An Instance primarily consists of a FeatureVector of features and a 
 * FeatureVector of labels.  The use of a FeatureVector for labels allows  
 * for multiclass, multilabel, label ranking, and cost-sensitive data.  An Instance 
 * acts as the primary unit of data to be passed to learning algorithms. 
 * 
 * @author ksmall
 */

public class SingleInstance implements Instance {

	/** a description of the instance */
    public String identifier;
    /** the FeatureVector of features */
    public FeatureVector features;
    /** the FeatureVector of labels */
    public FeatureVector labels;
    /** indicates if hidden output labels are respected */
    protected boolean labelRespect;

    /**
     * the primary constructor
     * 
     * @param identifier	the Instance identifier
     * @param featureRespect	indicates if hidden status of features is respected
     * @param labelRespect	indicates if hidden status of labels is respected
     */
    public SingleInstance(String identifier, boolean featureRespect, boolean labelRespect) {
    	features = new FeatureVector(featureRespect);
    	labels = null;
    	this.labelRespect = labelRespect;
    	this.identifier = new String(identifier);
    	// results = new InstanceResult();
    	// results = null;
    }

    /**
     * Constructor which assumes the hidden status of features and labels is respected.
     * 
     * @param identifier	the Instance identifier
     */
    public SingleInstance(String identifier) {
    	this(identifier, true, true);
    }
    
    public SingleInstance(SingleInstance instance) {
    	this(instance.identifier, instance.features.respectHidden, instance.labelRespect);
    	features = instance.features.deepCopy();
    	if (instance.labels == null)
    		labels = null;
    	else
    		labels = instance.labels.deepCopy();
    }
    
    /**
     * Adds a Feature to the feature FeatureVector
     * 
     * @param f	the Feature to be added
     */
    public void addFeature(Feature f) {
    	features.addFeature(f);
    }

    /**
     * Adds all of the Feature instances in the input FeatureVector to the
     * feature FeatureVector
     * 
     * @param f	the FeatureVector to be added
     */
    public void addFeatures(FeatureVector f) {
    	features.addFeatures(f);	
    }

    /*
    public void pruneFeatures(HashSet pruneSet) {
    	features.pruneFeatures(pruneSet);
    }
    */
    
    /**
     * Adds a label to the label FeatureVector
     * 
     * @param l	the label to be added
     */
    public void addLabel(Feature l) {
    	if (labels == null)
    		labels = new FeatureVector(labelRespect);
    	labels.addFeature(l);
    }

    /**
     * Adds all of the labels contained in the input FeatureVector to the
     * label FeatureVector
     * 
     * @param l	the FeatureVector of labels to be added
     */
    public void addLabels(FeatureVector l) {
    	if (labels == null)
    		labels = new FeatureVector(labelRespect);
    	labels.addFeatures(l);
    }

    /**
     * The iterator for the feature FeatureVector
     * 
     * @return	the feature FeatureVector iterator
     */
    public Iterator<Feature> featureIterator() {
    	return features.iterator();
    }
    
    /**
     * The iterator for the label FeatureVector
     * 
     * @return	the label FeatureVector iterator
     */
    public Iterator<Feature> labelIterator() {
    	if (labels == null)
    		return null;
    	return labels.iterator();
    }
    
    /**
     * Returns the first label in the label FeatureVector.  Often used in cases
     * where there is a single label (standard supervised learning) or the label
     * FeatureVector has been sorted by strength.
     * 
     * @return	the first label in the label FeatureVector
     */
    public Feature firstLabel() {
    	if (labels == null)
    		return null;
    	return labelIterator().next();
    }

    /**
     * Indicates if the first label in the label FeatureVector is visible.
     * 
     * @return	{@code true} if the first label is visible, else {@code false}
     */
    public boolean labelVisible() {
    	return firstLabel().isVisible();
    }

    /**
     * Returns a hashcode based on the identifier of this Instance
     * 
     * @return	the hash code
     */
    public int hashCode() {
    	return identifier.hashCode();
    }

    /**
     * Trims the feature and label FeatureVector instances to size
     */
    public void trim() {
   		features.trim();	
    	if (labels != null)
    		labels.trim();
    }

    /**
     * Normalizes the feature FeatureVector to size 1 based on the stated norm.
     * 
     * @param p	the norm used for normalization
     */
    public void normalize(double p) {
    	if (features != null)
    		features.scale(1.0 / norm(p));
    }
    
    public double norm(double p) {
    	return features.norm(p);
    }

    // designed for sequential experiments
    public void scaleFeatures(double scalar) {
    	features.scale(scalar);
    }
    
    /**
     * a String representation of this Instance
     */
    public String toString() {
    	String result = new String(identifier + "|[");
    	if (labels == null)
    		result += "null";
    	else {
    		//result += labels;
    		// TODO -- assuming a single label for the moment
    		for (Feature f : labels)
    			result += f.toString(); //f.identifier();
    	}
    	result += "] " + features;
    	return result;
    }

	public SingleInstance copy() {
    	SingleInstance copy = new SingleInstance(identifier, features.respectHidden, labelRespect);
    	copy.features = features.copy();
    	if (labels == null)
    		copy.labels = null;
    	else
    		copy.labels = labels.copy();
    	return copy;
	}

	public SingleInstance deepCopy() {
		return new SingleInstance(this);
	}

	public String identifier() {
		return identifier;
	}

	public boolean equals(Instance instance) {
		return identifier.equals(instance.identifier());
	}

    // for active learning debugging (doesn't do null stuff yet)
    /*
    public String activeToString() {
    	return new String(identifier + "|" + "[" + labels + 
    			"](" + confidence + ")");
    }	
    */
}	
