package com.moysof.obo;

import static com.moysof.obo.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.moysof.obo.CommonUtilities.SENDER_ID;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

public class LoadingActivity extends ActionBarActivity {

	private AsyncTask<Void, Void, Void> mRegisterTask;
	private String mMyRegId;
	public static LoadingActivity activity;

	@SuppressLint({ "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setStatusBarColor(Color.parseColor("#000000"));
		}

		activity = this;

		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(getApplicationContext());
		GCMRegistrar.checkManifest(getApplicationContext());
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		mMyRegId = GCMRegistrar.getRegistrationId(getApplicationContext());
		Log.d("Log", mMyRegId);
		if (mMyRegId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(getApplicationContext(), SENDER_ID);
		} else {
			// Device is already registered on GCM, check server.
			if (GCMRegistrar.isRegisteredOnServer(getApplicationContext())) {
				// Skips registration.
				checkIfLoggedIn();
				// mGridAdapter.notifyDataSetChanged();
			} else {
				// Try to register again, but not in the UI thread. 
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						boolean registered = ServerUtilities.register(getApplicationContext(),
								mMyRegId);
						// At this point all attempts to register with the app
						// server failed, so we need to unregister the device
						// from GCM - the app will try to register again when
						// it is restarted. Note that GCM will send an
						// unregistered callback upon completion, but
						// GCMIntentService.onUnregistered() will ignore it.
						if (!registered) {
							GCMRegistrar.unregister(getApplicationContext());
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}

	}

	public static void checkIfLoggedIn() {
		
		if (PreferenceManager.getDefaultSharedPreferences(activity)
				.getString("id", "").equals("")) {
			activity.startActivity(new Intent(activity,
					LoadingMenuActivity.class));
		} else {
			activity.startActivity(new Intent(activity, MainActivity.class));
		}
		activity.finish();
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("Log",
					"recieved message " + intent.getExtras().getString("data"));
		}
	};

	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		unregisterReceiver(mHandleMessageReceiver);
		GCMRegistrar.onDestroy(getApplicationContext());
		super.onDestroy();
	}

}
