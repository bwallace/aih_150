package ml.extraction;

import java.util.ArrayList;
import java.util.HashMap;

import ml.instance.Feature;
import ml.instance.Instance;
import ml.instance.SingleInstance;
import ml.instance.structured.StructuredInstance;

public class ExplanationLexicon extends Lexicon {
	
	public HashMap<Integer, Explanation> explanations;

	public ExplanationLexicon() {
		super();
		explanations = new HashMap<Integer, Explanation>();
	}
	
    public boolean add(String extractor, String key, LexiconEntry entry) {
    	/*
    	Explanation e = new Explanation(extractor, entry.id, key);
    	System.out.println(e);
    	*/
		explanations.put(entry.id, new Explanation(extractor, entry.id, key));
		return super.add(extractor, key, entry);  // since overwritten anyway
    }

    public int get(String source, String key, boolean addCount) {
    	//int id = super.get(source, key, addCount);
    	int id = super.get(source, key, addCount);
    	explanations.put(id, new Explanation(source, id, key));
    	return id;
    }
    
	public Explanation getExplanation(int id) {
		//System.out.println(id);
		return explanations.get(id);
	}

	public ArrayList<Explanation> getExplanations(SingleInstance instance) {
		ArrayList<Explanation> result = new ArrayList<Explanation>();
		for (Feature feature : instance.features) {
			result.add(getExplanation(feature.identifier()));
		}
		return result;
	}
	
	public ArrayList<ArrayList<Explanation>> getExplanations(StructuredInstance instance) {
		ArrayList<ArrayList<Explanation>> result = new ArrayList<ArrayList<Explanation>>();
		for (Instance local : instance)
			result.add(getExplanations((SingleInstance) local));
		return result;
	}
}
