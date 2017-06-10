package com.mmtechbd.remotehealthmonitor.utils;

import android.util.Base64;

import com.mmtechbd.remotehealthmonitor.BuildConfig;
import com.mmtechbd.remotehealthmonitor.model.AccessToken;
import com.mmtechbd.remotehealthmonitor.model.RequestToken;
import com.mmtechbd.remotehealthmonitor.model.WAuth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static android.util.Base64.*;
import static com.mmtechbd.remotehealthmonitor.utils.Universal.getStringFromUrl;

/**
 * Created by Roaim on 27-Nov-16.
 */

public class Withings {
    private static final String CONSUMER_SECRET = BuildConfig.WITHINGS_CONSUMER_SECRET;

    public static String getTokenUrl(String userId, String token, String secret) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String nonce = getNonce();
        int timestamp = getTimestamp();
        String base = getBaseAccessToken(token, nonce, timestamp);
        String sign = getSignature(base, CONSUMER_SECRET + secret).trim();
        String signature = URLEncoder.encode(sign, "UTF-8");
        return "https://oauth.withings.com/account/access_token?oauth_consumer_key=710204947d7bc9c5c31b59135ce12b783c226e42f5110396380367b18f70f3&oauth_nonce=" +
                nonce +
                "&oauth_signature=" +
                signature +
                "&oauth_signature_method=HMAC-SHA1&oauth_timestamp=" +
                timestamp +
                "&oauth_token=" +
                token +
                "&oauth_version=1.0";
    }

    public static WAuth getAuth() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        RequestToken requestToken = getRequestToken();
        if (requestToken!=null) {
            String nonce = getNonce();
            int timestamp = getTimestamp();
            String base = getBaseAuth(requestToken.getToken(), nonce, timestamp);
            String sign = getSignature(base, CONSUMER_SECRET + requestToken.getTokenSecret()).trim();
            String signature = URLEncoder.encode(sign, "UTF-8");
            String url =  "https://oauth.withings.com/account/authorize?oauth_consumer_key=710204947d7bc9c5c31b59135ce12b783c226e42f5110396380367b18f70f3&oauth_nonce=" +
                    nonce +
                    "&oauth_signature=" +
                    signature +
                    "&oauth_signature_method=HMAC-SHA1&oauth_timestamp=" +
                    timestamp +
                    "&oauth_token=" +
                    requestToken.getToken() +
                    "&oauth_version=1.0";
            return new WAuth(url,requestToken);
        }
        return null;
    }

    private static RequestToken getRequestToken() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String uri = getReqTokUrl();
        print("Withings reqToken url = "+uri);
        URL url = new URL(uri);
        String token = getStringFromUrl(url).trim();
        print("Withings Request token = "+token);
        if (token.contains("&")) {
            String[] queryParams = token.split("&");
            String[] keyValueToken = queryParams[0].split("=");
            String[] keyValueSecret = queryParams[1].split("=");
            return new RequestToken (keyValueToken[1], keyValueSecret[1]);
        }
        return null;
    }

    private static void print(String s) {
        System.out.println(s);
    }

    private static String getReqTokUrl() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String nonce = getNonce();
        int timeStamp = getTimestamp();
        String base = getBaseRequestToken(nonce,timeStamp);
        String sign = getSignature(base,CONSUMER_SECRET).trim();
        print("getReqTokUrl sign = "+sign);
        String signature = URLEncoder.encode(sign,"UTF-8");
        print("getReqTokUrl sign = "+signature);
        return "https://oauth.withings.com/account/request_token?oauth_callback=http%3A%2F%2Frpm.mmtechbd.com%2F&oauth_consumer_key=710204947d7bc9c5c31b59135ce12b783c226e42f5110396380367b18f70f3&oauth_nonce="+
                nonce+
                "&oauth_signature="+
                signature+
                "&oauth_signature_method=HMAC-SHA1&oauth_timestamp="+
                timeStamp+
                "&oauth_version=1.0";
    }

    private static String getBaseData(String token, String nonce, int timestamp, String uid) {
        return "GET&http%3A%2F%2Fwbsapi.withings.net%2Fmeasure&action%3Dgetmeas%26oauth_consumer_key%3D710204947d7bc9c5c31b59135ce12b783c226e42f5110396380367b18f70f3%26oauth_nonce%3D" +
                nonce +
                "%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D" +
                timestamp +
                "%26oauth_token%3D" +
                token +
                "%26oauth_version%3D1.0%26userid%3D" +
                uid;
    }

    private static String getBaseAccessToken(String token, String nonce, int timeStamp) {
        return "GET&https%3A%2F%2Foauth.withings.com%2Faccount%2Faccess_token&oauth_consumer_key%3D710204947d7bc9c5c31b59135ce12b783c226e42f5110396380367b18f70f3%26oauth_nonce%3D" +
                nonce +
                "%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D" +
                timeStamp +
                "%26oauth_token%3D" +
                token +
                "%26oauth_version%3D1.0";
    }

    private static String getBaseAuth(String token, String nonce, int timeStamp) {
        return "GET&https%3A%2F%2Foauth.withings.com%2Faccount%2Fauthorize&oauth_consumer_key%3D710204947d7bc9c5c31b59135ce12b783c226e42f5110396380367b18f70f3%26oauth_nonce%3D" +
                nonce +
                "%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D" +
                timeStamp +
                "%26oauth_token%3D" +
                token +
                "%26oauth_version%3D1.0";
    }

    private static String getBaseRequestToken(String nonce, int times) {
        return "GET&https%3A%2F%2Foauth.withings.com%2Faccount%2Frequest_token&oauth_callback%3Dhttp%253A%252F%252Frpm.mmtechbd.com%252F%26oauth_consumer_key%3D710204947d7bc9c5c31b59135ce12b783c226e42f5110396380367b18f70f3%26oauth_nonce%3D"+
                nonce+
                "%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D"+
                times+
                "%26oauth_version%3D1.0";
    }

    private static String getNonce() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String md5 = ""+Math.random();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
    }

    private static String getSignature(String base, String key) throws UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeyException {
        return new String(hmacSha1(base,key));
    }

    private static byte[] hmacSha1(String base, String key) throws UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeyException {
        String type = "HmacSHA1";
        SecretKeySpec secret = new SecretKeySpec(key.getBytes(), type);
        Mac mac = Mac.getInstance(type);
        mac.init(secret);
        byte[] bytes = mac.doFinal(base.getBytes());
        return encode(bytes, DEFAULT);
    }

    private static int getTimestamp() {
        return (int) (System.currentTimeMillis()/1000);
    }

    public static URL getActivitiesUrl(AccessToken accessToken) throws MalformedURLException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String nonce = getNonce();
        int timestamp = getTimestamp();
        String base = getBaseData(accessToken.getwTokenKey(), nonce, timestamp,accessToken.getwUid());
        String sign = getSignature(base, CONSUMER_SECRET + accessToken.getwTokenSecret()).trim();
        String signature = URLEncoder.encode(sign, "UTF-8");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        String today = sdf.format(cal.getTime());
        cal.set(2016,0,1);
        String pastDay = sdf.format(cal.getTime());

        String url = "http://wbsapi.withings.net/v2/measure?action=getactivity&oauth_consumer_key=710204947d7bc9c5c31b59135ce12b783c226e42f5110396380367b18f70f3&oauth_nonce=" +
                nonce +
                "&oauth_signature=" +
                signature +
                "&oauth_signature_method=HMAC-SHA1&oauth_timestamp=" +
                timestamp +
                "&oauth_token=" +
                accessToken.getwTokenKey() +
                "&oauth_version=1.0&userid=" +
                accessToken.getwUid() +
                "&startdateymd=" +
                pastDay +
                "&enddateymd=" +
                today;

        return new URL(url);
    }

    public static URL getBodyMeasuresUrl(AccessToken accessToken) throws MalformedURLException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String nonce = getNonce();
        int timestamp = getTimestamp();
        String base = getBaseData(accessToken.getwTokenKey(), nonce, timestamp,accessToken.getwUid());
        String sign = getSignature(base, CONSUMER_SECRET + accessToken.getwTokenSecret()).trim();
        String signature = URLEncoder.encode(sign, "UTF-8");

        String url = "http://wbsapi.withings.net/measure?action=getmeas&oauth_consumer_key=710204947d7bc9c5c31b59135ce12b783c226e42f5110396380367b18f70f3&oauth_nonce=" +
                nonce +
                "&oauth_signature=" +
                signature +
                "&oauth_signature_method=HMAC-SHA1&oauth_timestamp=" +
                timestamp +
                "&oauth_token=" +
                accessToken.getwTokenKey() +
                "&oauth_version=1.0&userid=" +
                accessToken.getwUid();

        return new URL(url);
    }
}
