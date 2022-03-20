package com.semis.gradvek.entity;

import org.apache.parquet.example.data.simple.SimpleGroup;

public abstract class Entity {
	
	public abstract String addCommand ();
	
	public abstract String getType ();
	
	public boolean filter (SimpleGroup data) {
		return (true);
	}
	
	public boolean canCombine () {
		return (true);
	}
	
}
