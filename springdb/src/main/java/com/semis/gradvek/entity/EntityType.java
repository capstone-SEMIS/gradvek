package com.semis.gradvek.entity;

/**
 * The list of types of entities supported by the Gradvek database
 * @author ymachkasov
 *
 */
public enum EntityType {
	AdverseEvent (AdverseEvent.class, "meddraCode"), 
	Drug (Drug.class, "chembl_code"), 
	Gene (Gene.class, null), 
	Target (Target.class, "targetId"), 
	AssociatedWith (AssociatedWith.class, null),
	MechanismOfAction (MechanismOfAction.class, null);
	
	/**
	 * The class of the corresponding entity (a subclass of Entity)
	 */
	private final Class<? extends Entity> mClass;
	
	/**
	 * The optional name of the field by which the entities of this type
	 * should be indexed in the database
	 */
	private final String mIndexField;
	
	private EntityType (
			Class<? extends Entity> typeClass,
			String indexField)
	{
		mClass = typeClass;
		mIndexField = indexField;
	}
	
	public String getIndexField () {
		return (mIndexField);
	}
	
	public Class<? extends Entity> getEntityClass () {
		return (mClass);
	}
	
	/**
	 * Indicates whether entities of this type can be created in batch mode
	 * (that is, they are not dependent on uniqueness and other Cypher variables)
	 * @return if returns true, the entity is batchable
	 */
	public boolean canCombine () {
		return (!Edge.class.isAssignableFrom (mClass) && !Edges.class.isAssignableFrom (mClass));
	}
	
	/**
	 * The string representing a Cypher command to count all entities of this type
	 * @param type
	 * @return
	 */
	public static String toCountString (EntityType type) {
		String ret = "";
		switch (type) {
			case AdverseEvent:
			case Drug:
			case Gene:
			case Target:
				ret = "MATCH (n:" + type.toString () + ") RETURN COUNT (n)";
			break;

			case AssociatedWith:
				ret = "MATCH (:Drug)-[n]->(:AdverseEvent) RETURN COUNT (n)";
			break;
			
			case MechanismOfAction:
				ret = "MATCH (:Drug)-[n]->(:Target) RETURN COUNT (n)";
		}
		
		return (ret);
	}
}
