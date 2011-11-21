package ml.sample;

/**
 * Implements a Sample from which Event instances can be drawn from.
 * 
 * @author ksmall
 */

public interface Sample<T> {

	/**
	 * Resets the iterator to the first item in the Sample
	 */
	public void reset();
	
	/**
	 * Returns the next item for the Sample iterator
	 * 
	 * @return	the next item in the Sample
	 */
	public T next();

}
