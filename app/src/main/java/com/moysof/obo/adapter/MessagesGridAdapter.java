package com.moysof.obo.adapter;

import java.util.ArrayList;

import android.content.Context;
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

public class MessagesGridAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<String> mIdsArrayList;
	private ArrayList<Boolean> mMineArrayList;
	private ArrayList<String> mTextsArrayList;
	private ArrayList<String> mTimesArrayList;
	private ArrayList<Integer> mSeenArrayList;
	private String mName;
	private String mAuthorPhoto;
	private DisplayImageOptions mDefaultOptions;

	public MessagesGridAdapter(Context context, String name,
			String authorPhoto, ArrayList<String> ids, ArrayList<Boolean> mine,
			ArrayList<String> texts, ArrayList<String> times,
			ArrayList<Integer> seen) {
		mContext = context;
		mName = name;
		mAuthorPhoto = authorPhoto;
		mIdsArrayList = ids;
		mMineArrayList = mine;
		mTextsArrayList = texts;
		mTimesArrayList = times;
		mSeenArrayList = seen;

		// Create global configuration and initialize ImageLoader with this
		// config
		mDefaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).build();
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
		if (mMineArrayList.get(position)) {
			convertView = inflater.inflate(R.layout.item_messages_out_grid,
					parent, false);
		} else {
			convertView = inflater.inflate(R.layout.item_messages_in_grid,
					parent, false);
		}

		// set up row data from holder
		if (mMineArrayList.get(position)) {
			((TextView) convertView.findViewById(R.id.messagesTextTxt))
					.setText(mTextsArrayList.get(position));
			if (mSeenArrayList.get(position) == 0) {
				convertView.findViewById(R.id.messageLayout)
						.setBackgroundResource(R.color.green_transparent);
			}
		} else {
			((TextView) convertView.findViewById(R.id.messagesTextTxt))
					.setText(mTextsArrayList.get(position));
			((TextView) convertView.findViewById(R.id.messagesAuthorTxt))
					.setText(mName);
			ImageLoader.getInstance().displayImage(
					mAuthorPhoto,
					((ImageView) convertView
							.findViewById(R.id.messagesAuthorImg)),
					mDefaultOptions);
		}
		((TextView) convertView.findViewById(R.id.messagesTimeTxt))
				.setText(mTimesArrayList.get(position));
		return convertView;
	}

}
