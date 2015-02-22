package com.moysof.obo.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextRobotoLight extends TextView {

	public TextRobotoLight(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TextRobotoLight(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TextRobotoLight(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
					"fonts/Roboto-Light.ttf");
			setTypeface(tf);
		}
	}

}