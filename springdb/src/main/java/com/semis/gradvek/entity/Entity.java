package com.semis.gradvek.entity;

import org.apache.parquet.example.data.simple.SimpleGroup;

public abstract class Entity {
	
	public abstract String toCommand ();
	
	public abstract String getType ();
	
	public boolean importParquet (SimpleGroup data) {
		return false;
	}
}
