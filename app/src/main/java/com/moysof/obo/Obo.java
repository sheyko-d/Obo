package com.moysof.obo;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalService;

public class Obo extends Application {
	private SharedPreferences mPrefs;
	public static Obo context;
	private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;

	// note that these credentials will differ between live & sandbox
	// environments.
	private static final String CONFIG_CLIENT_ID
			= "AW1ZTvywXtQgpDJJvV6Z4yWQ2MMwsD19lG1VTAemT1SB96v3-DuCYyn95PCWjxAaBPOEpS7Ue-JPU8Hn";

	private static PayPalConfiguration config = new PayPalConfiguration()
			.environment(CONFIG_ENVIRONMENT).clientId(CONFIG_CLIENT_ID);

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && checkSelfPermission
                    (Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)) {
                Location location = lm
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    mPrefs.edit().putString("lat", location.getLatitude() + "")
                            .commit();
                    mPrefs.edit().putString("lng", location.getLongitude() + "")
                            .commit();
                }

				lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
						locationListener);
            }
		} else {
			Location location = lm
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location != null) {
				mPrefs.edit().putString("lat", location.getLatitude() + "")
						.commit();
				mPrefs.edit().putString("lng", location.getLongitude() + "")
						.commit();
			}

			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
					locationListener);
		}

		Intent intent = new Intent(this, PayPalService.class);
		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
		startService(intent);
	}

	private final LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			mPrefs.edit().putString("lat", location.getLatitude() + "")
					.commit();
			mPrefs.edit().putString("lng", location.getLongitude() + "")
					.commit();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}
	};

	public static Context getAppContext() {
		return context;
	}

	public static boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			Toast.makeText(context, "Error: No internet connection", Toast.LENGTH_LONG)
					.show();
			return false;
		} else
			return true;
	}
}
