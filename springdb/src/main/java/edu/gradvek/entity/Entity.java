package edu.gradvek.entity;

public abstract class Entity {
	
	public static Entity parse (String json) {
		return (null);
	}

	public final String toCommand () {
		return (toString ());
	}
	
	public abstract String getType ();
}
