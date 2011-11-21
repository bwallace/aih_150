package nlp.extraction.relations;

import java.io.PrintWriter;
import nlp.core.Entity;
import nlp.core.Relation;
import nlp.extraction.PropertyExtractor;

public class RelationLengthExtractor extends PropertyExtractor {
	
	public RelationLengthExtractor() {
		super("RelationDistance(words)");
	}

	public RelationLengthExtractor(org.w3c.dom.Element extractor) {
		this();
	}
	
	// assumes Relation
	public String key(Object o, Object target) {
		Relation r = (Relation) o;	// could force this more elegantly (but unnecessary)
		Entity arg0 = r.getArgument(0);
		Entity arg1 = r.getArgument(1);
		int start = Integer.parseInt(arg0.getProperty("end")) + 1;
		int end = Integer.parseInt(arg1.getProperty("start")) - 1;
		int length = end - start + 1; 
    	if (length >= 0) 
    		return new String("RelationLength=" + length);
    	return null;
    }
	
	public void xml(PrintWriter out) {
		out.println("<Extractor>");
		out.println("<type>" + getClass().getName() + "</type>");
		out.println("</Extractor>");
	}
}
