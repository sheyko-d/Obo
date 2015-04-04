package com.moysof.obo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moysof.obo.typeface.TypefaceSpan;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends ActionBarActivity {

	private TextView mNameEdittxt;
	private TextView mSurnameEdittxt;
	private TextView mEmailEdittxt;
	private TextView mZipEdittxt;
	private TextView mPhoneEdittxt;
	private TextView mPasswordEdittxt;
	private String mName;
	private String mSurname;
	private String mEmail;
	private String mZip;
	private String mPhone;
	private String mPassword;
	private String mPhotoName;
	private Button mPhotoBtn;
	private int mRotateDegrees = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);

		overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setIcon(R.drawable.ic_back);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);

		SpannableString s = new SpannableString(getResources().getString(
				R.string.title_activity_edit_profile));
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

		String photo = PreferenceManager.getDefaultSharedPreferences(this)
				.getString("photo", "");
		if (!photo.equals("")) {
			ImageLoader.getInstance().displayImage(
					PreferenceManager.getDefaultSharedPreferences(this)
							.getString("photo", ""),
					((ImageView) findViewById(R.id.editImg)));
		} else {
			findViewById(R.id.editRotateLeft).setVisibility(View.GONE);
			findViewById(R.id.editRotateRight).setVisibility(View.GONE);
		}

		mNameEdittxt = (TextView) findViewById(R.id.editNameEdittxt);
		mSurnameEdittxt = (TextView) findViewById(R.id.editSurnameEdittxt);
		mEmailEdittxt = (TextView) findViewById(R.id.editEmailEdittxt);
		mZipEdittxt = (TextView) findViewById(R.id.editZipEdittxt);
		mPhoneEdittxt = (TextView) findViewById(R.id.editPhoneEdittxt);
		mPasswordEdittxt = (TextView) findViewById(R.id.editPasswordEdittxt);

		mPhotoBtn = (Button) findViewById(R.id.editPhotoBtn);
		mPhotoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				registerForContextMenu(mPhotoBtn);
				openContextMenu(mPhotoBtn);
				unregisterForContextMenu(mPhotoBtn);
			}
		});

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		mName = prefs.getString("name", "");
		mSurname = prefs.getString("surname", "");
		mEmail = prefs.getString("email", "");
		mZip = prefs.getString("zip", "");
		mPhone = prefs.getString("phone", "");
		mNameEdittxt.setText(mName);
		mSurnameEdittxt.setText(mSurname);
		mEmailEdittxt.setText(mEmail);
		mZipEdittxt.setText(mZip);
		mPhoneEdittxt.setText(mPhone);
		
		if (!Obo.isNetworkConnected()) {
			findViewById(R.id.editRotateRight).setVisibility(View.GONE);
			findViewById(R.id.editRotateLeft).setVisibility(View.GONE);
			mPhotoBtn.setEnabled(false);
			mPhotoBtn.setAlpha(0.6f);
			findViewById(R.id.editBtn).setEnabled(false);
		}
	}

	public void save(View v) {
		mName = mNameEdittxt.getText().toString();
		mSurname = mSurnameEdittxt.getText().toString();
		mEmail = mEmailEdittxt.getText().toString();
		mZip = mZipEdittxt.getText().toString();
		mPhone = mPhoneEdittxt.getText().toString();
		mPassword = mPasswordEdittxt.getText().toString();
		if (mName.equals("")) {
			Toast.makeText(EditProfileActivity.this, "Error: Name is empty",
					Toast.LENGTH_LONG).show();
		} else if (mSurname.equals("")) {
			Toast.makeText(EditProfileActivity.this, "Error: Surname is empty",
					Toast.LENGTH_LONG).show();
		} else {
			new SaveInfoTask().execute();
		}
	}

	class SaveInfoTask extends AsyncTask<String, Void, Void> {

		private ProgressDialog loadingDialog;
		private int errorCode = 0;
		private String mResponseString;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loadingDialog = new ProgressDialog(EditProfileActivity.this);
			loadingDialog.setMessage("Loading...");
			loadingDialog.show();
		}

		@Override
		protected Void doInBackground(String... files) {
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("id",
						PreferenceManager.getDefaultSharedPreferences(
								EditProfileActivity.this).getString("id", "")));
				nameValuePairs.add(new BasicNameValuePair("name", mName));
				nameValuePairs.add(new BasicNameValuePair("surname", mSurname));
				nameValuePairs.add(new BasicNameValuePair("email", mEmail));
				nameValuePairs.add(new BasicNameValuePair("zip", mZip));
				nameValuePairs.add(new BasicNameValuePair("phone", mPhone));
				nameValuePairs.add(new BasicNameValuePair("rotation_degrees",
						mRotateDegrees + ""));
				nameValuePairs
						.add(new BasicNameValuePair("password", mPassword));
				DefaultHttpClient client = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(Constansts.POST_INFO_URL);
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = client.execute(httpPost);
					mResponseString = EntityUtils
							.toString(response.getEntity());
					Log(mResponseString);
					JSONObject jsonResponse = new JSONObject(mResponseString);
					if (jsonResponse.has("result")) {
						if (jsonResponse.getString("result").equals("success")) {
							String photo = jsonResponse.getString("photo");
							if (!photo.equals("")) {
								PreferenceManager
										.getDefaultSharedPreferences(
												EditProfileActivity.this)
										.edit().putString("photo", photo)
										.commit();
							}
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
			loadingDialog.cancel();
			if (errorCode == -1) {
				if (mRotateDegrees != 0) {
					File imageFile = ImageLoader
							.getInstance()
							.getDiscCache()
							.get(PreferenceManager.getDefaultSharedPreferences(
									EditProfileActivity.this).getString(
									"photo", ""));
					if (imageFile.exists()) {
						imageFile.delete();
					}
				}
				MemoryCacheUtils.removeFromCache(PreferenceManager
						.getDefaultSharedPreferences(EditProfileActivity.this)
						.getString("photo", ""), ImageLoader.getInstance()
						.getMemoryCache());
				Toast.makeText(EditProfileActivity.this,
						"Profile info is updated", Toast.LENGTH_LONG).show();
				finish();
			} else if (errorCode == 0) {
				Toast.makeText(EditProfileActivity.this,
						"Unknown error: " + mResponseString, Toast.LENGTH_LONG)
						.show();
			} else if (errorCode == 1) {
				Toast.makeText(EditProfileActivity.this,
						"Error: Some fields are empty", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_profile_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_gallery:
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(intent, "Select Picture"),
					Constansts.PICK_IMAGE);
			return true;
		case R.id.action_camera:
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			mPhotoName = System.currentTimeMillis() + ".jpg";
			File folder = new File(Environment.getExternalStorageDirectory()
					+ "/obo/");
			if (!folder.exists()) {
				folder.mkdir();
			}
			File photo = new File(Environment.getExternalStorageDirectory()
					+ "/obo/", mPhotoName);

			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
			startActivityForResult(intent, Constansts.TAKE_PHOTO);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == Constansts.PICK_IMAGE & data != null) {
				Uri selectedImageURI = data.getData();
				new PostPhotoTask().execute(getPath(getApplicationContext(),
						selectedImageURI));
			} else if (requestCode == Constansts.TAKE_PHOTO) {
				File photo = new File(Environment.getExternalStorageDirectory()
						+ "/obo/" + mPhotoName);
				new PostPhotoTask().execute(photo.getAbsolutePath());
			}
			mRotateDegrees = 0;
			findViewById(R.id.editRotateLeft).setVisibility(View.VISIBLE);
			findViewById(R.id.editRotateRight).setVisibility(View.VISIBLE);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	class PostPhotoTask extends AsyncTask<String, Void, Void> {
		private ProgressDialog loadingDialog;
		private int errorCode = 0;
		private String mResponseString;
		private String mPhoto;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loadingDialog = new ProgressDialog(EditProfileActivity.this);
			loadingDialog.setMessage("Loading...");
			loadingDialog.show();
		}

		@Override
		protected Void doInBackground(String... photos) {
			try {
				MultipartEntityBuilder multipartEntity = MultipartEntityBuilder
						.create();

				multipartEntity.addPart("id", new StringBody(PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext())
						.getString("id", ""), ContentType.TEXT_PLAIN));

				multipartEntity.addPart("photo", new FileBody(new File(
						photos[0])));
				DefaultHttpClient client = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(Constansts.POST_PHOTO_URL);
				try {
					httpPost.setEntity(multipartEntity.build());
					HttpResponse response = client.execute(httpPost);
					mResponseString = EntityUtils
							.toString(response.getEntity());
					Log(mResponseString);
					JSONObject jsonResponse = new JSONObject(mResponseString);
					if (jsonResponse.has("result")) {
						if (jsonResponse.getString("result").equals("success")) {
							mPhoto = jsonResponse.getString("photo");
							errorCode = -1;
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
				Toast.makeText(EditProfileActivity.this, "Photo is updated",
						Toast.LENGTH_LONG).show();
				findViewById(R.id.editProgressBar).setVisibility(View.VISIBLE);
				ImageLoader.getInstance().displayImage(mPhoto,
						((ImageView) findViewById(R.id.editImg)),
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
									View arg1, Bitmap arg2) {
								findViewById(R.id.editProgressBar)
										.setVisibility(View.GONE);
							}

							@Override
							public void onLoadingCancelled(String arg0,
									View arg1) {
							}
						});
				PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext())
						.edit().putString("photo", mPhoto).commit();
			} else if (errorCode == 0) {
				Toast.makeText(EditProfileActivity.this,
						"Unknown error: " + mResponseString, Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}

			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	public void rotateLeft(View v) {
		mRotateDegrees += 90;
		findViewById(R.id.editImg).setRotation(mRotateDegrees);
	}

	public void rotateRight(View v) {
		mRotateDegrees += 90;
		findViewById(R.id.editImg).setRotation(mRotateDegrees);
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