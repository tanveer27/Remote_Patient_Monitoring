package com.mmtechbd.remotehealthmonitor.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mmtechbd.remotehealthmonitor.BuildConfig;
import com.mmtechbd.remotehealthmonitor.R;
import com.mmtechbd.remotehealthmonitor.User_Panel;
import com.mmtechbd.remotehealthmonitor.adapter.DoctorAdapter;
import com.mmtechbd.remotehealthmonitor.model.Doctor;
import com.mmtechbd.remotehealthmonitor.net.DataDownloader;
import com.mmtechbd.remotehealthmonitor.net.DoctorSubscriber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class Doctor_List extends Fragment implements AdapterView.OnItemClickListener {
    private static final String ROOT_URL = BuildConfig.ROOT_URL;
    private ListView listView;
    private DoctorAdapter mAdapter;
    private String token;
    private ProgressBar mProgress;

    public Doctor_List() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            token = getArguments().getString(User_Panel.ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_doctor__list, container, false);
        mProgress = (ProgressBar) view.findViewById(R.id.progressBar);
        listView = (ListView)view.findViewById(R.id.list_doctors);
        mAdapter = new DoctorAdapter(getActivity());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
        DataDownloader.download(getActivity(), ROOT_URL + "/api/getDoctorList", token, /*progress,*/ new DataDownloader.DataCallback() {
            @Override
            public void onStartReceiving() {
                if (mAdapter!=null) mAdapter.removeAllData();
            }

            @Override
            public void onReceivedResult(String result) {
                if (mProgress!=null) mProgress.setVisibility(View.GONE);
                if (mAdapter!=null) mAdapter.removeAllData();
                try {
                    JSONArray array = new JSONArray(result);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        if (mAdapter!=null) mAdapter.addData(object);
                    }
                    if (mAdapter!=null) mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTokenMisMatch() {
                User_Panel.logOut(getActivity());
            }
        });
        return view;
    }

    private void print(String token) {
        System.out.println(token);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Doctor doctor = mAdapter.getItem(i);
        showDoctorDialog(doctor);
    }

    private void showDoctorDialog(final Doctor doctor) {
        View view = View.inflate(getActivity(),R.layout.activity_show_doctor_details,null);
        TextView doctor_fname=(TextView)view.findViewById(R.id.doctor_fname);
        TextView doctor_degree=(TextView)view.findViewById(R.id.doctor_degree);
        TextView doctor_speciality=(TextView)view.findViewById(R.id.doctor_speciality);
        TextView doctor_consulting_hour=(TextView)view.findViewById(R.id.doctor_consulting_hour);
        TextView doctor_hospital=(TextView)view.findViewById(R.id.doctor_hospital);
        doctor_fname.setText(doctor.getName());
        doctor_degree.setText(doctor.getDegree());
        doctor_speciality.setText(doctor.getSpeciality());
        doctor_consulting_hour.setText(doctor.getConsultingHour());
        doctor_hospital.setText(doctor.getHospital());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setIcon(R.drawable.ic_info);
        builder.setTitle("Doctor Detail");
        builder.setNegativeButton("DISMISS",null);
        builder.setPositiveButton("SUBSCRIBE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new DoctorSubscriber(getActivity(), doctor.getId(), new DoctorSubscriber.DoctorSubscriberCallback() {
                    @Override
                    public void onSuccess(String s) {
                        Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailed() {
                        Toast.makeText(getActivity(),"Failed to subscribe! Maybe you are already subscribed.",Toast.LENGTH_SHORT).show();
                    }
                }).execute(ROOT_URL+"/api/subscribe");
            }
        });
        builder.show();
    }
}