package ml.loss;

/**
 * The loss function abstract class.  Takes in a predicted labeling and a correct labeling as 
 * input, returning a real value which represents the loss incurred by making the stated
 * prediction.
 * 
 * @author ksmall
 */
public interface LossFunction {
	
	/**
	 * Returns the loss for the predicted labeling given the correct labeling.
	 * 
	 * @param correct		the correct labeling
	 * @param predicted		the predicted labeling
	 * @return				the loss for this prediction
	 */
	public double loss(Object correct, Object predicted);
}
