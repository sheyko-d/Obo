package com.moysof.obo.adapter;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.moysof.obo.R;

public class SearchListAdapter extends BaseAdapter {

	private Context mContext;
	private JSONArray mHistoryJSONArray;

	public SearchListAdapter(Context context, JSONArray historyJSONArray) {
		mContext = context;
		mHistoryJSONArray = historyJSONArray;
	}

	@Override
	public int getCount() {
		return mHistoryJSONArray.length();
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
		convertView = inflater
				.inflate(R.layout.item_list_search, parent, false);

		try {
			((TextView) convertView.findViewById(R.id.searchTitleTxt))
					.setText(mHistoryJSONArray.getString(position));
		} catch (JSONException e) {
			((TextView) convertView.findViewById(R.id.searchTitleTxt))
					.setText("");
		}
		return convertView;
	}

}
