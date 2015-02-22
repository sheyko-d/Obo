package com.moysof.obo.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextRobotoThinItalic extends TextView {

	public TextRobotoThinItalic(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TextRobotoThinItalic(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TextRobotoThinItalic(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
					"fonts/Roboto-ThinItalic.ttf");
			setTypeface(tf);
		}
	}

}