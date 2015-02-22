package com.moysof.obo;

import org.json.JSONArray;
import org.json.JSONException;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class MainFragment extends Fragment {

	private String mTitle;
	private Double mPrice;
	private String mLocation;
	private JSONArray mPhotoArray;
	private Integer mPhotoNum;
	private String mDistance;

	public MainFragment(String title, Double price, String location,
			String photos, Integer photoNum, String distance) {
		mTitle = title;
		mPrice = price;
		mLocation = location;
		mPhotoNum = photoNum;
		mDistance = distance;
		try {
			mPhotoArray = new JSONArray(photos);
		} catch (JSONException e) {
			mPhotoArray = new JSONArray();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.fragment_main, container,
				false);

		((TextView) v.findViewById(R.id.mainFrTitleTxt)).setText(mTitle);
		((TextView) v.findViewById(R.id.mainFrPriceTxt)).setText("$" + mPrice
				+ " obo");
		((TextView) v.findViewById(R.id.mainFrLocationTxt)).setText(mLocation);
		((TextView) v.findViewById(R.id.mainFrDistanceTxt)).setText(mDistance);

		// Create global configuration and initialize ImageLoader with this
		// config
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.imageScaleType(ImageScaleType.EXACTLY)
				.resetViewBeforeLoading(true).bitmapConfig(Bitmap.Config.RGB_565)
				.cacheOnDisk(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getActivity()).defaultDisplayImageOptions(defaultOptions)
				.build();
		ImageLoader.getInstance().init(config);

		try {
			ImageLoader.getInstance().displayImage(
					mPhotoArray.getString(mPhotoNum),
					((ImageView) v.findViewById(R.id.mainFrImg)),
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
								Bitmap bitmap) {
							v.findViewById(R.id.mainFrProgressBar)
									.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
						}
					});
		} catch (JSONException e) {
			Log(e);
		}

		return v;
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}

}