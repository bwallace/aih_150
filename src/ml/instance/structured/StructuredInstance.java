package ml.instance.structured;

import java.util.ArrayList;
import java.util.Iterator;

import ml.instance.Instance;
import ml.instance.SingleInstance;

/**
 * The StructuredInstance class is intended for the principle purpose of holding a vector of instances
 * necessary for a StructuredPrediction.
 * TODO: Do we really want SingleInstance throughout?
 * 
 * @author ksmall
 */

public class StructuredInstance implements Instance, Iterable<SingleInstance> {

	public String identifier;
	protected ArrayList<SingleInstance> instances;  // can be unlabeled
	public Object label;   // this means a LossFunction is needed
	protected boolean labelRespect;   // for active purposes again
	
	public StructuredInstance(String identifier) {
		this.identifier = new String(identifier);
		instances =  new ArrayList<SingleInstance>();
	}
	
	// note that the label isn't a deepCopy unless we work at this
	public StructuredInstance(StructuredInstance instance) {
		this(instance.identifier);
		for (SingleInstance i : instance.instances)
			instances.add(i.deepCopy());
		label = instance.label;
		this.labelRespect = instance.labelRespect;
	}
	
	public void addInstance(SingleInstance i) {
		instances.add(i);
	}
	
	/*
    public void pruneFeatures(HashSet pruneSet) {
    	for (Iterator it = iterator(); it.hasNext(); ) {
    		((Instance) it.next()).pruneFeatures(pruneSet);
    	}
    }
    */
	
	public Iterator<SingleInstance> iterator() {
		return instances.iterator();
	}
	
	public Instance get(int index) {
		return (Instance) instances.get(index);
	}
	
	public void setLabel(Object o) {
		label = o;
	}
	
	public int size() {
		return instances.size();
	}
	
	public void trim() {
		instances.trimToSize();
	}
	
	// just normalizes each element independently (note size is number of instances)
	// TODO should also create a "global" normalize (although probably not very interesting)
	public void localNormalize(double p) {
		for (SingleInstance instance : this)
			instance.normalize(p);
	}
	
	public void localNormalize() {
		localNormalize(2);
	}
	
	public String toString() {
		String result = new String("{" + identifier + "[" + label.getClass().getName() + "]}");
		for (Iterator<SingleInstance> it = iterator(); it.hasNext(); ) {
			result += it.next();
			if (it.hasNext())
				result += "||";
		}
		return result;
	}

	public StructuredInstance copy() {
		StructuredInstance copy = new StructuredInstance(identifier);
		copy.instances.addAll(instances);
		copy.label = label;
		copy.labelRespect = labelRespect;
		return copy;
	}

	public StructuredInstance deepCopy() {
		return new StructuredInstance(this);
	}

	public String identifier() {
		// TODO Auto-generated method stub
		return identifier;
	}

	public boolean equals(Instance instance) {
		return identifier.equals(instance.identifier());
	}
}
