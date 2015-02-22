package com.moysof.obo;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.moysof.obo.typeface.TypefaceSpan;

public class PolicyActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_policy);

		overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

		Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setIcon(R.drawable.ic_back);

		SpannableString s = new SpannableString(getResources().getString(
				R.string.title_activity_policy));
		s.setSpan(new TypefaceSpan(this, "Comfortaa-Bold.ttf"), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new RelativeSizeSpan(1.2f), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);

		if (Obo.isNetworkConnected()) {
			final WebView webview = (WebView) findViewById(R.id.policyWebview);
			webview.setWebViewClient(new WebViewClient() {

				@Override
				public void onPageFinished(WebView view, String url) {
					findViewById(R.id.policyProgressBar).setVisibility(
							View.GONE);
					webview.setVisibility(View.VISIBLE);
				}
			});
			webview.loadUrl("http://obo.moyersoftware.com/app/policy.php");
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.exit_to_right, R.anim.enter_from_left);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		overridePendingTransition(R.anim.exit_to_right, R.anim.enter_from_left);
		return super.onOptionsItemSelected(item);
	}
}
