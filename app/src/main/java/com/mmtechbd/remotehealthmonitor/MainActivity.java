package com.mmtechbd.remotehealthmonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import static com.mmtechbd.remotehealthmonitor.utils.Universal.getToken;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        String token= getToken(this);
        System.out.println("MainActivity token = "+token);
        if(token.equals("")){
            Intent intent=new Intent(this,LoginPanel.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else {
            Intent intent=new Intent(this,User_Panel.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("token",token);
            startActivity(intent);
        }
        finish();
    }

}

