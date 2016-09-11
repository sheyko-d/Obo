/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.moysof.obo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.moysof.obo.adapter.MainDrawerAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
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
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

import static com.moysof.obo.CommonUtilities.SENDER_ID;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "Log";
	private static Integer pos;
	private String mTitle;
	private String mPhoto;
	private Context mContext;
	private static JSONArray itemsJSON;

	public GCMIntentService() {
		super(SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		ServerUtilities.register(context, registrationId);
		LoadingActivity.checkIfLoggedIn();
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			ServerUtilities.unregister(context, registrationId);
		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			Log.i(TAG, "Ignoring unregister callback");
		}
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		mContext = context;
		Toast.makeText(Obo.getAppContext(), "Recieved GCM message",
				Toast.LENGTH_SHORT).show();
		Bundle bundle = intent.getExtras();
		Log.d("Log", "msg recieved");
		for (String key : bundle.keySet()) {
			Object value = bundle.get(key);
			Log.d(TAG, String.format("%s %s (%s)", key, value.toString(), value
					.getClass().getName()));
		}

		try {
			final JSONObject dataJSON = new JSONObject(intent.getExtras()
					.getString("data"));
			String type = dataJSON.getString("type");
			if (type.equals("message")) {
				String threadId = dataJSON.getString("thread_id");
				String senderId = dataJSON.getString("sender_id");
				String itemId = dataJSON.getString("item_id");
				String recipientId = dataJSON.getString("recipient_id");
				String title = dataJSON.getString("title");
				String message = dataJSON.getString("message");
				String author = dataJSON.getString("author");
				String timestamp = dataJSON.getString("timestamp");
				String photo = dataJSON.getString("photo");

				Integer unreadCount = dataJSON.getInt("unread_count");
				sendBroadcast(new Intent(Constansts.INTENT_RECIEVED_MESSAGE)
						.putExtra("unreadCount", unreadCount));
				Log("activity = " + MessagesContinuedActivity.activity);
				if (!senderId.equals(PreferenceManager
						.getDefaultSharedPreferences(this).getString("id", ""))
						& MessagesContinuedActivity.activity == null
						& PreferenceManager
								.getDefaultSharedPreferences(context)
								.getBoolean("msgNotification", true)) {
					generateNotification(context, threadId, itemId,
							recipientId, title, author, message, timestamp,
							photo);
				}
			} else if (type.equals("wanted")) {
				if (PreferenceManager.getDefaultSharedPreferences(context)
						.getBoolean("wantedNotification", true)) {
					pos = dataJSON.getInt("pos");
					mTitle = dataJSON.getString("title");
					mPhoto = dataJSON.getString("photo");
					new GetItemsTask().execute();
				}
			} else if (type.equals("seen")) {
				MainDrawerAdapter.activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						try {
							MainDrawerAdapter.unreadCount = dataJSON
									.getInt("unread_count");
							PreferenceManager
									.getDefaultSharedPreferences(
											getApplicationContext())
									.edit()
									.putInt("unread_count",
											dataJSON.getInt("unread_count"))
									.commit();
							MainDrawerAdapter.adapter.notifyDataSetChanged();
						} catch (JSONException e) {
							Log(e);
							MainDrawerAdapter.unreadCount = 0;
							MainDrawerAdapter.adapter.notifyDataSetChanged();
						}
					}
				});
			}
		} catch (Exception e) {
			Log.d("Log", e + "");
		}
	}

	class GetItemsTask extends AsyncTask<String, Void, Void> {

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
				DefaultHttpClient client = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(Constansts.GET_ITEMS_URL);
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = client.execute(httpPost);
					mResponseString = EntityUtils
							.toString(response.getEntity());
					Log(mResponseString);
					JSONObject jsonResponse = new JSONObject(mResponseString);
					if (jsonResponse.has("result")) {
						if (jsonResponse.getString("result").equals("success")) {
							itemsJSON = new JSONArray(
									jsonResponse.getString("array"));
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
				generateNotification(mContext, mTitle, mPhoto);
			}
		}
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 * 
	 * @param message2
	 */
	private static void generateNotification(final Context context,
			final String title, final String photo) {
		final NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		ImageLoader
				.getInstance()
				.loadImage(
						photo,
						new ImageSize(
								(int) context
										.getResources()
										.getDimension(
												android.R.dimen.notification_large_icon_height),
								(int) context
										.getResources()
										.getDimension(
												android.R.dimen.notification_large_icon_width)),
						new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String arg0, View arg1) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onLoadingFailed(String arg0, View arg1,
									FailReason arg2) {
								Intent notificationIntent = new Intent(context,
										ProductActivity.class)
										.putExtra("json", itemsJSON.toString())
										.putExtra("pos", pos)
										.addFlags(
												Intent.FLAG_ACTIVITY_SINGLE_TOP);
								PendingIntent contentIntent = PendingIntent
										.getActivity(
												context,
												0,
												notificationIntent,
												PendingIntent.FLAG_UPDATE_CURRENT);

								NotificationCompat.Builder builder = new NotificationCompat.Builder(
										context);

								Resources res = context.getResources();
								builder.setContentIntent(contentIntent)
										.setSmallIcon(
												R.drawable.ic_stat_action_shopping_cart)
										.setLargeIcon(
												Bitmap.createScaledBitmap(
														BitmapFactory
																.decodeResource(
																		res,
																		R.drawable.avatar_placeholder),
														(int) res
																.getDimension(android.R.dimen.notification_large_icon_height),
														(int) res
																.getDimension(android.R.dimen.notification_large_icon_width),
														false))
										.setTicker(
												"obo has found a product matching one of your keywords!")
										.setWhen(System.currentTimeMillis())
										.setAutoCancel(true)
										.setContentTitle(
												title + " from wanted list")
										.setContentText(
												"Is currently being sold on obo");
								Notification n = builder.build();
								n.defaults |= Notification.DEFAULT_VIBRATE;
								nm.notify(0, n);
							}

							@Override
							public void onLoadingComplete(String arg0,
									View arg1, Bitmap bitmap) {
								Log("put pos = " + pos);
								Intent notificationIntent = new Intent(context,
										ProductActivity.class)
										.putExtra("json", itemsJSON.toString())
										.putExtra("pos", pos)
										.addFlags(
												Intent.FLAG_ACTIVITY_SINGLE_TOP);
								PendingIntent contentIntent = PendingIntent
										.getActivity(
												context,
												0,
												notificationIntent,
												PendingIntent.FLAG_UPDATE_CURRENT);

								NotificationCompat.Builder builder = new NotificationCompat.Builder(
										context);

								builder.setContentIntent(contentIntent)
										.setSmallIcon(
												R.drawable.ic_stat_action_shopping_cart)
										.setLargeIcon(bitmap)
										.setTicker(
												"obo has found a product matching one of your keywords!")
										.setWhen(System.currentTimeMillis())
										.setAutoCancel(true)
										.setContentTitle(
												title + " from wanted list")
										.setContentText(
												"Is currently being sold on obo");
								Notification n = builder.build();
								n.defaults |= Notification.DEFAULT_VIBRATE;
								nm.notify(0, n);
							}

							@Override
							public void onLoadingCancelled(String arg0,
									View arg1) {
								// TODO Auto-generated method stub

							}
						});

	}

	private static void generateNotification(final Context context,
			final String id, final String itemId, final String recipientId,
			final String title, final String author, final String message,
			final String timestamp, final String photo) {

		Log(id + ", " + itemId + ", " + recipientId);
		final NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// Create global configuration and initialize ImageLoader with this
		// config
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisk(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).defaultDisplayImageOptions(defaultOptions).build();
		ImageLoader.getInstance().init(config);

		Log("start loding notification icon");
		ImageLoader
				.getInstance()
				.loadImage(
						photo,
						new ImageSize(
								(int) context
										.getResources()
										.getDimension(
												android.R.dimen.notification_large_icon_height),
								(int) context
										.getResources()
										.getDimension(
												android.R.dimen.notification_large_icon_width)),
						new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String arg0, View arg1) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onLoadingFailed(String arg0, View arg1,
									FailReason arg2) {
								Intent notificationIntent = new Intent(context,
										MessagesContinuedActivity.class);

								notificationIntent.putExtra("id", id);
								notificationIntent.putExtra("item_id", itemId);
								notificationIntent.putExtra("recipient_id",
										recipientId);
								notificationIntent
										.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
								PendingIntent contentIntent = PendingIntent
										.getActivity(
												context,
												0,
												notificationIntent,
												PendingIntent.FLAG_UPDATE_CURRENT);

								NotificationCompat.Builder builder = new NotificationCompat.Builder(
										context);

								Resources res = context.getResources();
								builder.setContentIntent(contentIntent)
										.setSmallIcon(
												R.drawable.ic_stat_communication_chat)
										.setLargeIcon(
												Bitmap.createScaledBitmap(
														BitmapFactory
																.decodeResource(
																		res,
																		R.drawable.avatar_placeholder),
														(int) res
																.getDimension(android.R.dimen.notification_large_icon_height),
														(int) res
																.getDimension(android.R.dimen.notification_large_icon_width),
														false))
										.setTicker("New obo message")
										.setWhen(
												Long.parseLong(timestamp) * 1000)
										.setAutoCancel(true)
										.setContentTitle(title)
										.setContentText(author + ": " + message);
								Notification n = builder.build();
								n.defaults |= Notification.DEFAULT_VIBRATE;
								nm.notify(0, n);
							}

							@Override
							public void onLoadingComplete(String arg0,
									View arg1, Bitmap bitmap) {
								Log("notif icon is loaded");
								Intent notificationIntent = new Intent(context,
										MessagesContinuedActivity.class);
								notificationIntent.putExtra("id", id);
								notificationIntent.putExtra("item_id", itemId);
								notificationIntent.putExtra("recipient_id",
										recipientId);
								notificationIntent
										.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
								PendingIntent contentIntent = PendingIntent
										.getActivity(
												context,
												0,
												notificationIntent,
												PendingIntent.FLAG_UPDATE_CURRENT);

								NotificationCompat.Builder builder = new NotificationCompat.Builder(
										context);

								builder.setContentIntent(contentIntent)
										.setSmallIcon(
												R.drawable.ic_stat_communication_chat)
										.setLargeIcon(bitmap)
										.setTicker("New obo message")
										.setWhen(
												Long.parseLong(timestamp) * 1000)
										.setAutoCancel(true)
										.setContentTitle(title)
										.setContentText(author + ": " + message);
								Notification n = builder.build();
								n.defaults |= Notification.DEFAULT_VIBRATE;
								nm.notify(0, n);
							}

							@Override
							public void onLoadingCancelled(String arg0,
									View arg1) {
								// TODO Auto-generated method stub

							}
						});
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}
}
