package com.moysof.obo.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextRobotoThin extends TextView {

	public TextRobotoThin(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TextRobotoThin(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TextRobotoThin(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
					"fonts/Roboto-Thin.ttf");
			setTypeface(tf);
		}
	}

}