package ml.extraction;

public class Explanation {

	public String extractor;
	public int id;
	public String key;
	
	public Explanation(String extractor, int id, String key) {
		this.extractor = extractor;
		this.id = id;
		this.key = key;
	}
	
	/*
	public Explanation(String extractor, int id) {
		this(extractor, id, null);
	}
	*/
	
	public String toString() {
		return extractor + ":" + key + "(" + id + ")";
	}
}
