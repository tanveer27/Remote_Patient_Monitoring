package com.mmtechbd.remotehealthmonitor.fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mmtechbd.remotehealthmonitor.R;
import com.mmtechbd.remotehealthmonitor.User_Panel;
import com.mmtechbd.remotehealthmonitor.adapter.ActivityAdapter;
import com.mmtechbd.remotehealthmonitor.model.Activity;
import com.mmtechbd.remotehealthmonitor.model.Prescriptions;
import com.mmtechbd.remotehealthmonitor.net.DataDownloader;
import com.mmtechbd.remotehealthmonitor.net.ReportStatusSender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.mmtechbd.remotehealthmonitor.BuildConfig.ROOT_URL;
import static com.mmtechbd.remotehealthmonitor.net.DataDownloader.download;

public class PrescriptionFragment extends Fragment implements View.OnClickListener, DataDownloader.DataCallback {
    private static final String ARG_PARAM = "param";
    private String mParam;
    private ProgressBar mProgress;
    private TextView tvWeight, tvGlucose, tvBp, tvTemp, tvSpo2, tvActivity;

    public PrescriptionFragment() {
        // Required empty public constructor
    }

    public static PrescriptionFragment newInstance(String param) {
        PrescriptionFragment fragment = new PrescriptionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_prescription, container, false);
        tvWeight =(TextView)v.findViewById(R.id.tvWeight);
        tvGlucose =(TextView)v.findViewById(R.id.tvGlucose);
        tvBp =(TextView)v.findViewById(R.id.tvBp);
        tvTemp =(TextView)v.findViewById(R.id.tvTemp);
        tvSpo2 =(TextView)v.findViewById(R.id.tvSpo2);
        tvActivity =(TextView)v.findViewById(R.id.tvActivity);
        mProgress = (ProgressBar) v.findViewById(R.id.progressBar);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        reloadData();
        return v;
    }

    private void reloadData() {
        download(getActivity(), ROOT_URL + "/api/getPrescriptions", mParam, /*progress,*/ this);
    }

    @Override
    public void onClick(View view) {
        reloadData();
    }

    @Override
    public void onStartReceiving() {
        if (mProgress!=null) mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReceivedResult(String result) {
        if (mProgress!=null) mProgress.setVisibility(View.GONE);
        Prescriptions prescriptions = new Prescriptions(result);
        if (prescriptions.isOk()) {
            tvWeight.setText(prescriptions.getWeight());
            tvGlucose.setText(prescriptions.getGlucose());
            tvBp.setText(prescriptions.getBp());
            tvTemp.setText(prescriptions.getTemp());
            tvSpo2.setText(prescriptions.getSpo2());
            tvActivity.setText(prescriptions.getActivity());
        }
    }

    @Override
    public void onTokenMisMatch() {
        User_Panel.logOut(getActivity());
    }
}
