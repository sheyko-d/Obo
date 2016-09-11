package com.moysof.obo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.moysof.obo.typeface.TypefaceSpan;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

public class LogInActivity extends ActionBarActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private String mUsername = "";
    private String mPassword = "";
    private AlertDialog mForgetDialog;
    protected String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);

        mUsernameEditText = (EditText) findViewById(R.id.loginUsernameEditTxt);
        mPasswordEditText = (EditText) findViewById(R.id.loginPasswordEditTxt);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        SpannableString s = new SpannableString(getResources().getString(
                R.string.title_activity_log_in));
        s.setSpan(new TypefaceSpan(this, "Comfortaa-Bold.ttf"), 0, s.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new RelativeSizeSpan(1.2f), 0, s.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setTitle(s);

        SpannableString ss = new SpannableString(getResources().getString(
                R.string.forgot_password));
        LinkSpan clickableSpanPrivacy = new LinkSpan() {
            @Override
            public void onClick(View textView) {
                showRestoreDialog();
            }

        };

        ss.setSpan(clickableSpanPrivacy, 0, 21,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView forgotTxt = (TextView) findViewById(R.id.loginForgotTxt);
        forgotTxt.setText(ss);
        forgotTxt.setMovementMethod(LinkMovementMethod.getInstance());

        askPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void askPermissions() {
        // Here, thisActivity is the current activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE
                );
            }
        }
    }

    private void showRestoreDialog() {
        AlertDialog.Builder forgetDialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(
                R.layout.dialog_restore, null);
        forgetDialogBuilder.setView(dialogView);
        forgetDialogBuilder.setNegativeButton("Cancel", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mForgetDialog.cancel();
            }
        });
        forgetDialogBuilder.setPositiveButton("Send", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mEmail = ((EditText) dialogView
                        .findViewById(R.id.restoreEditTxt)).getText()
                        .toString();
                if (mEmail.equals("")) {
                    Toast.makeText(LogInActivity.this, "Error: Email is empty",
                            Toast.LENGTH_LONG).show();
                } else {
                    new RestoreTask().execute();
                }
                mForgetDialog.cancel();
            }
        });
        mForgetDialog = forgetDialogBuilder.create();
        mForgetDialog.show();
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

    public void logIn(View v) {
        mUsername = mUsernameEditText.getText().toString();
        mPassword = mPasswordEditText.getText().toString();

        if (mUsername.equals("")) {
            Toast.makeText(getApplicationContext(), "Error: Username is empty",
                    Toast.LENGTH_LONG).show();
        } else if (mPassword.equals("")) {
            Toast.makeText(getApplicationContext(), "Error: Password is empty",
                    Toast.LENGTH_LONG).show();
        } else if (Obo.isNetworkConnected()) {
            new LoginTask().execute();
        }
    }

    class LoginTask extends AsyncTask<String, Void, Void> {

        private ProgressDialog loadingDialog;
        private int errorCode = 0;
        private String mResponseString;
        private String mId = "";
        private String mName = "";
        private String mSurname = "";
        private String mEmail = "";
        private String mZip = "";
        private String mPhone = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = new ProgressDialog(LogInActivity.this);
            loadingDialog.setMessage("Loading...");
            loadingDialog.show();
        }

        @Override
        protected Void doInBackground(String... files) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs
                        .add(new BasicNameValuePair("username", mUsername));
                nameValuePairs
                        .add(new BasicNameValuePair("password", mPassword));
                nameValuePairs.add(new BasicNameValuePair("gcm_id",
                        GCMRegistrar.getRegistrationId(LogInActivity.this)));
                nameValuePairs
                        .add(new BasicNameValuePair(
                                "phone",
                                ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE))
                                        .getLine1Number()));
                DefaultHttpClient client = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(Constansts.LOG_IN_URL);
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = client.execute(httpPost);
                    mResponseString = EntityUtils
                            .toString(response.getEntity());
                    Log(mResponseString);
                    JSONObject jsonResponse = new JSONObject(mResponseString);
                    if (jsonResponse.has("result")) {
                        if (jsonResponse.getString("result").equals("success")) {
                            mId = jsonResponse.getString("id");
                            mName = jsonResponse.getString("name");
                            mSurname = jsonResponse.getString("surname");
                            mEmail = jsonResponse.getString("email");
                            mZip = jsonResponse.getString("zip");
                            mPhone = jsonResponse.getString("phone");

                            errorCode = -1;
                        } else if (jsonResponse.getString("result").equals(
                                "empty")) {
                            errorCode = 1;
                        } else if (jsonResponse.getString("result").equals(
                                "incorrect")) {
                            errorCode = 2;
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
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(LogInActivity.this);
            prefs.edit().putString("username", mUsername).commit();
            loadingDialog.dismiss();
            if (errorCode == -1) {
                Editor editor = PreferenceManager.getDefaultSharedPreferences(
                        LogInActivity.this).edit();
                editor.putString("id", mId);
                editor.putString("name", mName);
                editor.putString("surname", mSurname);
                editor.putString("email", mEmail);
                editor.putString("zip", mZip);
                editor.putString("phone", mPhone);
                editor.commit();
                startActivity(new Intent(LogInActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                try {
                    LoadingMenuActivity.activity.finish();
                } catch (Exception e) {
                }
                finish();
            } else if (errorCode == 0) {
                Toast.makeText(LogInActivity.this,
                        "Unknown error: " + mResponseString, Toast.LENGTH_LONG)
                        .show();
            } else if (errorCode == 1) {
                Toast.makeText(LogInActivity.this,
                        "Error: Some fields are empty", Toast.LENGTH_LONG)
                        .show();
            } else if (errorCode == 2) {
                Toast.makeText(LogInActivity.this,
                        "Error: Username or password is incorrect",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    class RestoreTask extends AsyncTask<String, Void, Void> {

        private ProgressDialog loadingDialog;
        private int errorCode = 0;
        private String mResponseString;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = new ProgressDialog(LogInActivity.this);
            loadingDialog.setMessage("Loading...");
            loadingDialog.show();
        }

        @Override
        protected Void doInBackground(String... files) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("email", mEmail));
                DefaultHttpClient client = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(Constansts.RESTORE_URL);
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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
                        } else if (jsonResponse.getString("result").equals(
                                "incorrect")) {
                            errorCode = 2;
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
            loadingDialog.cancel();
            if (errorCode == -1) {
                Toast.makeText(LogInActivity.this, "Email has been sent",
                        Toast.LENGTH_LONG).show();
            } else if (errorCode == 0) {
                Toast.makeText(LogInActivity.this,
                        "Unknown error: " + mResponseString, Toast.LENGTH_LONG)
                        .show();
            } else if (errorCode == 1) {
                Toast.makeText(LogInActivity.this,
                        "Error: Some fields are empty", Toast.LENGTH_LONG)
                        .show();
            } else if (errorCode == 2) {
                Toast.makeText(LogInActivity.this,
                        "Error: User with this email isn't found",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void Log(Object text) {
        Log.d("Log", text + "");
    }

}