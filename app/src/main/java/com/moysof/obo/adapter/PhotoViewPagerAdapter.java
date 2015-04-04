package com.moysof.obo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.moysof.obo.PhotoFragment;

import org.json.JSONArray;
import org.json.JSONException;

public class PhotoViewPagerAdapter extends FragmentStatePagerAdapter {

	private JSONArray mPhotosJSON;

	public PhotoViewPagerAdapter(FragmentManager fm, JSONArray photosJSON) {
		super(fm);

		mPhotosJSON = photosJSON;
	}

	@Override
	public Fragment getItem(int pos) {
		try {
			return PhotoFragment.newInstance(mPhotosJSON.getString(pos));
		} catch (JSONException e) {
			return PhotoFragment.newInstance("");
		}
	}

	@Override
	public int getCount() {
		return mPhotosJSON.length();
	}
}