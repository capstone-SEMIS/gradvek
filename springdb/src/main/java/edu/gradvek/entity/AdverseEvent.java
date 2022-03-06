package edu.gradvek.entity;

public class AdverseEvent extends NamedEntity {
	private final String mId;
	private final String mMeddraCode;
	
	public AdverseEvent (String name, String id, String code) {
		super (name);
		mId = id;
		mMeddraCode = code;
	}
	
	@Override
	public final String getType () {
		return ("AdverseEvent");
	}
	
	@Override
	public final String toString () {
		return (
			"(" + getName() + ":" + getType()  
			+ " {adverseEventId: \'" + mId
			+ "\', meddraCode: \'" + mMeddraCode
			+ "\'})"
			
		);
	}

}
