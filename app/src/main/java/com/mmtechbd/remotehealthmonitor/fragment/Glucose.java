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
import com.mmtechbd.remotehealthmonitor.adapter.GlucoseAdapter;
import com.mmtechbd.remotehealthmonitor.net.DataDownloader;
import com.mmtechbd.remotehealthmonitor.net.ReportStatusSender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class Glucose extends Fragment implements View.OnClickListener {


    private ListView datalistView;
    private GlucoseAdapter mAdapter;
    private String token;
    private static final String ROOT_URL = BuildConfig.ROOT_URL;
    private ProgressBar mProgress;

    public Glucose() {
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
        View view=inflater.inflate(R.layout.fragment_glucose, container, false);
        datalistView=(ListView)view.findViewById(R.id.report_List_glucose);
        mProgress = (ProgressBar) view.findViewById(R.id.progressBar);
        mAdapter = new GlucoseAdapter(getActivity());
        datalistView.setAdapter(mAdapter);
        datalistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                com.mmtechbd.remotehealthmonitor.model.Glucose glucose = mAdapter.getItem(position);
                showReportDialog(glucose);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        reloadData();
        return view;
    }

    private void showReportDialog(com.mmtechbd.remotehealthmonitor.model.Glucose glucose) {
        new ReportStatusSender(getActivity(), glucose.getDataId(), new ReportStatusSender.ReportCallback() {
            @Override
            public void onSuccess() {
                reloadData();
            }

            @Override
            public void onFailed() {
            }
        }).execute(ROOT_URL+"/api/getGlucoseData");
        View view = View.inflate(getActivity(),R.layout.activity_show_glucose_reports,null);
        TextView report_date=(TextView)view.findViewById(R.id.glucoseData_date);
        TextView glucose_before_food=(TextView)view.findViewById(R.id.glucoseData_bf);
//        TextView glucose_after_food=(TextView)view.findViewById(R.id.glucoseData_af);
        TextView glucose_report=(TextView)view.findViewById(R.id.glucose_report);
        TextView glucose_prescription=(TextView)view.findViewById(R.id.glucose_prescription);
        TextView tvDinnerSituation = (TextView) view.findViewById(R.id.tvDinnerSituation);

        TextView tvNote = (TextView) view.findViewById(R.id.reportData_note);
        TextView tvDevice = (TextView) view.findViewById(R.id.reportData_device);
        TextView tvDoc = (TextView) view.findViewById(R.id.reportData_doc_name);
        TextView tvUpdated = (TextView) view.findViewById(R.id.reportData_updated);

        report_date.setText(glucose.getDateCreated());
        glucose_before_food.setText(glucose.getGlucose());
        glucose_report.setText(glucose.getReport());
        glucose_prescription.setText(glucose.getPrescription());
        tvDinnerSituation.setText(glucose.getDinnerSituation());

        tvNote.setText(glucose.getNote());
        tvDevice.setText(glucose.getDevice());
        tvDoc.setText(glucose.getDocName());
        tvUpdated.setText(glucose.getDateUpdated());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setIcon(R.drawable.ic_info);
        builder.setTitle("Glucose Report Detail");
        builder.setPositiveButton("DISMISS",null);
        builder.show();
    }

    public void reloadData() {
        DataDownloader.download(getActivity(), ROOT_URL + "/api/getGlucoseReports", token, /*progress,*/ new DataDownloader.DataCallback() {
            @Override
            public void onStartReceiving() {
                if (mProgress!=null) mProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedResult(String result) {
                if (mProgress!=null) mProgress.setVisibility(View.GONE);
                if (mAdapter!=null) mAdapter.removeAllGlucose();
                try {
                    JSONArray array = new JSONArray(result);
                    for (int i = 0; i<array.length();i++) {
                        JSONObject object = array.getJSONObject(i);
                        if (mAdapter!=null) mAdapter.addGlucose(object);
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
    }

    @Override
    public void onClick(View view) {
        reloadData();
    }
}
