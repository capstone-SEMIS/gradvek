package com.semis.gradvek.entity;

import java.util.Map;
import java.util.stream.Collectors;

public abstract class Edge extends Entity {

	private String mFrom;
	private String mTo;
	private Map<String, String> mParams;

	public Edge (String from, String to, Map<String, String> params) {
		mFrom = from;
		mTo = to;
		mParams = params;
	}

	@Override
	public abstract String getType ();

	public String getFrom () {
		return mFrom;
	}

	public void setFrom (String from) {
		mFrom = from;
	}

	public String getTo () {
		return mTo;
	}

	public void setTo (String to) {
		mTo = to;
	}

	public Map<String, String> getParams () {
		return mParams;
	}

	public void setParams (Map<String, String> params) {
		mParams = params;
	}

//	@Override
//	public String addCommand () {
//		final StringBuilder sb = new StringBuilder ();
//
//		sb.append ('(').append (mFrom).append (')');
//		sb.append ('-');
//		sb.append ("[:").append (getType ());
//		if (mParams != null && mParams.size () > 0) {
//			String paramString = mParams.entrySet ().stream ().map (e -> e.getKey () + ":" + e.getValue ())
//					.collect (Collectors.joining (","));
//			sb.append (" {").append (paramString).append ("}");
//		}
//		sb.append (']');
//		sb.append ("->");
//		sb.append ('(').append (mTo).append (')');
//		return (sb.toString ());
//	}
//
}
