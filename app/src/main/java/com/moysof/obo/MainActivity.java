package com.moysof.obo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import android.widget.GridView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.ZoomOutSlideTransformer;
import com.google.android.gcm.GCMRegistrar;
import com.moysof.obo.adapter.MainDrawerAdapter;
import com.moysof.obo.adapter.ViewPagerAdapter;
import com.moysof.obo.typeface.TypefaceSpan;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private GridView mDrawerGrid;
    private MainDrawerAdapter mDrawerAdapter;
    private ViewPager mViewPager;
    public JSONArray mItemsJSON;
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
    private SharedPreferences mPrefs;
    private LocationManager mLm;
    private String mSearchQuery = "";
    private ViewPagerAdapter mPagerAdapter;
    private AlertDialog mRateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (ActivityCompat.checkSelfPermission
                (this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (ActivityCompat.checkSelfPermission
                (this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 0);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (ActivityCompat.checkSelfPermission
                (this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);

        Log(GCMRegistrar.getRegistrationId(this));

        if (getIntent().getBooleanExtra("drawer", false)) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        SpannableString s = new SpannableString(getResources().getString(
                R.string.app_name));
        s.setSpan(new TypefaceSpan(this, "Comfortaa-Bold.ttf"), 0, s.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new RelativeSizeSpan(1.3f), 0, s.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setTitle(s);

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
        mDrawerAdapter = new MainDrawerAdapter(0, this, mDrawerGrid,
                mDrawerLayout);
        mDrawerGrid.setAdapter(mDrawerAdapter);

        if (!(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if (!PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext())
                    .getString("lat", "-1").equals("-1")) {
                if (Obo.isNetworkConnected()) {
                    new GetItemsTask().execute();
                } else {
                    findViewById(R.id.mainViewPager).setVisibility(View.GONE);
                    findViewById(R.id.mainProgressBar).setVisibility(View.GONE);
                    findViewById(R.id.mainPlaceholderLayout).setVisibility(
                            View.VISIBLE);
                }
            } else {
                mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                mLm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = mLm
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null) {
                    location = mLm
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                if (location != null) {
                    mPrefs.edit().putString("lat", location.getLatitude() + "")
                            .commit();
                    mPrefs.edit().putString("lng", location.getLongitude() + "")
                            .commit();
                    if (Obo.isNetworkConnected()) {
                        new GetItemsTask().execute();
                    } else {
                        findViewById(R.id.mainViewPager).setVisibility(View.GONE);
                        findViewById(R.id.mainProgressBar).setVisibility(View.GONE);
                        findViewById(R.id.mainPlaceholderLayout).setVisibility(
                                View.VISIBLE);
                    }
                } else {
                    mLm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                            locationListener);
                    mLm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
                            0, locationListener);
                    findViewById(R.id.mainPlaceholderGPSLayout).setVisibility(
                            View.VISIBLE);
                    findViewById(R.id.mainProgressBar).setVisibility(View.GONE);
                }
            }
        }

        mViewPager = (ViewPager) findViewById(R.id.mainViewPager);
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                mTitlesArrayList, mPricesArrayList, mLocationsArrayList,
                mPhotosArrayList, mPhotoNumsArrayList, mDistancesArrayList);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageTransformer(true, new ZoomOutSlideTransformer());

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        if (prefs.getBoolean("rateDialog", true)) {
            Integer launchCount = prefs.getInt("launchCount", 0);
            launchCount = launchCount + 1;
            if (launchCount >= 10) {
                showRateDialog();
                prefs.edit().putInt("launchCount", 0).commit();
            } else {
                prefs.edit().putInt("launchCount", launchCount).commit();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showRateDialog() {
        AlertDialog.Builder rateDialogBuilder = new AlertDialog.Builder(this);
        rateDialogBuilder
                .setMessage("If you enjoy using obo, please take a moment to rate the app. Thank you for your support!");
        rateDialogBuilder.setPositiveButton("Rate", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse("market://details?id=" + getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id="
                                    + getPackageName())));
                }
            }
        });
        rateDialogBuilder.setNegativeButton("Later", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mRateDialog.cancel();
            }
        });
        rateDialogBuilder.setNeutralButton("Don't show again ",
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceManager
                                .getDefaultSharedPreferences(MainActivity.this)
                                .edit().putBoolean("rateDialog", false)
                                .commit();
                        mRateDialog.cancel();
                    }
                });
        mRateDialog = rateDialogBuilder.create();
        mRateDialog.show();
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (!(ActivityCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)) {

                findViewById(R.id.mainPlaceholderGPSLayout)
                        .setVisibility(View.GONE);
                mPrefs.edit().putString("lat", location.getLatitude() + "")
                        .commit();
                mPrefs.edit().putString("lng", location.getLongitude() + "")
                        .commit();
                if (Obo.isNetworkConnected()) {
                    new GetItemsTask().execute();
                } else {
                    findViewById(R.id.mainViewPager).setVisibility(View.GONE);
                    findViewById(R.id.mainProgressBar).setVisibility(View.GONE);
                    findViewById(R.id.mainPlaceholderLayout).setVisibility(
                            View.VISIBLE);
                }
                // Turning off
                mLm.removeUpdates(locationListener);
            }
            mLm = null;
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

	/*
     * public boolean onCreateOptionsMenu(Menu menu) {
	 * super.onCreateOptionsMenu(menu); MenuInflater inflater =
	 * getMenuInflater(); inflater.inflate(R.menu.main, menu); MenuItem
	 * searchItem = menu.findItem(R.id.search); SearchView searchView =
	 * (SearchView) searchItem.getActionView();
	 * 
	 * SearchManager searchManager = (SearchManager)
	 * getSystemService(Context.SEARCH_SERVICE); if (null != searchManager) {
	 * searchView.setSearchableInfo(searchManager
	 * .getSearchableInfo(getComponentName())); }
	 * searchView.setIconifiedByDefault(false);
	 * 
	 * return true; }
	 */

    class GetItemsTask extends AsyncTask<String, Void, Void> {

        private int errorCode = 0;
        private String mResponseString;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.mainPlaceholderLayout).setVisibility(View.GONE);
            findViewById(R.id.mainViewPager).setVisibility(View.GONE);
            findViewById(R.id.mainProgressBar).setVisibility(View.VISIBLE);
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
                DefaultHttpClient client = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(Constansts.GET_ITEMS_URL);
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
                        mCategoriesIdsArrayList.clear();
                        mCategoriesArrayList.clear();
                        mDescArrayList.clear();
                        mTimestampsArrayList.clear();
                        mDistancesArrayList.clear();
                        mContactChoicesArrayList.clear();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mPagerAdapter.notifyDataSetChanged();
                            }
                        });

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

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mPagerAdapter.notifyDataSetChanged();
                                }
                            });
                        }

                        errorCode = -1;
                    } else if (jsonResponse.getString("result").equals(
                            "empty")) {
                        errorCode = 1;
                    } else {
                        errorCode = 0;
                    }
                }
            } catch (Exception e) {
                Log(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (errorCode == -1) {
                mPagerAdapter.notifyDataSetChanged();
            } else if (errorCode == 0) {
                Toast.makeText(MainActivity.this,
                        "Unknown error: " + mResponseString, Toast.LENGTH_LONG)
                        .show();
            } else if (errorCode == 1) {
                Toast.makeText(MainActivity.this,
                        "Error: Some fields are empty", Toast.LENGTH_LONG)
                        .show();
            }

            if (mIdsArrayList.size() == 0) {
                findViewById(R.id.mainPlaceholderLayout).setVisibility(
                        View.VISIBLE);
            } else {
                findViewById(R.id.mainPlaceholderLayout).setVisibility(
                        View.GONE);
            }

            findViewById(R.id.mainProgressBar).setVisibility(View.GONE);
            findViewById(R.id.mainViewPager).setVisibility(View.VISIBLE);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(searchItem,
                new OnActionExpandListener() {

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        mSearchQuery = "";
                        if (!PreferenceManager
                                .getDefaultSharedPreferences(
                                        getApplicationContext())
                                .getString("lat", "-1").equals("-1")) {
                            if (Obo.isNetworkConnected()) {
                                new GetItemsTask().execute();
                            } else {
                                findViewById(R.id.mainViewPager).setVisibility(View.GONE);
                                findViewById(R.id.mainProgressBar).setVisibility(View.GONE);
                                findViewById(R.id.mainPlaceholderLayout).setVisibility(
                                        View.VISIBLE);
                            }
                        }
                        return true;
                    }
                });
        SearchView mSearchView = (SearchView) MenuItemCompat
                .getActionView(searchItem);
        mSearchView.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchQuery = query;
                if (Obo.isNetworkConnected()) {
                    new GetItemsTask().execute();
                } else {
                    findViewById(R.id.mainViewPager).setVisibility(View.GONE);
                    findViewById(R.id.mainProgressBar).setVisibility(View.GONE);
                    findViewById(R.id.mainPlaceholderLayout).setVisibility(
                            View.VISIBLE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        });

        final SearchAutoComplete searchAutoComplete = (SearchAutoComplete) mSearchView
                .findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(Color.parseColor("#55ffffff"));

        ViewGroup searchPlate = (ViewGroup) mSearchView
                .findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchPlate.setBackgroundResource(R.drawable.searchview_patch);

        return true;
    }

    public void product(View v) {
        int pos = mViewPager.getCurrentItem();

        startActivity(new Intent(this, ProductActivity.class).putExtra("json",
                mItemsJSON.toString()).putExtra("pos", pos));
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

    public void settings(View v) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    public static void Log(Object text) {
        Log.d("Log", text + "");
    }

}
