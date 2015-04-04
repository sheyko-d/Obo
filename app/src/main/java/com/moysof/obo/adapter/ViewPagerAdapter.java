package com.moysof.obo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.moysof.obo.MainFragment;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

	private ArrayList<String> mTitlesArrayList;
	private ArrayList<Double> mPricesArrayList;
	private ArrayList<String> mLocationsArrayList;
	private ArrayList<String> mPhotosArrayList;
	private ArrayList<Integer> mPhotoNumsArrayList;
	private ArrayList<String> mDistancesArrayList;

	public ViewPagerAdapter(FragmentManager fm, ArrayList<String> titles,
			ArrayList<Double> prices, ArrayList<String> locations,
			ArrayList<String> photos, ArrayList<Integer> photoNums,
			ArrayList<String> distances) {
		super(fm);

		mTitlesArrayList = titles;
		mPricesArrayList = prices;
		mLocationsArrayList = locations;
		mPhotosArrayList = photos;
		mPhotoNumsArrayList = photoNums;
		mDistancesArrayList = distances;
	}

	@Override
	public Fragment getItem(int pos) {
		return MainFragment.newInstance(mTitlesArrayList.get(pos),
				mPricesArrayList.get(pos), mLocationsArrayList.get(pos),
				mPhotosArrayList.get(pos), mPhotoNumsArrayList.get(pos),
				mDistancesArrayList.get(pos));
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return mTitlesArrayList.size();
	}
}