package com.semis.gradvek.entity;

/**
 * The list of types of entities supported by the Gradvek database
 * @author ymachkasov
 *
 */
public enum EntityType implements Constants {
	AdverseEvent(AdverseEvent.class, ADVERSE_EVENT_ID_STRING),
	Drug(Drug.class, DRUG_ID_STRING),
	Gene(Gene.class, GENE_ID_STRING),
	Target(Target.class, TARGET_ID_STRING),
	Pathway(Pathway.class, PATHWAY_ID_STRING),
	AssociatedWith(AssociatedWith.class, null),
	MechanismOfAction(MechanismOfAction.class, null),
	Participates(Participates.class, null),
	Involves(Involves.class, null),
	Dataset(Dataset.class, "dataset"),
	Action(Action.class, null),
	MousePhenotype(MousePhenotype.class, MOUSE_PHENOTYPE_ID);


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

	public static EntityType fromIndex(String index) {
		for (EntityType entityType : EntityType.values()) {
			if (index.equalsIgnoreCase(entityType.getIndexField())) {
				return entityType;
			}
		}
		return null;
	}
	
	public static EntityType fromEntityClass (Class<? extends Entity> c) {
		for (EntityType type: EntityType.values ()) {
			if (c.equals (type.getEntityClass ())) {
				return type;
			}
		}
		
		return null;
	}
	/**
	 * Indicates whether entities of this type can be created in batch mode
	 * (that is, they are not dependent on uniqueness and other Cypher variables)
	 * @return if returns true, the entity is batchable
	 */
	public boolean canCombine () {
		return (
				!Edge.class.isAssignableFrom (mClass) 
				&& !Edges.class.isAssignableFrom (mClass)
				&& !Dataset.class.isAssignableFrom (mClass)
		);
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
			case MousePhenotype:
			case Pathway:
			case Dataset:
				ret = "MATCH (n:" + type.toString () + ") RETURN COUNT (n)";
			break;

			case AssociatedWith:
				ret = "MATCH (:Drug)-[n]->(:AdverseEvent) RETURN COUNT (n)";
			break;
			
			case MechanismOfAction:
			case Action:
				ret = "MATCH (:Drug)-[n]->(:Target) RETURN COUNT (n)";
			break;
			
			case Involves:
				ret = "MATCH (:Target)-[n]->(:Gene) RETURN COUNT (n)";
			break;
			
			case Participates:
				ret = "MATCH (:Target)-[n]->(:Pathway) RETURN COUNT (n)";

			break;
		}
		
		return (ret);
	}
}
