package ml.learn.online;

import ml.instance.Feature;
import ml.instance.Instance;

/**
 * An interface for online learning.
 * 
 * @author ksmall
 */
public interface Online {

	public void train(Instance example, Feature prediction);
	public void train(Instance example);

}
