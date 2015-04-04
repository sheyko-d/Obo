package com.moysof.obo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoFragment extends Fragment {

    private String mPhoto;
    private ImageView mFullscreenImg;
    private PhotoViewAttacher mAttacher;
    private static final String EXTRA_PHOTO = "photo";

    public static PhotoFragment newInstance(String photo) {
        PhotoFragment f = new PhotoFragment();
        Bundle bdl = new Bundle();
        bdl.putString(EXTRA_PHOTO, photo);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhoto = getArguments().getString(EXTRA_PHOTO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_fullscreen_image,
                container, false);

        mFullscreenImg = (ImageView) v.findViewById(R.id.productFullscreenImg);
        mAttacher = new PhotoViewAttacher(mFullscreenImg);
        mAttacher.setMaximumScale(20);

        // Create global configuration and initialize ImageLoader with this
        // config
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getActivity()).defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
        ImageLoader.getInstance().displayImage(mPhoto, mFullscreenImg,
                new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String arg0, View arg1) {
                    }

                    @Override
                    public void onLoadingFailed(String arg0, View arg1,
                                                FailReason arg2) {
                    }

                    @Override
                    public void onLoadingComplete(String arg0, View arg1,
                                                  Bitmap arg2) {
                        v.findViewById(R.id.productFullscreenProgressBar)
                                .setVisibility(View.GONE);
                        mAttacher.update();
                    }

                    @Override
                    public void onLoadingCancelled(String arg0, View arg1) {
                    }
                });

        return v;
    }

    public static void Log(Object text) {
        Log.d("Log", text + "");
    }

}