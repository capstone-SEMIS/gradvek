package com.semis.gradvek.entity;

import java.util.Map;

/**
 * Base class for an immutable object to represent a relationship between
 * two entities in the Neo4j database (an edge on the graph)
 * @author ymachkasov
 *
 */

public abstract class Edge extends Entity {

	/**
	 * the identifier for the source of the relationship
	 */
	private final String mFrom;
	
	/**
	 * the identifier for the target of the relationship
	 */
	private final String mTo;
	
	/**
	 * The list of parameters which are the property of the relationship
	 */
	private final Map<String, String> mParams;

	public Edge (String from, String to, Map<String, String> params) {
		mFrom = from;
		mTo = to;
		mParams = params;
	}

	public String getFrom () {
		return mFrom;
	}

	public String getTo () {
		return mTo;
	}

	public Map<String, String> getParams () {
		return mParams;
	}
}
