package com.semis.gradvek.entity;

public class Gene extends NamedEntity {
	private final String mId;

	public Gene(String id) {
		super(id);
		mId = id;
	}


	@Override
	public String getType() {
		return ("Gene");
	}

	@Override
	public final String toString () {
		return (
			"(" + getName()
			+ ":" + getType()
			+ " {"
			+ super.toString()
			+ ", geneId:\'" + mId
			+ "\'})"
		);
			
	}
}
