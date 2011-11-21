package ml.learn.multiclass.online;

import ml.extraction.Lexicon;
import ml.instance.Feature;
import ml.instance.Instance;
import ml.learn.Learner;
import ml.learn.online.Online;
import ml.learn.online.OnlineLearner;
import ml.loss.LossFunction;
import ml.sample.RandomAccessSample;
import ml.sample.Sample;

public abstract class WinnerTakeAll extends ml.learn.multiclass.WinnerTakeAll implements Online {

	public WinnerTakeAll(Learner learner, String[] targets, Lexicon labels) {
		super(learner, targets, labels);
	}

	public WinnerTakeAll(Learner learner, Lexicon labels) {
		super(learner, labels);
	}

	public WinnerTakeAll(Learner learner) {
		super(learner);
	}

	public double evaluate(Sample<Instance> testSample, LossFunction loss) {
		return OnlineLearner.staticEvaluate(this, testSample, loss);
	}

	public abstract void train(Instance example, Feature prediction);
	
	public void train(Instance example) {
		train(example, null);
	}

	/*
	public void train(Instance example) {
		train(example, trainEvaluate(example).winner());
	}
	*/
	
	public void train(Sample<Instance> examples) {
    	OnlineLearner learner = (OnlineLearner) baseLearner;
		OnlineLearner.staticTrain(this, examples, learner.iterations());
	}

	public void train(RandomAccessSample<Instance> examples) {
    	OnlineLearner learner = (OnlineLearner) baseLearner;
		OnlineLearner.staticTrain(this, examples, learner.iterations(), learner.shuffle());
	}

	public WinnerTakeAll copy() {
		return (WinnerTakeAll) super.copy();
	}
	
	public WinnerTakeAll deepCopy() {
		return (WinnerTakeAll) super.deepCopy();
	}
}
