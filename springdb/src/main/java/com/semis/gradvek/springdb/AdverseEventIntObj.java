package com.semis.gradvek.springdb;

import com.semis.gradvek.entity.AdverseEvent;

public class AdverseEventIntObj extends AdverseEvent {

	private double mLlr;

	public double getLlr () {
		return mLlr;
	}

	public void setLlr (double llr) {
		mLlr = llr;
	}

	public AdverseEventIntObj (String name, String id, String code) {
		super (name, id, code);
	}

	public AdverseEventIntObj (AdverseEvent e, String llr) {
		super (e.getName (), e.getId (), e.getMeddraCode ());
		mLlr = Double.valueOf (llr);
	}
}
