package com.moysof.obo;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.moysof.obo.adapter.MainDrawerAdapter;
import com.moysof.obo.adapter.SellListAdapter;
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

public class SellActivity extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private GridView mDrawerGrid;
	private MainDrawerAdapter mDrawerAdapter;
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
	private ArrayList<String> mOnSaleArrayList = new ArrayList<String>();
	private ArrayList<String> mIsSoldArrayList = new ArrayList<String>();
	private ArrayList<Integer> mViewsArrayList = new ArrayList<Integer>();
	private ArrayList<Integer> mDurationsArrayList = new ArrayList<Integer>();
	private ArrayList<String> mContactChoicesArrayList = new ArrayList<String>();
	public SellListAdapter mListAdapter;
	public ListView mList;
	private String mDeleteId;
	private String mSoldId;
	private int mEditPos;
	private JSONArray mItemsJSON;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sell);

		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

		Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
		setSupportActionBar(toolbar);

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
				R.string.title_activity_sell));
		s.setSpan(new TypefaceSpan(this, "Comfortaa-Bold.ttf"), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new RelativeSizeSpan(1.3f), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);

		mDrawerGrid = (GridView) findViewById(R.id.left_drawer);
		mDrawerAdapter = new MainDrawerAdapter(2, this, mDrawerGrid,
				mDrawerLayout);
		mDrawerGrid.setAdapter(mDrawerAdapter);

		if (Obo.isNetworkConnected()) {
			new GetMyItemsTask().execute();
		} else {
			findViewById(R.id.sellBtn).setEnabled(false);
			findViewById(R.id.sellPlaceholderLayout).setEnabled(false);
			findViewById(R.id.sellPlaceholderLayout)
					.setVisibility(View.VISIBLE);
			findViewById(R.id.sellProgressBar).setVisibility(View.GONE);
		}
	}

	class GetMyItemsTask extends AsyncTask<String, Void, Void> {

		private int errorCode = 0;
		private String mResponseString;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... files) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				nameValuePairs.add(new BasicNameValuePair("id",
						PreferenceManager.getDefaultSharedPreferences(
								getApplicationContext()).getString("id", "")));
				DefaultHttpClient client = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(Constansts.GET_MY_ITEMS_URL);
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = client.execute(httpPost);
					mResponseString = EntityUtils
							.toString(response.getEntity());
					Log(mResponseString);
					JSONObject jsonResponse = new JSONObject(mResponseString);
					if (jsonResponse.has("result")) {
						if (jsonResponse.getString("result").equals("success")) {
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
							mContactChoicesArrayList.clear();
							mOnSaleArrayList.clear();
							mViewsArrayList.clear();
							mDurationsArrayList.clear();
							mIsSoldArrayList.clear();

							mItemsJSON = new JSONArray(
									jsonResponse.getString("array"));
							for (int i = 0; i < mItemsJSON.length(); i++) {
								mIdsArrayList.add(mItemsJSON.getJSONObject(i)
										.getString("id"));
								mTitlesArrayList.add(mItemsJSON
										.getJSONObject(i).getString("title"));
								mPricesArrayList.add(mItemsJSON
										.getJSONObject(i).getDouble("price"));
								mLocationsArrayList
										.add(mItemsJSON.getJSONObject(i)
												.getString("location"));
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
								mOnSaleArrayList.add(mItemsJSON
										.getJSONObject(i).getString("on_sale"));
								mIsSoldArrayList.add(mItemsJSON
										.getJSONObject(i).getString("is_sold"));
								mViewsArrayList.add(mItemsJSON.getJSONObject(i)
										.getInt("views"));
								mContactChoicesArrayList.add(mItemsJSON
										.getJSONObject(i).getString(
												"contact_choices"));
								mDurationsArrayList.add(mItemsJSON
										.getJSONObject(i).getInt("duration"));
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
			if (errorCode == -1) {
			} else if (errorCode == 0) {
				Toast.makeText(SellActivity.this,
						"Unknown error: " + mResponseString, Toast.LENGTH_LONG)
						.show();
			} else if (errorCode == 1) {
				Toast.makeText(SellActivity.this,
						"Error: Some fields are empty", Toast.LENGTH_LONG)
						.show();
			}

			findViewById(R.id.sellProgressBar).setVisibility(View.GONE);

			if (mIdsArrayList.size() > 0) {
				mListAdapter = new SellListAdapter(SellActivity.this,
						mTitlesArrayList, mIdsArrayList, mOnSaleArrayList, mIsSoldArrayList,
						mViewsArrayList);
				mList = (ListView) findViewById(R.id.sellList);
				mList.setAdapter(mListAdapter);
				registerForContextMenu(mList);
				mList.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int pos, long item_id) {
						Log(mItemsJSON.toString());
						startActivity(new Intent(SellActivity.this,
								ProductActivity.class).putExtra("json",
								mItemsJSON.toString()).putExtra("pos", pos));
					}
				});
				mList.setVisibility(View.VISIBLE);
			} else {
				findViewById(R.id.sellPlaceholderLayout).setVisibility(
						View.VISIBLE);
			}
		}
	}

	class DeleteItemTask extends AsyncTask<String, Void, Void> {

		private int errorCode = 0;
		private String mResponseString;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			findViewById(R.id.sellProgressBar).setVisibility(View.VISIBLE);
			mList.setVisibility(View.INVISIBLE);
		}

		@Override
		protected Void doInBackground(String... files) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("id",
						PreferenceManager.getDefaultSharedPreferences(
								getApplicationContext()).getString("id", "")));
				nameValuePairs
						.add(new BasicNameValuePair("item_id", mDeleteId));
				DefaultHttpClient client = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(Constansts.DELETE_ITEM_URL);
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = client.execute(httpPost);
					mResponseString = EntityUtils
							.toString(response.getEntity());
					Log(mResponseString);
					JSONObject jsonResponse = new JSONObject(mResponseString);
					if (jsonResponse.has("result")) {
						errorCode = -1;
					} else if (jsonResponse.getString("result").equals("empty")) {
						errorCode = 1;
					} else {
						errorCode = 0;
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
			if (errorCode == -1) {
				new GetMyItemsTask().execute();
			} else if (errorCode == 0) {
				Toast.makeText(SellActivity.this,
						"Unknown error: " + mResponseString, Toast.LENGTH_LONG)
						.show();
				findViewById(R.id.sellProgressBar).setVisibility(View.GONE);
			} else if (errorCode == 1) {
				Toast.makeText(SellActivity.this,
						"Error: Some fields are empty", Toast.LENGTH_LONG)
						.show();
				findViewById(R.id.sellProgressBar).setVisibility(View.GONE);
			}
		}
	}
	
	class SoldItemTask extends AsyncTask<String, Void, Void> {

		private int errorCode = 0;
		private String mResponseString;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			findViewById(R.id.sellProgressBar).setVisibility(View.VISIBLE);
			mList.setVisibility(View.INVISIBLE);
		}

		@Override
		protected Void doInBackground(String... files) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("id",
						PreferenceManager.getDefaultSharedPreferences(
								getApplicationContext()).getString("id", "")));
				nameValuePairs
						.add(new BasicNameValuePair("item_id", mSoldId));
				DefaultHttpClient client = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(Constansts.SOLD_ITEM_URL);
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = client.execute(httpPost);
					mResponseString = EntityUtils
							.toString(response.getEntity());
					Log(mResponseString);
					JSONObject jsonResponse = new JSONObject(mResponseString);
					if (jsonResponse.has("result")) {
						errorCode = -1;
					} else if (jsonResponse.getString("result").equals("empty")) {
						errorCode = 1;
					} else {
						errorCode = 0;
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
			if (errorCode == -1) {
				new GetMyItemsTask().execute();
			} else if (errorCode == 0) {
				Toast.makeText(SellActivity.this,
						"Unknown error: " + mResponseString, Toast.LENGTH_LONG)
						.show();
				findViewById(R.id.sellProgressBar).setVisibility(View.GONE);
			} else if (errorCode == 1) {
				Toast.makeText(SellActivity.this,
						"Error: Some fields are empty", Toast.LENGTH_LONG)
						.show();
				findViewById(R.id.sellProgressBar).setVisibility(View.GONE);
			}
		}
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sell_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.action_delete:
			mDeleteId = mIdsArrayList.get(info.position);
			new DeleteItemTask().execute();
			break;
		case R.id.action_sold:
			mSoldId = mIdsArrayList.get(info.position);
			new SoldItemTask().execute();
			break;
		case R.id.action_edit:
			mEditPos = info.position;
			startActivity(new Intent(SellActivity.this, SellAddActivity.class)
					.putExtra("title", mTitlesArrayList.get(mEditPos))
					.putExtra("category", mCategoriesArrayList.get(mEditPos))
					.putExtra("price", mPricesArrayList.get(mEditPos) + "")
					.putExtra("desc", mDescArrayList.get(mEditPos))
					.putExtra("duration", mDurationsArrayList.get(mEditPos))
					.putExtra("contact_choices",
							mContactChoicesArrayList.get(mEditPos))
					.putExtra("photos", mPhotosArrayList.get(mEditPos))
					.putExtra("photo_num", mPhotoNumsArrayList.get(mEditPos))
					.putExtra("item_id", mIdsArrayList.get(mEditPos)));
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	public void add(View v) {
		startActivity(new Intent(this, SellAddActivity.class));
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, MainActivity.class));
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

}