package ml.sample;

import shared.Copyable;

/**
 * An Event represents the element used to generate Sample objects.  Using 
 * language from statistics largely for entertainment value.
 * 
 * @author ksmall
 */

public class Event<T extends Copyable<T>> implements Copyable<Event<T>> {
	
	/** weight assigned to this particular Event */
	public double weight;
	/** distributional weight assigned to this Event (often generated from weight) */
	public double probability;
	/** the actual outcome if this Event is selected */
	public T outcome;
	
	/**
	 * The primary Event constructor
	 * 
	 * @param outcome		the outcome returned if Event is selected
	 * @param weight		the weight associated with this event
	 * @param isProbability	{@code true} if the weight is a probability
	 */
	public Event(T outcome, double weight, boolean isProbability) {
		this.outcome = outcome;
		this.weight = weight;
		if (isProbability)
			this.probability = this.weight;
		else
			this.probability = 0.0;
	}
	
	/**
	 * Event constructor which assumes that the input weight is not derived from
	 * a distribution.
	 * 
	 * @param outcome	the outcome returned is Event is selected
	 * @param weight	the weight associated with this Event
	 */
	public Event(T outcome, double weight) {
		this(outcome, weight, false);
	}

	/**
	 * Event constructor which assumes that the Events are from a uniform distribution
	 * 
	 * @param outcome	the outcome returned is Event is selected
	 */
	public Event(T outcome) {
		this(outcome, 1.0);
	}
		
	/**
	 * Returns a String representation of this Event instance
	 * 
	 * @return	the {@code String} representation of this {@code Event}
	 */
	public String toString() {
		return new String("EVENT[" + outcome + "] w:" + weight + " p:" + probability);
	}
		
	public Event<T> copy() {
		Event<T> result = new Event<T>(this.outcome, this.weight);
		result.probability = this.probability;
		return result;
	}
	
	public Event<T> deepCopy() {
		Event<T> result = null;
		if (this.outcome instanceof shared.Copyable<?>) {
			result = new Event<T>(this.outcome.deepCopy(), this.weight);
			result.probability = this.probability;
		}
		else
			throw new UnsupportedOperationException("Event outcome cannot deepCopy");
		return result;
	}
}
