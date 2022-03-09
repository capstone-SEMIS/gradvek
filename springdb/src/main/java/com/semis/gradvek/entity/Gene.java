package com.semis.gradvek.entity;

public class Gene extends NamedEntity {
	private final String mId;

	public Gene(String name, String id) {
		super(name);
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
			+ "geneId:\'" + mId
			+ "\'})"
		);
			
	}
}
