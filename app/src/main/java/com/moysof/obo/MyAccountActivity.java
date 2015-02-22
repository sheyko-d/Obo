package com.moysof.obo;

import java.math.BigDecimal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.moysof.obo.adapter.AccountsListAdapter;
import com.moysof.obo.adapter.MainDrawerAdapter;
import com.moysof.obo.typeface.TypefaceSpan;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PaymentActivity;

public class MyAccountActivity extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private GridView mDrawerGrid;
	private MainDrawerAdapter mDrawerAdapter;
	private ListView mList;
	private AccountsListAdapter mAdapter;
	private TextView mUsernameTxt;
	private SharedPreferences mPrefs;
	protected AlertDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_account);
		
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
				R.string.title_activity_my_account));
		s.setSpan(new TypefaceSpan(this, "Comfortaa-Bold.ttf"), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new RelativeSizeSpan(1.3f), 0, s.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);

		mDrawerGrid = (GridView) findViewById(R.id.left_drawer);
		mDrawerAdapter = new MainDrawerAdapter(5, this, mDrawerGrid,
				mDrawerLayout);
		mDrawerGrid.setAdapter(mDrawerAdapter);

		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		mList = (ListView) findViewById(R.id.accountList);
		mAdapter = new AccountsListAdapter(this);
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(new OnItemClickListener() {

			private AlertDialog mDonateDialog;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					startActivity(new Intent(MyAccountActivity.this,
							EditProfileActivity.class));
					return;
				case 1:
					AlertDialog.Builder builder = new AlertDialog.Builder(
							MyAccountActivity.this);
					builder.setMessage("Are you sure you want to clear history?");
					builder.setPositiveButton("Clear", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							mPrefs.edit().putString("history_array", "[]")
									.commit();
							mDialog.cancel();
							Toast.makeText(MyAccountActivity.this,
									"History is cleared", Toast.LENGTH_SHORT)
									.show();
						}
					});
					builder.setNegativeButton("Cancel", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							mDialog.cancel();
						}
					});
					mDialog = builder.create();
					mDialog.show();
					return;
				case 2:
					try {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri
								.parse("market://details?id="
										+ getPackageName())));
					} catch (android.content.ActivityNotFoundException anfe) {
						startActivity(new Intent(
								Intent.ACTION_VIEW,
								Uri.parse("http://play.google.com/store/apps/details?id="
										+ getPackageName())));
					}
					return;
				case 3:
					AlertDialog.Builder donateDialogBuilder = new AlertDialog.Builder(
							MyAccountActivity.this);
					final View dialogView = getLayoutInflater().inflate(
							R.layout.dialog_donate, null);
					donateDialogBuilder.setView(dialogView);
					donateDialogBuilder.setPositiveButton("Donate",
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									String price = ((EditText) dialogView
											.findViewById(R.id.accountDonateEditTxt))
											.getText().toString();
									if (!price.equals("")) {
										try {
											mDonateDialog.cancel();
											PayPalPayment thingToBuy = getThingToBuy(
													Integer.parseInt(price),
													PayPalPayment.PAYMENT_INTENT_SALE);
											Intent intent = new Intent(
													MyAccountActivity.this,
													PaymentActivity.class);
											intent.putExtra(
													PaymentActivity.EXTRA_PAYMENT,
													thingToBuy);
											startActivityForResult(
													intent,
													Constansts.REQUEST_CODE_PAYMENT);
										} catch (Exception e) {
											Toast.makeText(
													MyAccountActivity.this,
													"Error: Price is incorrect",
													Toast.LENGTH_LONG).show();
										}
									} else {
										Toast.makeText(MyAccountActivity.this,
												"Please, enter price",
												Toast.LENGTH_LONG).show();
									}
								}
							});
					donateDialogBuilder.setNegativeButton("Cancel",
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mDonateDialog.cancel();
								}
							});
					mDonateDialog = donateDialogBuilder.create();
					mDonateDialog.show();
					return;
				case 4:
					startActivity(new Intent(MyAccountActivity.this,
							PrivacyActivity.class));
					return;
				case 5:
					startActivity(new Intent(MyAccountActivity.this,
							SafetyActivity.class));
					return;
				case 6:
					mPrefs.edit().clear().commit();
					startActivity(new Intent(MyAccountActivity.this,
							LoadingMenuActivity.class));
					finish();
					return;
				}
			}
		});

		mUsernameTxt = (TextView) findViewById(R.id.accountUsernameTxt);
		if (!mPrefs.getString("username", "").equals("")) {
			mUsernameTxt.setText(mPrefs.getString("username", ""));
		} else {
			mUsernameTxt.setText(mPrefs.getString("name", "") + " "
					+ mPrefs.getString("surname", ""));

		}
	}

	private PayPalPayment getThingToBuy(int price, String paymentIntent) {
		return new PayPalPayment(new BigDecimal(price + ".00"), "USD",
				"Donate to obo", paymentIntent);
	}

	@Override
	public void onResume() {
		super.onResume();
		String photo = PreferenceManager.getDefaultSharedPreferences(this)
				.getString("photo", "");
		if (!photo.equals("")) {
			// Create global configuration and initialize ImageLoader with this
			// config
			DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
					.cacheInMemory(true).cacheOnDisk(true).build();
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					this).defaultDisplayImageOptions(defaultOptions).build();
			ImageLoader.getInstance().init(config);

			ImageLoader.getInstance().displayImage(
					PreferenceManager.getDefaultSharedPreferences(this)
							.getString("photo", ""),
					((ImageView) findViewById(R.id.accountImg)));
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
	public void onBackPressed() {
		startActivity(new Intent(this, MainActivity.class));
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

}