package com.semis.gradvek.entity;

import java.util.Map;
import java.util.List;

public abstract class Edges extends Entity {

	/**
	 * the identifier for the source of the relationship
	 */
	private final List<String> mFrom;
	
	/**
	 * the identifier for the target of the relationship
	 */
	private final List<String> mTo;
	
	/**
	 * The list of parameters which are the property of the relationship
	 */
	private final Map<String, String> mParams;

	public Edges (List<String> from, List<String> to, Map<String, String> params) {
		mFrom = from;
		mTo = to;
		mParams = params;
	}

	public List<String> getFrom () {
		return mFrom;
	}

	public List<String> getTo () {
		return mTo;
	}

	public Map<String, String> getParams () {
		return mParams;
	}
}
