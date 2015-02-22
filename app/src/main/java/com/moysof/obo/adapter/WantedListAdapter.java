package com.moysof.obo.adapter;

import java.util.ArrayList;

import org.json.JSONArray;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.moysof.obo.Holder;
import com.moysof.obo.R;

public class WantedListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<String> mIdsArrayList;
	private ArrayList<String> mTitlesArrayList;
	private ArrayList<String> mCountArrayList;
	private ArrayList<String> mKeywordsArrayList;

	public WantedListAdapter(Context context, ArrayList<String> titles,
			ArrayList<String> ids, ArrayList<String> count,
			ArrayList<String> keywords) {
		mContext = context;
		mIdsArrayList = ids;
		mTitlesArrayList = titles;
		mCountArrayList = count;
		mKeywordsArrayList = keywords;
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
		Holder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_list_sell, parent,
					false);
			holder = new Holder(convertView);
			convertView.setTag(holder); // setting Holder as arbitrary object
										// for row
		} else { // view recycling
			// row already contains Holder object
			holder = (Holder) convertView.getTag();
		}

		// set up row data from holder
		((TextView) convertView.findViewById(R.id.sellTitleTxt))
				.setText(mTitlesArrayList.get(position));
		if (!mCountArrayList.get(position).equals("-1")) {
			((TextView) convertView.findViewById(R.id.sellDurationTxt))
					.setText(parseArray(mKeywordsArrayList.get(position)));
		} else {
			convertView.findViewById(R.id.sellInactiveTxt).setVisibility(
					View.VISIBLE);
			convertView.findViewById(R.id.sellDurationTxt).setVisibility(
					View.GONE);
		}
		((TextView) convertView.findViewById(R.id.sellPosTxt))
				.setText((position + 1) + "");
		if ((mCountArrayList.get(position) + "").endsWith("1")) {
			((TextView) convertView.findViewById(R.id.sellViewsTxt))
					.setText(mCountArrayList.get(position) + " keyword");
		} else {
			((TextView) convertView.findViewById(R.id.sellViewsTxt))
					.setText(mCountArrayList.get(position) + " keywords");
		}
		return convertView;
	}

	private String parseArray(String string) {
		try {
			StringBuilder keywordsBuilder = new StringBuilder();
			JSONArray keywords = new JSONArray(string);
			for (int i = 0; i < keywords.length(); i++) {
				keywordsBuilder.append(keywords.get(i));
				if (i<keywords.length()-1){
					keywordsBuilder.append(", ");
				}
			}
			return keywordsBuilder.toString();
		} catch (Exception e) {
			return "";
		}
	}

}
