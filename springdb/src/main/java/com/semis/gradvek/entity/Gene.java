package com.semis.gradvek.entity;

/**
 * The immutable object representing a gene from the OpenTargets
 * database
 * 
 * @author ymachkasov
 *
 */
public class Gene extends NamedEntity {
	private String mId;

	public Gene(String id) {
		super(id);
		mId = id;
	}


	public String getId () {
		return mId;
	}


	public void setId (String id) {
		mId = id;
	}


	@Override
	public final String addCommand () {
		return (
			"CREATE (" + getName()
			+ ":Gene"
			+ " {"
			+ super.toString()
			+ ", geneId:\'" + mId
			+ "\'})"
		);
			
	}
}
