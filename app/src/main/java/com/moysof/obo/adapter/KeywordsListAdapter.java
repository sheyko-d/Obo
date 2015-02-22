package com.moysof.obo.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.moysof.obo.R;

public class KeywordsListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<String> mTitlesArrayList;

	public KeywordsListAdapter(Context context, ArrayList<String> titles) {
		mContext = context;
		mTitlesArrayList = titles;
	}

	@Override
	public int getCount() {
		return mTitlesArrayList.size();
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
	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.item_list_keywords, parent,
				false);
		((TextView) convertView).setText(mTitlesArrayList.get(position));
		return convertView;
	}

}
