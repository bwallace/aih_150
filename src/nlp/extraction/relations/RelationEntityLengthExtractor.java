package nlp.extraction.relations;

import java.io.PrintWriter;

import nlp.core.Relation;
import nlp.extraction.PropertyExtractor;

// states the number of entities between the two arg entities
// assumes naming convention of sequential entity ids
// TODO -- add prefix to xml
public class RelationEntityLengthExtractor extends PropertyExtractor {
	
	protected String entityPrefix;
	
	public RelationEntityLengthExtractor(String entityPrefix) {
		super("RelationDistance(entity)");
		this.entityPrefix = entityPrefix;
	}
	
	public RelationEntityLengthExtractor() {
		this("e");
	}

	public RelationEntityLengthExtractor(org.w3c.dom.Element extractor) {
		this();
	}
	
	// assumes Relation
	public String key(Object o, Object target) {
		Relation r = (Relation) o;	// could force this more elegantly (but unnecessary)
		int firstID = Integer.parseInt(r.getArgument(0).getID().substring(entityPrefix.length()));
		int secondID = Integer.parseInt(r.getArgument(1).getID().substring(entityPrefix.length()));
		int length = Math.abs(secondID - firstID) - 1; 
    	if (length >= 0) 
    		return new String("RelationEntityLength=" + length);
    	return null;
	}
	
	public void xml(PrintWriter out) {
		out.println("<Extractor>");
		out.println("<type>" + getClass().getName() + "</type>");
		out.println("</Extractor>");
	}
}
