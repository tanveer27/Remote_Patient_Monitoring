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
import com.mmtechbd.remotehealthmonitor.LoginPanel;
import com.mmtechbd.remotehealthmonitor.R;
import com.mmtechbd.remotehealthmonitor.User_Panel;
import com.mmtechbd.remotehealthmonitor.adapter.WeightAdapter;
import com.mmtechbd.remotehealthmonitor.net.DataDownloader;
import com.mmtechbd.remotehealthmonitor.net.ReportStatusSender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Weight extends Fragment implements View.OnClickListener{
    private static final String ROOT_URL = BuildConfig.ROOT_URL;
    private ListView datalistView;
    private WeightAdapter mAdapter;
    private String token;
    private ProgressBar mProgress;

    public Weight() {
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
        View view=inflater.inflate(R.layout.fragment_weight, container, false);
        mProgress = (ProgressBar) view.findViewById(R.id.progressBar);
        datalistView=(ListView)view.findViewById(R.id.report_List_weight);
        mAdapter = new WeightAdapter(getActivity());
        datalistView.setAdapter(mAdapter);
        datalistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                com.mmtechbd.remotehealthmonitor.model.Weight weight = mAdapter.getItem(position);
                showReportDialog(weight);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        reloadData();
        return view;
    }

    private void showReportDialog(com.mmtechbd.remotehealthmonitor.model.Weight weight) {
            new ReportStatusSender(getActivity(), weight.getId(), new ReportStatusSender.ReportCallback() {
                @Override
                public void onSuccess() {
                    reloadData();
                }

                @Override
                public void onFailed() {
                }
            }).execute(ROOT_URL + "/api/getWeightData");
        View v = View.inflate(getActivity(),R.layout.activity_show_weight_reports,null);
        TextView report_date=(TextView)v.findViewById(R.id.WeightData_date);
        TextView weightVal =(TextView)v.findViewById(R.id.WeightData);
        TextView weight_report=(TextView)v.findViewById(R.id.weight_report);
        TextView weight_prescription=(TextView)v.findViewById(R.id.weight_prescription);
        TextView tvBmi = (TextView) v.findViewById(R.id.tvBmi);

        TextView tvNote = (TextView) v.findViewById(R.id.reportData_note);
        TextView tvDevice = (TextView) v.findViewById(R.id.reportData_device);
        TextView tvDoc = (TextView) v.findViewById(R.id.reportData_doc_name);
        TextView tvUpdated = (TextView) v.findViewById(R.id.reportData_updated);

        tvBmi.setText(weight.getBmi());
        report_date.setText(weight.getDateCreated());
        weightVal.setText(weight.getWeight());
        weight_report.setText(weight.getReport());
        weight_prescription.setText(weight.getPrescription());

        tvNote.setText(weight.getNote());
        tvDevice.setText(weight.getDevice());
        tvDoc.setText(weight.getDocName());
        tvUpdated.setText(weight.getDateUpdated());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setIcon(R.drawable.ic_info);
        builder.setTitle("Weight Report Detail");
        builder.setPositiveButton("DISMISS",null);
        builder.show();
    }

    public void reloadData() {
        DataDownloader.download(getActivity(), ROOT_URL + "/api/getWeightReports", token, /*progress,*/ new DataDownloader.DataCallback() {
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
        });
    }

    @Override
    public void onClick(View view) {
        reloadData();
    }
}
