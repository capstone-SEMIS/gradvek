package com.semis.gradvek.entity;

import org.apache.parquet.example.data.simple.SimpleGroup;

public class AdverseEvent extends NamedEntity {
	private String mId;
	private String mMeddraCode;
	
	public AdverseEvent (String name, String id, String code) {
		super (name);
		mId = id;
		mMeddraCode = code;
	}
	
	public AdverseEvent(SimpleGroup data) {
		super(data.getString ("name", 0));
		mId = data.getString ("id", 0);
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
	public final String addCommand () {
		return (
			"CREATE (" + getName() + ":" + getType()  
			+ " {"
			+ super.toString()
			+ "adverseEventId: \'" + mId
			+ "\', meddraCode: \'" + mMeddraCode
			+ "\'})"
			
		);
	}

}
