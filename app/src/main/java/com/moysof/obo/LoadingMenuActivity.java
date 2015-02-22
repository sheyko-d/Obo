package com.moysof.obo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.google.android.gcm.GCMRegistrar;

public class LoadingMenuActivity extends ActionBarActivity {
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private UiLifecycleHelper uiHelper;
	private String mName = "";
	private String mSurname = "";
	private String mId = "";
	private String mEmail = "";
	private String mZip = "";
	private String mPhone = "";
	public static Activity activity;

	@SuppressLint({ "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading_menu);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setStatusBarColor(Color.parseColor("#000000"));
		}

		activity = this;

		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

		try {
			Session.getActiveSession().closeAndClearTokenInformation();
		} catch (Exception e) {
		}
		uiHelper = new UiLifecycleHelper(this, new SessionStatusCallback());
		uiHelper.onCreate(savedInstanceState);

	}

	public void signUp(View v) {
		startActivity(new Intent(this, SignUpActivity.class));
	}

	public void logIn(View v) {
		startActivity(new Intent(this, LogInActivity.class));
	}

	public void facebook(View v) {
		if (Obo.isNetworkConnected()) {
			Session session = Session.getActiveSession();
			if (session != null && session.isOpened()) {
				getFacebookInfo(session);
			} else {
				session.openForRead(new Session.OpenRequest(this)
						.setPermissions(
								Arrays.asList("public_profile", "email"))
						.setCallback(statusCallback));
			}
		}
	}

	private class SessionStatusCallback implements Session.StatusCallback {

		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			if (exception == null) {
				getFacebookInfo(session);
			} else {
				session.closeAndClearTokenInformation();
				Toast.makeText(LoadingMenuActivity.this, "Error: " + exception,
						Toast.LENGTH_LONG).show();
			}
		}

	}

	private void getFacebookInfo(Session session) {
		if (session.getState().isOpened()) {
			Request.newMeRequest(session, new Request.GraphUserCallback() {

				// callback after Graph API response with user
				// object
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user != null) {
						Log(user);
						mId = user.getId();
						mName = user.getFirstName();
						mSurname = user.getLastName();
						mEmail = (String) response.getGraphObject()
								.getProperty("email");

						new FacebookLoginTask().execute();
					} else {
						Toast.makeText(LoadingMenuActivity.this,
								"Error: Can't get facebook info",
								Toast.LENGTH_LONG).show();
					}
				}
			}).executeAsync();
		} else if (session.getState().isClosed()) {
			Toast.makeText(LoadingMenuActivity.this, "Error: Can't login",
					Toast.LENGTH_LONG).show();
		}
	}

	class FacebookLoginTask extends AsyncTask<String, Void, Void> {
		private ProgressDialog loadingDialog;
		private int errorCode = 0;
		private String mResponseString;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loadingDialog = new ProgressDialog(LoadingMenuActivity.this);
			loadingDialog.setMessage("Loading...");
			loadingDialog.show();
		}

		@Override
		protected Void doInBackground(String... files) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("id", mId));
				nameValuePairs.add(new BasicNameValuePair("name", mName));
				nameValuePairs.add(new BasicNameValuePair("surname", mSurname));
				nameValuePairs.add(new BasicNameValuePair("email", mEmail));
				nameValuePairs
						.add(new BasicNameValuePair(
								"phone",
								((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE))
										.getLine1Number()));
				nameValuePairs.add(new BasicNameValuePair("gcm_id",
						GCMRegistrar
								.getRegistrationId(LoadingMenuActivity.this)));
				DefaultHttpClient client = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(Constansts.FACEBOOK_LOGIN_URL);
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
							mName = jsonResponse.getString("name");
							mSurname = jsonResponse.getString("surname");
							mEmail = jsonResponse.getString("email");
							mZip = jsonResponse.getString("zip");
							mPhone = jsonResponse.getString("phone");
							errorCode = -1;
						} else if (jsonResponse.getString("result").equals(
								"empty")) {
							errorCode = 1;
						} else if (jsonResponse.getString("result").equals(
								"error")) {
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
			try {
				loadingDialog.dismiss();
			} catch (Exception e) {
			}
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(LoadingMenuActivity.this);
			prefs.edit().putString("id", mId).commit();
			prefs.edit().putString("username", "").commit();
			prefs.edit().putString("name", mName).commit();
			prefs.edit().putString("email", mEmail).commit();
			prefs.edit().putString("zip", mZip).commit();
			prefs.edit().putString("phone", mPhone).commit();
			prefs.edit().putString("surname", mSurname).commit();
			prefs.edit()
					.putString(
							"photo",
							"http://graph.facebook.com/" + mId
									+ "/picture?type=large").commit();
			Log("http://graph.facebook.com/" + mId + "/picture?type=large");
			if (errorCode == -1) {
				startActivity(new Intent(LoadingMenuActivity.this,
						MainActivity.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				finish();
			} else if (errorCode == 0) {
				Toast.makeText(LoadingMenuActivity.this,
						"Unknown error: " + mResponseString, Toast.LENGTH_LONG)
						.show();
			} else if (errorCode == 1) {
				Toast.makeText(LoadingMenuActivity.this,
						"Error: Some fields are empty", Toast.LENGTH_LONG)
						.show();
			} else if (errorCode == 2) {
				Toast.makeText(LoadingMenuActivity.this,
						"Error: Can't create user", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
		uiHelper.onSaveInstanceState(savedState);
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}
}
