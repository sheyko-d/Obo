package com.moysof.obo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.moysof.obo.Holder;
import com.moysof.obo.R;

public class SellListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<String> mIdsArrayList;
	private ArrayList<String> mTitlesArrayList;
	private ArrayList<String> mOnSaleArrayList;
	private ArrayList<String> mIsSoldArrayList;
	private ArrayList<Integer> mViewsArrayList;

	public SellListAdapter(Context context, ArrayList<String> titles,
			ArrayList<String> ids, ArrayList<String> onSale,
			ArrayList<String> isSold, ArrayList<Integer> views) {
		mContext = context;
		mIdsArrayList = ids;
		mTitlesArrayList = titles;
		mOnSaleArrayList = onSale;
		mIsSoldArrayList = isSold;
		mViewsArrayList = views;
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

		if (mIsSoldArrayList.get(position).equals("1")) {
			convertView.findViewById(R.id.sellSoldTxt).setVisibility(
					View.VISIBLE);
			((TextView) convertView.findViewById(R.id.sellSoldTxt))
					.setText("SOLD");
			convertView.findViewById(R.id.sellDurationTxt).setVisibility(
					View.GONE);
		} else if (!mOnSaleArrayList.get(position).equals("-1")) {
			((TextView) convertView.findViewById(R.id.sellDurationTxt))
					.setText(mOnSaleArrayList.get(position));
		} else {
			convertView.findViewById(R.id.sellInactiveTxt).setVisibility(
					View.VISIBLE);
			convertView.findViewById(R.id.sellDurationTxt).setVisibility(
					View.GONE);
		}

		((TextView) convertView.findViewById(R.id.sellPosTxt))
				.setText((position + 1) + "");
		if ((mViewsArrayList.get(position) + "").endsWith("1")) {
			((TextView) convertView.findViewById(R.id.sellViewsTxt))
					.setText(mViewsArrayList.get(position) + " view");
		} else {
			((TextView) convertView.findViewById(R.id.sellViewsTxt))
					.setText(mViewsArrayList.get(position) + " views");
		}
		return convertView;
	}

}
