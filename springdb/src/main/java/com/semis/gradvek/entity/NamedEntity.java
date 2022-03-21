package com.semis.gradvek.entity;

/**
 * The base class for all entities possessing a name 
 * @author ymachkasov
 *
 */
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
