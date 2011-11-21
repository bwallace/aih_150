package ml.sample;

import shared.Copyable;

/**
 * Used to define a Sample where there is a distribution defined over the Sample.
 * 
 * @author ksmall
 *
 * @param <T>
 */
public interface DistributionSample<T extends Copyable<T>, U extends Event<T>> extends RandomAccessSample<T> {

	public void add(Event<T> e);
	public void generateDistribution();
	public T distributionNext();
	public DistributionSample<T,U> rejectionSample(double Z);
	public DistributionSample<T,U> rejectionSample();
	
}
