package com.semis.gradvek.entity;

public class Drug extends NamedEntity {

	private final String mChemblCode;
	
	public Drug (String name, String code) {
		super (name);
		mChemblCode = code;
	}

	@Override
	public String getType() {
		return ("Drug");
	}
	
	@Override
	public final String toString () {
		return (
			"(" + getName()
			+ ":" + getType()
			+ " {drugId:\'" + getName()
			+ "\', chembl_code: \'" + mChemblCode
			+ "\'})"
		);
			
	}
}
