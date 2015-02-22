package com.moysof.obo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ProductFragment extends Fragment {

	private TextView mTitleTxt;
	private TextView mPriceTxt;
	private TextView mLocationTxt;
	private TextView mDescTxt;
	private TextView mTimeTxt;
	private ImageView mImg;
	private JSONArray mItemsJSON;
	private String mCategory;
	private String mId;
	private JSONObject mContactChoices;
	private String mTitle;
	private String mPrice;
	private String mLocation;
	private String mDistance;
	private long mTimestamp;
	private String mDesc;
	private int mPhotoNum;
	private int mCurrentPhotoPos;
	private JSONArray mPhotosArray;
	private ArrayList<String> mContactOptionsArrayList = new ArrayList<String>();
	private int mPos;
	private ViewGroup mPhotoLayout;
	private TextView mCounterTxt;
	private int mSize;
	private View mV;

	public ProductFragment(int pos, int size, String category, String id,
			JSONObject contactChoices, String title, String price,
			String location, String distance, long timestamp, String desc,
			int photoNum, int currentPhotoPos, JSONArray photosArray) {
		mPos = pos;
		mSize = size;
		mCategory = category;
		mId = id;
		mContactChoices = contactChoices;
		mTitle = title;
		mPrice = price;
		mLocation = location;
		mDistance = distance;
		mTimestamp = timestamp;
		mDesc = desc;
		mPhotoNum = photoNum;
		mCurrentPhotoPos = currentPhotoPos;
		mPhotosArray = photosArray;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mV = inflater.inflate(R.layout.fragment_product, container, false);
		mCounterTxt = (TextView) mV.findViewById(R.id.productCounterTxt);
		mTitleTxt = (TextView) mV.findViewById(R.id.productTitleTxt);
		mPriceTxt = (TextView) mV.findViewById(R.id.productPriceTxt);
		mLocationTxt = (TextView) mV.findViewById(R.id.productLocationTxt);
		mDescTxt = (TextView) mV.findViewById(R.id.productDescTxt);
		mTimeTxt = (TextView) mV.findViewById(R.id.productTimeTxt);
		mImg = (ImageView) mV.findViewById(R.id.productImg);

		mImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				expandImage();
			}
		});

		mV.findViewById(R.id.productContactBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						contact();
					}
				});

		if (ProductActivity.mine) {
			mV.findViewById(R.id.productContactBtn).setEnabled(false);
		}

		mCounterTxt.setText((mPos + 1) + "/" + mSize);
		mTitleTxt.setText(mTitle);
		mPriceTxt.setText("$" + mPrice + " obo");
		mLocationTxt.setText(mLocation + " (" + mDistance + ")");
		mTimeTxt.setText(parseTime(mTimestamp));
		mDescTxt.setText(mDesc);

		try {

			// Create global configuration and initialize ImageLoader with this
			// config
			DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.displayer(new FadeInBitmapDisplayer(300))
					.cacheOnDisk(true).build();
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					getActivity()).defaultDisplayImageOptions(defaultOptions)
					.build();
			ImageLoader.getInstance().init(config);

			ImageLoader.getInstance().displayImage(
					mPhotosArray.getString(mPhotoNum),
					((ImageView) mV.findViewById(R.id.productImg)),
					defaultOptions, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
						}

						@Override
						public void onLoadingComplete(String arg0, View arg1,
								Bitmap arg2) {
							mV.findViewById(R.id.productProgressBar)
									.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
						}
					});

			if (mPhotosArray.length() > 1) {
				mV.findViewById(R.id.productHorizontalScrollView)
						.setVisibility(View.VISIBLE);
				mPhotoLayout = (ViewGroup) mV
						.findViewById(R.id.productPhotoLayout);
				for (int i = 0; i < mPhotosArray.length(); i++) {
					ViewGroup photoFrame = (ViewGroup) (getActivity()
							.getLayoutInflater().inflate(R.layout.photo, null));
					ImageView photo = (ImageView) photoFrame
							.findViewById(R.id.photoImg);
					if (i == mPhotoNum) {
						photo.setBackgroundResource(R.drawable.photo_border);
					}
					ImageLoader.getInstance().displayImage(
							mPhotosArray.getString(i), photo, defaultOptions,
							new ImageLoadingListener() {

								@Override
								public void onLoadingStarted(String arg0,
										View arg1) {
								}

								@Override
								public void onLoadingFailed(String arg0,
										View arg1, FailReason arg2) {
								}

								@Override
								public void onLoadingComplete(String arg0,
										View view, Bitmap arg2) {
									view.setVisibility(View.VISIBLE);
								}

								@Override
								public void onLoadingCancelled(String arg0,
										View arg1) {
								}
							});
					photoFrame.setTag(i);
					photoFrame.setOnClickListener(changePhotoListener);
					mPhotoLayout.addView(photoFrame);
				}
			}
		} catch (JSONException e) {
			Log(e);
		}
		return mV;
	}

	OnClickListener changePhotoListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			changePhoto(v);
		}
	};

	public void expandImage() {
		startActivity(new Intent(getActivity(), FullscreenImageActivity.class)
				.putExtra("pos", mCurrentPhotoPos).putExtra("photos",
						mPhotosArray.toString()));
	}

	public void changePhoto(View v) {
		mPhotoLayout = (ViewGroup) mV.findViewById(R.id.productPhotoLayout);
		Log("change photo");
		for (int i = 0; i < mPhotoLayout.getChildCount(); i++) {
			mPhotoLayout.getChildAt(i).findViewById(R.id.photoImg)
					.setBackgroundResource(0);
		}

		v.findViewById(R.id.photoImg).setBackgroundResource(
				R.drawable.photo_border);
		try {
			// Create global configuration and initialize ImageLoader with this
			// config
			DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
					.cacheInMemory(true).cacheOnDisk(true).build();
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					getActivity()).defaultDisplayImageOptions(defaultOptions)
					.build();
			ImageLoader.getInstance().init(config);
			mCurrentPhotoPos = Integer.parseInt(v.getTag() + "");
			ImageLoader.getInstance().displayImage(
					mPhotosArray.getString(Integer.parseInt(v.getTag() + "")),
					mImg);
		} catch (Exception e) {
			Log(e);
		}

	}

	public void contact() {
		mContactOptionsArrayList.clear();
		if (mContactChoices.has("messenger")) {
			mContactOptionsArrayList.add("obo Messenger");
		}
		if (mContactChoices.has("email")) {
			mContactOptionsArrayList.add("Email");
		}
		if (mContactChoices.has("phone")) {
			mContactOptionsArrayList.add("Phone");
		}

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
				getActivity());

		// Set adapter to list in dialog final ArrayAdapter<String>
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				getActivity(), R.layout.select_dialog_singlechoice,
				mContactOptionsArrayList);

		// Add "Cancel" button to dialog
		dialogBuilder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		dialogBuilder.setSingleChoiceItems(arrayAdapter, -1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (mContactOptionsArrayList.get(which).equals(
								"obo Messenger")) {
							try {
								startActivity(new Intent(getActivity(),
										MessagesContinuedActivity.class)
										.putExtra(
												"recipient_id",
												mContactChoices
														.getString("messenger"))
										.putExtra("item_id", mId));
							} catch (JSONException e) {
								Toast.makeText(getActivity(),
										"Error: Can't contact seller",
										Toast.LENGTH_LONG).show();
							}
						} else if (mContactOptionsArrayList.get(which).equals(
								"Email")) {
							try {
								Intent emailIntent = new Intent(
										Intent.ACTION_SENDTO, Uri.fromParts(
												"mailto", mContactChoices
														.getString("email"),
												null));

								emailIntent.putExtra(Intent.EXTRA_SUBJECT, "\""
										+ mTitle + "\" - obo");
								startActivity(Intent.createChooser(emailIntent,
										"Send email..."));
							} catch (JSONException e) {
								Toast.makeText(getActivity(),
										"Error: Can't send email",
										Toast.LENGTH_LONG).show();
							}
						} else if (mContactOptionsArrayList.get(which).equals(
								"Phone")) {
							try {
								Intent intent = new Intent(Intent.ACTION_DIAL);
								intent.setData(Uri.parse("tel:"
										+ mContactChoices.getString("phone")));
								startActivity(intent);
							} catch (JSONException e) {
								Toast.makeText(getActivity(),
										"Error: Can't send email",
										Toast.LENGTH_LONG).show();
							}
						}
						dialog.dismiss();
					}
				});
		dialogBuilder.show();

	}

	private CharSequence parseTime(long timestamp) {
		int seconds = (int) ((System.currentTimeMillis() / 1000) - timestamp);
		String ending;
		if (seconds < 60) {
			ending = " second";
		} else if (seconds < 60 * 60) {
			seconds = seconds / 60;
			ending = " minute";
		} else if (seconds < 60 * 60 * 24) {
			seconds = seconds / 60 / 60;
			ending = " hour";
		} else if (seconds < 60 * 60 * 24 * 7) {
			seconds = seconds / 60 / 60 / 24;
			ending = " day";
		} else if (seconds < 60 * 60 * 24 * 31) {
			seconds = seconds / 60 / 60 / 24 / 7;
			ending = " week";
		} else if (seconds < 60 * 60 * 24 * 31 * 12) {
			seconds = seconds / 60 / 60 / 24 / 7 / 31;
			ending = " month";
		} else {
			seconds = seconds / 60 / 60 / 24 / 7 / 31 / 12;
			ending = " year";
		}
		if (!(seconds + "").endsWith("1")) {
			ending = ending + "s";
		}
		return seconds + ending;
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}

}