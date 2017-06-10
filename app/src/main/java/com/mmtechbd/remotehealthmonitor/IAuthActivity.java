package com.mmtechbd.remotehealthmonitor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import static com.mmtechbd.remotehealthmonitor.utils.IHealth.AUTH_QUERY_PARAM;
import static com.mmtechbd.remotehealthmonitor.utils.IHealth.AUTH_URL;
import static com.mmtechbd.remotehealthmonitor.utils.IHealth.REDIRECT_URL;

public class IAuthActivity extends Activity {
    public static final String EXTRA_URL = "url";
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iauth);
        WebView mWebView = (WebView) findViewById(R.id.webViewIhealth);
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        WebSettings webSettings= mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(true);
        mWebView.setWebViewClient(new WClient());
        mWebView.loadUrl(AUTH_URL);
    }

    private class WClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO: Implement this method
            Uri uri = Uri.parse(url);
            Uri redirectUri = Uri.parse(REDIRECT_URL);
            if(uri.getHost().equals(redirectUri.getHost())){
                String code = uri.getQueryParameter(AUTH_QUERY_PARAM);
                if (code!=null) {
                    Intent data = new Intent();
                    data.putExtra(EXTRA_URL, code);
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
