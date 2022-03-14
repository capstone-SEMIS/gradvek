package com.semis.gradvek.entity;

public class AdverseEvent extends NamedEntity {
	private String mId;
	private String mMeddraCode;
	
	public AdverseEvent () {
		// Factory
	}
	
	public AdverseEvent (String name, String id, String code) {
		super (name);
		mId = id;
		mMeddraCode = code;
	}
	
	@Override
	public final String getType () {
		return ("AdverseEvent");
	}
	
	public String getId () {
		return mId;
	}

	public void setId (String id) {
		mId = id;
	}

	public String getMeddraCode () {
		return mMeddraCode;
	}

	public void setMeddraCode (String meddraCode) {
		mMeddraCode = meddraCode;
	}

	@Override
	public final String toCommand () {
		return (
			"(" + getName() + ":" + getType()  
			+ " {"
			+ super.toString()
			+ "adverseEventId: \'" + mId
			+ "\', meddraCode: \'" + mMeddraCode
			+ "\'})"
			
		);
	}

}
