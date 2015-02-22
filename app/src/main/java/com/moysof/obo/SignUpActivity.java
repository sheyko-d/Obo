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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.moysof.obo.typeface.TypefaceSpan;

public class SignUpActivity extends ActionBarActivity {

	private EditText mUsernameEditText;
	private EditText mPasswordEditText;
	private EditText mPasswordAgainEditText;
	private EditText mEmailEditText;
	private ImageView mCheckmarkImg;

	private String mUsername = "";
	private String mPassword = "";
	private String mPasswordAgain = "";
	private String mEmail = "";
	private ProgressBar mCheckmarkProgressBar;
	protected boolean mUsernameIsEmpty;
	public HttpPost mHttpPost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

		Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
		setSupportActionBar(toolbar);

		mUsernameEditText = (EditText) findViewById(R.id.signupUsernameEditTxt);
		mPasswordEditText = (EditText) findViewById(R.id.signupPasswordEditTxt);
		mPasswordAgainEditText = (EditText) findViewById(R.id.signupPasswordAgainEditTxt);
		mEmailEditText = (EditText) findViewById(R.id.signupEmailEditTxt);
		mCheckmarkImg = (ImageView) findViewById(R.id.signupCheckmarkImg);
		mCheckmarkProgressBar = (ProgressBar) findViewById(R.id.signupCheckmarkProgressBar);

		mUsernameEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mCheckmarkImg.setVisibility(View.INVISIBLE);
				if (!s.toString().equals("")) {
					mUsernameIsEmpty = false;
					mCheckmarkProgressBar.setVisibility(View.VISIBLE);
					if (mHttpPost != null) {
						mHttpPost.abort();
					}
					new CheckUsernameTask().execute(s.toString());
				} else {
					mUsernameIsEmpty = true;
					mCheckmarkImg.setVisibility(View.INVISIBLE);
					mCheckmarkProgressBar.setVisibility(View.INVISIBLE);
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

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);

		SpannableString s = new SpannableString(getResources().getString(
				R.string.title_activity_sign_up));
		s.setSpan(new TypefaceSpan(this, "Comfortaa-Bold.ttf"), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new RelativeSizeSpan(1.2f), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);

		SpannableString ss = new SpannableString(getResources().getString(
				R.string.sign_up_terms_title));
		LinkSpan clickableSpanPrivacy = new LinkSpan() {
			@Override
			public void onClick(View textView) {
				startActivity(new Intent(SignUpActivity.this,
						PrivacyActivity.class));
			}
		};

		LinkSpan clickableSpanSafety = new LinkSpan() {
			@Override
			public void onClick(View textView) {
				startActivity(new Intent(SignUpActivity.this,
						SafetyActivity.class));
			}
		};

