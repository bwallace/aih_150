package ml.interactive.sample.IFSC;

// think about PartialInstance to some degree
// instanceof is the magic command to compare different types
public class argmin extends InteractiveComparator {

    public int compare(InteractiveEvent o1, InteractiveEvent o2) {
    	return Double.compare(o1.score, o2.score);
    }

    public boolean equals(InteractiveEvent o1, InteractiveEvent o2) {
    	return o1.score == o2.score;
    }

    public double lastValue() {
    	return Double.POSITIVE_INFINITY;
    }
        
    public double firstValue() {
    	return Double.NEGATIVE_INFINITY;
    }
}
