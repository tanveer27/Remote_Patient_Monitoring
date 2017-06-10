package com.mmtechbd.remotehealthmonitor.net;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.mmtechbd.remotehealthmonitor.utils.Universal;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.lang.String.format;

/**
 * Created by Roaim on 14-Nov-16.
 */

public class DataDownloader extends AsyncTask<String, String, String> {
    private Context mContext;
    private DataCallback mCallback;
    private Progress mProgress;
    private ProgressBar mProgressBar;

    public static void download(Context context, String url, String token, DataCallback callback) {
        final String params[] = new String[]{url,token};
        DataDownloader downloader = new DataDownloader(context, callback);
        downloader.execute(params);
    }

    public static void download(Context context, String url, String token, Progress progress, DataCallback callback) {
        final String params[] = new String[]{url,token};
        DataDownloader downloader = new DataDownloader(context, callback,progress);
        downloader.execute(params);
    }

    public static void download(Context context, String url, String token, ProgressBar progress, DataCallback callback) {
        final String params[] = new String[]{url,token};
        DataDownloader downloader = new DataDownloader(context, callback,progress);
        downloader.execute(params);
    }

    private DataDownloader(Context context, DataCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
    }

    private DataDownloader(Context context, DataCallback callback, Progress progress) {
        this.mContext = context;
        this.mCallback = callback;
        this.mProgress = progress;
    }

    private DataDownloader(Context context, DataCallback callback, ProgressBar progress) {
        this.mContext = context;
        this.mCallback = callback;
        this.mProgressBar = progress;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mCallback.onStartReceiving();
        if (mProgress!=null) mProgress.show(mContext);
        if (mProgressBar!=null) mProgressBar.setVisibility(View.VISIBLE);
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected String doInBackground(String... urls) {
        try {
            String token = urls[1];
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            String auth = "Bearer " +token;
            connection.setRequestProperty ("Authorization", auth);
            connection.connect();
            int responseCode = connection.getResponseCode();
            System.out.println(format("Response code for %s is : %d\nToken = %s",url.toString(),responseCode,token));
            if (responseCode==200) {
                return Universal.getStringFromStream(connection.getInputStream());
            }
        }  catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (mProgress!=null) mProgress.dismiss();
        if (mProgressBar!=null) mProgressBar.setVisibility(View.GONE);
        System.out.println("Response body = "+s);
        if (s==null) return;
        if (!s.contains("token mismatch")) {
            mCallback.onReceivedResult(s);
        } else {
            mCallback.onTokenMisMatch();
        }
    }

    public interface DataCallback {
        void onStartReceiving();
        void onReceivedResult(String result);
        void onTokenMisMatch();
    }

    public static class Progress {
        private ProgressDialog pd;

        String getTitle() {
            return title;
        }

        String getMessage() {
            return message;
        }

        boolean isCancelable() {
            return cancelable;
        }

        private final String title, message;
        private final boolean cancelable;

        public Progress(String title, String message, boolean cancelable) {
            this.title = title;
            this.message = message;
            this.cancelable = cancelable;
        }

        public void show(Context context) {
            pd = new ProgressDialog(context);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            if (getTitle()!=null) pd.setTitle(getTitle());
            if (getMessage()!=null) pd.setMessage(getMessage());
            pd.setCancelable(isCancelable());
            pd.show();
        }

        public void dismiss() {
            if (pd!=null) pd.dismiss();
        }
    }
}
