package com.mmtechbd.remotehealthmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mmtechbd.remotehealthmonitor.net.ProfileUpdater;

public class User_Profile_Settings extends AppCompatActivity {

    private static final String ROOT_URL = BuildConfig.ROOT_URL;
    private EditText fname;
    private EditText lname;
    private EditText sex;
    private EditText dob;
    private EditText add;
    private EditText weight;
    private EditText height;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__profile__settings);
        Intent intent=getIntent();
        // userName=intent.getStringExtra("userName");
        token=intent.getStringExtra("token");
        //email="tanveer27@gmail.com";
        fname=(EditText)findViewById(R.id.userFName);
        lname=(EditText)findViewById(R.id.userLName);
        sex=(EditText)findViewById(R.id.userSex);
        dob=(EditText)findViewById(R.id.userDob);
        weight=(EditText)findViewById(R.id.userWeight);
        height=(EditText)findViewById(R.id.userHeight);
        add=(EditText)findViewById(R.id.userAddress);
        fname.requestFocus();
    }
    public void updateUser(View view){
        String fn = fname.getText().toString();
        String ln = lname.getText().toString();
        String sx = sex.getText().toString();
        String db = dob.getText().toString();
        String wgt = weight.getText().toString();
        String hgt = height.getText().toString();
        String ads = add.getText().toString();
        if (fn.isEmpty() || ln.isEmpty() || sx.isEmpty() || db.isEmpty() || wgt.isEmpty() || hgt.isEmpty() || ads.isEmpty()) {
            return;
        } else {
            String[] params = new String[]{fn,ln,sx,db,hgt,wgt,ads};
            new ProfileUpdater(this, new ProfileUpdater.ProfileUpdateCallback() {
                @Override
                public void onSuccess(String s) {
                    makeTost(s);
                    finish();
                }

                @Override
                public void onFailed() {
                    makeTost("Failed to update profile!");
                }
            }).execute(params);
        }
    }

    private void makeTost(String s) {
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }

}
