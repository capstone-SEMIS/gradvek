package com.semis.gradvek.entity;

import org.apache.commons.text.StringEscapeUtils;

public abstract class NamedEntity extends Entity {
	private String mName;
	
	protected NamedEntity () {
		// Factory
	}
	
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

	public static void main (String [] args) {
		System.out.println (StringEscapeUtils.escapeEcmaScript ("RNA guanylyltransferase and 5'-phosphatase"));
	}
}
