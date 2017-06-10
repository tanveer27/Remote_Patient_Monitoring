package com.mmtechbd.remotehealthmonitor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.mmtechbd.remotehealthmonitor.model.WAuth;
import com.mmtechbd.remotehealthmonitor.utils.Withings;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Roaim on 28-Nov-16.
 */
public class WAuthActivity extends Activity {
    public static final String EXTRA_USER_ID = "userid";
    public static final String EXTRA_TOKEN = "token";
    public static final String EXTRA_SECRET = "secret";
    private ProgressBar mProgress;
    private WAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iauth);
        final WebView mWebView = (WebView) findViewById(R.id.webViewIhealth);
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        WebSettings webSettings= mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(true);
        mWebView.setWebViewClient(new WClient());
        new AsyncTask<String,String,String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    auth = Withings.getAuth();
                    return auth != null ? auth.getAuthUrl() : null;
                } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
                    e.printStackTrace();
                    finish();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s!=null) mWebView.loadUrl(s);
                else finish();
            }
        }.execute();
    }

    private class WClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO: Implement this method
            Uri uri = Uri.parse(url);
            Uri redirectUri = Uri.parse(BuildConfig.ROOT_URL);
            if(uri.getHost().equals(redirectUri.getHost())){
                String param = uri.getQueryParameter(EXTRA_USER_ID);
                System.out.println("Withings UserId = " +param);
                if (param!=null) {
                    Intent data = new Intent();
                    data.putExtra(EXTRA_USER_ID, param);
                    data.putExtra(EXTRA_TOKEN, auth.getToken());
                    data.putExtra(EXTRA_SECRET, auth.getTokenSecret());
                    setResult(RESULT_OK, data);
                }
                finish();
            } else {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mProgress.setVisibility(View.GONE);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            view.loadUrl("about:blank");
            finish();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            view.loadUrl("about:blank");
            finish();
        }
    }
}
