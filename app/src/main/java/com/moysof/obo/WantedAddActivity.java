package com.moysof.obo;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.moysof.obo.adapter.KeywordsListAdapter;
import com.moysof.obo.typeface.TypefaceSpan;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class WantedAddActivity extends ActionBarActivity {

	private ArrayList<String> mTitlesArrayList = new ArrayList<>();
	private ListView mList;
	private KeywordsListAdapter mAdapter;
	private Button mKeywordBtn;
	private EditText mKeywordEditTxt;
	private String mName;
	private String mPrice;
	private String mKeywords;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wanted_add);

		overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

		Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setIcon(R.drawable.ic_back);

		SpannableString s = new SpannableString(getResources().getString(
				R.string.title_activity_wanted));
		s.setSpan(new TypefaceSpan(this, "Comfortaa-Bold.ttf"), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new RelativeSizeSpan(1.2f), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);

		// Create global configuration and initialize ImageLoader with this
		// config
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisk(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).defaultDisplayImageOptions(defaultOptions).build();
		ImageLoader.getInstance().init(config);

		mKeywordBtn = (Button) findViewById(R.id.wantedKeywordBtn);
		mKeywordEditTxt = (EditText) findViewById(R.id.wantedKeywordEditTxt);
		mKeywordEditTxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.toString().equals("")) {
					mKeywordBtn.setEnabled(false);
				} else {
					mKeywordBtn.setEnabled(true);
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

		mList = (ListView) findViewById(R.id.keywordsList);
		mAdapter = new KeywordsListAdapter(this, mTitlesArrayList);
		mList.setAdapter(mAdapter);
	}

	public void addKeyword(View v) {
		mTitlesArrayList.add(mKeywordEditTxt.getText().toString());
		mKeywordEditTxt.setText("");
		mAdapter.notifyDataSetChanged();
	}

	public void save(View v) {
		mName = ((EditText) findViewById(R.id.wantedNameEditTxt)).getText()
				.toString();
		mPrice = ((EditText) findViewById(R.id.wantedPriceEditTxt)).getText()
				.toString();

		if (mName.equals("")) {
			Toast.makeText(this, "Error: Name is empty", Toast.LENGTH_LONG)
					.show();
		} else if (mPrice.equals("")) {
			Toast.makeText(this, "Error: Price is empty", Toast.LENGTH_LONG)
					.show();
		} else if (mTitlesArrayList.size() == 0) {
			Toast.makeText(this, "Error: Enter at least one keyword",
					Toast.LENGTH_LONG).show();
		} else {
			JSONArray keywordsJSON = new JSONArray();
			for (int i = 0; i < mTitlesArrayList.size(); i++) {
				keywordsJSON.put(mTitlesArrayList.get(i));
			}
			mKeywords = keywordsJSON.toString();
			new PostWantedTask().execute();
		}
	}

	class PostWantedTask extends AsyncTask<String, Void, Void> {
		private ProgressDialog loadingDialog;
		private int errorCode = 0;
		private String mResponseString;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loadingDialog = new ProgressDialog(WantedAddActivity.this);
			loadingDialog.setMessage("Loading...");
			loadingDialog.show();
		}

		@Override
		protected Void doInBackground(String... files) {
			try {
				MultipartEntityBuilder multipartEntity = MultipartEntityBuilder
						.create();

				multipartEntity.addPart("id", new StringBody(PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext())
						.getString("id", ""), ContentType.TEXT_PLAIN));

				multipartEntity.addPart("name", new StringBody(mName,
						ContentType.TEXT_PLAIN));
				multipartEntity.addPart("price", new StringBody(mPrice,
						ContentType.TEXT_PLAIN));
				multipartEntity.addPart("keywords", new StringBody(mKeywords,
						ContentType.APPLICATION_JSON));
				DefaultHttpClient client = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(Constansts.POST_WANTED_URL);
				try {
					httpPost.setEntity(multipartEntity.build());
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
			loadingDialog.dismiss();
			if (errorCode == -1) {
				Toast.makeText(WantedAddActivity.this, "Item is added",
						Toast.LENGTH_LONG).show();
				startActivity(new Intent(WantedAddActivity.this,
						WantedActivity.class)
						.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				finish();
			} else if (errorCode == 0) {
				Toast.makeText(WantedAddActivity.this,
						"Unknown error: " + mResponseString, Toast.LENGTH_LONG)
						.show();
			} else if (errorCode == 1) {
				Toast.makeText(WantedAddActivity.this,
						"Error: Some fields are empty", Toast.LENGTH_LONG)
						.show();
			}
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