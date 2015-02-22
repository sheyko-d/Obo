package com.moysof.obo;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

class LinkSpan extends ClickableSpan {// extend ClickableSpan

	String clicked;

	public LinkSpan() {
		// TODO Auto-generated constructor stub
		super();
	}

	@Override
	public void onClick(View tv) {
	}

	@Override
	public void updateDrawState(TextPaint ds) {// override updateDrawState
		ds.setUnderlineText(false); // set to false to remove underline
	}
}