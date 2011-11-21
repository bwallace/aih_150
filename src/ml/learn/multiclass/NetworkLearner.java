package ml.learn.multiclass;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;

import ml.extraction.Lexicon;
import ml.learn.Learner;

/**
 * A NetworkLearner implements the situation when there are several learners, each
 * representing a prototype that are all combined into a new classification.  The
 * primary use here is multiclass classification.  In summary, this supports reduction
 * based multiprototype predictions.  
 * 
 * This should eventually be extended to error correcting output codes for the
 * multiclass case.
 * 
 * @author ksmall
 */
public abstract class NetworkLearner extends Learner {

	/** the network of learners */
	protected HashMap<Integer, Learner> network;
	/** the learner type that makes up the network members */
	protected Learner baseLearner;
	
	/**
	 * Standard constructor; may want to consider an empty parameter version for
	 * some cases.  This assumes that the input learner is newly constructed, although
	 * this may be more thoroughly tested in future versions.
	 * 
	 * @param learner	the base learner which will constitute network members
	 */
	public NetworkLearner(Learner learner) {
		network = new HashMap<Integer, Learner>();
		this.baseLearner = learner;
	}
	
	public NetworkLearner(NetworkLearner learner) {
		this.baseLearner = learner.deepCopy();
		this.network = new HashMap<Integer, Learner>();
		for (Iterator<Integer> it = learner.network.keySet().iterator(); it.hasNext(); ) {
			Integer key = it.next();
			network.put(new Integer(key), learner.network.get(key).deepCopy());
		}
	}
	
	/**
	 * Method for adding a new class to the output space provided by the specific
	 * NetworkLearner construct.
	 * 
	 * @param id	the id of the added output
	 */
	public abstract void addClass(int id);
	
	public abstract void addClasses(String[] targets, Lexicon labels);
	
	public void addClasses(Lexicon labels) {
		addClasses((String[]) labels.description.keySet().toArray(new String[0]), labels);
	}
	
	public void finish() {
		for (Iterator<Integer> it = network.keySet().iterator(); it.hasNext(); )
			network.get(it.next()).finish();
	}
	
	// keeps the base learner and resets all of the member classifiers
	// a hard reset would just be a new instantiation
	public void reset() {
		for (Iterator<Integer> it = network.keySet().iterator(); it.hasNext(); )
			network.get(it.next()).reset();
	}
	
	public void xmlBody(PrintWriter out) {
		super.xmlBody(out);
		for (Integer id : network.keySet()) {
			out.println("<prototype>");
			out.println("<id>" + id + "</id>");
			network.get(id).xml(out);
			out.println("</prototype>");
		}
	}
	
	/*
	public void write(PrintWriter out) {
		System.out.println("Doesn't write yet.");
	}
	
	public void read(BufferedReader in) {
		System.out.println("Doesn't read yet.");
	}
	*/
	
	public NetworkLearner copy() {
		NetworkLearner copy = (NetworkLearner) clone();
		copy.baseLearner = this.baseLearner;
		copy.network = new HashMap<Integer, Learner>();
		for (Iterator<Integer> it = network.keySet().iterator(); it.hasNext(); ) {
			Integer key = it.next();
			copy.network.put(key, network.get(key));
		}
		return copy;
	}
	
	public NetworkLearner deepCopy() {
		NetworkLearner copy = (NetworkLearner) clone();
		copy.baseLearner = this.baseLearner.deepCopy();
		copy.network = new HashMap<Integer, Learner>();
		for (Iterator<Integer> it = network.keySet().iterator(); it.hasNext(); ) {
			Integer key = it.next();
			copy.network.put(new Integer(key), network.get(key).deepCopy());
		}
		return copy;
	}
}
