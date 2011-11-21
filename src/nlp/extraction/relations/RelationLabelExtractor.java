package nlp.extraction.relations;

import java.util.Iterator;


import nlp.core.Relation;
import nlp.extraction.PropertyExtractor;
import nlp.extraction.filter.TextFilter;

// going to have to add a filter to eliminate unwanted labels
public class RelationLabelExtractor extends PropertyExtractor {

	protected boolean directional;  // determines if relation direction is signified
	protected String entityPrefix;
	
	public RelationLabelExtractor(boolean directional, String entityPrefix) {
		super("label");   // being a bit presumptuous
		this.directional = directional;
		this.entityPrefix = entityPrefix;
	}

	public RelationLabelExtractor() {
		this(true, "e");
	}
	
	// just decides directionality by entity index
	// have to fix if the number of entities is greater than 9
	public String key(Object o, Object target) {
		Relation r = (Relation) o;	// could force this more elegantly (but unnecessary)

    	String value = r.getProperty(property);
    	if (filters != null) {
    		for (Iterator<TextFilter> it = filters.iterator(); it.hasNext(); )
    			value = ((TextFilter) it.next()).filter(value);
    	}
    	
    	String result = null;
    	if (value != null) {
    		result = new String(property);
    		if (printFilters && (filterString.length() > 0))
    			result += "{" + filterString + "}";
    		result += "=" + value;
    		if (directional) {
    			String arg0 = r.getArgument(0).getID();
    			String arg1 = r.getArgument(1).getID();
    			int id0 = Integer.parseInt(arg0.substring(entityPrefix.length()));
    			int id1 = Integer.parseInt(arg1.substring(entityPrefix.length()));
    			if (id0 < id1)
    				result += "(0,1)";
    			else
    				result += "(1,0)";
    		}
    	}
    	return result;
    }
}
