package com.mmtechbd.remotehealthmonitor.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.mmtechbd.remotehealthmonitor.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.mmtechbd.remotehealthmonitor.utils.Universal.getStringFromStream;
import static java.lang.String.format;

/**
 * Created by Roaim on 16-Nov-16.
 */

public class SignIn extends AsyncTask<String,String,String> {
    private static final String ROOT_URL = BuildConfig.ROOT_URL+"/api/authenticate";
    private static final String KEY_PARAM_EMAIL = "email";
    private static final String KEY_PARAM_PASS = "password";
    private static final String KEY_PARAM_TYPE = "type";
    private final SignInCallback mCallback;
    private Context mContext;
    private String email, pass, type;
    private ProgressDialog pd;

    public SignIn(Context context, String email, String pass, String type, SignInCallback callback) {
        this.mContext = context;
        this.email = email;
        this.pass = pass;
        this.type = type;
        this.mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(mContext);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Please wait...");
        pd.setTitle("Authenticating....");
        pd.show();
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            URL urlPost = new URL(ROOT_URL);
            HttpURLConnection connection = (HttpURLConnection) urlPost.openConnection();
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            Uri.Builder builder = new Uri.Builder();
            builder.appendQueryParameter(KEY_PARAM_EMAIL,email);
            builder.appendQueryParameter(KEY_PARAM_PASS,pass);
            builder.appendQueryParameter(KEY_PARAM_TYPE,type);
            String query = builder.build().getEncodedQuery();
            OutputStream out = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
            writer.write(query);
            writer.close();
            out.close();
            connection.connect();
            int responseCode = connection.getResponseCode();
            System.out.println(format("Url = %s ; ResponseCode = %d",urlPost.toString(),responseCode));
            if (responseCode == 200) {
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
        if (pd!=null) pd.dismiss();
        if (s!=null) {
            System.out.println(s);
            if (mCallback!=null){
                try {
                    mCallback.onSuccess(new JSONObject(s));
                } catch (JSONException e) {
                    e.printStackTrace();
                    mCallback.onFailed(s.trim());
                }
            }
        } else {
            if (mCallback!=null) mCallback.onFailed(null);
        }
    }

    public interface SignInCallback {
        void onSuccess(JSONObject s);
        void onFailed(String msg);
    }
}
