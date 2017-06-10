package com.mmtechbd.remotehealthmonitor.fragment;

import android.content.Context;
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

import com.mmtechbd.remotehealthmonitor.BuildConfig;
import com.mmtechbd.remotehealthmonitor.R;
import com.mmtechbd.remotehealthmonitor.User_Panel;
import com.mmtechbd.remotehealthmonitor.adapter.Spo2Adapter;
import com.mmtechbd.remotehealthmonitor.model.Spo2;
import com.mmtechbd.remotehealthmonitor.net.DataDownloader;
import com.mmtechbd.remotehealthmonitor.net.ReportStatusSender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.mmtechbd.remotehealthmonitor.net.DataDownloader.download;

public class Spo2Fragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, DataDownloader.DataCallback {
    private static final String ROOT_URL = BuildConfig.ROOT_URL;
    private String token;
    private ListView datalistView;
    private Spo2Adapter mAdapter;
    private ProgressBar mProgress;

    public Spo2Fragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_blood__pressure, container, false);
        mProgress = (ProgressBar) view.findViewById(R.id.progressBar);
        datalistView=(ListView)view.findViewById(R.id.report_List_Bp);
        mAdapter = new Spo2Adapter(getActivity());
        datalistView.setAdapter(mAdapter);
        datalistView.setOnItemClickListener(this);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        reloadData();
        return view;
    }

    @Override
    public void onStartReceiving() {
        if (mProgress!=null) mProgress.setVisibility(View.VISIBLE);
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

    public void reloadData() {
        download(getActivity(), ROOT_URL + "/api/getSPO2Reports", token, /*progress,*/ this);
    }

    @Override
    public void onClick(View view) {
        reloadData();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Spo2 bp = mAdapter.getItem(i);
        showSpo2ReportDialog(bp);
    }

    private void showSpo2ReportDialog(Spo2 bp) {
        new ReportStatusSender(getActivity(), bp.getId(), new ReportStatusSender.ReportCallback() {
            @Override
            public void onSuccess() {
                reloadData();
            }

            @Override
            public void onFailed() {
            }
        }).execute(ROOT_URL+"/api/getSPO2Data");
        View v = View.inflate(getActivity(),R.layout.activity_show_spo2_reports,null);
        TextView report_date=(TextView)v.findViewById(R.id.reportData_date);
        TextView data_bps=(TextView)v.findViewById(R.id.reportData_bps);
        TextView data_bpd=(TextView)v.findViewById(R.id.reportData_bpd);
        TextView data_report=(TextView)v.findViewById(R.id.reportData_report);
        TextView data_prescription=(TextView)v.findViewById(R.id.reportData_prescription);

        TextView tvNote = (TextView) v.findViewById(R.id.reportData_note);
        TextView tvDevice = (TextView) v.findViewById(R.id.reportData_device);
        TextView tvDoc = (TextView) v.findViewById(R.id.reportData_doc_name);
        TextView tvUpdated = (TextView) v.findViewById(R.id.reportData_updated);

        report_date.setText(bp.getDateCreated());
        data_bps.setText(bp.getBloodOxygen());
        data_bpd.setText(bp.getPulse());
        data_report.setText(bp.getReport());
        data_prescription.setText(bp.getPrescription());

        tvNote.setText(bp.getNote());
        tvDevice.setText(bp.getDevice());
        tvDoc.setText(bp.getDocName());
        tvUpdated.setText(bp.getDateUpdated());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setIcon(R.drawable.ic_info);
        builder.setTitle("SPO2 Report Detail");
        builder.setPositiveButton("DISMISS",null);
        builder.show();
    }

}
