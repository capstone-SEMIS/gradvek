package com.semis.gradvek.entity;

public abstract class NamedEntity extends Entity {
	private String mName;
	
	protected NamedEntity (String name) {
		mName = name;
	}
	
	public String getName() {
		return mName;
	}
	
	public void setName (String name) {
		mName = name;
	}

	@Override
	public String toString( ) {
		return (getName());
	}
}
