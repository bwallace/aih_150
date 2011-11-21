package ml.sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import ml.utility.Globals;

import shared.Copyable;

/**
 * The most commonly used implementation of a RandomAccessSample
 * 
 * @author ksmall
 *
 * @param <T>
 */
public class ArrayListSample<T extends Copyable<T>, U extends Event<T>> implements 
		DistributionSample<T,U>, Iterable<U> {

	/** the list of events in the Sample */
	protected ArrayList<U> events;
	/** the Sample Iterator */
	protected Iterator<U> iterator;
	/** a sum of the Event scores in the Sample */
	protected double sum;
	/** the maximum Event score in the Sample */
	protected double max;

	
	/**
	 * The Sample constructor
	 */
	public ArrayListSample() {
		events = new ArrayList<U>();
		sum = 0.0;
		max = 0.0;
	}
	
	/**
	 * Sample constructor using a filename and InstanceGenerator (assumes instance per line)
	 * note: we are tying ourselves to Instance items right here with the generics (but sort of makes sense)
	 * I actually don't think this is true anymore
	 * TODO -- note direct use of event here (overload?)
	 */
	public ArrayListSample(String datafile, Parser<T> parser) {
		this();
		parser.open(datafile);
		T item = null;
		while ((item = parser.next()) != null)
			add(new Event<T>(item));
		reset();
	}

	public T next() {
		if (iterator.hasNext())
			return nextEvent().outcome;
		return null;
	}

	public U nextEvent() {
		if (iterator.hasNext())
			return iterator.next();
		return null;
	}
	
	public U remove(int index) {
		if (index < size())
			return events.remove(index);
		else
			return null;
	}
	
	public void reset() {
		iterator = iterator();		
	}

	public Iterator<U> iterator() {
		return events.iterator();
	}
	
	@SuppressWarnings("unchecked")   // don't like, but look at later
	public void add(Event<T> e) {
		events.add((U) e);
		sum += e.weight;
		max = Math.max(max, e.weight);		
	}
	
	// TODO -- overload
	public void add(T e) {
		add(new Event<T>(e));
	}
	
	public void add(Sample<T> sample) {
		sample.reset();
		T item = null;
		while ((item = sample.next()) != null)
			add(item);
	}
	
	public T get(int index) {
		return events.get(index).outcome;
	}

	public void permute() {
		Collections.shuffle(events, Globals.rng);
		reset();
	}

	public int size() {
		return events.size();
	}

	// only subsamples from the front when removing for the time being (7/20/09 to get older version without remove)
	// TODO -- overload
	public ArrayListSample<T,U> subSample(double percentage, boolean remove) {
		ArrayListSample<T,U> result = new ArrayListSample<T,U>();
		int count = 0;
		if (percentage >= 1.0)
			count = (int) percentage;
		else
			count = (int) (percentage * size());
		for (int i = 0; i < count; i++) {
			if ((remove) && (events.size() > 0))
				result.add(remove(0));
			
			//else if (events.size() >= i)
			//	result.add(events.get(i).copy());
			
			else if (iterator.hasNext())
				result.add(iterator.next().copy());
			else
				break;
		}
		if (remove)
			calculateProperties();
		result.calculateProperties();
		//result.reset();
		return result;
	}
	
	public void calculateProperties() {
		sum = 0.0;
		max = 0.0;
		for (Event<T> e : this) {
			sum += e.weight;
			max = Math.max(max, e.weight);
		}
		reset();
	}

	// TODO actually make this work (the idea is to return next item stochastically)
	public T distributionNext() {
		return next();
	}

	public void generateDistribution() {
		for (Event<T> e : this)
			e.probability = e.weight / sum;
	}

	public DistributionSample<T,U> rejectionSample(double Z) {
		ArrayListSample<T,U> result = new ArrayListSample<T,U>();
		for (U e : this) {
			if (Globals.randDouble() < (e.probability / Z))
				result.add(e.copy());
		}
		result.reset();
		return result;
	}

	public DistributionSample<T,U> rejectionSample() {
		return rejectionSample(max / sum);
	}
}
