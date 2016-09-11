package com.moysof.obo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moysof.obo.adapter.MessagesGridAdapter;
import com.moysof.obo.typeface.TypefaceSpan;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

import static com.moysof.obo.CommonUtilities.SERVER_URL;

public class MessagesContinuedActivity extends ActionBarActivity {
	private ArrayList<String> mThreadIdsArrayList = new ArrayList<String>();
	private ArrayList<Boolean> mMineArrayList = new ArrayList<Boolean>();
	private ArrayList<String> mTextsArrayList = new ArrayList<String>();
	private ArrayList<String> mTimesArrayList = new ArrayList<String>();
	private ArrayList<Integer> mSeenArrayList = new ArrayList<Integer>();
	public GridView mGrid;
	private String mMessage;
	private EditText mEditTxt;
	private Button mBtn;
	private String mThreadId;
	public String mRecepientId;
	private String mItemThreadId;
	public static MessagesContinuedActivity activity;
	public static Bitmap mBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages_continued);

		overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

		Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
		setSupportActionBar(toolbar);

		activity = this;

		mEditTxt = (EditText) findViewById(R.id.messagesEditTxt);
		mBtn = (Button) findViewById(R.id.messagesBtn);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);

		SpannableString s = new SpannableString(getResources().getString(
				R.string.title_activity_messages));
		s.setSpan(new TypefaceSpan(this, "Comfortaa-Bold.ttf"), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new RelativeSizeSpan(1.3f), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);

		IntentFilter filter = new IntentFilter();
		filter.addAction(Constansts.INTENT_RECIEVED_MESSAGE);
		registerReceiver(receiver, filter);

		onNewIntent(getIntent());
	}

	@Override
	public void onNewIntent(Intent intent) {
		Log("onNewIntent2");

		Bundle extras = intent.getExtras();
		if (extras != null) {
			mThreadId = getIntent().getStringExtra("id");
			mItemThreadId = getIntent().getStringExtra("item_id");
			mRecepientId = getIntent().getStringExtra("recipient_id");

			Log("continued activity = " + mThreadId + ", " + mItemThreadId
					+ ", " + mRecepientId);
			new GetMessagesTask().execute();
			sendBroadcast(new Intent(Constansts.INTENT_RECIEVED_MESSAGE)
					.putExtra("unreadCount", 0));
		}
	}

	@Override
	public void onDestroy() {
		activity = null;
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					MessagesContinuedActivity.this
							.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									new GetMessagesTask().execute();
								}
							});
				}
			}, 1000);
		}
	};
	public String mAuthorPhoto;

	class GetMessagesTask extends AsyncTask<String, Void, Void> {

		private int errorCode = 0;
		private String mResponseString;
		private MessagesGridAdapter mGridAdapter;
		private String mTitle;
		private Double mPrice;
		private String mPhoto;
		private ImageLoader mImageLoader;
		private String mName;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... files) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("item_id",
						mItemThreadId));
				nameValuePairs.add(new BasicNameValuePair("thread_id",
						mThreadId));
				nameValuePairs.add(new BasicNameValuePair("sender_id",
						PreferenceManager.getDefaultSharedPreferences(
								MessagesContinuedActivity.this).getString("id",
								"")));
				nameValuePairs.add(new BasicNameValuePair("recipient_id",
						mRecepientId));
				DefaultHttpClient client = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(Constansts.GET_MESSAGES_URL);
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = client.execute(httpPost);
					mResponseString = EntityUtils
							.toString(response.getEntity());
					Log(mResponseString);
					JSONObject jsonResponse = new JSONObject(mResponseString);
					if (jsonResponse.has("result")) {
						if (jsonResponse.getString("result").equals("success")) {
							mThreadIdsArrayList.clear();
							mMineArrayList.clear();
							mTextsArrayList.clear();
							mTimesArrayList.clear();
							mSeenArrayList.clear();

							mThreadId = jsonResponse.getString("thread_id");
							mTitle = jsonResponse.getString("title");
							mPrice = jsonResponse.getDouble("price");
							mPhoto = jsonResponse.getString("photo");
							mAuthorPhoto = jsonResponse
									.getString("author_photo");
							mName = jsonResponse.getString("name");

							JSONArray itemsJSON = new JSONArray(
									jsonResponse.getString("array"));
							for (int i = 0; i < itemsJSON.length(); i++) {
								mThreadIdsArrayList.add(itemsJSON
										.getJSONObject(i).getString("id"));
								mMineArrayList.add(itemsJSON.getJSONObject(i)
										.getBoolean("mine"));
								mTextsArrayList.add(itemsJSON.getJSONObject(i)
										.getString("text"));
								mTimesArrayList.add(itemsJSON.getJSONObject(i)
										.getString("time"));
								mSeenArrayList.add(itemsJSON.getJSONObject(i)
										.getInt("seen"));
							}
							errorCode = -1;
						} else if (jsonResponse.getString("result").equals(
								"empty")) {
							errorCode = 1;
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
				mGridAdapter = new MessagesGridAdapter(
						MessagesContinuedActivity.this, mName, mAuthorPhoto,
						mThreadIdsArrayList, mMineArrayList, mTextsArrayList,
						mTimesArrayList, mSeenArrayList);
				mGrid = (GridView) findViewById(R.id.messagesGrid);
				mGrid.setAdapter(mGridAdapter);
				((TextView) findViewById(R.id.messagesTitleTxt))
						.setText(mTitle);
				((TextView) findViewById(R.id.messagesPriceTxt)).setText("$"
						+ String.format("%.2f", mPrice) + " obo");
				// Create global configuration and initialize ImageLoader with
				// this
				// config
				DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
						.cacheInMemory(true).cacheOnDisk(true)
						.imageScaleType(ImageScaleType.EXACTLY).build();
				ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
						MessagesContinuedActivity.this)
						.defaultDisplayImageOptions(defaultOptions).build();
				mImageLoader = ImageLoader.getInstance();
				mImageLoader.init(config);

				mImageLoader.displayImage(mPhoto,
						((ImageView) findViewById(R.id.messagesItemImg)),
						new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String arg0, View arg1) {
							}

							@Override
							public void onLoadingFailed(String arg0, View arg1,
														FailReason arg2) {
							}

							@Override
							public void onLoadingComplete(String arg0,
														  View arg1, Bitmap bitmap) {
								mBitmap = bitmap;
							}

							@Override
							public void onLoadingCancelled(String arg0,
														   View arg1) {
							}
						});
				PreferenceManager
						.getDefaultSharedPreferences(
								MessagesContinuedActivity.this).edit()
						.putInt("unreadCount", 0).commit();
			} else if (errorCode == 0) {
				Toast.makeText(MessagesContinuedActivity.this,
						"Unknown error: " + mResponseString, Toast.LENGTH_LONG)
						.show();
			}
			findViewById(R.id.messagesProgressBar).setVisibility(View.GONE);
		}
	}

	public void send(View v) {
		mMessage = mEditTxt.getText().toString();
		if (!mMessage.equals("")) {
			mBtn.setEnabled(false);
			mEditTxt.setText("");
			new sendMessageTask().execute();
		} else {

		}
	}

	private class sendMessageTask extends AsyncTask<Void, Void, Void> {

		private int errorCode;
		private String mResponseString;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("sender_id",
					PreferenceManager.getDefaultSharedPreferences(
							getApplicationContext()).getString("id", "")));
			nameValuePairs.add(new BasicNameValuePair("reciever_id",
					mRecepientId));
			nameValuePairs.add(new BasicNameValuePair("message", mMessage));
			nameValuePairs.add(new BasicNameValuePair("thread_id", mThreadId));
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(SERVER_URL);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				mResponseString = EntityUtils.toString(response.getEntity());
				JSONObject jsonResponse = new JSONObject(mResponseString);
				if (jsonResponse.getString("result").equals("success")) {
					errorCode = -1;
				} else if (jsonResponse.getString("result").equals("empty")) {
					errorCode = 1;
				} else {
					errorCode = 0;
				}
				Log.d("Log", mResponseString);
			} catch (Exception e) {
				Log.d("Log", "Server error: " + e.getMessage());
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (errorCode == -1) {
			} else if (errorCode == 0) {
				Toast.makeText(MessagesContinuedActivity.this,
						"Unknown error: " + mResponseString, Toast.LENGTH_LONG)
						.show();
			} else if (errorCode == 1) {
				Toast.makeText(MessagesContinuedActivity.this,
						"Error: Some fields are empty", Toast.LENGTH_LONG)
						.show();
			}
			/*
			 * // mMessages.add(mFinalMessage); SimpleDateFormat sdf = new
			 * SimpleDateFormat("HH:mm"); Date d = new Date();
			 * d.setSeconds((int) (System.currentTimeMillis() / 1000)); String
			 * timestamp = sdf.format(d); /* mTimestamps.add(timestamp);
			 * mIsMyMessages.add(true); mGridAdapter.notifyDataSetChanged();
			 */
			mBtn.setEnabled(true);
			// new GetMessagesTask().execute();
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

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}

}
