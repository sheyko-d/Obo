package com.moysof.obo;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.moysof.obo.adapter.PhotoViewPagerAdapter;

public class FullscreenImageActivity extends ActionBarActivity {

	private ViewPager mPhotoViewPager;
	private PhotoViewPagerAdapter mPhotoPagerAdapter;
	private JSONArray mPhotosJSON;
	private int mPhotoPos;
	private ActionBar mActionBar;
	private Toolbar mToolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

		setContentView(R.layout.activity_fullscreen_image);

		mToolbar = (Toolbar) findViewById(R.id.mytoolbar);
		setSupportActionBar(mToolbar);

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);

		try {
			mPhotosJSON = new JSONArray(getIntent().getStringExtra("photos"));
		} catch (JSONException e) {
			mPhotosJSON = new JSONArray();
		}

		mPhotoPos = getIntent().getIntExtra("pos", 0);

		mActionBar.setTitle((mPhotoPos + 1) + " of " + mPhotosJSON.length());

		mPhotoViewPager = (ViewPager) findViewById(R.id.productPhotoViewPager);
		mPhotoPagerAdapter = new PhotoViewPagerAdapter(
				getSupportFragmentManager(), mPhotosJSON);
		mPhotoViewPager.setAdapter(mPhotoPagerAdapter);

		mPhotoViewPager.setCurrentItem(mPhotoPos);

		mPhotoViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int pos) {
				mActionBar.setTitle((pos + 1) + " of " + mPhotosJSON.length());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
