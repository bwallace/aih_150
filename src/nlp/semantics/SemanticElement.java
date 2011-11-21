package nlp.semantics;

public class SemanticElement {
	
	public String phrase;
	public String score;
	public String count;
	
	public SemanticElement(String phrase, String score, String count) {
		this.phrase = phrase;
		this.score = score;
		this.count = count;
	}
	
	public String toString() {
		return new String("[" + phrase + "," + score + "," + count + "]");	
	}
}
