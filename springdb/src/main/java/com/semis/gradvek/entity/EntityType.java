package com.semis.gradvek.entity;

public enum EntityType {
	AdverseEvent, Drug, Gene, Target;

	public static Class<? extends Entity> getEntityClass (EntityType type) {
		Class<? extends Entity> ret = null;
		switch (type) {
			case AdverseEvent:
				ret = com.semis.gradvek.entity.AdverseEvent.class;
			break;
			case Drug:
				ret = com.semis.gradvek.entity.Drug.class;
			break;
			case Gene:
				ret = com.semis.gradvek.entity.Gene.class;
			break;
			case Target:
				ret = com.semis.gradvek.entity.Target.class;
			break;
		}

		return ret;
	}
}
