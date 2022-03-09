package com.semis.gradvek.entity;

public abstract class NamedEntity extends Entity {
	private final String mName;
	
	protected NamedEntity (String name) {
		mName = name;
	}
	
	public String getName() {
		return mName;
	}
	
	@Override
	public String toString( ) {
		return ("name:\'" + getName());
	}

}
