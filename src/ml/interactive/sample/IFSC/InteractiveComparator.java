package ml.interactive.sample.IFSC;

import java.util.Comparator;

public abstract class InteractiveComparator implements Comparator<InteractiveEvent> {

    public abstract int compare(InteractiveEvent o1, InteractiveEvent o2);
    public abstract boolean equals(InteractiveEvent o1, InteractiveEvent o2);
    public abstract double lastValue();
    public abstract double firstValue();

}
