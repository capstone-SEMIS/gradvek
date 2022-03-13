package com.semis.gradvek.entity;

public class Drug extends NamedEntity {

	private String mChemblCode;
	
	public Drug () {
		// Factory
	}
	
	public Drug (String name, String code) {
		super (name);
		mChemblCode = code;
	}

	@Override
	public String getType() {
		return ("Drug");
	}
	
	public String getChemblCode () {
		return mChemblCode;
	}

	public void setChemblCode (String chemblCode) {
		mChemblCode = chemblCode;
	}

	@Override
	public final String toCommand () {
		return (
			"(" + getName()
			+ ":" + getType()
			+ " {"
			+ super.toString()
			+ "drugId:\'" + getName()
			+ "\', chembl_code: \'" + mChemblCode
			+ "\'" 
			+ "})"
		);
			
	}
}
