package ml.inference;

import java.util.ArrayList;
import java.util.Collections;
import ml.inference.constraint.Constraint;
import ml.instance.structured.StructuredAssignment;
import ml.instance.structured.StructuredInstance;
import ml.instance.structured.StructuredPrediction;

/**
 * BeamSearch implements a beam search based inference algorithm for turning StructuredPrecition instances into
 * StructuredAssignment instances consistent with the output constraints.
 * TODO	allow for constraints to be changed at inference
 * 
 * @author ksmall
 */

public class BeamSearch extends Inference {

	/** the beam size of the search */
    protected int beamSize;
    /** the structural constraints - note that a ConstraintConjunction can include many constraints */
    protected Constraint constraint;
    /** the set of state successor candidates */
    protected ArrayList<StructuredAssignment> candidates;
    /** the comparator used for ordering StructuredAssignment instances */
    protected AssignmentComparator comparator;
    /* leaving "firstAll" off (which I think meant returning max when no viable solutions were found) */

    /**
     * Constructor
     * 
     * @param beamSize		the search beam size
     * @param softmax		indicates whether softmax values ({@code true}) or raw activations ({@code false})
     * 						should be used for inference 
     */
    public BeamSearch(Constraint constraint, int beamSize, boolean softmax) {
    	super(softmax);
    	candidates = null;
    	this.constraint = constraint;
    	this.beamSize = beamSize;
    	//this.softmax = softmax;
    	this.comparator = new AssignmentComparator(softmax);
    }

    /**
     * Constructor which assumes that softmax values should be used for inference
     * 
     * @param constraint	the output space constraints
     * @param beamSize		the search beam size
     */
    public BeamSearch(Constraint constraint, int beamSize) {
    	this(constraint, beamSize, true);
    }

    /**
     * Performs a single search step.  Namely, this procedure pops the top scoring
     * item from the candidate set, expands the resulting StructuredAssignment, adds
     * these expanded items to the candidate set, and prunes the set to the specified
     * beam size.  Note that if a completed assignment is the highest scoring element
     * in the candidate set, it is returned as a solution; otherwise {@code null} is
     * returned (meaning no solutions were found).
     * 
     * @return	a solution StructuredAssignment if found, else {@code null}
     */
    protected StructuredAssignment step() {
    	StructuredAssignment sa = (StructuredAssignment) candidates.remove(0);
    	ArrayList<StructuredAssignment> expansion = sa.expand(constraint);
    	if (expansion == null) {
    		if (sa.isComplete())
    			return sa;
    	}
    	else {
    		candidates.addAll(expansion);
    		//Collections.sort(candidates, new AssignmentComparator(softmax));
    		Collections.sort(candidates, comparator);
    		if (candidates.size() > beamSize)
    			candidates.subList(beamSize, candidates.size()).clear();
    	}
    	return null;
    }

    /*
    public StructuredAssignment infer(StructuredInstance si,
				      StructuredPrediction sp, boolean assignVisible) {
    	if (softmax)
    		sp.softmax();
    	StructuredAssignment root = new StructuredAssignment(sp);
    	if (assignVisible) {
    		root.assignVisible(si);
    		if (root.allVisible())
    			return root;
    	}
    	candidates = root.expand(constraint);
    	//Collections.sort(candidates, new AssignmentComparator(softmax));
    	Collections.sort(candidates, comparator);
    	if (candidates.size() > beamSize)
    		candidates.subList(beamSize, candidates.size()).clear();

    	// System.out.println("gd: " + candidates);
    	while (candidates.size() > 0) {
    		StructuredAssignment state = step();
    		if (state != null)
    			return state;
    	}
    	return null;
    	// crappy hack for no feasible state (should state?) - removing for now
    	
    	//StructuredAssignment result = StructuredAssignment.max(sp);
    	//if (assignVisible)
    	//	result.assignVisible(si);
    	//return result;
    	
    }
	*/

    public ArrayList<StructuredAssignment> infer(StructuredInstance si, StructuredAssignment root,
    		boolean assignVisible, int k) {
    	if (assignVisible)
    		root.assignVisible(si);
    	candidates = root.expand(constraint);
    	//Collections.sort(candidates, new AssignmentComparator(softmax));
    	Collections.sort(candidates, comparator);
    	if (candidates.size() > beamSize)
    		candidates.subList(beamSize, candidates.size()).clear();
	 
    	ArrayList<StructuredAssignment> result = new ArrayList<StructuredAssignment>();
    	while ((result.size() < k) && (candidates.size() > 0)) {
    		StructuredAssignment state = step();
    		if (state != null)
    			result.add(state);
    	}
    	if (result.size() == 0)
    		result = null;
    	else
    		result.trimToSize();
    	return result;    	
    }
    
    public ArrayList<StructuredAssignment> infer(StructuredInstance si, StructuredPrediction sp, 
			   boolean assignVisible, int k) {
    	if (softmax)
    		sp.softmax();
    	StructuredAssignment root = new StructuredAssignment(sp);
    	return infer(si, root, assignVisible, k);
    }
    
    // TODO
    public BeamSearch copy() {
    	return null;
    }
    
    // TODO
    public BeamSearch deepCopy() {
    	return null;
    }
}

