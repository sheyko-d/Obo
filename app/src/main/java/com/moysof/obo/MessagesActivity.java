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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.moysof.obo.adapter.MainDrawerAdapter;
import com.moysof.obo.adapter.ThreadsListAdapter;
import com.moysof.obo.typeface.TypefaceSpan;

public class MessagesActivity extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private GridView mDrawerGrid;
	private MainDrawerAdapter mDrawerAdapter;
	private ArrayList<String> mIdsArrayList = new ArrayList<String>();
	private ArrayList<String> mAuthorsArrayList = new ArrayList<String>();
	private ArrayList<String> mItemIdsArrayList = new ArrayList<String>();
	private ArrayList<String> mSenderIdsArrayList = new ArrayList<String>();
	private ArrayList<String> mRecipientIdsArrayList = new ArrayList<String>();
	private ArrayList<String> mTitlesArrayList = new ArrayList<String>();
	private ArrayList<String> mCaptionsArrayList = new ArrayList<String>();
	private ArrayList<String> mPhotosArrayList = new ArrayList<String>();
	private ArrayList<String> mTimesArrayList = new ArrayList<String>();
	private ThreadsListAdapter mListAdapter;
	private ListView mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
		setSupportActionBar(toolbar);

		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		SpannableString s = new SpannableString(getResources().getString(
				R.string.title_activity_messages));
		s.setSpan(new TypefaceSpan(this, "Comfortaa-Bold.ttf"), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new RelativeSizeSpan(1.3f), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);
		actionBar.setDisplayShowHomeEnabled(false);

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
		mDrawerGrid = (GridView) findViewById(R.id.left_drawer);
		mDrawerAdapter = new MainDrawerAdapter(4, this, mDrawerGrid,
				mDrawerLayout);
		mDrawerGrid.setAdapter(mDrawerAdapter);

		mListAdapter = new ThreadsListAdapter(MessagesActivity.this,
				mIdsArrayList, mAuthorsArrayList, mTitlesArrayList, mCaptionsArrayList,
				mPhotosArrayList, mTimesArrayList);
		mList = (ListView) findViewById(R.id.messagesList);
		mList.setAdapter(mListAdapter);

		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent messagesContinuedIntent = new Intent(
						MessagesActivity.this, MessagesContinuedActivity.class);
				messagesContinuedIntent.putExtra("id",
						mIdsArrayList.get(position));
				messagesContinuedIntent.putExtra("item_id",
						mItemIdsArrayList.get(position));
				messagesContinuedIntent.putExtra("sender_id",
						mSenderIdsArrayList.get(position));
				messagesContinuedIntent.putExtra("recipient_id",
						mRecipientIdsArrayList.get(position));

				startActivity(messagesContinuedIntent);
			}
		});

		if (Obo.isNetworkConnected()) {
			new GetThreadsTask().execute();
		} else {
			findViewById(R.id.messagesPlaceholderLayout).setEnabled(false);
			findViewById(R.id.messagesPlaceholderLayout).setVisibility(
					View.VISIBLE);
			findViewById(R.id.messagesProgressBar).setVisibility(
					View.GONE);
		}

		IntentFilter filter = new IntentFilter();
		filter.addAction(Constansts.INTENT_RECIEVED_MESSAGE);
		registerReceiver(receiver, filter);
	}

	class GetThreadsTask extends AsyncTask<String, Void, Void> {

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
								MessagesActivity.this).getString("id", "")));
				DefaultHttpClient client = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(Constansts.GET_THREADS_URL);
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
							mItemIdsArrayList.clear();
							mAuthorsArrayList.clear();
							mSenderIdsArrayList.clear();
							mRecipientIdsArrayList.clear();
							mTitlesArrayList.clear();
							mCaptionsArrayList.clear();
							mPhotosArrayList.clear();
							mTimesArrayList.clear();

							JSONArray itemsJSON = new JSONArray(
									jsonResponse.getString("array"));
							for (int i = 0; i < itemsJSON.length(); i++) {
								mIdsArrayList.add(itemsJSON.getJSONObject(i)
										.getString("id"));
								mAuthorsArrayList.add(itemsJSON
										.getJSONObject(i).getString("author"));
								mItemIdsArrayList.add(itemsJSON
										.getJSONObject(i).getString("item_id"));
								mSenderIdsArrayList.add(itemsJSON
										.getJSONObject(i)
										.getString("sender_id"));
								mRecipientIdsArrayList.add(itemsJSON
										.getJSONObject(i).getString(
												"recipient_id"));
								mTitlesArrayList.add(itemsJSON.getJSONObject(i)
										.getString("title"));
								mCaptionsArrayList.add(itemsJSON.getJSONObject(
										i).getString("caption"));
								mPhotosArrayList.add(itemsJSON.getJSONObject(i)
										.getString("photo"));
								mTimesArrayList.add(itemsJSON.getJSONObject(i)
										.getString("time"));
							}
							errorCode = -1;
						} else if (jsonResponse.getString("result").equals(
								"empty")) {
							errorCode = 1;
						} else if (jsonResponse.getString("result").equals(
								"no_messages")) {
							errorCode = 2;
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
				mListAdapter.notifyDataSetChanged();
				findViewById(R.id.messagesPlaceholderLayout).setVisibility(
						View.GONE);
			} else if (errorCode == 0) {
				Toast.makeText(MessagesActivity.this,
						"Unknown error: " + mResponseString, Toast.LENGTH_LONG)
						.show();
			} else if (errorCode == 1) {
				Toast.makeText(MessagesActivity.this,
						"Error: Some fields are empty", Toast.LENGTH_LONG)
						.show();
			} else if (errorCode == 2) {
				findViewById(R.id.messagesPlaceholderLayout).setVisibility(
						View.VISIBLE);
			}
			findViewById(R.id.messagesProgressBar).setVisibility(View.GONE);
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

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			new GetThreadsTask().execute();
			PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext())
					.edit()
					.putInt("unread_count", 99)
					.commit();
			MainDrawerAdapter.unreadCount = 99;
			MainDrawerAdapter.adapter.notifyDataSetChanged();
		}
	};

	public static void Log(Object text) {
		Log.d("Log", text + "");
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
	public void onBackPressed() {
		startActivity(new Intent(this, MainActivity.class));
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
}