		ss.setSpan(clickableSpanPrivacy, 62, 76,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		ss.setSpan(
				new ForegroundColorSpan(getResources().getColor(R.color.green)),
				62, 76, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		ss.setSpan(clickableSpanSafety, 111, 124,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		ss.setSpan(
				new ForegroundColorSpan(getResources().getColor(R.color.green)),
				111, 124, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		TextView termsTxt = (TextView) findViewById(R.id.signupTermsTxt);
		termsTxt.setText(ss);
		termsTxt.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		overridePendingTransition(R.anim.exit_to_right, R.anim.enter_from_left);
		return false;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.exit_to_right, R.anim.enter_from_left);
	}

	public void signUp(View v) {
		mUsername = mUsernameEditText.getText().toString();
		mPassword = mPasswordEditText.getText().toString();
		mPasswordAgain = mPasswordAgainEditText.getText().toString();
		mEmail = mEmailEditText.getText().toString();

		if (mUsername.equals("")) {
			Toast.makeText(getApplicationContext(), "Error: Username is empty",
					Toast.LENGTH_LONG).show();
		} else if (mPassword.equals("")) {
			Toast.makeText(getApplicationContext(), "Error: Password is empty",
					Toast.LENGTH_LONG).show();
		} else if (mEmail.equals("")) {
			Toast.makeText(getApplicationContext(), "Error: Email is empty",
					Toast.LENGTH_LONG).show();
		} else if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
			Toast.makeText(getApplicationContext(), "Error: Email is invalid",
					Toast.LENGTH_LONG).show();
		} else if (!mPassword.equals(mPasswordAgain)) {
			Toast.makeText(getApplicationContext(),
					"Error: Passwords don't match", Toast.LENGTH_LONG).show();
		} else if (Obo.isNetworkConnected()) {
			new SignUpTask().execute();
		}
	}

	class SignUpTask extends AsyncTask<String, Void, Void> {
		private ProgressDialog loadingDialog;
		private int errorCode = 0;
		private String mResponseString;
		private String mId;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loadingDialog = new ProgressDialog(SignUpActivity.this);
			loadingDialog.setMessage("Loading...");
			loadingDialog.show();
		}

		@Override
		protected Void doInBackground(String... files) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs
						.add(new BasicNameValuePair(
								"phone",
								((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE))
										.getLine1Number()));
				nameValuePairs
						.add(new BasicNameValuePair("username", mUsername));
				nameValuePairs
						.add(new BasicNameValuePair("password", mPassword));
				nameValuePairs.add(new BasicNameValuePair("email", mEmail));
				nameValuePairs.add(new BasicNameValuePair("gcm_id",
						GCMRegistrar.getRegistrationId(SignUpActivity.this)));
				DefaultHttpClient client = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(Constansts.SIGN_UP_URL);
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = client.execute(httpPost);
					mResponseString = EntityUtils
							.toString(response.getEntity());
					Log(mResponseString);
					JSONObject jsonResponse = new JSONObject(mResponseString);
					if (jsonResponse.has("result")) {
						if (jsonResponse.getString("result").equals("success")) {
							mId = jsonResponse.getString("id");
							errorCode = -1;
						} else if (jsonResponse.getString("result").equals(
								"empty")) {
							errorCode = 1;
						} else if (jsonResponse.getString("result").equals(
								"exists")) {
							errorCode = 2;
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
				Editor editor = PreferenceManager.getDefaultSharedPreferences(
						SignUpActivity.this).edit();
				editor.putString("id", mId);
				editor.putString("username", mUsername);
				editor.putString("email", mEmail);
				editor.commit();
				startActivity(new Intent(SignUpActivity.this,
						MainActivity.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				try {
					LoadingMenuActivity.activity.finish();
				} catch (Exception e) {
				}
				finish();
			} else if (errorCode == 0) {
				Toast.makeText(SignUpActivity.this,
						"Unknown error: " + mResponseString, Toast.LENGTH_LONG)
						.show();
			} else if (errorCode == 1) {
				Toast.makeText(SignUpActivity.this,
						"Error: Some fields are empty", Toast.LENGTH_LONG)
						.show();
			} else if (errorCode == 2) {
				Toast.makeText(SignUpActivity.this,
						"Error: User already exists", Toast.LENGTH_LONG).show();
			}
		}
	}

	class CheckUsernameTask extends AsyncTask<String, Void, String> {
		private int errorCode = 0;
		private String mResponseString;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... usernames) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				nameValuePairs.add(new BasicNameValuePair("username",
						usernames[0]));
				DefaultHttpClient client = new DefaultHttpClient();

				mHttpPost = null;
				mHttpPost = new HttpPost(Constansts.CHECK_USERNAME_URL);
				try {
					mHttpPost
							.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = client.execute(mHttpPost);
					mResponseString = EntityUtils
							.toString(response.getEntity());
					Log(mResponseString);
					JSONObject jsonResponse = new JSONObject(mResponseString);
					if (jsonResponse.has("result")) {
						if (jsonResponse.getString("result").equals("success")) {
							errorCode = -1;
						} else {
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
			return usernames[0];
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (errorCode == -1) {
				if (mUsernameEditText.getText().toString().equals(result)) {
					if (!mUsernameIsEmpty) {
						mCheckmarkImg.setVisibility(View.VISIBLE);
						mCheckmarkImg
								.setImageResource(R.drawable.ic_checkmark_success);
					}
					mCheckmarkProgressBar.setVisibility(View.INVISIBLE);
				} else {
					if (mCheckmarkProgressBar.getVisibility() == View.INVISIBLE
							& !mUsernameIsEmpty) {
						mCheckmarkProgressBar.setVisibility(View.VISIBLE);
					}
				}
			} else if (errorCode == 1) {
				if (mUsernameEditText.getText().toString().equals(result)) {
					if (!mUsernameIsEmpty) {
						mCheckmarkImg.setVisibility(View.VISIBLE);
						mCheckmarkImg
								.setImageResource(R.drawable.ic_checkmark_fail);
					}
					mCheckmarkProgressBar.setVisibility(View.INVISIBLE);
				} else {
					if (mCheckmarkProgressBar.getVisibility() == View.INVISIBLE
							& !mUsernameIsEmpty) {
						mCheckmarkProgressBar.setVisibility(View.VISIBLE);
					}
				}
			}
		}
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}

}