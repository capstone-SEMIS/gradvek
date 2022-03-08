package com.semis.gradvek.entity;

import java.util.Map;
import java.util.stream.Collectors;

public class Edge extends Entity {

	private final String mType;
	private final String mFrom;
	private final String mTo;
	private final Map<String, String> mParams;
	
	public Edge(String type, String from, String to, Map<String, String> params) {
		mType = type;
		mFrom = from;
		mTo = to;
		mParams = params;
	}

	@Override
	public String getType() {
		return (mType);
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder ();
		
		sb.append('(').append(mFrom).append(')');
		sb.append('-');
		sb.append("[:").append(mType);
		if (mParams != null && mParams.size() > 0) {
			String paramString = mParams.entrySet()
					.stream()
					.map(e -> e.getKey() + ":" + e.getValue())
					.collect(Collectors.joining(","));
			sb.append(" {").append(paramString).append("}");
		}
		sb.append(']');
		sb.append("->");
		sb.append('(').append(mTo).append(')');		
		return (sb.toString());
	}

}
