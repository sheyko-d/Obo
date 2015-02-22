package com.moysof.obo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.moysof.obo.adapter.MainDrawerAdapter;
import com.moysof.obo.adapter.WantedListAdapter;
import com.moysof.obo.typeface.TypefaceSpan;

public class WantedActivity extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private GridView mDrawerGrid;
	private MainDrawerAdapter mDrawerAdapter;
	private ArrayList<String> mIdsArrayList = new ArrayList<String>();
	private ArrayList<String> mTitlesArrayList = new ArrayList<String>();
	private ArrayList<String> mCountArrayList = new ArrayList<String>();
	private ArrayList<String> mKeywordsArrayList = new ArrayList<String>();
	public WantedListAdapter mListAdapter;
	public ListView mList;
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wanted);
		
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
				R.string.title_activity_wanted));
		s.setSpan(new TypefaceSpan(this, "Comfortaa-Bold.ttf"), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new RelativeSizeSpan(1.3f), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);

		mDrawerGrid = (GridView) findViewById(R.id.left_drawer);
		mDrawerAdapter = new MainDrawerAdapter(3, this, mDrawerGrid,
				mDrawerLayout);
		mDrawerGrid.setAdapter(mDrawerAdapter);

		if (Obo.isNetworkConnected()) {
			new GetWantedTask().execute();
		} else {
			findViewById(R.id.wantedBtn).setEnabled(false);
			findViewById(R.id.wantedPlaceholderLayout).setEnabled(false);
			findViewById(R.id.wantedPlaceholderLayout).setVisibility(
					View.VISIBLE);
			findViewById(R.id.wantedProgressBar).setVisibility(
					View.GONE);
		}
	}

	class GetWantedTask extends AsyncTask<String, Void, Void> {

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

				nameValuePairs.add(new BasicNameValuePair("id",
						PreferenceManager.getDefaultSharedPreferences(
								getApplicationContext()).getString("id", "")));
				DefaultHttpClient client = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(Constansts.GET_WANTED_URL);
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
							mCountArrayList.clear();
							mKeywordsArrayList.clear();

							mItemsJSON = new JSONArray(
									jsonResponse.getString("array"));
							for (int i = 0; i < mItemsJSON.length(); i++) {
								mIdsArrayList.add(mItemsJSON.getJSONObject(i)
										.getString("id"));
								mTitlesArrayList.add(mItemsJSON
										.getJSONObject(i).getString("title"));
								mCountArrayList.add(mItemsJSON.getJSONObject(i)
										.getString("count"));
								mKeywordsArrayList
										.add(mItemsJSON.getJSONObject(i)
												.getString("keywords"));
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
				Toast.makeText(WantedActivity.this,
						"Unknown error: " + mResponseString, Toast.LENGTH_LONG)
						.show();
			} else if (errorCode == 1) {
				Toast.makeText(WantedActivity.this,
						"Error: Some fields are empty", Toast.LENGTH_LONG)
						.show();
			}

			findViewById(R.id.wantedProgressBar).setVisibility(View.GONE);
			findViewById(R.id.wantedList).setVisibility(View.VISIBLE);

			if (mIdsArrayList.size() > 0) {
				mListAdapter = new WantedListAdapter(WantedActivity.this,
						mTitlesArrayList, mIdsArrayList, mCountArrayList,
						mKeywordsArrayList);
				mList = (ListView) findViewById(R.id.wantedList);
				mList.setAdapter(mListAdapter);

				registerForContextMenu(mList);
			} else {
				findViewById(R.id.wantedPlaceholderLayout).setVisibility(
						View.VISIBLE);
			}
		}

	}

	class RemoveWantedTask extends AsyncTask<String, Void, Void> {

		private int errorCode = 0;
		private String mResponseString;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			findViewById(R.id.wantedProgressBar).setVisibility(View.VISIBLE);
			findViewById(R.id.wantedList).setVisibility(View.INVISIBLE);
		}

		@Override
		protected Void doInBackground(String... ids) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				nameValuePairs.add(new BasicNameValuePair("id", ids[0]));
				DefaultHttpClient client = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(Constansts.REMOVE_WANTED_URL);
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = client.execute(httpPost);
					mResponseString = EntityUtils
							.toString(response.getEntity());
					Log(mResponseString);
					JSONObject jsonResponse = new JSONObject(mResponseString);
					if (jsonResponse.has("result")) {
						if (jsonResponse.getString("result").equals("success")) {
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
				Toast.makeText(WantedActivity.this, "Item is removed",
						Toast.LENGTH_SHORT).show();
				new GetWantedTask().execute();
			} else if (errorCode == 0) {
				Toast.makeText(WantedActivity.this,
						"Unknown error: " + mResponseString, Toast.LENGTH_LONG)
						.show();
			} else if (errorCode == 1) {
				Toast.makeText(WantedActivity.this,
						"Error: Some fields are empty", Toast.LENGTH_LONG)
						.show();
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
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.wanted_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_delete:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			new RemoveWantedTask().execute(mIdsArrayList.get(info.position));
			return true;
		default:
			return super.onContextItemSelected(item);
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

	public void add(View v) {
		startActivity(new Intent(this, WantedAddActivity.class));
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
