package com.mmtechbd.remotehealthmonitor;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.mmtechbd.remotehealthmonitor.model.AccessToken;
import com.mmtechbd.remotehealthmonitor.utils.Withings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.mmtechbd.remotehealthmonitor.utils.IHealth.getActivityUrl;
import static com.mmtechbd.remotehealthmonitor.utils.IHealth.getBpDataUrl;
import static com.mmtechbd.remotehealthmonitor.utils.IHealth.getGlucoseUrl;
import static com.mmtechbd.remotehealthmonitor.utils.IHealth.getSpo2DataUrl;
import static com.mmtechbd.remotehealthmonitor.utils.IHealth.getWeightUrl;
import static com.mmtechbd.remotehealthmonitor.utils.TokenUtils.getAccessToken;
import static com.mmtechbd.remotehealthmonitor.utils.Universal.getStringFromStream;
import static com.mmtechbd.remotehealthmonitor.utils.Universal.getStringFromUrl;
import static com.mmtechbd.remotehealthmonitor.utils.Universal.isConnected;
import static com.mmtechbd.remotehealthmonitor.utils.Withings.getActivitiesUrl;
import static com.mmtechbd.remotehealthmonitor.utils.Withings.getBodyMeasuresUrl;
import static java.lang.String.format;

public class DataUploaderService extends Service {
    private static final int ONGOING_NOTIFICATION_ID = 570;
    private static final String KEY_EXTRA = "token";
    public static final String KEY_EXTRA_FROM_SERVICE = "from.service";
    private boolean isFailed;

    private static String getUploadUrl(String route) {
        return format("%s/api/%s",BuildConfig.ROOT_URL,route);
    }
    private AccessToken accessToken;

    public static void start(Context context, String token) {
        Intent intent = new Intent(context,DataUploaderService.class);
        intent.putExtra(KEY_EXTRA,token);
        context.startService(intent);
    }

    public DataUploaderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String token = intent.getStringExtra(KEY_EXTRA);
        if (token!=null && !token.isEmpty()) {
            startForegroundService(token);
            accessToken = getAccessToken(this);
            try {
                uploadBpDate(token);
                uploadGlucose(token);
                uploadSpo2(token);
                uploadWeight(token);
                uploadActivity(token);
                uploadWithingsData(token);
            } catch (MalformedURLException | NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
                e.printStackTrace();
            }
        }
        return START_NOT_STICKY;
    }

    private void uploadActivity(String token) throws MalformedURLException {
        String route = "enterActivityData";
        URL url = getActivityUrl(accessToken);
        uploadData(route,token,url);
    }

    private void uploadWeight(String token) throws MalformedURLException {
        String route = "enterWeightData";
        URL url = getWeightUrl(accessToken);
        uploadData(route,token,url);
    }

    private void uploadSpo2(String token) throws MalformedURLException {
        String route = "enterSPO2Data";
        URL url = getSpo2DataUrl(accessToken);
        uploadData(route,token,url);
    }

    private void uploadGlucose(String token) throws MalformedURLException {
        String route = "enterGlucoseData";
        URL url = getGlucoseUrl(accessToken);
        uploadData(route,token,url);
    }

    private void uploadBpDate(final String token) throws MalformedURLException {
        String route = "enterBpData";
        URL url = getBpDataUrl(accessToken);
        uploadData(route,token,url);
    }

    private void uploadData(String route, String token, URL url) {
        final String params[] = new String[]{getUploadUrl(route),token,AccessToken.DEVICE_IHEALTH};
        if (isConnected(this)) new IDataUploader(url).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,params);
    }

    private void uploadWithingsData( String token) throws MalformedURLException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String route = "enterWithingsData";
        final String params[] = new String[]{getUploadUrl(route),token};
        if (isConnected(this)) new IDataUploader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,params);
    }

    private class IDataUploader extends  AsyncTask<String,String,String> {
        private URL mUrl;
        IDataUploader(URL url) {
            this.mUrl = url;
        }

        IDataUploader() {
        }

        @Override
        protected String doInBackground(String... params) {
            if (params.length<2) {
                print("wrong params provided : "+params.toString());
                return null;
            }
            try {
                String data = null;
                String activities = null, bodyMeasures = null;
                if (mUrl!=null) data = getStringFromUrl(mUrl);
                else {
                    activities = getStringFromUrl(getActivitiesUrl(accessToken));
                    bodyMeasures = getStringFromUrl(getBodyMeasuresUrl(accessToken));
                    JSONObject objAct = new JSONObject(activities);
                    JSONObject objMes = new JSONObject(bodyMeasures);
                    JSONObject objBodyAct = objAct.getJSONObject("body");
                    JSONObject objBodyMes = objMes.getJSONObject("body");
                    JSONArray arrayAct = objBodyAct.getJSONArray("activities");
                    JSONArray arrayMes = objBodyMes.getJSONArray("measuregrps");
                    activities = arrayAct.toString();
                    bodyMeasures = arrayMes.toString();
                }
                print(String.format("Data == %s\nActivities == %s\nBodyMeasures == %s",data,activities,bodyMeasures));
                URL urlPost = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) urlPost.openConnection();
                connection.setConnectTimeout(15000);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                String auth = "Bearer " +params[1];
                connection.setRequestProperty ("Authorization", auth);
                Uri.Builder builder = new Uri.Builder();
                if (data!=null) builder.appendQueryParameter("data",data);
                else {
                    builder.appendQueryParameter("activities",activities);
                    builder.appendQueryParameter("body_measures",bodyMeasures);
                }
                if (params.length>=3) builder.appendQueryParameter("device",params[2]);
//                else builder.appendQueryParameter("device",AccessToken.DEVICE_IHEALTH);
                String query = builder.build().getEncodedQuery();
                OutputStream out = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
                writer.write(query);
                writer.close();
                out.close();
                connection.connect();
                print("Connected to "+urlPost.toString());
                print("Response code == "+connection.getResponseCode());

                if (connection.getResponseCode() == 200) {
                    return getStringFromStream(connection.getInputStream());
                } else {
                    print(getStringFromStream(connection.getErrorStream()));
                }
            } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s==null) {
                isFailed = true;
                print("result == null");
//                makeText("Failed to upload health data.");
            } else {
                print("Result == "+s);
                //makeText(s);
            }
            stopSelf();
        }
    }

    private void print(String msg) {
        System.out.println(msg);
    }

    private void makeText(String s) {
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }

    private void startForegroundService(String token) {
        Intent notificationIntent = new Intent(this, User_Panel.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra(KEY_EXTRA,token);
        notificationIntent.putExtra(KEY_EXTRA_FROM_SERVICE,true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(pendingIntent);
        builder.setContentText("Collecting health data...");
        builder.setContentTitle("Uploading health data...");
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        builder.setSmallIcon(R.drawable.ic_upload);
        builder.setOngoing(true);
        builder.setProgress(0,0,true);
        Notification notfication = builder.build();
        startForeground(ONGOING_NOTIFICATION_ID,notfication);
    }
}
