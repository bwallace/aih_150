package shared;

/**
 * The Copyable interfaces, which requires the implementation of a copy
 * (shallow copy - essentially a typed clone) and deepCopy.  Also, it
 * is suggested that all classes have copy constructors which implement
 * a deepCopy.
 * 
 * @author ksmall
 */

public interface Copyable<T> {

	/**
	 * A shallow copy; all primitives and shallow copies of
	 * member structures
	 * 
	 * @return	the copied instance
	 */
	public T copy();
	
	/**
	 * A deep copy; all primitives and a recursive deep copy
	 * of all member structures
	 * 
	 * @return	the copied instance
	 */
	public T deepCopy();
	
	// this would mean just pointers, elements, and arrays (arrays still up for debate)
	//public T surfaceCopy();
}
