package com.moysof.obo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.moysof.obo.adapter.MainDrawerAdapter;
import com.moysof.obo.typeface.TypefaceSpan;

public class SettingsActivity extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private GridView mDrawerGrid;
	private MainDrawerAdapter mDrawerAdapter;
	private EditText mEditTxt;
	private SharedPreferences mPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
		setSupportActionBar(toolbar);

		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

		ActionBar actionBar = getSupportActionBar();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.string.app_name, R.string.app_name) {

			/** Called when a drawer has settled in a completely closed state. */
			@Override
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			/** Called when a drawer has settled in a completely open state. */
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);

		SpannableString s = new SpannableString(getResources().getString(
				R.string.title_activity_settings));
		s.setSpan(new TypefaceSpan(this, "Comfortaa-Bold.ttf"), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new RelativeSizeSpan(1.3f), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);

		mDrawerGrid = (GridView) findViewById(R.id.left_drawer);
		mDrawerAdapter = new MainDrawerAdapter(6, this, mDrawerGrid,
				mDrawerLayout);
		mDrawerGrid.setAdapter(mDrawerAdapter);

		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mEditTxt = (EditText) findViewById(R.id.settingsEditTxt);
		mEditTxt.setText(mPrefs.getInt("radius", 30) + "");
		mEditTxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!s.toString().equals("")) {
					mPrefs.edit()
							.putInt("radius", Integer.parseInt(s.toString()))
							.commit();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		Log.d("Log", ""
				+ PreferenceManager.getDefaultSharedPreferences(this)
						.getBoolean("wantedNotification", true));

		if (PreferenceManager
				.getDefaultSharedPreferences(SettingsActivity.this).getBoolean(
						"wantedNotification", true)) {
			((RadioGroup) findViewById(R.id.settingsRadioGroup))
					.check(R.id.settingsRadioYes);
		} else {
			((RadioGroup) findViewById(R.id.settingsRadioGroup))
					.check(R.id.settingsRadioNo);
		}
		((RadioGroup) findViewById(R.id.settingsRadioGroup))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if (checkedId == R.id.settingsRadioYes) {
							PreferenceManager
									.getDefaultSharedPreferences(
											SettingsActivity.this).edit()
									.putBoolean("wantedNotification", true)
									.commit();
						} else {
							PreferenceManager
									.getDefaultSharedPreferences(
											SettingsActivity.this).edit()
									.putBoolean("wantedNotification", false)
									.commit();
						}
					}
				});

		if (PreferenceManager
				.getDefaultSharedPreferences(SettingsActivity.this).getBoolean(
						"msgNotification", true)) {
			((RadioGroup) findViewById(R.id.settingsRadioGroupMsg))
					.check(R.id.settingsRadioMsgYes);
		} else {
			((RadioGroup) findViewById(R.id.settingsRadioGroupMsg))
					.check(R.id.settingsRadioMsgNo);
		}
		((RadioGroup) findViewById(R.id.settingsRadioGroupMsg))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if (checkedId == R.id.settingsRadioMsgYes) {
							PreferenceManager
									.getDefaultSharedPreferences(
											SettingsActivity.this).edit()
									.putBoolean("msgNotification", true)
									.commit();
						} else {
							PreferenceManager
									.getDefaultSharedPreferences(
											SettingsActivity.this).edit()
									.putBoolean("msgNotification", false)
									.commit();
						}
					}
				});
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void about(View v) {
		startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, MainActivity.class));
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

}
