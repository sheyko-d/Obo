package com.moysof.obo.adapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.moysof.obo.Constansts;
import com.moysof.obo.Holder;
import com.moysof.obo.MainActivity;
import com.moysof.obo.MessagesActivity;
import com.moysof.obo.MyAccountActivity;
import com.moysof.obo.Obo;
import com.moysof.obo.R;
import com.moysof.obo.SearchActivity;
import com.moysof.obo.SellActivity;
import com.moysof.obo.SettingsActivity;
import com.moysof.obo.WantedActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

public class MainDrawerAdapter extends BaseAdapter {

	public static Activity activity;
	private Integer mPos;
	private String[] mTitlesArray;
	private Integer[] mIconsArray = { R.drawable.ic_drawer_home,
			R.drawable.ic_drawer_search, R.drawable.ic_drawer_sell,
			R.drawable.ic_drawer_wanted, R.drawable.ic_drawer_messages,
			R.drawable.ic_drawer_account, R.drawable.ic_drawer_settings };
	private DrawerLayout mDrawerLayout;
	public static int unreadCount;
	private GridView mGridView;
	public static MainDrawerAdapter adapter;

	public MainDrawerAdapter(Integer pos, final Activity activity,
			GridView gridView, DrawerLayout drawerLayout) {
		adapter = this;
		MainDrawerAdapter.activity = activity;
		mGridView = gridView;
		mDrawerLayout = drawerLayout;
		mPos = pos;
		mTitlesArray = activity.getResources().getStringArray(
				R.array.main_drawer);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									final int position, long id) {
				if (mPos == position) {
					return;
				}
				mDrawerLayout.closeDrawers();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						switch (position) {
							case 0:
								activity.startActivity(new Intent(activity,
										MainActivity.class)
										.putExtra("drawer", true));
								activity.finish();
								return;
							case 1:
								activity.startActivity(new Intent(activity,
										SearchActivity.class).putExtra("drawer",
										true));
								activity.finish();
								return;
							case 2:
								activity.startActivity(new Intent(activity,
										SellActivity.class)
										.putExtra("drawer", true));
								activity.finish();
								return;
							case 3:
								activity.startActivity(new Intent(activity,
										WantedActivity.class).putExtra("drawer",
										true));
								activity.finish();
								return;
							case 4:
								activity.startActivity(new Intent(activity,
										MessagesActivity.class).putExtra("drawer",
										true));
								activity.finish();
								return;
							case 5:
								activity.startActivity(new Intent(activity,
										MyAccountActivity.class).putExtra("drawer",
										true));
								activity.finish();
								return;
							case 6:
								activity.startActivity(new Intent(activity,
										SettingsActivity.class).putExtra("drawer",
										true));
								activity.finish();
								return;
						}
					}
				}, 200);
			}
		});

		IntentFilter filter = new IntentFilter();
		filter.addAction(Constansts.INTENT_RECIEVED_MESSAGE);
		Obo.getAppContext().registerReceiver(receiver, filter);

		unreadCount = PreferenceManager.getDefaultSharedPreferences(activity)
				.getInt("unread_count", 0);
		activity.sendBroadcast(new Intent(Constansts.INTENT_RECIEVED_MESSAGE)
				.putExtra("unreadCount", unreadCount));

		new GetUnreadNumTask().execute();
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			unreadCount = intent.getExtras().getInt("unreadCount");
			Log.d("Log", "adapter mUnreadCount = " + unreadCount);
			mTitlesArray = null;
			mTitlesArray = activity.getResources().getStringArray(
					R.array.main_drawer);
			notifyDataSetChanged();
		}
	};

	@Override
	public int getCount() {
		return mTitlesArray.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	class GetUnreadNumTask extends AsyncTask<String, Void, Void> {

		private int errorCode = 0;
		private String mResponseString;
		private int mNewCount;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... files) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				nameValuePairs.add(new BasicNameValuePair("id",
						PreferenceManager.getDefaultSharedPreferences(activity)
								.getString("id", "")));
				DefaultHttpClient client = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(Constansts.GET_UNREAD_NUM_URL);
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = client.execute(httpPost);
					mResponseString = EntityUtils
							.toString(response.getEntity());
					Log(mResponseString);
					JSONObject jsonResponse = new JSONObject(mResponseString);
					if (jsonResponse.has("result")) {
						if (jsonResponse.getString("result").equals("success")) {
							mNewCount = jsonResponse.getInt("unread_count");
							errorCode = -1;
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
				if (unreadCount != mNewCount) {
					PreferenceManager.getDefaultSharedPreferences(activity)
							.edit().putInt("unread_count", mNewCount).commit();
					activity.sendBroadcast(new Intent(Constansts.INTENT_RECIEVED_MESSAGE)
							.putExtra("unreadCount", mNewCount));
				}
			}
		}
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Holder holder;
		if (convertView == null) {
			if (position == mPos) {
				convertView = inflater.inflate(
						R.layout.item_drawer_main_selected, parent, false);
			} else {
				convertView = inflater.inflate(R.layout.item_drawer_main,
						parent, false);
			}
			holder = new Holder(convertView);
			convertView.setTag(holder); // setting Holder as arbitrary object
										// for row
		} else { // view recycling
			// row already contains Holder object
			holder = (Holder) convertView.getTag();
		}

		if (position == 4) {
			int unreadCount = PreferenceManager.getDefaultSharedPreferences(
					activity).getInt("unread_count", 0);
			if (unreadCount != 0) {
				convertView.findViewById(R.id.drawerCountTxt).setVisibility(
						View.VISIBLE);
				((TextView) convertView.findViewById(R.id.drawerCountTxt))
						.setText(unreadCount + "");
				if (unreadCount > 9) {
					((TextView) convertView.findViewById(R.id.drawerCountTxt))
							.setTextSize(10);
				}
			} else {
				convertView.findViewById(R.id.drawerCountTxt).setVisibility(
						View.GONE);
			}
		}

		// set up row data from holder
		((TextView) convertView.findViewById(R.id.drawerTitle))
				.setText(mTitlesArray[position]);
		((ImageView) convertView.findViewById(R.id.drawerIcon))
				.setImageResource(mIconsArray[position]);

		return convertView;
	}

}
