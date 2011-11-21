package ml.learn.structured.online;

import java.io.PrintWriter;
import java.util.Iterator;

import ml.instance.Instance;
import ml.inference.Inference;
import ml.instance.structured.StructuredAssignment;
import ml.instance.structured.StructuredInstance;
import ml.learn.Learner;
import ml.learn.online.Online;

public class InferenceBasedTraining extends OnlineStructuredLearner {

    public InferenceBasedTraining(Inference inference) {
    	super(inference);
    }
        
    public InferenceBasedTraining(Learner learner, Inference inference) {
		super(learner, inference);
	}

    public InferenceBasedTraining(InferenceBasedTraining learner) {
    	super(learner);
    }

	public void train(StructuredInstance example) {
    	StructuredAssignment prediction = trainEvaluate(example);
		Iterator<Learner> itLearner = iterator();
    	Online learner = null;
    	int counter = 0;
    	if (learners.size() == 1)
    		learner = (Online) itLearner.next();
    	for (Instance instance : example) {
    		if (learners.size() > 1)
    			learner = (Online) itLearner.next();
    		//System.out.println(learner);
    		//System.out.println(instance);
    		//System.out.println(prediction);
    		learner.train(instance, prediction.get(counter++));
    	}
    }
    
	public void xmlBody(PrintWriter out) {
		super.xmlBody(out);
		out.println("<Inference>" + inference.getClass().getName() + "</Inference>");
	}

	/*
	@Override
	public void read(BufferedReader in) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(PrintWriter out) {
		// TODO Auto-generated method stub
		
	}
	*/
	
	public InferenceBasedTraining copy() {
		return (InferenceBasedTraining) super.copy();
	}
	
	public InferenceBasedTraining deepCopy() {
		return (InferenceBasedTraining) super.deepCopy();
	}
}
