package nlp.semantics;

import java.util.ArrayList;
import java.util.Iterator;

public class SemanticSet implements Comparable<SemanticSet>, Iterable<SemanticElement> {
	
	public String key;
	public String description;
	public ArrayList<SemanticElement> phrases;
	public long sum;
	
	public SemanticSet(String key, String description) {
		this.key = key;
		this.description = description;
		phrases = new ArrayList<SemanticElement>();
		sum = 0;
	}
	
	public void add(SemanticElement phrase) {
		phrases.add(phrase);
		sum += Long.parseLong(phrase.count);
	}
	
	public int size() {
		return phrases.size();
	}
	
	public double entropy() {
		if (sum == 0)
			return 0.0;
		double result = 0.0;
		for (SemanticElement element : phrases) {
			double p = Double.parseDouble(element.count) / sum;
			result -= p * (Math.log(p) / Math.log(2.0));  // (not anymore)
		}
		return result;
	}
	
	public String toString() {
		return new String(key + ";" + description + ";" + entropy() + ";" + phrases);
	}

	public int compareTo(SemanticSet o) {
		return Double.compare(entropy(), o.entropy());
	}

	public Iterator<SemanticElement> iterator() {
		return phrases.iterator();
	}
}