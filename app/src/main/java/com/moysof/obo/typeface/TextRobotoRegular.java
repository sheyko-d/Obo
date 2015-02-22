package com.moysof.obo.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextRobotoRegular extends TextView {

	public TextRobotoRegular(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TextRobotoRegular(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TextRobotoRegular(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
					"fonts/Roboto-Regular.ttf");
			setTypeface(tf);
		}
	}

}