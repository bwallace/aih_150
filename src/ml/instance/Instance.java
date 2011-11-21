package ml.instance;

import shared.Copyable;

/**
 * The instance interface.
 * 
 * @author ksmall
 */
public interface Instance extends Copyable<Instance> {

	public String identifier();  // just to have something :)
	public boolean equals(Instance instance);	
}
