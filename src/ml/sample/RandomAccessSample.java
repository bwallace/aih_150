package ml.sample;

public interface RandomAccessSample<T> extends Sample<T> {

	/**
	 * Adds an item to the RandomAccessSample
	 * 
	 * @param e		the Event to be added
	 */
	public void add(T e);
	public void add(Sample<T> sample);
	
	/**
	 * Randomizes the ordering of the Sample; also resets the Iterator to
	 * point to the first Event in the Sample
	 */
	public int size();
	
	/**
	 * Randomizes the ordering of the Sample; also expected to reset the Iterator to
	 * point to the first item in the RandomAccessSample
	 */
	public void permute();
	
	/**
	 * Returns the item at the specified index
	 * 
	 * @param index	the specified index
	 * @return	the item at the specified index
	 */
	public T get(int index);
	
	/**
	 * Returns a subsample based on the size of the {@code percentage} input.
	 * Starts counting the return size from the current position of the Iterator,
	 * so {@code reset} if the subsample should be taken from the beginning of the list
	 * (although handy for deriving 80/20 splits, cross-validation sets, etc.).  However,
	 * the properties are recalculated for the generated Sample.
	 * 
	 * TODO -- recomment this to reflect changes with remove
	 * 
	 * @param percentage	The percentage of the Sample to be included in the subsample.
	 * 						If the percentage is greater than or equal to 1, assumed to be
	 * 						the actual count (which will be returned if specified number of
	 * 						Events remain in the Sample from the position of the Iterator)
	 * @return	the specified subsample with the Iterator set at the front of the list
	 */
	public RandomAccessSample<T> subSample(double percentage, boolean remove);
	
	/**
	 * Returns an Iterator for the Sample.
	 * 
	 * @return	the Sample Iterator through the instances
	 */
	/*
	public Iterator<T> iterator();
	*/
	
	/**
	 * Returns a String representation of this Sample instance
	 *
	 * @return	the {@code String} representation of this {@code Sample}
	 */
	public String toString();
}
