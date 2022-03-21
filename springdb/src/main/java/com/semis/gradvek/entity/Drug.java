package com.semis.gradvek.entity;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.parquet.example.data.simple.SimpleGroup;

public class Drug extends NamedEntity {

	private String mChemblCode;
	
	public Drug (String name, String code) {
		super (name);
		mChemblCode = code;
	}

	public Drug(SimpleGroup data) {
		super(data.getString ("name", 0));
		mChemblCode = data.getString ("id", 0);
	}

	public String getChemblCode () {
		return mChemblCode;
	}

	public void setChemblCode (String chemblCode) {
		mChemblCode = chemblCode;
	}

	@Override
	public final String addCommand () {
		return ("CREATE (:Drug" + " {" + "drugId:\'" + StringEscapeUtils.escapeEcmaScript (super.toString ()) + "\', "
				+ "chembl_code:\'" + StringEscapeUtils.escapeEcmaScript (mChemblCode) + "\'})");
	}
	
	@Override
	public boolean filter (SimpleGroup data) {
		return (true);
	}
	
}
