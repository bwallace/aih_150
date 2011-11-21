package ml.extraction;

public class LexiconEntry {

	public int id;
	public int count;
	
	public LexiconEntry(int id, int count) {
		this.id = id;
		this.count = count;
	}
	
	public String toString() {
		return new String(id + "," + count);
	}
}
