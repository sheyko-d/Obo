package com.moysof.obo.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextRobotoBold extends TextView {

	public TextRobotoBold(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TextRobotoBold(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TextRobotoBold(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
					"fonts/Roboto-Bold.ttf");
			setTypeface(tf);
		}
	}

}