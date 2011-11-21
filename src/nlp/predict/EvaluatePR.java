package nlp.predict;

import ml.instance.structured.StructuredInstance;
import ml.sample.Sample;

public interface EvaluatePR {

	// TODO - something smart here
	public abstract void evaluate(Sample<StructuredInstance> data, String trueTag);
	public abstract void evaluate(String fileName, String trueLabel);

	public abstract double recall(Sample<StructuredInstance> data, String trueTag, String predictedTag);
	public abstract double precision(Sample<StructuredInstance> data, String trueTag, String predictedTag);
	
	// TODO - something smart here also
	public abstract double recall();
	public abstract double precision();

	/*
	public double f1(double p, double r) {
		if ((p == 0.0) && (r == 0.0))
			return 0.0;
		return (2.0 * p * r) / (p + r);
	}
	*/
}
