package shared;

// even though trivial to make from scratch, inspired by
// Dan Bikel's parsing engine (with sorting - should maybe change)
public class IntPair {

	public static final int MAX_CLASSES = 31;
	
	public int first;
	public int second;
	
	// sorts for unique hashCode convenience
	public IntPair(int first, int second) {
		this.first = Math.min(first, second);
		this.second = Math.max(first, second);
	}
	
	public int hashCode() {
		return IntPair.MAX_CLASSES * first + second;
	}
	
	public boolean equals(Object o) {
		IntPair other = (IntPair) o;  // should exception check instead
		return ((other.first == first) && (other.second == second));
	}
	
	public static int hashCode(int first, int second) {
		return IntPair.MAX_CLASSES * Math.min(first, second) + Math.max(first, second);
	}
	
	public String toString() {
		return new String(first + "," + second);
	}
}
