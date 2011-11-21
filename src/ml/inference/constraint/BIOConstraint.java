package ml.inference.constraint;

import ml.instance.structured.StructuredAssignment;

/**
 * Used to enforce BIO segmentation.
 * 
 * @author ksmall
 */
public class BIOConstraint extends Constraint {

	protected String[] labels;
	
	public BIOConstraint(String[] labels) {
		this.labels = labels;
	}
	
	// TODO -- note that this does not allow for partial structures as I cannot follow a hidden label
	public boolean valid(StructuredAssignment example) {
		//System.out.println(example);
		for (int i = 0; i < example.size(); i++) {
    		if (example.get(i) == null)
    			continue;
			//System.out.println(example.assignments[i]);
			String label = labels[example.get(i).identifier()].split("=")[1];
			if (label.startsWith("I-")) {
				if (i == 0)
					return false;
				else {
					String prev = labels[example.get(i-1).identifier()].split("=")[1];
					//System.out.println(prev + "," + label);
					// O may follow O
					// I-label may follow I-label
					// I-label may follow B-label
					if ((!prev.equals(label.replace("I-", "B-"))) &&
						(!prev.equals(label))) {
						//System.out.println("false");
						return false;
					}
				}
			}
		}
		return true;
	}
}
