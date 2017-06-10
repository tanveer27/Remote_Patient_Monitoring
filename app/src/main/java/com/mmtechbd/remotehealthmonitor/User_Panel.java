package com.mmtechbd.remotehealthmonitor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mmtechbd.remotehealthmonitor.fragment.ActivityFragment;
import com.mmtechbd.remotehealthmonitor.fragment.Blood_Pressure;
import com.mmtechbd.remotehealthmonitor.fragment.Doctor_List;
import com.mmtechbd.remotehealthmonitor.fragment.Glucose;
import com.mmtechbd.remotehealthmonitor.fragment.PrescriptionFragment;
import com.mmtechbd.remotehealthmonitor.fragment.Spo2Fragment;
import com.mmtechbd.remotehealthmonitor.fragment.Temperature;
import com.mmtechbd.remotehealthmonitor.fragment.Weight;

import static android.widget.Toast.makeText;
import static com.mmtechbd.remotehealthmonitor.utils.Universal.isConnected;

public class User_Panel extends AppCompatActivity {
    public static final String ARG_PARAM = "param";
    private static SectionsPagerAdapter mSectionsPagerAdapter;
    private static String token;
    private boolean isFromService;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        token=intent.getStringExtra("token");
        isFromService = intent.getBooleanExtra(DataUploaderService.KEY_EXTRA_FROM_SERVICE,false);
        System.out.println("UserPanel token = "+token); //+"\nisFromService = "+isFromService);
        if(token.equals("") || token == null){
            logOut(this);
        }
        setContentView(R.layout.activity_user__panel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        /*TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);*/
//        if (!isFromService && isConnected(this)) DataUploaderService.upload(this,token);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFromService && isConnected(this)) DataUploaderService.start(this,token);
        isFromService = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user__panel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profile_settings) {
            Intent intent = new Intent(User_Panel.this, User_Profile_Settings.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.logout) {
            logOut(this);
        } else if (id == R.id.menu_upload) {
            if (isConnected(this)) DataUploaderService.start(this,token);
            else makeText(this,"Please turn on Internet Connection.",Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private long currTime;
    @Override
    public void onBackPressed() {
        if (currTime+2000<System.currentTimeMillis()) {
            makeText(this,"Tap back button again to exit.",Toast.LENGTH_SHORT).show();
            currTime = System.currentTimeMillis();
        } else super.onBackPressed();
    }

    public static void logOut(Context context) {
        if (context!=null) {
            SharedPreferences prefs = context.getSharedPreferences("myToken", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("token", "");
            editor.apply();
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            ((Activity)context).finish();
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private Fragment fragment;
        private Bundle args;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            args = new Bundle();
            args.putString(ARG_PARAM, token);
        }

        private final Fragment[] fragments = {
                new Doctor_List(),
                PrescriptionFragment.newInstance(token),
                new Weight(),
                new Glucose(),
                new Blood_Pressure(),
                new Temperature(),
                new Spo2Fragment(),
                ActivityFragment.newInstance(token)
        };

        private final String[] titles = {
                "Doctor List",
                "Prescriptions",
                "Weight & BMI",
                "Glucose",
                "Blood Pressure",
                "Temperature",
                "Spo2 and Pulse",
                "Activity"
        };

        @Override
        public Fragment getItem(int position) {
//            Fragment fragment=null;
            fragment = fragments[position];
            if (!(fragment instanceof ActivityFragment || fragment instanceof PrescriptionFragment)){
                fragment.setArguments(args);
            }
            return  fragment;
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.length>position?titles[position]:"Add title in User_Panel.java";
        }
    }

    public String getToken(){
        return token;
    }
}
