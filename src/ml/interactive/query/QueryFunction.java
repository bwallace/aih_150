package ml.interactive.query;

import shared.Copyable;
import ml.learn.Learner;

public abstract class QueryFunction<T extends Copyable<T>> {

    protected boolean probability;

    public QueryFunction(boolean probability) {
    	this.probability = probability;
    }

    public QueryFunction() { 
    	this(false); 
    }

    // the example is going to be assumed to be an Instance or StructuredInstance
    // (depending on the situation)
    public abstract double score(Learner learner, T example);
}
