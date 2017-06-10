package com.mmtechbd.remotehealthmonitor.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class AccessToken {
    public static final String DEVICE_IHEALTH = "ihealth";
    public static final String DEVICE_WITHINGS = "withings";
    public static final String KEY_ACCESS_TOKEN = "AccessToken";
    public static final String KEY_EXPIRE_ACCESS_TOKEN = "Expires";
    public static final String KEY_REFRESH_TOKEN = "RefreshToken";
    public static final String KEY_EXPIRE_REFRESH_TOKEN = "RefreshTokenExpires";
    public static final String KEY_USER_ID = "UserID";
    private static final String KEY_U_ID = "u_id";

    private String accessToken;
    private String userId;
    private String refreshToken;

    private String wTokenKey;
    private String wTokenSecret;
    private String wUid;
    private long expireAccess, expireRefresh;

    private String mExpRefresh;
    private String mExpAccess;
    private boolean hasIhealth, hasWithings;

    public AccessToken() {
    }

    public AccessToken(JSONObject mObj, boolean timeConvert) {
        try {
            setvalues(mObj, timeConvert);
        } catch (JSONException e) {
            System.out.println(e.toString());
        }
    }

    public AccessToken(JSONObject object) {
        try {
            JSONArray array = object.getJSONArray("devicetokens");
//            print(array.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                print(obj.toString());
                if (obj.has("device")) {
                    String device = obj.getString("device");
                    print(device);
                    if (device.equals(DEVICE_IHEALTH)) {
                        setvalues(obj);
                        this.hasIhealth = true;
                    } else if (device.equals(DEVICE_WITHINGS)) {
                        setwTokenKey(obj.getString("itoken"));
                        setwTokenSecret(obj.getString("refresh_token"));
                        setwUid(obj.getString("u_id"));
                        this.hasWithings = true;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setvalues(JSONObject obj) throws JSONException {
        setAccessToken(obj.getString("itoken"));
        setRefreshToken(obj.getString("refresh_token"));
        setUserId(obj.getString("u_id"));
        mExpRefresh = obj.getString("exp_refresh");
        mExpAccess = obj.getString("exp");
        if (mExpAccess.isEmpty()) {
            setExpireAccess(0);
        } else {
            long expires = obj.getLong("exp");
            setExpireAccess(expires);
        }
        if (mExpRefresh.isEmpty()) {
            setExpireRefresh(0);
        } else {
            long expires = obj.getLong("exp_refresh");
            setExpireRefresh(expires);
        }
    }

    private void print(String msg) {
        System.out.println(msg);
    }

    private void setvalues(JSONObject obj, boolean timeConvert) throws JSONException {
        setAccessToken(obj.getString(KEY_ACCESS_TOKEN));
        setRefreshToken(obj.getString(KEY_REFRESH_TOKEN));
        setUserId(obj.getString(KEY_USER_ID));
        mExpRefresh = obj.getString(KEY_EXPIRE_REFRESH_TOKEN);
        mExpAccess = obj.getString(KEY_EXPIRE_ACCESS_TOKEN);
        if (mExpAccess.isEmpty()) {
            setExpireAccess(0);
        } else {
            long expires = obj.getLong(KEY_EXPIRE_ACCESS_TOKEN);
            if (timeConvert) {
                expires *= 1000;
                expires += System.currentTimeMillis();
                mExpAccess = String.valueOf(expires);
            }
            setExpireAccess(expires);
        }
        if (mExpRefresh.isEmpty()) {
            setExpireRefresh(0);
        } else {
            long expires = obj.getLong(KEY_EXPIRE_REFRESH_TOKEN);
            if (timeConvert) {
                expires *= 1000;
                expires += System.currentTimeMillis();
                mExpRefresh = String.valueOf(expires);
            }
            setExpireRefresh(expires);
        }
    }


    public String getwTokenKey() {
        return wTokenKey;
    }

    public void setwTokenKey(String wTokenKey) {
        this.wTokenKey = wTokenKey;
    }

    public String getwTokenSecret() {
        return wTokenSecret;
    }

    public void setwTokenSecret(String wTokenSecret) {
        this.wTokenSecret = wTokenSecret;
    }

    public String getwUid() {
        return wUid;
    }

    public void setwUid(String wUid) {
        this.wUid = wUid;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setExpireAccess(long expireAccess) {
        this.expireAccess = expireAccess;
    }

    public long getExpireAccess() {
        return expireAccess;
    }

    public void setExpireRefresh(long expireRefresh) {
        this.expireRefresh = expireRefresh;
    }

    public long getExpireRefresh() {
        return expireRefresh;
    }

    public String getExpAccess() {
        return mExpAccess;
    }

    public String getExpRefresh() {
        return mExpRefresh;
    }

    public void setExpAccess(String string) {
        this.mExpAccess = string;
    }

    public void setExpRefresh(String string) {
        this.mExpRefresh = string;
    }

    public boolean hasIhealth() {
        return hasIhealth;
    }

    public boolean hasWithings() {
        return hasWithings;
    }
}
