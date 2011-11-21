package ml.learn.structured.online;

import java.io.PrintWriter;
import java.util.Iterator;
import ml.instance.Instance;
import ml.inference.Inference;
import ml.instance.structured.StructuredInstance;
import ml.learn.Learner;
import ml.learn.online.Online;

public class OnlinePlusInference extends OnlineStructuredLearner {

    public OnlinePlusInference(Inference inference) {
    	super(inference);
    }

    public OnlinePlusInference(Learner learner, Inference inference) {
		super(learner, inference);
	}

    public OnlinePlusInference(OnlinePlusInference learner) {
    	super(learner);
    }

	public void train(StructuredInstance example) {
    	Iterator<Learner> itLearner = iterator();
    	Online learner = null;
    	if (learners.size() == 1)
    		learner = (Online) itLearner.next();
    	for (Instance instance : example) {
    		if (learners.size() > 1)
    			learner = (Online) itLearner.next();
    		learner.train(instance);
    	}
    }
    	
	public OnlinePlusInference copy() {
		return (OnlinePlusInference) super.copy();
	}

	public OnlinePlusInference deepCopy() {
		return (OnlinePlusInference) super.deepCopy();
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
}
