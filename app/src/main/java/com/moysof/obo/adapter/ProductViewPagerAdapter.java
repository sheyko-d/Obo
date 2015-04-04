package com.moysof.obo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.moysof.obo.ProductFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductViewPagerAdapter extends FragmentStatePagerAdapter {

	private ArrayList<String> mCategoriesArrayList = new ArrayList<String>();
	private ArrayList<String> mIdsArrayList = new ArrayList<String>();
	private ArrayList<String> mContactOptionsArrayList = new ArrayList<String>();
	private ArrayList<String> mTitlesArrayList = new ArrayList<String>();
	private ArrayList<String> mPricesArrayList = new ArrayList<String>();
	private ArrayList<String> mLocationsArrayList = new ArrayList<String>();
	private ArrayList<String> mDistancesArrayList = new ArrayList<String>();
	private ArrayList<Long> mTimestampsArrayList = new ArrayList<Long>();
	private ArrayList<String> mDescsArrayList = new ArrayList<String>();
	private ArrayList<Integer> mPhotoNumsArrayList = new ArrayList<Integer>();
	private ArrayList<String> mPhotosArrayList = new ArrayList<String>();
	private String mCategory;
	private String mId;
	private JSONObject mContactChoices;
	private String mTitle;
	private String mPrice;
	private String mLocation;
	private String mDistance;
	private long mTimestamp;
	private String mDesc;
	private int mPhotoNum;
	private int mCurrentPhotoPos;
	private JSONArray mPhotosArray;

	public ProductViewPagerAdapter(FragmentManager fm,
			ArrayList<String> categoriesArrayList,
			ArrayList<String> idsArrayList,
			ArrayList<String> contactOptionsArrayList,
			ArrayList<String> titlesArrayList,
			ArrayList<String> pricesArrayList,
			ArrayList<String> locationsArrayList,
			ArrayList<String> distancesArrayList,
			ArrayList<Long> timestampsArrayList,
			ArrayList<String> descsArrayList,
			ArrayList<Integer> photoNumsArrayList,
			ArrayList<String> photosArrayList) {
		super(fm);
		mCategoriesArrayList = categoriesArrayList;
		mIdsArrayList = idsArrayList;
		mContactOptionsArrayList = contactOptionsArrayList;
		mTitlesArrayList = titlesArrayList;
		mPricesArrayList = pricesArrayList;
		mLocationsArrayList = locationsArrayList;
		mDistancesArrayList = distancesArrayList;
		mTimestampsArrayList = timestampsArrayList;
		mDescsArrayList = descsArrayList;
		mPhotoNumsArrayList = photoNumsArrayList;
		mPhotosArrayList = photosArrayList;
	}

	@Override
	public Fragment getItem(int pos) {
		try {
			mCategory = mCategoriesArrayList.get(pos);
			mId = mIdsArrayList.get(pos);
			mContactChoices = new JSONObject(mContactOptionsArrayList.get(pos));
			mTitle = mTitlesArrayList.get(pos);
			mPrice = mPricesArrayList.get(pos);
			mLocation = mLocationsArrayList.get(pos);
			mDistance = mDistancesArrayList.get(pos);
			mTimestamp = mTimestampsArrayList.get(pos);
			mDesc = mDescsArrayList.get(pos);
			mPhotoNum = mPhotoNumsArrayList.get(pos);
			mCurrentPhotoPos = mPhotoNum;
			mPhotosArray = new JSONArray(mPhotosArrayList.get(pos));
			return ProductFragment.newInstance(pos, mIdsArrayList.size(), mCategory,
					mId, mContactChoices, mTitle, mPrice, mLocation, mDistance,
					mTimestamp, mDesc, mPhotoNum, mCurrentPhotoPos,
					mPhotosArray);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public int getCount() {
		return mIdsArrayList.size();
	}
}