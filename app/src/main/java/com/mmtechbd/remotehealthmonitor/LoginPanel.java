package com.mmtechbd.remotehealthmonitor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mmtechbd.remotehealthmonitor.model.AccessToken;
import com.mmtechbd.remotehealthmonitor.net.ItokenSender;
import com.mmtechbd.remotehealthmonitor.net.SignIn;
import com.mmtechbd.remotehealthmonitor.net.WtokenSender;
import com.mmtechbd.remotehealthmonitor.utils.TokenUtils;
import com.mmtechbd.remotehealthmonitor.utils.Withings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static android.widget.Toast.makeText;
import static com.mmtechbd.remotehealthmonitor.net.SignIn.*;
import static com.mmtechbd.remotehealthmonitor.utils.IHealth.getRefreshTokenUrl;
import static com.mmtechbd.remotehealthmonitor.utils.IHealth.getTokenUrl;
import static com.mmtechbd.remotehealthmonitor.utils.Universal.isConnected;

/**
 * A login screen that offers login via email/password.
 */
public class LoginPanel extends AppCompatActivity{

    public static final String SHARED_PREFERENCE_TOKEN = "myToken";
    public static final int REQ_CODE = 724;
    private static final int REQ_CODE_WITHINGS = 9484;

    private EditText mEmailView;
    private EditText mPasswordView;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private boolean okItoken, okWtoken;
    private String mToken;
//    public static final String ROOT_URL = "http://ehealth.mmtechbd.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_panel);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        prefs = getSharedPreferences(SHARED_PREFERENCE_TOKEN, Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.putString("token", "");
        editor.commit();

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected(LoginPanel.this)) attemptLogin(view);
                else Toast.makeText(LoginPanel.this,"Please turn your internet connection on.",Toast.LENGTH_LONG).show();
            }
        });

    }
    public void attemptLogin(final View view){
        final String user_Email=mEmailView.getText().toString();
        final String user_Pass=mPasswordView.getText().toString();
        if (user_Email.isEmpty() || user_Pass.isEmpty()){
            if (user_Email.isEmpty()) mEmailView.setError("Please enter your email address here.");
            if (user_Pass.isEmpty()) mPasswordView.setError("Please enter your password here.");
            return;
        }
        new SignIn(this, user_Email, user_Pass, "pat", new SignInCallback() {
            @Override
            public void onSuccess(JSONObject object) {

                try {
                    mToken = object.getString("token");
                    print("Tkn == "+mToken );
                    AccessToken accessToken = new AccessToken(object);
                    TokenUtils.saveToPref(editor,accessToken);
                    if (accessToken.getExpireAccess()-(1000*60*60*24*7)>System.currentTimeMillis() && accessToken.hasWithings()) {
                        editor.putString("token", mToken);
                        if (editor.commit()){
                            Intent intent = new Intent(LoginPanel.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    } else if (accessToken.getExpireAccess()==0 || !accessToken.hasWithings()){
                        if (accessToken.getExpireAccess()==0) {
                            Intent intent = new Intent(LoginPanel.this, IAuthActivity.class);
                            startActivityForResult(intent, REQ_CODE);
                        } else okItoken = true;
                        if (!accessToken.hasWithings()) {
                            Intent intent = new Intent(LoginPanel.this, WAuthActivity.class);
                            startActivityForResult(intent,REQ_CODE_WITHINGS);
                        } else okWtoken = true;
                    } else {
                        new ItokenSender(LoginPanel.this, mToken) {
                            @Override
                            public void onSuccess(String token) {
                                logIn();
                            }
                        }.execute(getRefreshTokenUrl(accessToken.getRefreshToken()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    makeToast("Something went wrong!");
                }
            }

            @Override
            public void onFailed(String msg) {
                if (msg==null) makeToast("Something went wrong!");
                else makeToast(String.format("Could not log in: %s.",msg));
            }
        }).execute();

    }

    private void makeToast(String s) {
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }

    private long currTime;
    @Override
    public void onBackPressed() {
        if (currTime+2000<System.currentTimeMillis()) {
            makeText(this,"Tap back button again to exit.",Toast.LENGTH_SHORT).show();
            currTime = System.currentTimeMillis();
        } else super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_CODE) {
                String authCode = data.getStringExtra(IAuthActivity.EXTRA_URL);
                String tokenUrl = getTokenUrl(authCode);
                print(String.format("onIActivityResult: authcode = %s ; \ntokenUrl = %s",authCode,tokenUrl));
                new ItokenSender(this, mToken) {
                    @Override
                    public void onSuccess(String string) {
                        if (okWtoken) logIn();
                        okItoken = true;
                    }
                }.execute(tokenUrl);
            } else if (requestCode == REQ_CODE_WITHINGS) {
                String userId = data.getStringExtra(WAuthActivity.EXTRA_USER_ID);
                String token = data.getStringExtra(WAuthActivity.EXTRA_TOKEN);
                String secret = data.getStringExtra(WAuthActivity.EXTRA_SECRET);
                try {
                    String url = Withings.getTokenUrl(userId,token,secret);
                    print(String.format("onWActivityResult: userId = %s ; token = %s ; secret = %s ; \nurl = %s",userId,token,secret,url));
                    new WtokenSender(this, mToken) {
                        @Override
                        public void onSuccess(String string) {
                            if (okItoken) logIn();
                            okWtoken = true;
                        }
                    }.execute(url);
                } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "Something went wrong! Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    private void print(String msg) {
        System.out.println(msg);
    }

    private void logIn() {
        editor.putString("token", mToken);
        editor.commit();
        Intent intent = new Intent(this, User_Panel.class);
        intent.putExtra("token", mToken);
        startActivity(intent);
        finish();
    }
}
