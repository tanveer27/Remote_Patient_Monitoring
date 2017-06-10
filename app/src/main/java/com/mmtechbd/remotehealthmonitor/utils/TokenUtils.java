package com.mmtechbd.remotehealthmonitor.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.mmtechbd.remotehealthmonitor.model.AccessToken;


/**
 * Created by Roaim on 11-Nov-16.
 */

public class TokenUtils {

    public static final String SHARED_PREFERENCE_TOKEN = "myToken";
    public static final String KEY_W_TOKEN = "oauth_token";
    public static final String KEY_W_SECRET = "oauth_token_secret";
    public static final String KEY_W_UID = "userid";

    public static void saveToPref(SharedPreferences.Editor editor, AccessToken accessToken) {
        editor.putString(AccessToken.KEY_ACCESS_TOKEN, accessToken.getAccessToken());
        editor.putString(AccessToken.KEY_REFRESH_TOKEN,accessToken.getRefreshToken());
        editor.putString(AccessToken.KEY_EXPIRE_ACCESS_TOKEN,accessToken.getExpAccess());
        editor.putString(AccessToken.KEY_EXPIRE_REFRESH_TOKEN,accessToken.getExpRefresh());
        editor.putString(AccessToken.KEY_USER_ID,accessToken.getUserId());
        saveToPref(editor,accessToken.getwTokenKey(),accessToken.getwTokenSecret(),accessToken.getwUid());
    }

    public static void saveToPref(SharedPreferences.Editor editor, String token, String secret, String uid) {
        editor.putString(KEY_W_TOKEN,token);
        editor.putString(KEY_W_SECRET,secret);
        editor.putString(KEY_W_UID,uid);
    }

    public static AccessToken getAccessToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFERENCE_TOKEN,Context.MODE_PRIVATE);
        return getAccessToken(sp);
    }

    public static AccessToken getAccessToken(SharedPreferences pref) {
        AccessToken accessToken = new AccessToken();
        accessToken.setUserId(pref.getString(AccessToken.KEY_USER_ID,""));
        accessToken.setAccessToken(pref.getString(AccessToken.KEY_ACCESS_TOKEN,""));
        accessToken.setRefreshToken(pref.getString(AccessToken.KEY_REFRESH_TOKEN,""));
        accessToken.setExpAccess(pref.getString(AccessToken.KEY_EXPIRE_ACCESS_TOKEN,""));
        accessToken.setExpRefresh(pref.getString(AccessToken.KEY_EXPIRE_REFRESH_TOKEN,""));
        accessToken.setwUid(pref.getString(KEY_W_UID,""));
        accessToken.setwTokenSecret(pref.getString(KEY_W_SECRET,""));
        accessToken.setwTokenKey(pref.getString(KEY_W_TOKEN,""));
        return accessToken;
    }
}
