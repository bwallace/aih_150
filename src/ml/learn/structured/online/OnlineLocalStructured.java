package ml.learn.structured.online;

import ml.inference.AllMaximum;
import ml.learn.Learner;

public class OnlineLocalStructured extends OnlinePlusInference {

    public OnlineLocalStructured(boolean softmax) {
    	super(new AllMaximum(softmax));
    }

    public OnlineLocalStructured() {
    	super(new AllMaximum());
    }

    public OnlineLocalStructured(Learner learner, boolean softmax) {
    	this(softmax);
    	addLearner(learner);
    }

    public OnlineLocalStructured(Learner learner) {
    	this();
    	addLearner(learner);
    }
    
    public OnlineLocalStructured(OnlineLocalStructured learner) {
    	super(learner);
    }
    
    public OnlineLocalStructured copy() {
    	return (OnlineLocalStructured) super.copy();
    }
    
    public OnlineLocalStructured deepCopy() {
    	return (OnlineLocalStructured) super.deepCopy();
    }    
}
