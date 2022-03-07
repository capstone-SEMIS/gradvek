package edu.gradvek.entity;

public class Target extends NamedEntity {
	
	private final String mId;

	public Target(String name, String id) {
		super(name);
		mId = id;
	}

	@Override
	public String getType() {
		return ("Target");
	}

	@Override
	public final String toString () {
		return (
			"(" + getName()
			+ ":" + getType()
			+ " {targetId:\'" + mId
			+ "\'})"
		);
			
	}
}
