package com.mmtechbd.remotehealthmonitor.net;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mmtechbd.remotehealthmonitor.BuildConfig;
import com.mmtechbd.remotehealthmonitor.LoginPanel;
import com.mmtechbd.remotehealthmonitor.User_Panel;
import com.mmtechbd.remotehealthmonitor.model.AccessToken;
import com.mmtechbd.remotehealthmonitor.utils.TokenUtils;
import com.mmtechbd.remotehealthmonitor.utils.Universal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.mmtechbd.remotehealthmonitor.utils.TokenUtils.KEY_W_SECRET;
import static com.mmtechbd.remotehealthmonitor.utils.TokenUtils.KEY_W_TOKEN;
import static com.mmtechbd.remotehealthmonitor.utils.TokenUtils.KEY_W_UID;
import static com.mmtechbd.remotehealthmonitor.utils.Universal.getStringFromStream;
import static com.mmtechbd.remotehealthmonitor.utils.Universal.getStringFromUrl;
import static com.mmtechbd.remotehealthmonitor.utils.Universal.getToken;

/**
 * Created by Roaim on 11-Nov-16.
 */

public abstract class WtokenSender extends AsyncTask<String,String,String> {

    private static final String POST_URL = BuildConfig.ROOT_URL+"/api/itoken";
    private final String mToken;

    public WtokenSender(Context mContext, String token) {
        this.mContext = mContext;
        this.mToken = token;
        if (mToken==null) System.out.println("mToken is null at ItokenSender");
    }

    private Context mContext;
    private ProgressDialog mProgress;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgress = new ProgressDialog(mContext);
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress.setTitle("Validating...");
        mProgress.setMessage("Please keep patience...");
        mProgress.setCancelable(false);
        mProgress.show();
    }

    @Override
    protected String doInBackground(String... urls) {
        String uId = null, token = null, secret = null;
        try {
            URL url = new URL(urls[0]);
            String tokenString = getStringFromUrl(url);
            if (tokenString.contains("&")) {
                String tokenArray[] = tokenString.split("&");
                for (String param : tokenArray) {
                    if (param.contains("=")) {
                        String[] keyValue = param.split("=");
                        String key = keyValue[0].trim();
                        String value = keyValue[1].trim();
                        if (key.equals(KEY_W_SECRET)) {
                            secret = value;
                        } else if (key.equals(KEY_W_TOKEN)) {
                            token = value;
                        } else if (key.equals(KEY_W_UID)) {
                            uId = value;
                        }
                    }
                }
            }

            if (uId==null || token==null || secret==null) return null;

            URL urlPost = new URL(POST_URL);
            HttpURLConnection connection = (HttpURLConnection) urlPost.openConnection();
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            String auth = "Bearer " +mToken;
            connection.setRequestProperty ("Authorization", auth);
            Uri.Builder builder = new Uri.Builder();
            builder.appendQueryParameter(AccessToken.KEY_USER_ID,uId);
            System.out.println(uId);
            builder.appendQueryParameter(AccessToken.KEY_ACCESS_TOKEN, token);
            builder.appendQueryParameter(AccessToken.KEY_EXPIRE_ACCESS_TOKEN,"0");
            builder.appendQueryParameter(AccessToken.KEY_REFRESH_TOKEN,secret);
            builder.appendQueryParameter(AccessToken.KEY_EXPIRE_REFRESH_TOKEN,"0");
            builder.appendQueryParameter("device",AccessToken.DEVICE_WITHINGS);
            String query = builder.build().getEncodedQuery();
            OutputStream out = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
            writer.write(query);
            writer.close();
            out.close();
            connection.connect();

            if (connection.getResponseCode() == 200) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences(LoginPanel.SHARED_PREFERENCE_TOKEN,Context.MODE_PRIVATE).edit();
                TokenUtils.saveToPref(editor,token,secret,uId);
                editor.apply();
                return getStringFromStream(connection.getInputStream());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mProgress.dismiss();
        if (s==null) {
            Toast.makeText(mContext,"Something went wrong! Please try again.",Toast.LENGTH_LONG).show();
            return;
        } else {
            System.out.println("w token sender result = "+s);
            onSuccess(s);
        }

    }

    public abstract void onSuccess(String token);
}
