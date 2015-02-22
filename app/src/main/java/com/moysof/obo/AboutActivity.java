package com.moysof.obo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.moysof.obo.typeface.TypefaceSpan;

public class AboutActivity extends ActionBarActivity {
	
	// Dima changed something

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);

		SpannableString s = new SpannableString(getResources().getString(
				R.string.title_activity_about));
		s.setSpan(new TypefaceSpan(this, "Comfortaa-Bold.ttf"), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new RelativeSizeSpan(1.2f), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);

		try {
			((TextView) findViewById(R.id.aboutTxt)).setText(String
					.format(getResources().getString(R.string.about_txt),
							getPackageManager().getPackageInfo(
									getPackageName(), 0).versionName));
		} catch (Exception e) {
			((TextView) findViewById(R.id.aboutTxt)).setText("n\\a");
		}

		SpannableString ss = new SpannableString(getResources().getString(
				R.string.about_link_txt));
		LinkSpan clickableSpanPrivacy = new LinkSpan() {
			@Override
			public void onClick(View textView) {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse(getResources().getString(
								R.string.about_link_url))));
			}
		};

		ss.setSpan(clickableSpanPrivacy, 0,
				getResources().getString(R.string.about_link_txt).length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		TextView linkTxt = (TextView) findViewById(R.id.aboutLinkTxt);
		linkTxt.setText(ss);
		linkTxt.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return false;
	}

}