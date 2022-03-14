package com.semis.gradvek.entity;

public class Gene extends NamedEntity {
	private String mId;

	public Gene(String id) {
		super(id);
		mId = id;
	}


	@Override
	public String getType() {
		return ("Gene");
	}

	public String getId () {
		return mId;
	}


	public void setId (String id) {
		mId = id;
	}


	@Override
	public final String toCommand () {
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
