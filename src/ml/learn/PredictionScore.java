package ml.learn;

import ml.instance.Instance;

/**
 * The PredictionScore interface indicates that the classifier making predictions
 * also returns a confidence associated with the prediction.
 * 
 * @author ksmall
 */

public interface PredictionScore {

    /**
     * The activation value of the Learner for a given Instance
     * 
     * @param example	the Instance for which the Learner score is desired
     * @return	the Learner score associated with this instance
     */
    public double score(Instance example);
    
    /**
     * The activation value of the Learner "training hypothesis" for a 
     * given Instance.  This is intended for ensemble algorithms and
     * such.
     * 
     * @param example	the Instance for which the training hypothesis score is desired
     * @return	the training hypothesis score associated with the input Instance
     */
    public double trainScore(Instance example);	
}
