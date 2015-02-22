package com.moysof.obo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.moysof.obo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ThreadsListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<String> mIdsArrayList = new ArrayList<String>();
	private ArrayList<String> mAuthorsArrayList = new ArrayList<String>();
	private ArrayList<String> mTitlesArrayList = new ArrayList<String>();
	private View mConvertView;
	private ArrayList<String> mCaptionsArrayList = new ArrayList<String>();
	private ArrayList<String> mPhotosArrayList = new ArrayList<String>();
	private ArrayList<String> mTimesArrayList = new ArrayList<String>();
	private DisplayImageOptions mDefaultOptions;

	public ThreadsListAdapter(Context context, ArrayList<String> ids,
			ArrayList<String> authors, ArrayList<String> titles,
			ArrayList<String> captions, ArrayList<String> photos,
			ArrayList<String> times) {
		mContext = context;
		mIdsArrayList = ids;
		mAuthorsArrayList = authors;
		mTitlesArrayList = titles;
		mCaptionsArrayList = captions;
		mPhotosArrayList = photos;
		mTimesArrayList = times;

		mDefaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY)
				.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).defaultDisplayImageOptions(mDefaultOptions).build();
		ImageLoader.getInstance().init(config);

	}

	@Override
	public int getCount() {
		return mIdsArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.item_threads_list, parent,
				false);
		//
		mConvertView = convertView;

		((TextView) convertView.findViewById(R.id.messagesTitleTxt))
				.setText(mTitlesArrayList.get(position));
		((TextView) convertView.findViewById(R.id.messagesTimeTxt))
				.setText(mTimesArrayList.get(position));
		((TextView) convertView.findViewById(R.id.messagesCaptionTxt))
				.setText(" — " + mCaptionsArrayList.get(position));
		((TextView) convertView.findViewById(R.id.messagesAuthorTxt))
				.setText(mAuthorsArrayList.get(position));

		try {
			ImageLoader.getInstance().displayImage(
					mPhotosArrayList.get(position),
					((ImageView) mConvertView.findViewById(R.id.messageImg)),
					mDefaultOptions, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onLoadingComplete(String arg0, View view,
								Bitmap bitmap) {
							view.setBackgroundResource(0);
						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							// TODO Auto-generated method stub

						}
					});
		} catch (Exception e) {
		}

		return convertView;
	}

}
