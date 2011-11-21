package nlp.core;

import nlp.Globals;

/**
 * Represents a BinaryRelation between two Entity items.
 * 
 * @author ksmall
 */

public class BinaryRelation extends Relation {

	/**
	 * The default constructor.
	 */
	public BinaryRelation() {
		super();
	}
	
	/**
	 * The full constructor.
	 * 
	 * @param id	the id of this BinaryRelation
	 * @param label	the label associated with this BinaryRelation
	 * @param e0	the first Entity
	 * @param e1	the second Entity
	 */
	public BinaryRelation(String id, String label, Entity e0, Entity e1) {
		super(id, label);
		addEntity(e0);
		addEntity(e1);
	}
	
	/**
	 * The "no relation" constructor.  Since most BinaryRelation instances will actually
	 * indicate that there is no relation between the entities, this constructor automatically
	 * handles this case.  Note that the label can be changed later.
	 * 
	 * @param e0	the first Entity
	 * @param e1	the second Entity
	 */
	public BinaryRelation(Entity e0, Entity e1) {
		this(Globals.NO_RELATION_ID, Globals.NO_RELATION_LABEL, e0, e1);
	}

	/**
	 * The number of Token items between the two Entity instances that comprise
	 * this BinaryRelation
	 * 
	 * @return	the distance (in Tokens) between the two Entities
	 */
	public int gapLength() {
		Entity a0 = getArgument(0);
		Entity a1 = getArgument(1);
		int startA0 = Integer.parseInt(a0.getProperty("start"));
		int endA0 = Integer.parseInt(a0.getProperty("end"));
		int startA1 = Integer.parseInt(a1.getProperty("start"));
		int endA1 = Integer.parseInt(a1.getProperty("end"));
		if (startA0 < startA1)
			return startA1 - endA0 - 1;
		else
			return startA0 - endA1 - 1;
	}

	public BinaryRelation copy() {
		return (BinaryRelation) super.copy();
	}
	
	public BinaryRelation deepCopy() {
		return (BinaryRelation) super.deepCopy();
	}
}
