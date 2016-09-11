package com.moysof.obo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.SearchView.SearchAutoComplete;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.moysof.obo.adapter.MainDrawerAdapter;
import com.moysof.obo.adapter.SearchListAdapter;
import com.moysof.obo.adapter.ViewPagerAdapter;
import com.moysof.obo.typeface.TypefaceSpan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

public class SearchActivity extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private GridView mDrawerGrid;
	private MainDrawerAdapter mDrawerAdapter;
	private ViewGroup mTabBarLayout;
	private ListView mListView;
	private SearchListAdapter mAdapter;
	private ActionBar mActionBar;
	private ArrayList<String> mCategoryIdsArrayList = new ArrayList<String>();
	private ArrayList<String> mCategoryTitlesArrayList = new ArrayList<String>();
	private ArrayList<String> mNumbersArrayList = new ArrayList<String>();
	private int mSelectedPos = -1;
	private ArrayList<String> mIdsArrayList = new ArrayList<String>();
	private ArrayList<String> mTitlesArrayList = new ArrayList<String>();
	private ArrayList<Double> mPricesArrayList = new ArrayList<Double>();
	private ArrayList<String> mLocationsArrayList = new ArrayList<String>();
	private ArrayList<String> mPhotosArrayList = new ArrayList<String>();
	private ArrayList<Integer> mPhotoNumsArrayList = new ArrayList<Integer>();
	private ArrayList<String> mDescArrayList = new ArrayList<String>();
	private ArrayList<Long> mTimestampsArrayList = new ArrayList<Long>();
	private ArrayList<String> mDistancesArrayList = new ArrayList<String>();
	private ArrayList<String> mCategoriesArrayList = new ArrayList<String>();
	private ArrayList<String> mCategoriesIdsArrayList = new ArrayList<String>();
	private ArrayList<String> mContactChoicesArrayList = new ArrayList<String>();
	private ViewPager mViewPager;
	private ViewPagerAdapter mPagerAdapter;
	private String mSearchQuery = "";
	private JSONArray mItemsJSON;
	private SharedPreferences mPrefs;
	private JSONArray mHistoryJSONArray;
	private MenuItem mSearchItem;
	private SearchView mSearchView;
	private SearchAutoComplete mSearchAutoComplete;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

		Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
		setSupportActionBar(toolbar);

		mActionBar = getSupportActionBar();
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

		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(false);
		SpannableString s = new SpannableString(getResources().getString(
				R.string.title_activity_search));
		s.setSpan(new TypefaceSpan(this, "Comfortaa-Bold.ttf"), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new RelativeSizeSpan(1.3f), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		mActionBar.setTitle(s);
		mActionBar.setDisplayShowHomeEnabled(false);

		mDrawerGrid = (GridView) findViewById(R.id.left_drawer);
		mDrawerAdapter = new MainDrawerAdapter(1, this, mDrawerGrid,
				mDrawerLayout);
		mDrawerGrid.setAdapter(mDrawerAdapter);

		mTabBarLayout = (ViewGroup) findViewById(R.id.tabBarLayout);

		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		try {
			mHistoryJSONArray = new JSONArray(mPrefs.getString("history_array",
					"[]"));
		} catch (JSONException e) {
			mHistoryJSONArray = new JSONArray();
		}
		if (mHistoryJSONArray.length() == 0) {
			findViewById(R.id.searchListView).setVisibility(View.GONE);
		}

		mListView = (ListView) findViewById(R.id.searchListView);
		mAdapter = new SearchListAdapter(this, mHistoryJSONArray);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mSearchItem.expandActionView();
				try {
					mSearchAutoComplete.setText(mHistoryJSONArray
							.getString(position));
					mSearchQuery = mHistoryJSONArray.getString(position);
					new GetItemsTask().execute();
				} catch (JSONException e) {
					mSearchItem.collapseActionView();
				}
			}
		});

		mViewPager = (ViewPager) findViewById(R.id.searchViewPager);
		mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
				mTitlesArrayList, mPricesArrayList, mLocationsArrayList,
				mPhotosArrayList, mPhotoNumsArrayList, mDistancesArrayList);
		mViewPager.setAdapter(mPagerAdapter);

		if (Obo.isNetworkConnected()) {
			new GetCategoriesTask().execute();
		} else {
			findViewById(R.id.searchViewPager).setVisibility(View.GONE);
			findViewById(R.id.searchProgressBar).setVisibility(View.GONE);
			findViewById(R.id.searchCategoryProgressBar).setVisibility(
					View.GONE);
			findViewById(R.id.searchPlaceholderLayout).setVisibility(
					View.VISIBLE);
		}
	}

	class GetCategoriesTask extends AsyncTask<String, Void, Void> {

		private int errorCode = 0;
		private String mResponseString;
		private JSONArray mItemsJSON;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... files) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				DefaultHttpClient client = new DefaultHttpClient();
				nameValuePairs.add(new BasicNameValuePair("radius",
						PreferenceManager.getDefaultSharedPreferences(
								getApplicationContext()).getInt("radius", 30)
								+ ""));
				nameValuePairs
						.add(new BasicNameValuePair("lat", PreferenceManager
								.getDefaultSharedPreferences(
										getApplicationContext()).getString(
										"lat", "-1")));
				nameValuePairs
						.add(new BasicNameValuePair("lng", PreferenceManager
								.getDefaultSharedPreferences(
										getApplicationContext()).getString(
										"lng", "-1")));
				HttpPost httpPost = new HttpPost(
						Constansts.GET_CATEGORIES_NUMBERS_URL);

				try {
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = client.execute(httpPost);
					mResponseString = EntityUtils
							.toString(response.getEntity());
					Log(mResponseString);
					JSONObject jsonResponse = new JSONObject(mResponseString);
					if (jsonResponse.has("result")) {
						if (jsonResponse.getString("result").equals("success")) {
							mCategoryIdsArrayList.clear();
							mCategoryTitlesArrayList.clear();
							mNumbersArrayList.clear();

							mItemsJSON = new JSONArray(
									jsonResponse.getString("array"));
							for (int i = 0; i < mItemsJSON.length(); i++) {
								mCategoryIdsArrayList.add(mItemsJSON
										.getJSONObject(i).getString("id"));
								mCategoryTitlesArrayList.add(mItemsJSON
										.getJSONObject(i).getString("title"));
								mNumbersArrayList.add(mItemsJSON.getJSONObject(
										i).getString("number"));
							}
							errorCode = -1;
						} else {
							errorCode = 0;
						}
					}
				} catch (ClientProtocolException e) {
					Log(e);
				} catch (IOException e) {
					Log(e);
				}
			} catch (JSONException e) {
				Log(e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			findViewById(R.id.searchCategoryProgressBar).setVisibility(
					View.GONE);
			if (errorCode == -1) {
				for (int i = 0; i < mCategoryTitlesArrayList.size(); i++) {
					View tabView;
					tabView = getLayoutInflater().inflate(R.layout.tab_search,
							null);
					((TextView) tabView.findViewById(R.id.tabTitleTxt))
							.setText(mCategoryTitlesArrayList.get(i));
					((TextView) tabView.findViewById(R.id.tabCountTxt))
							.setText(mNumbersArrayList.get(i));
					if (mNumbersArrayList.get(i).length() > 1) {
						((TextView) tabView.findViewById(R.id.tabCountTxt))
								.setTextSize(10);
					}
					tabView.setTag(i);
					mTabBarLayout.addView(tabView);
				}
			} else if (errorCode == 0) {
				Toast.makeText(SearchActivity.this,
						"Error: Can't get categories", Toast.LENGTH_LONG)
						.show();
				finish();
			}
		}
	}

	class GetItemsTask extends AsyncTask<String, Void, Void> {

		private int errorCode = 0;
		private String mResponseString;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			findViewById(R.id.searchViewPager).setVisibility(View.GONE);
			findViewById(R.id.searchListView).setVisibility(View.GONE);
			findViewById(R.id.searchLayout).setVisibility(View.VISIBLE);
			findViewById(R.id.searchPlaceholderLayout).setVisibility(View.GONE);
			findViewById(R.id.searchProgressBar).setVisibility(View.VISIBLE);

			if (!mSearchQuery.equals("")) {
				mHistoryJSONArray.put(mSearchQuery);
			}
			mPrefs.edit()
					.putString("history_array", mHistoryJSONArray.toString())
					.commit();
			mAdapter.notifyDataSetChanged();
		}

		@Override
		protected Void doInBackground(String... files) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("radius",
						PreferenceManager.getDefaultSharedPreferences(
								getApplicationContext()).getInt("radius", 30)
								+ ""));
				nameValuePairs
						.add(new BasicNameValuePair("lat", PreferenceManager
								.getDefaultSharedPreferences(
										getApplicationContext()).getString(
										"lat", "-1")));
				nameValuePairs
						.add(new BasicNameValuePair("lng", PreferenceManager
								.getDefaultSharedPreferences(
										getApplicationContext()).getString(
										"lng", "-1")));
				nameValuePairs
						.add(new BasicNameValuePair("query", mSearchQuery));
				if (mSelectedPos != -1) {
					nameValuePairs.add(new BasicNameValuePair("category_id",
							mCategoryIdsArrayList.get(mSelectedPos)));
				}
				DefaultHttpClient client = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(Constansts.GET_ITEMS_URL);
				try {
					mIdsArrayList.clear();
					mTitlesArrayList.clear();
					mPricesArrayList.clear();
					mLocationsArrayList.clear();
					mPhotosArrayList.clear();
					mPhotoNumsArrayList.clear();
					mDescArrayList.clear();
					mTimestampsArrayList.clear();
					mDistancesArrayList.clear();
					mCategoriesArrayList.clear();
					mCategoriesIdsArrayList.clear();
					mContactChoicesArrayList.clear();

					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = client.execute(httpPost);
					mResponseString = EntityUtils
							.toString(response.getEntity());
					Log(mResponseString);
					JSONObject jsonResponse = new JSONObject(mResponseString);
					if (jsonResponse.has("result")) {
						if (jsonResponse.getString("result").equals("success")) {

							mItemsJSON = new JSONArray(
									jsonResponse.getString("array"));
							for (int i = 0; i < mItemsJSON.length(); i++) {
								mIdsArrayList.add(mItemsJSON.getJSONObject(i)
										.getString("id"));
								mTitlesArrayList.add(mItemsJSON
										.getJSONObject(i).getString("title"));
								mPricesArrayList.add(mItemsJSON
										.getJSONObject(i).getDouble("price"));
								String location = mItemsJSON.getJSONObject(i)
										.getString("location");
								if (location.length() > 25) {
									location = location.substring(0, 25) + "...";
								}
								mLocationsArrayList.add(location);
								mPhotosArrayList.add(new JSONObject(mItemsJSON
										.getJSONObject(i).getString("photos"))
										.getJSONArray("photos").toString());
								mPhotoNumsArrayList.add(new JSONObject(
										mItemsJSON.getJSONObject(i).getString(
												"photos")).getInt("main_num"));
								mDescArrayList.add(mItemsJSON.getJSONObject(i)
										.getString("desc"));
								mTimestampsArrayList.add(mItemsJSON
										.getJSONObject(i).getLong("timestamp"));
								mDistancesArrayList
										.add(mItemsJSON.getJSONObject(i)
												.getString("distance"));
								mCategoriesArrayList
										.add(mItemsJSON.getJSONObject(i)
												.getString("category"));
								mCategoriesIdsArrayList.add(mItemsJSON
										.getJSONObject(i).getString(
												"category_id"));
								mContactChoicesArrayList.add(mItemsJSON
										.getJSONObject(i).getString(
												"contact_choices"));
							}

							errorCode = -1;
						} else if (jsonResponse.getString("result").equals(
								"empty")) {
							errorCode = 1;
						} else {
							errorCode = 0;
						}
					}
				} catch (ClientProtocolException e) {
					Log(e);
				} catch (IOException e) {
					Log(e);
				}
			} catch (JSONException e) {
				Log(e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mPagerAdapter.notifyDataSetChanged();
			if (errorCode == -1) {
			} else if (errorCode == 0) {
				Toast.makeText(SearchActivity.this,
						"Unknown error: " + mResponseString, Toast.LENGTH_LONG)
						.show();
			} else if (errorCode == 1) {
				Toast.makeText(SearchActivity.this,
						"Error: Some fields are empty", Toast.LENGTH_LONG)
						.show();
			}

			if (mIdsArrayList.size() == 0) {
				findViewById(R.id.searchPlaceholderLayout).setVisibility(
						View.VISIBLE);
				findViewById(R.id.searchViewPager).setVisibility(View.GONE);
			} else {
				findViewById(R.id.searchPlaceholderLayout).setVisibility(
						View.GONE);
				findViewById(R.id.searchViewPager).setVisibility(View.VISIBLE);
			}

			findViewById(R.id.searchProgressBar).setVisibility(View.GONE);

		}
	}

	public void selectTab(View v) {
		mSelectedPos = Integer.parseInt(v.getTag() + "");

		findViewById(R.id.searchListView).setVisibility(View.GONE);
		findViewById(R.id.searchViewPager).setVisibility(View.VISIBLE);

		mTabBarLayout.removeAllViews();

		for (int i = 0; i < mCategoryTitlesArrayList.size(); i++) {
			View tabView = null;
			if (i == mSelectedPos) {
				tabView = getLayoutInflater().inflate(
						R.layout.tab_search_selected, null);
			} else {
				tabView = getLayoutInflater()
						.inflate(R.layout.tab_search, null);
			}
			((TextView) tabView.findViewById(R.id.tabTitleTxt))
					.setText(mCategoryTitlesArrayList.get(i));
			((TextView) tabView.findViewById(R.id.tabCountTxt))
					.setText(mNumbersArrayList.get(i));
			if (Integer.parseInt(mNumbersArrayList.get(i)) > 9) {
				((TextView) tabView.findViewById(R.id.tabCountTxt))
						.setTextSize(10);
			}
			tabView.setTag(i);
			mTabBarLayout.addView(tabView);
		}

		new GetItemsTask().execute();

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
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.search, menu);
		mSearchItem = menu.findItem(R.id.action_search);
		MenuItemCompat.setOnActionExpandListener(mSearchItem,
				new OnActionExpandListener() {

					@Override
					public boolean onMenuItemActionExpand(MenuItem item) {
						return true;
					}

					@Override
					public boolean onMenuItemActionCollapse(MenuItem item) {
						mSearchQuery = "";
						if (mHistoryJSONArray.length() > 0) {
							findViewById(R.id.searchListView).setVisibility(
									View.VISIBLE);
						}
						findViewById(R.id.searchLayout)
								.setVisibility(View.GONE);
						return true;
					}
				});
		mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
		mSearchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				mSearchQuery = query;
				new GetItemsTask().execute();
				return false;
			}

			@Override
			public boolean onQueryTextChange(String query) {
				return false;
			}
		});
		mSearchAutoComplete = (SearchAutoComplete) mSearchView
				.findViewById(android.support.v7.appcompat.R.id.search_src_text);
		mSearchAutoComplete.setHintTextColor(Color.parseColor("#55ffffff"));

		mSearchAutoComplete.requestFocus();

		MenuItemCompat.expandActionView(mSearchItem);
		mSearchView.setIconified(false);

		ViewGroup searchPlate = (ViewGroup) mSearchView
				.findViewById(android.support.v7.appcompat.R.id.search_plate);
		searchPlate.setBackgroundResource(R.drawable.searchview_patch);

		return true;
	}

	@Override
	public void onBackPressed() {
		if (mSelectedPos == -1) {
			startActivity(new Intent(this, MainActivity.class));
			super.onBackPressed();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		} else {
			mSelectedPos = -1;
			mSearchQuery = "";
			if (mHistoryJSONArray.length() > 0) {
				findViewById(R.id.searchListView).setVisibility(View.VISIBLE);
			}
			findViewById(R.id.searchLayout).setVisibility(View.GONE);
			mTabBarLayout.removeAllViews();
			for (int i = 0; i < mCategoryTitlesArrayList.size(); i++) {
				View tabView = getLayoutInflater().inflate(R.layout.tab_search,
						null);
				((TextView) tabView.findViewById(R.id.tabTitleTxt))
						.setText(mCategoryTitlesArrayList.get(i));
				((TextView) tabView.findViewById(R.id.tabCountTxt))
						.setText(mNumbersArrayList.get(i));
				if (Integer.parseInt(mNumbersArrayList.get(i)) > 9) {
					((TextView) tabView.findViewById(R.id.tabCountTxt))
							.setTextSize(10);
				}
				tabView.setTag(i);
				mTabBarLayout.addView(tabView);
			}
			((HorizontalScrollView) findViewById(R.id.searchScrollView))
					.fullScroll(View.FOCUS_LEFT);
		}
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

	public void product(View v) {
		int pos = mViewPager.getCurrentItem();

		startActivity(new Intent(this, ProductActivity.class).putExtra("json",
				mItemsJSON.toString()).putExtra("pos", pos));
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}
}