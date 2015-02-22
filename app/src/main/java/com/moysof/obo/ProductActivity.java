package com.moysof.obo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.MenuItem;

import com.moysof.obo.adapter.ProductViewPagerAdapter;
import com.moysof.obo.typeface.TypefaceSpan;

public class ProductActivity extends ActionBarActivity {

	private String mTitle;
	private JSONArray mItemsJSON;
	private int mPos;
	private String mCategory;
	private ViewPager mViewPager;
	private ProductViewPagerAdapter mPagerAdapter;

	private ArrayList<String> mCategoriesArrayList = new ArrayList<String>();
	private ArrayList<String> mIdsArrayList = new ArrayList<String>();
	private ArrayList<String> mContactOptionsArrayList = new ArrayList<String>();
	private ArrayList<String> mTitlesArrayList = new ArrayList<String>();
	private ArrayList<String> mPricesArrayList = new ArrayList<String>();
	private ArrayList<String> mLocationsArrayList = new ArrayList<String>();
	private ArrayList<String> mDistancesArrayList = new ArrayList<String>();
	private ArrayList<Long> mTimestampsArrayList = new ArrayList<Long>();
	private ArrayList<String> mDescsArrayList = new ArrayList<String>();
	private ArrayList<Integer> mPhotoNumsArrayList = new ArrayList<Integer>();
	private ArrayList<String> mPhotosArrayList = new ArrayList<String>();
	private int mSkippedItems = 0;
	private String mCategoryId;
	private String id;
	public static boolean mine = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
		setSupportActionBar(toolbar);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);

		mine = false;

		mViewPager = (ViewPager) findViewById(R.id.productViewPager);
		mPagerAdapter = new ProductViewPagerAdapter(
				getSupportFragmentManager(), mCategoriesArrayList,
				mIdsArrayList, mContactOptionsArrayList, mTitlesArrayList,
				mPricesArrayList, mLocationsArrayList, mDistancesArrayList,
				mTimestampsArrayList, mDescsArrayList, mPhotoNumsArrayList,
				mPhotosArrayList);
		mViewPager.setAdapter(mPagerAdapter);

		onNewIntent(getIntent());
	}

	@Override
	public void onNewIntent(Intent intent) {
		Log("onNewIntent");

		Log("onNewIntent pos = " + intent.getIntExtra("pos", -1));

		Bundle extras = intent.getExtras();
		if (extras != null) {
			try {
				mItemsJSON = new JSONArray(intent.getStringExtra("json"));
				mPos = intent.getIntExtra("pos", -1);
				Log("new intent = " + mItemsJSON + ", " + mPos);

				mCategoryId = mItemsJSON.getJSONObject(mPos).getString(
						"category_id");
				mCategory = mItemsJSON.getJSONObject(mPos)
						.getString("category");
				id = mItemsJSON.getJSONObject(mPos).getString("id");

				if (mItemsJSON
						.getJSONObject(mPos)
						.getString("author_id")
						.equals(PreferenceManager.getDefaultSharedPreferences(
								this).getString("id", ""))) {
					mine = true;
				}
			} catch (JSONException e) {
				Log("product activity ex = " + e);
				mItemsJSON = new JSONArray();
				mPos = -1;
			}
			SpannableString s = new SpannableString(
					mCategory.toLowerCase(Locale.getDefault()));
			s.setSpan(new TypefaceSpan(this, "Comfortaa-Bold.ttf"), 0,
					s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			s.setSpan(new RelativeSizeSpan(1.3f), 0, s.length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			getSupportActionBar().setTitle(s);

			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			Set<String> seenIds = prefs.getStringSet("seen_ids",
					new HashSet<String>());
			if (!seenIds.contains(id)) {
				seenIds.add(id);
				prefs.edit().putStringSet("seen_ids", seenIds).commit();
				new AddViewTask().execute();
			}
			for (int i = 0; i < mItemsJSON.length(); i++) {
				try {
					if (mCategoryId.equals(mItemsJSON.getJSONObject(i)
							.getString("category_id"))) {
						mCategoriesArrayList.add(mItemsJSON.getJSONObject(i)
								.getString("category"));
						mIdsArrayList.add(mItemsJSON.getJSONObject(i)
								.getString("id"));
						mContactOptionsArrayList.add(mItemsJSON
								.getJSONObject(i).getString("contact_choices"));// JSON
																				// Object
						mTitlesArrayList.add(mItemsJSON.getJSONObject(i)
								.getString("title"));
						mPricesArrayList.add(mItemsJSON.getJSONObject(i)
								.getString("price"));
						mLocationsArrayList.add(mItemsJSON.getJSONObject(i)
								.getString("location"));
						mDistancesArrayList.add(mItemsJSON.getJSONObject(i)
								.getString("distance"));
						mTimestampsArrayList.add(mItemsJSON.getJSONObject(i)
								.getLong("timestamp"));
						mDescsArrayList.add(mItemsJSON.getJSONObject(i)
								.getString("desc"));
						mPhotoNumsArrayList.add(new JSONObject(mItemsJSON
								.getJSONObject(i).getString("photos"))
								.getInt("main_num"));
						mPhotosArrayList.add(new JSONObject(mItemsJSON
								.getJSONObject(i).getString("photos"))
								.getString("photos"));// JSONArray
						if (mItemsJSON.getJSONObject(i).getString("id")
								.equals(id)) {
							Log("equals to " + id);
							mPos = mSkippedItems;
						}
						mSkippedItems++;
					}
				} catch (JSONException e) {
					Log(e);
				}
			}

			mViewPager.setCurrentItem(mPos);
			mPagerAdapter.notifyDataSetChanged();
		}
	}

	class AddViewTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... files) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("id", id));
			DefaultHttpClient client = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(Constansts.ADD_VIEW_URL);
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				client.execute(httpPost);
			} catch (ClientProtocolException e) {
				Log(e);
			} catch (IOException e) {
				Log(e);
			}
			return null;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		if (itemId == android.R.id.home) {
			finish();
		}
		return true;
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}
}
