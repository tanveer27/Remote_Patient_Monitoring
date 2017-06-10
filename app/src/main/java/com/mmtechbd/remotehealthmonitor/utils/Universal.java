package com.mmtechbd.remotehealthmonitor.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import static com.mmtechbd.remotehealthmonitor.LoginPanel.SHARED_PREFERENCE_TOKEN;
/**
 * Created by Roaim on 11-Nov-16.
 */

public class Universal {

    public static String getToken(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCE_TOKEN, Context.MODE_PRIVATE).getString("token","");
    }
    public static String getStringFromUrl(URL url) throws IOException {
        // TODO: Implement this method
        return getStringFromStream(url.openStream());
    }

    public static String getStringFromStream(InputStream is) throws IOException {
        // TODO: Implement this method
        BufferedReader in = new BufferedReader(
                new InputStreamReader(is));
        String line ;
        StringBuilder sb = new StringBuilder();
        while ((line = in.readLine()) != null) {
            sb.append(line).append("\n");
        }
        in.close();
        is.close();
        return sb.toString();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = manager.getActiveNetworkInfo();
        return netInfo!=null && netInfo.isConnectedOrConnecting();
    }
}
