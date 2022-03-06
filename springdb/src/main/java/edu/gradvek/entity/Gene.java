package edu.gradvek.entity;

public class Gene extends NamedEntity {
	private final String mId;

	public Gene(String name, String id) {
		super(name);
		mId = id;
	}


	@Override
	public String getType() {
		return ("Gene");
	}

}
