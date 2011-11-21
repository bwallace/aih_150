package ml.instance.structured;

import java.util.ArrayList;
import java.util.Iterator;

import shared.Copyable;
import ml.instance.Prediction;

/**
 * A vector of predictions, which are passed to an inference mechanism to make a structured assignment.
 * 
 * @author ksmall
 */
public class StructuredPrediction implements Copyable<StructuredPrediction>, Iterable<Prediction>{

	protected ArrayList<Prediction> predictions;
	
	public StructuredPrediction() {
		predictions = new ArrayList<Prediction>();
	}
	
	public StructuredPrediction(StructuredPrediction prediction) {
		this();
		for (Prediction p : prediction)
			predictions.add(p.deepCopy());
	}
	
	public void addPrediction(Prediction p) {
		predictions.add(p);
	}
	
	public Iterator<Prediction> iterator() {
		return predictions.iterator();
	}
	
	public Prediction get(int index) {
		return (Prediction) predictions.get(index);
	}
	
	// not a "global" softmax (obviously)
	public void softmax() {
		for (Prediction prediction : this)
			prediction.softmax();
	}
	
	public int size() {
		return predictions.size();
	}
	
	public void trim() {
		predictions.trimToSize();
	}
	
	public String toString() {
    	String result = new String();
    	Iterator<Prediction> it = iterator();
    	/*
    	for (Prediction prediction :  this)
    		result += prediction + "\n";
    	*/
    	while (it.hasNext()) {
    		result += it.next();
    		if (it.hasNext())
    			result += "\n";
    	}
    	return result;
    }

	public StructuredPrediction copy() {
		StructuredPrediction copy = new StructuredPrediction();
		copy.predictions.addAll(predictions);
		return copy;
	}

	public StructuredPrediction deepCopy() {
		return new StructuredPrediction(this);
	}
}
