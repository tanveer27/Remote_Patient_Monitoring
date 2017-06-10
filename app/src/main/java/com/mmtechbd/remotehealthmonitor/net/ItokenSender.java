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
import java.net.URL;

import static com.mmtechbd.remotehealthmonitor.utils.Universal.getStringFromUrl;
import static com.mmtechbd.remotehealthmonitor.utils.Universal.getToken;

/**
 * Created by Roaim on 11-Nov-16.
 */

public abstract class ItokenSender extends AsyncTask<String,String,String> {

    private static final String POST_URL = BuildConfig.ROOT_URL+"/api/itoken";
    private final String mToken;

    public ItokenSender(Context mContext, String token) {
        this.mContext = mContext;
        this.mToken = token;
        if (mToken==null) System.out.println("mToken is null at ItokenSender");
    }

    private android.content.Context mContext;
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
        try {
            URL url = new URL(urls[0]);
            JSONObject obj = new JSONObject(getStringFromUrl(url));
            AccessToken accessToken = new AccessToken(obj,true);

            URL urlPost = new URL(POST_URL);
            HttpURLConnection connection = (HttpURLConnection) urlPost.openConnection();
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            String auth = "Bearer " +mToken;
            connection.setRequestProperty ("Authorization", auth);
            Uri.Builder builder = new Uri.Builder();
            builder.appendQueryParameter(AccessToken.KEY_USER_ID,accessToken.getUserId());
            System.out.println(accessToken.getUserId());
            builder.appendQueryParameter(AccessToken.KEY_ACCESS_TOKEN, accessToken.getAccessToken());
            builder.appendQueryParameter(AccessToken.KEY_EXPIRE_ACCESS_TOKEN,accessToken.getExpAccess());
            builder.appendQueryParameter(AccessToken.KEY_REFRESH_TOKEN,accessToken.getRefreshToken());
            builder.appendQueryParameter(AccessToken.KEY_EXPIRE_REFRESH_TOKEN,accessToken.getExpRefresh());
            builder.appendQueryParameter("device",AccessToken.DEVICE_IHEALTH);
            String query = builder.build().getEncodedQuery();
            OutputStream out = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
            writer.write(query);
            writer.close();
            out.close();
            connection.connect();

            if (connection.getResponseCode() == 200) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences(LoginPanel.SHARED_PREFERENCE_TOKEN,Context.MODE_PRIVATE).edit();
                TokenUtils.saveToPref(editor,accessToken);
                editor.apply();
                return Universal.getStringFromStream(connection.getInputStream());
            }

        } catch (JSONException | IOException e) {
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
            System.out.println("I token sender result = "+s);
            onSuccess(s);
            /*Intent intent = new Intent(mContext, User_Panel.class);
            intent.putExtra("token", mToken);
            mContext.startActivity(intent);
            ((Activity)mContext).finish();*/
        }
    }

    public abstract void onSuccess(String token);
}
