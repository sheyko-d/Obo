package com.moysof.obo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.moysof.obo.R;

public class AccountsListAdapter extends BaseAdapter {

	private Context mContext;
	private String[] mTitles = { "Edit Profile", "Clear History", "Rate App",
			"Donate", "Privacy", "Safety", "Log Out" };

	public AccountsListAdapter(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return mTitles.length;
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
		convertView = inflater.inflate(R.layout.item_myaccount_list, parent,
				false);
		
		((TextView) convertView).setText(mTitles[position]);
		return convertView;
	}

}
