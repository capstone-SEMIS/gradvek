package com.semis.gradvek.entity;

public enum EntityType {
	AdverseEvent (AdverseEvent.class, null), 
	Drug (Drug.class, "chembl_code"), 
	Gene (Gene.class, null), 
	Target (Target.class, null), 
	Disease (Disease.class, null), 
	Causes (Causes.class, null);
	
	private final Class<? extends Entity> mClass;
	private final String mIndexField;
	
	private EntityType (
			Class<? extends Entity> typeClass,
			String indexField)
	{
		mClass = typeClass;
		mIndexField = indexField;
	}
	
	public Class<? extends Entity> getEntityClass () {
		return (mClass);
	}
	
	public static String toCountString (EntityType type) {
		String ret = "";
		switch (type) {
			case AdverseEvent:
			case Drug:
			case Gene:
			case Target:
			case Disease:
				ret = "MATCH (n:" + type.toString () + ") RETURN COUNT (n)";
			break;

			case Causes:
				ret = "MATCH (:Drug)-[n]->(:AdverseEvent) RETURN count(n)";
			break;
		}
		
		return (ret);
	}
	
	public String getIndexField () {
		return (mIndexField);
	}
}
