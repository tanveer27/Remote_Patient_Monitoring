package com.mmtechbd.remotehealthmonitor.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.mmtechbd.remotehealthmonitor.utils.Universal.getStringFromStream;
import static com.mmtechbd.remotehealthmonitor.utils.Universal.getToken;
import static java.lang.String.format;

/**
 * Created by Roaim on 16-Nov-16.
 */

public class ProfileUpdater extends AsyncTask<String,String,String> {
    private static final String ROOT_URL = "http://ehealth.mmtechbd.com/api/updateProfile";
    private static final String KEY_PARAM_FNAME = "first_name";
    private static final String KEY_PARAM_LNAME = "last_name";
    private static final String KEY_PARAM_SEX = "sex";
    private static final String KEY_PARAM_DOB = "dob";
    private static final String KEY_PARAM_HEIGHT = "height";
    private static final String KEY_PARAM_WEIGHT = "weight";
    private static final String KEY_PARAM_ADDRESS = "address";
    private final ProfileUpdateCallback mCallback;
    private Context mContext;
    private String mToken;
    private ProgressDialog pd;

    public ProfileUpdater(Context context, ProfileUpdateCallback callback) {
        this.mContext = context;
        this.mToken = getToken(context);
        this.mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(mContext);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Please wait...");
        pd.setTitle("Uploading Profile....");
        pd.show();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL urlPost = new URL(ROOT_URL);
            HttpURLConnection connection = (HttpURLConnection) urlPost.openConnection();
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            String auth = "Bearer " +mToken;
            connection.setRequestProperty ("Authorization", auth);
            Uri.Builder builder = new Uri.Builder();
            builder.appendQueryParameter(KEY_PARAM_FNAME,params[0]);
            builder.appendQueryParameter(KEY_PARAM_LNAME,params[1]);
            builder.appendQueryParameter(KEY_PARAM_SEX,params[2]);
            builder.appendQueryParameter(KEY_PARAM_DOB,params[3]);
            builder.appendQueryParameter(KEY_PARAM_HEIGHT,params[4]);
            builder.appendQueryParameter(KEY_PARAM_WEIGHT,params[5]);
            builder.appendQueryParameter(KEY_PARAM_ADDRESS,params[6]);
            String query = builder.build().getEncodedQuery();
            OutputStream out = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
            writer.write(query);
            writer.close();
            out.close();
            connection.connect();
            int responseCode = connection.getResponseCode();
            System.out.println(format("Url = %s ; ResponseCode = %d ; Token = %s",urlPost.toString(),responseCode,mToken));
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
        pd.dismiss();
        if (s!=null) {
            if (mCallback!=null) mCallback.onSuccess(s);


        } else {
            if (mCallback!=null) mCallback.onFailed();
        }
    }

    public static interface ProfileUpdateCallback {
        void onSuccess(String s);
        void onFailed();
    }
}
