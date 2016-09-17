package com.moysof.obo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.moysof.obo.typeface.TypefaceSpan;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.entity.mime.content.StringBody;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class SellAddActivity extends ActionBarActivity {

    private CheckBox mPhotoCheckbox1;
    private CheckBox mPhotoCheckbox2;
    private CheckBox mPhotoCheckbox3;
    private CheckBox mPhotoCheckbox4;
    private Spinner mCategoriesSpinner;
    private TextView mDurationTxt;
    private String[] mDurationArray;
    private String[] mDurationFullArray;
    private AlertDialog mDurationDialog;
    private int mWeeksNum = 1;
    private int mCurrentPhotoPos;
    private ImageView mPhotoImg1;
    private ImageView mPhotoImg2;
    private ImageView mPhotoImg3;
    private ImageView mPhotoImg4;
    private TextView mCostTxt;
    private Uri mPhotoUri;
    private String mPhotoName;
    private JSONObject mContactChoices = new JSONObject();
    private Integer mCategory;
    private CheckBox mContactCheckbox1;
    private CheckBox mContactCheckbox2;
    private CheckBox mContactCheckbox3;
    private Boolean mMessengerIsChecked = false;
    private Boolean mEmailIsChecked = false;
    private Boolean mPhoneIsChecked = false;
    private ArrayList<String> mPhotos = new ArrayList<>();
    private EditText mEmailEditTxt;
    private EditText mPhoneEditTxt;
    private ArrayList<String> mIdsArrayList = new ArrayList<String>();
    private ArrayList<String> mTitlesArrayList = new ArrayList<String>();
    private int mMainPhotoPos = -1;
    private SharedPreferences mPrefs;
    private String mPhone = "";
    private String mEmail = "";
    private String mTitle;
    private String mPrice;
    private String mDesc;
    private String mPhoto1 = "";
    private String mPhoto2 = "";
    private String mPhoto3 = "";
    private String mPhoto4 = "";
    private boolean mEditMode = false;
    private String mCategoryTitle;
    public int mCategoryPos = -1;
    private int mDuration;
    private String mItemId = "";
    private int mPhoto1Angle = 0;
    private int mPhoto2Angle = 0;
    private int mPhoto3Angle = 0;
    private int mPhoto4Angle = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_add);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (ActivityCompat.checkSelfPermission
                (this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 0);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (ActivityCompat.checkSelfPermission
                (this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.ic_back);
        actionBar.setDisplayShowHomeEnabled(false);

        SpannableString s = new SpannableString(getResources().getString(
                R.string.title_activity_sell));
        s.setSpan(new TypefaceSpan(this, "Comfortaa-Bold.ttf"), 0, s.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new RelativeSizeSpan(1.2f), 0, s.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setTitle(s);

        mEmailEditTxt = (EditText) findViewById(R.id.sellEmailEditTxt);
        mPhoneEditTxt = (EditText) findViewById(R.id.sellPhoneEditTxt);

        mPhotoCheckbox1 = (CheckBox) findViewById(R.id.sellPhotoCheckbox1);
        mPhotoCheckbox2 = (CheckBox) findViewById(R.id.sellPhotoCheckbox2);
        mPhotoCheckbox3 = (CheckBox) findViewById(R.id.sellPhotoCheckbox3);
        mPhotoCheckbox4 = (CheckBox) findViewById(R.id.sellPhotoCheckbox4);

        mContactCheckbox1 = (CheckBox) findViewById(R.id.sellContactCheckbox1);
        mContactCheckbox2 = (CheckBox) findViewById(R.id.sellContactCheckbox2);
        mContactCheckbox3 = (CheckBox) findViewById(R.id.sellContactCheckbox3);
        mContactCheckbox1
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        mMessengerIsChecked = isChecked;
                    }
                });
        mContactCheckbox2
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        mEmailIsChecked = isChecked;
                        if (isChecked) {
                            if (mEmail.equals("")) {
                                mEmailEditTxt.setVisibility(View.VISIBLE);
                            }
                        } else {
                            mEmailEditTxt.setVisibility(View.GONE);
                        }
                    }
                });
        mContactCheckbox3
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        mPhoneIsChecked = isChecked;
                        if (isChecked) {
                            if (mPhone.equals("")) {
                                mPhoneEditTxt.setVisibility(View.VISIBLE);
                            }
                        } else {
                            mPhoneEditTxt.setVisibility(View.GONE);
                        }
                    }
                });

        mPhotoCheckbox1.setOnClickListener(photoCheckboxClickListener);
        mPhotoCheckbox2.setOnClickListener(photoCheckboxClickListener);
        mPhotoCheckbox3.setOnClickListener(photoCheckboxClickListener);
        mPhotoCheckbox4.setOnClickListener(photoCheckboxClickListener);

        mCategoriesSpinner = (Spinner) findViewById(R.id.sellCategoriesSpinner);
        mDurationTxt = (TextView) findViewById(R.id.sellDurationTxt);
        mCostTxt = (TextView) findViewById(R.id.sellCostTxt);

        mDurationArray = getResources().getStringArray(R.array.duration);
        mDurationFullArray = getResources().getStringArray(
                R.array.duration_full);

        mDurationTxt.setText(mDurationArray[0]);
        mDurationTxt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openDurationDialog();
            }

            private void openDurationDialog() {
                final AlertDialog.Builder durationDialogBuilder = new AlertDialog.Builder(
                        SellAddActivity.this);

                ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(
                        SellAddActivity.this, R.layout.spinner_dropdown_item,
                        mDurationFullArray);

                Integer selectedPos;
                if (mWeeksNum <= 4) {
                    selectedPos = mWeeksNum - 1;
                } else {
                    selectedPos = 4;
                }

                durationDialogBuilder.setSingleChoiceItems(listAdapter,
                        selectedPos, new DialogInterface.OnClickListener() {

                            private AlertDialog mDurationOtherDialog;

                            @SuppressLint("InflateParams")
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                mDurationDialog.dismiss();
                                if (which < mDurationArray.length) {
                                    mWeeksNum = which + 1;
                                    mDurationTxt.setText(mDurationArray[which]);
                                    mCostTxt.setText("$"
                                            + String.format(
                                            "%.2f",
                                            Double.parseDouble((mWeeksNum - 1)
                                                    + "")).replace(",",
                                            "."));
                                } else {
                                    AlertDialog.Builder durationOtherDialogBuilder = new AlertDialog.Builder(
                                            SellAddActivity.this);
                                    View dialogView = getLayoutInflater()
                                            .inflate(R.layout.dialog_duration,
                                                    null);
                                    final EditText weekNumEditTxt = (EditText) dialogView
                                            .findViewById(R.id.sellWeekNumEditTxt);
                                    if (mWeeksNum != 0) {
                                        weekNumEditTxt.setText(mWeeksNum + "");
                                    }
                                    durationOtherDialogBuilder
                                            .setView(dialogView);
                                    durationOtherDialogBuilder
                                            .setNegativeButton(
                                                    "Cancel",
                                                    new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(
                                                                DialogInterface dialog,
                                                                int which) {
                                                            mDurationOtherDialog
                                                                    .dismiss();
                                                            openDurationDialog();
                                                        }
                                                    });
                                    durationOtherDialogBuilder
                                            .setPositiveButton(
                                                    "OK",
                                                    new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(
                                                                DialogInterface dialog,
                                                                int which) {
                                                            mDurationOtherDialog
                                                                    .dismiss();
                                                            mWeeksNum = Integer
                                                                    .parseInt(weekNumEditTxt
                                                                            .getText()
                                                                            .toString());
                                                            if (mWeeksNum == 0) {
                                                                mWeeksNum = 1;
                                                                Toast.makeText(
                                                                        SellAddActivity.this,
                                                                        "Error: Minimal duration is 1 week",
                                                                        Toast.LENGTH_LONG)
                                                                        .show();
                                                            }
                                                            if (mWeeksNum == 1) {
                                                                mDurationTxt
                                                                        .setText(mWeeksNum
                                                                                + " week");
                                                            } else {
                                                                mDurationTxt
                                                                        .setText(mWeeksNum
                                                                                + " weeks");
                                                            }
                                                            mCostTxt.setText("$"
                                                                    + String.format(
                                                                    "%.2f",
                                                                    Double.parseDouble((mWeeksNum - 1)
                                                                            + ""))
                                                                    .replace(
                                                                            ",",
                                                                            "."));
                                                        }
                                                    });
                                    mDurationOtherDialog = durationOtherDialogBuilder
                                            .create();
                                    mDurationOtherDialog.show();
                                }
                            }
                        });

                mDurationDialog = durationDialogBuilder.create();
                mDurationDialog.show();
            }
        });

        SpannableString ss = new SpannableString(getResources().getString(
                R.string.sell_terms_title));
        LinkSpan clickableSpanPrivacy = new LinkSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(SellAddActivity.this,
                        PolicyActivity.class));
            }
        };

        LinkSpan clickableSpanSafety = new LinkSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(SellAddActivity.this,
                        SafetyActivity.class));
            }
        };

        ss.setSpan(clickableSpanPrivacy, 70, 85,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.green)),
                70, 85, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ss.setSpan(clickableSpanSafety, 120, 133,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.green)),
                120, 133, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView termsTxt = (TextView) findViewById(R.id.sellTermsTxt);
        termsTxt.setText(ss);
        termsTxt.setMovementMethod(LinkMovementMethod.getInstance());

        mPhotoImg1 = (ImageView) findViewById(R.id.sellPhotoImg1);
        mPhotoImg2 = (ImageView) findViewById(R.id.sellPhotoImg2);
        mPhotoImg3 = (ImageView) findViewById(R.id.sellPhotoImg3);
        mPhotoImg4 = (ImageView) findViewById(R.id.sellPhotoImg4);

        registerForContextMenu(findViewById(R.id.sellPhotoLayout1));
        registerForContextMenu(findViewById(R.id.sellPhotoLayout2));
        registerForContextMenu(findViewById(R.id.sellPhotoLayout3));
        registerForContextMenu(findViewById(R.id.sellPhotoLayout4));

        // Create global configuration and initialize ImageLoader with this
        // config
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPhone = mPrefs.getString("phone", "");
        mEmail = mPrefs.getString("email", "");

        Intent sellIntent = getIntent();
        if (sellIntent.getExtras() != null) {
            mEditMode = true;

            mItemId = getIntent().getStringExtra("item_id");
            mTitle = getIntent().getStringExtra("title");
            mCategoryTitle = getIntent().getStringExtra("category");
            mPrice = getIntent().getStringExtra("price");
            mDesc = getIntent().getStringExtra("desc");
            mDuration = getIntent().getIntExtra("duration", 1);
            mMainPhotoPos = getIntent().getIntExtra("photo_num", 0);
            try {
                JSONArray mPhotosJSON = new JSONArray(getIntent()
                        .getStringExtra("photos"));
                for (int i = 0; i < mPhotosJSON.length(); i++) {
                    mPhotos.add(mPhotosJSON.getString(i));
                    if (i == 0) {
                        ImageLoader.getInstance().displayImage(
                                mPhotosJSON.getString(i), mPhotoImg1);
                        mPhotoCheckbox1.setEnabled(true);
                        mPhoto1 = mPhotosJSON.getString(i);
                    } else if (i == 1) {
                        ImageLoader.getInstance().displayImage(
                                mPhotosJSON.getString(i), mPhotoImg2);
                        mPhotoCheckbox2.setEnabled(true);
                        mPhoto2 = mPhotosJSON.getString(i);
                    } else if (i == 2) {
                        ImageLoader.getInstance().displayImage(
                                mPhotosJSON.getString(i), mPhotoImg3);
                        mPhotoCheckbox3.setEnabled(true);
                        mPhoto3 = mPhotosJSON.getString(i);
                    } else if (i == 3) {
                        ImageLoader.getInstance().displayImage(
                                mPhotosJSON.getString(i), mPhotoImg4);
                        mPhotoCheckbox4.setEnabled(true);
                        mPhoto4 = mPhotosJSON.getString(i);
                    }
                }
                if (mMainPhotoPos == 0) {
                    mPhotoCheckbox1.setChecked(true);
                } else if (mMainPhotoPos == 1) {
                    mPhotoCheckbox2.setChecked(true);
                } else if (mMainPhotoPos == 2) {
                    mPhotoCheckbox3.setChecked(true);
                } else if (mMainPhotoPos == 3) {
                    mPhotoCheckbox4.setChecked(true);
                }
            } catch (Exception e) {
                mPhotos = new ArrayList<String>();
            }
            try {
                mContactChoices = new JSONObject(getIntent().getStringExtra(
                        "contact_choices"));
                if (mContactChoices.has("messenger")) {
                    mContactCheckbox1.setChecked(true);
                    mMessengerIsChecked = true;
                }
                if (mContactChoices.has("email")) {
                    mContactCheckbox2.setChecked(true);
                    mEmailIsChecked = true;
                }
                if (mContactChoices.has("phone")) {
                    mContactCheckbox3.setChecked(true);
                    mPhoneIsChecked = true;
                }
            } catch (Exception e) {
                mContactChoices = new JSONObject();
            }
            if ((mDuration + "").endsWith("1")) {
                ((TextView) findViewById(R.id.sellDurationTxt))
                        .setText(mDuration + " Week");
            } else {
                ((TextView) findViewById(R.id.sellDurationTxt))
                        .setText(mDuration + " Weeks");
            }
            ((EditText) findViewById(R.id.sellTitleEditTxt)).setText(mTitle);
            ((EditText) findViewById(R.id.sellDescEditTxt)).setText(mDesc);
            ((EditText) findViewById(R.id.sellPriceEditTxt)).setText(String
                    .format("%.2f", Double.parseDouble(mPrice)).replaceAll(",",
                            "."));
            ((Button) findViewById(R.id.sellBtn))
                    .setText(R.string.sell_btn_save);
            findViewById(R.id.sellCategoriesSpinner).setEnabled(false);
            findViewById(R.id.sellDurationTxt).setEnabled(false);
            findViewById(R.id.sellCostLayout).setVisibility(View.GONE);
            findViewById(R.id.sellTermsTxt).setVisibility(View.GONE);
            findViewById(R.id.sellTermsSaveTxt).setVisibility(View.VISIBLE);

            ss = new SpannableString(getResources().getString(
                    R.string.sell_terms_title_save));
            clickableSpanPrivacy = new LinkSpan() {
                @Override
                public void onClick(View textView) {
                    startActivity(new Intent(SellAddActivity.this,
                            PolicyActivity.class));
                }
            };

            clickableSpanSafety = new LinkSpan() {
                @Override
                public void onClick(View textView) {
                    startActivity(new Intent(SellAddActivity.this,
                            SafetyActivity.class));
                }
            };

            ss.setSpan(clickableSpanPrivacy, 70, 85,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(
                    new ForegroundColorSpan(getResources().getColor(
                            R.color.green)), 70, 85,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            ss.setSpan(clickableSpanSafety, 120, 133,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(
                    new ForegroundColorSpan(getResources().getColor(
                            R.color.green)), 120, 133,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            termsTxt = (TextView) findViewById(R.id.sellTermsSaveTxt);
            termsTxt.setText(ss);
            termsTxt.setMovementMethod(LinkMovementMethod.getInstance());
        }

        new GetCategoriesTask().execute();
    }

    class GetCategoriesTask extends AsyncTask<String, Void, Void> {

        private int errorCode = 0;
        private String mResponseString;
        private JSONArray mItemsJSON;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... files) {
            try {
                DefaultHttpClient client = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(Constansts.GET_CATEGORIES_URL);
                try {
                    HttpResponse response = client.execute(httpPost);
                    mResponseString = EntityUtils
                            .toString(response.getEntity());
                    Log(mResponseString);
                    JSONObject jsonResponse = new JSONObject(mResponseString);
                    if (jsonResponse.has("result")) {
                        if (jsonResponse.getString("result").equals("success")) {
                            mIdsArrayList.clear();
                            mTitlesArrayList.clear();

                            mItemsJSON = new JSONArray(
                                    jsonResponse.getString("array"));
                            for (int i = 0; i < mItemsJSON.length(); i++) {
                                mIdsArrayList.add(mItemsJSON.getJSONObject(i)
                                        .getString("id"));
                                mTitlesArrayList.add(mItemsJSON
                                        .getJSONObject(i).getString("title"));
                                if (mEditMode) {
                                    if (mItemsJSON.getJSONObject(i)
                                            .getString("title")
                                            .equals(mCategoryTitle)) {
                                        mCategoryPos = i;
                                    }
                                }
                            }
                            errorCode = -1;
                        } else {
                            errorCode = 0;
                        }
                    }
                } catch (ClientProtocolException e) {
                    Log(e);
                } catch (IOException e) {
                    Log(e);
                }
            } catch (JSONException e) {
                Log(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (errorCode == -1) {
                findViewById(R.id.sellCategoriesProgressBar).setVisibility(
                        View.GONE);
                ArrayAdapter<String> spinnerCategoriesAdapter = new ArrayAdapter<String>(
                        SellAddActivity.this, R.layout.spinner_item,
                        mTitlesArrayList);
                spinnerCategoriesAdapter
                        .setDropDownViewResource(R.layout.spinner_dropdown_item);
                mCategoriesSpinner.setAdapter(spinnerCategoriesAdapter);
                mCategoriesSpinner
                        .setOnItemSelectedListener(new OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent,
                                                       View view, int position, long id) {
                                mCategory = Integer.parseInt(mIdsArrayList
                                        .get(position));
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                if (mCategoryPos != -1) {
                    mCategoriesSpinner.setSelection(mCategoryPos);
                }
            } else if (errorCode == 0) {
                Toast.makeText(SellAddActivity.this,
                        "Error: Can't get categories", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
        }
    }

    OnClickListener photoCheckboxClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            mPhotoCheckbox1.setChecked(false);
            mPhotoCheckbox2.setChecked(false);
            mPhotoCheckbox3.setChecked(false);
            mPhotoCheckbox4.setChecked(false);
            ((CheckBox) v).setChecked(true);

            mMainPhotoPos = Integer.parseInt(v.getTag() + "");
        }
    };

    private PayPalPayment getThingToBuy(String paymentIntent) {
        String amount = (mWeeksNum - 1) + ".00";
        Log("amount = " + amount);
        return new PayPalPayment(new BigDecimal(amount), "USD",
                "obo advertisment (for " + mWeeksNum + " weeks)", paymentIntent);
    }

    public void sell(View v) {
        if (mPhoneEditTxt.getVisibility() == View.VISIBLE) {
            mPhone = mPhoneEditTxt.getText().toString();
        }
        if (mEmailEditTxt.getVisibility() == View.VISIBLE) {
            mEmail = mEmailEditTxt.getText().toString();
        }

        mTitle = ((EditText) findViewById(R.id.sellTitleEditTxt)).getText()
                .toString();
        mPrice = ((EditText) findViewById(R.id.sellPriceEditTxt)).getText()
                .toString();
        mDesc = ((EditText) findViewById(R.id.sellDescEditTxt)).getText()
                .toString();
        if (mTitle.equals("")) {
            Toast.makeText(this, "Error: Title is empty", Toast.LENGTH_LONG)
                    .show();
        } else if (mPrice.equals("")) {
            Toast.makeText(this, "Error: Price is empty", Toast.LENGTH_LONG)
                    .show();
        } else if (!mMessengerIsChecked & !mEmailIsChecked & !mPhoneIsChecked) {
            Toast.makeText(this, "Error: Check at least one contact box",
                    Toast.LENGTH_LONG).show();
        } else if (!mEditMode & mEmailIsChecked & mEmail.equals("")) {
            Toast.makeText(this, "Error: Contact email is empty",
                    Toast.LENGTH_LONG).show();
        } else if (!mEditMode
                & mEmailIsChecked
                & !Patterns.EMAIL_ADDRESS.matcher(
                mEmailEditTxt.getText().toString()).matches()) {
            Toast.makeText(this, "Error: Contact email is invalid",
                    Toast.LENGTH_LONG).show();
        } else if (!mEditMode & mPhoneIsChecked & mPhone.equals("")) {
            Toast.makeText(this, "Error: Contact phone is empty",
                    Toast.LENGTH_LONG).show();
        } else if (mPhotos.size() == 0) {
            Toast.makeText(this, "Error: Upload at least one photo",
                    Toast.LENGTH_LONG).show();
        } else if (mMainPhotoPos == -1) {
            Toast.makeText(this, "Error: Select main photo", Toast.LENGTH_LONG)
                    .show();
        } else if (mDesc.equals("")) {
            Toast.makeText(this, "Error: Description is empty",
                    Toast.LENGTH_LONG).show();
        } else {
            try {
                mContactChoices = new JSONObject();
                if (mMessengerIsChecked) {
                    mContactChoices.put("messenger",
                            PreferenceManager.getDefaultSharedPreferences(this)
                                    .getString("id", ""));
                }
                if (mEmailIsChecked) {
                    mContactChoices.put("email", mEmail);
                }
                if (mPhoneIsChecked) {
                    mContactChoices.put("phone", mPhone);
                }
                mPrefs.edit().putString("email", mEmail).commit();
                mPrefs.edit().putString("phone", mPhone).commit();
                if (mWeeksNum > 1 & !mEditMode) {
                    PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
                    Intent intent = new Intent(SellAddActivity.this,
                            PaymentActivity.class);
                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
                    startActivityForResult(intent,
                            Constansts.REQUEST_CODE_PAYMENT);
                } else {
                    if (mMainPhotoPos > (mPhotos.size() - 1)) {
                        mMainPhotoPos = mPhotos.size() - 1;
                    }

                    new PostItemTask().execute();
                }
            } catch (JSONException e) {
                Toast.makeText(this, "Error: Invalid contact choices",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    class PostItemTask extends AsyncTask<String, Void, Void> {
        private ProgressDialog loadingDialog;
        private int errorCode = 0;
        private String mResponseString;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = new ProgressDialog(SellAddActivity.this);
            loadingDialog.setMessage("Loading...");
            loadingDialog.show();
        }

        @Override
        protected Void doInBackground(String... files) {
            try {
                MultipartEntityBuilder multipartEntity = MultipartEntityBuilder
                        .create();

                multipartEntity.addPart(
                        "author_id",
                        new StringBody(PreferenceManager
                                .getDefaultSharedPreferences(
                                        getApplicationContext()).getString(
                                        "id", ""), ContentType.TEXT_PLAIN));
                multipartEntity.addPart("edit_mode", new StringBody(mEditMode
                        + "", ContentType.TEXT_PLAIN));
                multipartEntity.addPart("title", new StringBody(mTitle,
                        ContentType.TEXT_PLAIN));
                if (!mEditMode) {
                    multipartEntity.addPart("category", new StringBody(
                            mCategory + "", ContentType.TEXT_PLAIN));
                    multipartEntity.addPart("duration", new StringBody(
                            mWeeksNum + "", ContentType.TEXT_PLAIN));
                } else {
                    JSONArray photosJSON = new JSONArray();
                    for (int i = 0; i < mPhotos.size(); i++) {
                        photosJSON.put(mPhotos.get(i));
                    }
                    multipartEntity.addPart("old_photos", new StringBody(
                            photosJSON.toString(), ContentType.TEXT_PLAIN));
                    multipartEntity.addPart("item_id", new StringBody(mItemId,
                            ContentType.TEXT_PLAIN));
                }
                multipartEntity.addPart("photo1_angle", new StringBody(
                        mPhoto1Angle + "", ContentType.TEXT_PLAIN));
                multipartEntity.addPart("photo2_angle", new StringBody(
                        mPhoto2Angle + "", ContentType.TEXT_PLAIN));
                multipartEntity.addPart("photo3_angle", new StringBody(
                        mPhoto3Angle + "", ContentType.TEXT_PLAIN));
                multipartEntity.addPart("photo4_angle", new StringBody(
                        mPhoto4Angle + "", ContentType.TEXT_PLAIN));
                multipartEntity.addPart("price", new StringBody(mPrice,
                        ContentType.TEXT_PLAIN));
                multipartEntity.addPart("contact_choices", new StringBody(
                        mContactChoices.toString(), ContentType.TEXT_PLAIN));
                multipartEntity.addPart("desc", new StringBody(mDesc,
                        ContentType.TEXT_PLAIN));
                multipartEntity.addPart("lat", new StringBody(PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext())
                        .getString("lat", ""), ContentType.TEXT_PLAIN));
                multipartEntity.addPart("lng", new StringBody(PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext())
                        .getString("lng", ""), ContentType.TEXT_PLAIN));
                multipartEntity.addPart("main_num", new StringBody(
                        mMainPhotoPos + "", ContentType.TEXT_PLAIN));
                Log.d("Log", "main photo: " + mMainPhotoPos);
                for (int i = 0; i < mPhotos.size(); i++) {
                    if (mPhotos.get(i) != null
                            & !mPhotos.get(i).contains("http://")) {
                        Log("Add photo " + mPhotos.get(i));
                        multipartEntity.addPart("photo" + i, new FileBody(
                                new File(mPhotos.get(i))));
                    }
                }
                DefaultHttpClient client = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(Constansts.POST_ITEM_URL);
                httpPost.setEntity(multipartEntity.build());
                HttpResponse response = client.execute(httpPost);
                mResponseString = EntityUtils
                        .toString(response.getEntity());
                Log(mResponseString);
                JSONObject jsonResponse = new JSONObject(mResponseString);
                if (jsonResponse.has("result")) {
                    if (jsonResponse.getString("result").equals("success")) {
                        errorCode = -1;
                    } else if (jsonResponse.getString("result").equals(
                            "empty")) {
                        errorCode = 1;
                    } else {
                        errorCode = 0;
                    }
                }
            } catch (Exception e) {
                Log("Posting exception: " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            loadingDialog.dismiss();
            if (errorCode == -1) {
                if (mEditMode) {
                    Toast.makeText(SellAddActivity.this, "Changes are saved",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SellAddActivity.this,
                            SellActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                } else {
                    Toast.makeText(SellAddActivity.this, "Item is posted",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SellAddActivity.this,
                            MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }
            } else if (errorCode == 0) {
                Toast.makeText(SellAddActivity.this,
                        "Unknown error: " + mResponseString, Toast.LENGTH_LONG)
                        .show();
            } else if (errorCode == 1) {
                Toast.makeText(SellAddActivity.this,
                        "Error: Some fields are empty", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        mCurrentPhotoPos = Integer.parseInt(v.getTag() + "");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sell_add_context, menu);
        boolean photoIsChosen = false;
        if (mCurrentPhotoPos == 1 & !mPhoto1.equals("")) {
            photoIsChosen = true;
        } else if (mCurrentPhotoPos == 2 & !mPhoto2.equals("")) {
            photoIsChosen = true;
        } else if (mCurrentPhotoPos == 3 & !mPhoto3.equals("")) {
            photoIsChosen = true;
        } else if (mCurrentPhotoPos == 4 & !mPhoto4.equals("")) {
            photoIsChosen = true;
        }
        if (photoIsChosen) {
            menu.findItem(R.id.action_delete).setVisible(true);
            menu.findItem(R.id.action_rotate_left).setVisible(true);
            menu.findItem(R.id.action_rotate_right).setVisible(true);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_gallery:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, "Select Picture"),
                        Constansts.PICK_IMAGE);
                return true;
            case R.id.action_camera:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mPhotoName = System.currentTimeMillis() + ".jpg";
                File folder = new File(Environment.getExternalStorageDirectory()
                        + "/obo/");
                if (!folder.exists()) {
                    folder.mkdir();
                }
                File photo = new File(Environment.getExternalStorageDirectory()
                        + "/obo/", mPhotoName);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                startActivityForResult(intent, Constansts.TAKE_PHOTO);
                return true;
            case R.id.action_delete:
                if (mCurrentPhotoPos == 1) {
                    mPhoto1 = "";
                    mPhotoImg1.setImageDrawable(null);
                    mPhotoCheckbox1.setEnabled(false);
                    createPhotosArray();
                    setCheckedPhoto();
                } else if (mCurrentPhotoPos == 2) {
                    mPhoto2 = "";
                    mPhotoImg2.setImageDrawable(null);
                    mPhotoCheckbox2.setEnabled(false);
                    createPhotosArray();
                    setCheckedPhoto();
                } else if (mCurrentPhotoPos == 3) {
                    mPhoto3 = "";
                    mPhotoImg3.setImageDrawable(null);
                    mPhotoCheckbox3.setEnabled(false);
                    createPhotosArray();
                    setCheckedPhoto();
                } else if (mCurrentPhotoPos == 4) {
                    mPhoto4 = "";
                    mPhotoImg4.setImageDrawable(null);
                    mPhotoCheckbox4.setEnabled(false);
                    createPhotosArray();
                    setCheckedPhoto();
                }
                return true;
            case R.id.action_rotate_right:
                if (mCurrentPhotoPos == 1) {
                    mPhoto1Angle += 90;
                    mPhotoImg1.setRotation(mPhoto1Angle);
                } else if (mCurrentPhotoPos == 2) {
                    mPhoto2Angle += 90;
                    mPhotoImg2.setRotation(mPhoto2Angle);
                } else if (mCurrentPhotoPos == 3) {
                    mPhoto3Angle += 90;
                    mPhotoImg3.setRotation(mPhoto3Angle);
                } else if (mCurrentPhotoPos == 4) {
                    mPhoto4Angle += 90;
                    mPhotoImg4.setRotation(mPhoto4Angle);
                }
                return true;
            case R.id.action_rotate_left:
                if (mCurrentPhotoPos == 1) {
                    mPhoto1Angle -= 90;
                    mPhotoImg1.setRotation(mPhoto1Angle);
                } else if (mCurrentPhotoPos == 2) {
                    mPhoto2Angle -= 90;
                    mPhotoImg2.setRotation(mPhoto2Angle);
                } else if (mCurrentPhotoPos == 3) {
                    mPhoto3Angle -= 90;
                    mPhotoImg3.setRotation(mPhoto3Angle);
                } else if (mCurrentPhotoPos == 4) {
                    mPhoto4Angle -= 90;
                    mPhotoImg4.setRotation(mPhoto4Angle);
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void setCheckedPhoto() {
        if (mMainPhotoPos == (mCurrentPhotoPos - 1)) {
            mPhotoCheckbox1.setChecked(false);
            mPhotoCheckbox2.setChecked(false);
            mPhotoCheckbox3.setChecked(false);
            mPhotoCheckbox4.setChecked(false);
            if (!mPhoto1.equals("")) {
                mPhotoCheckbox1.setChecked(true);
                mMainPhotoPos = 0;
            } else if (!mPhoto2.equals("")) {
                mPhotoCheckbox2.setChecked(true);
                mMainPhotoPos = 1;
            } else if (!mPhoto3.equals("")) {
                mPhotoCheckbox3.setChecked(true);
                mMainPhotoPos = 2;
            } else if (!mPhoto4.equals("")) {
                mPhotoCheckbox4.setChecked(true);
                mMainPhotoPos = 3;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constansts.PICK_IMAGE) {
            if (data != null) {
                if (resultCode == RESULT_OK) {
                    Uri selectedImageURI = data.getData();
                    if (mCurrentPhotoPos == 1) {
                        mPhoto1 = getPath(getApplicationContext(),
                                selectedImageURI);
                        ImageLoader.getInstance().displayImage(
                                selectedImageURI.toString(), mPhotoImg1);
                        mPhotoCheckbox1.setEnabled(true);
                        createPhotosArray();
                    } else if (mCurrentPhotoPos == 2) {
                        mPhoto2 = getPath(getApplicationContext(),
                                selectedImageURI);
                        ImageLoader.getInstance().displayImage(
                                selectedImageURI.toString(), mPhotoImg2);
                        mPhotoCheckbox2.setEnabled(true);
                    } else if (mCurrentPhotoPos == 3) {
                        mPhoto3 = getPath(getApplicationContext(),
                                selectedImageURI);
                        ImageLoader.getInstance().displayImage(
                                selectedImageURI.toString(), mPhotoImg3);
                        mPhotoCheckbox3.setEnabled(true);
                    } else if (mCurrentPhotoPos == 4) {
                        mPhoto4 = getPath(getApplicationContext(),
                                selectedImageURI);
                        ImageLoader.getInstance().displayImage(
                                selectedImageURI.toString(), mPhotoImg4);
                        mPhotoCheckbox4.setEnabled(true);
                    }
                    createPhotosArray();
                    Log(mPhotos.toString());
                }
            }
        } else if (requestCode == Constansts.TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                File photo = new File(Environment.getExternalStorageDirectory()
                        + "/obo/" + mPhotoName);
                mPhotoUri = Uri.fromFile(photo);
                if (mCurrentPhotoPos == 1) {
                    mPhoto1 = photo.getAbsolutePath();
                    ImageLoader.getInstance().displayImage(
                            mPhotoUri.toString(), mPhotoImg1);
                    mPhotoCheckbox1.setEnabled(true);
                } else if (mCurrentPhotoPos == 2) {
                    mPhoto2 = photo.getAbsolutePath();
                    ImageLoader.getInstance().displayImage(
                            mPhotoUri.toString(), mPhotoImg2);
                    mPhotoCheckbox2.setEnabled(true);
                } else if (mCurrentPhotoPos == 3) {
                    mPhoto3 = photo.getAbsolutePath();
                    ImageLoader.getInstance().displayImage(
                            mPhotoUri.toString(), mPhotoImg3);
                    mPhotoCheckbox3.setEnabled(true);
                } else if (mCurrentPhotoPos == 4) {
                    mPhoto4 = photo.getAbsolutePath();
                    ImageLoader.getInstance().displayImage(
                            mPhotoUri.toString(), mPhotoImg4);
                    mPhotoCheckbox4.setEnabled(true);
                }
                createPhotosArray();
                Log(mPhotos.toString());
            }
        } else if (requestCode == Constansts.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    Toast.makeText(SellAddActivity.this,
                            "Payment is successful", Toast.LENGTH_LONG).show();
                    new PostItemTask().execute();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(SellAddActivity.this, "Invalid payment",
                        Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == Constansts.REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth = data
                        .getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    String authorization_code = auth.getAuthorizationCode();
                    Toast.makeText(getApplicationContext(),
                            "Profile Sharing code received from PayPal",
                            Toast.LENGTH_LONG).show();

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("ProfileSharingExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("ProfileSharingExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createPhotosArray() {
        mPhotos.clear();
        if (!TextUtils.isEmpty(mPhoto1)) {
            mPhotos.add(mPhoto1);
        }
        if (!TextUtils.isEmpty(mPhoto2)) {
            mPhotos.add(mPhoto2);
        }
        if (!TextUtils.isEmpty(mPhoto3)) {
            mPhotos.add(mPhoto3);
        }
        if (!TextUtils.isEmpty(mPhoto4)) {
            mPhotos.add(mPhoto4);
        }
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    public void pickPhoto(View v) {
        openContextMenu(v);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        overridePendingTransition(R.anim.exit_to_right, R.anim.enter_from_left);
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.exit_to_right, R.anim.enter_from_left);
    }

    public static void Log(Object text) {
        Log.d("Log", text + "");
    }

}