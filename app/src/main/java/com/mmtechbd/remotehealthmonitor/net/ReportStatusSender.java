package com.mmtechbd.remotehealthmonitor.net;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.mmtechbd.remotehealthmonitor.utils.Universal.getToken;

/**
 * Created by Roaim on 16-Nov-16.
 */

public class ReportStatusSender extends AsyncTask<String,String,String> {
    private static final String KEY_PARAM = "data_id";
    private final ReportCallback mCallback;
    private String dataId;
    private String mToken;

    public ReportStatusSender(Context context, String dataId, ReportCallback callback) {
        this.dataId = dataId;
        this.mToken = getToken(context);
        this.mCallback = callback;
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            URL urlPost = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) urlPost.openConnection();
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            String auth = "Bearer " +mToken;
            connection.setRequestProperty ("Authorization", auth);
            Uri.Builder builder = new Uri.Builder();
            builder.appendQueryParameter(KEY_PARAM,dataId);
            String query = builder.build().getEncodedQuery();
            OutputStream out = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
            writer.write(query);
            writer.close();
            out.close();
            connection.connect();
            int responseCode = connection.getResponseCode();
            System.out.println(String.format("Url = %s ; ResponseCode = %d ; Token = %s",urlPost.toString(),responseCode,mToken));
            if (responseCode == 200) {
                return "done";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s!=null && s.equals("done")) {
            if (mCallback!=null) mCallback.onSuccess();
        } else {
            if (mCallback!=null) mCallback.onFailed();
        }
    }

    public static interface ReportCallback {
        void onSuccess();
        void onFailed();
    }
}
