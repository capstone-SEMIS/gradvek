package com.semis.gradvek.entity;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.parquet.example.data.simple.SimpleGroup;

public class Drug extends NamedEntity {

	private String mChemblCode;
	
	public Drug () {
		// Factory
	}
	
	public Drug (String name, String code) {
		super (name);
		mChemblCode = code;
	}

	public Drug(SimpleGroup data) {
		super(data.getString ("name", 0));
		mChemblCode = data.getString ("id", 0);
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
		return ("(:" + getType () + " {" + "name:\'" + StringEscapeUtils.escapeEcmaScript (super.toString ()) + "\', "
				+ "id:\'" + StringEscapeUtils.escapeEcmaScript (mChemblCode) + "\'})");
	}
	
	@Override
	public boolean filter (SimpleGroup data) {
		return (true);
	}
	
}
