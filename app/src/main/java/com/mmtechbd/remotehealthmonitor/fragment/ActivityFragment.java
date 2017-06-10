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
import com.mmtechbd.remotehealthmonitor.net.DataDownloader;
import com.mmtechbd.remotehealthmonitor.net.ReportStatusSender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.mmtechbd.remotehealthmonitor.BuildConfig.ROOT_URL;
import static com.mmtechbd.remotehealthmonitor.net.DataDownloader.download;

public class ActivityFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, DataDownloader.DataCallback {
    private static final String ARG_PARAM = "param";
    private String mParam;
    private ListView lv;
    private ProgressBar mProgress;
    private ActivityAdapter mAdapter;

    public ActivityFragment() {
        // Required empty public constructor
    }

    public static ActivityFragment newInstance(String param) {
        ActivityFragment fragment = new ActivityFragment();
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
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        mProgress = (ProgressBar) view.findViewById(R.id.progressBar);
        lv = (ListView) view.findViewById(R.id.lvActivity);
        mAdapter = new ActivityAdapter(getActivity());
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        reloadData();
        return view;
    }

    private void reloadData() {
        download(getActivity(), ROOT_URL + "/api/getActivityReports", mParam, /*progress,*/ this);
    }

    @Override
    public void onClick(View view) {
        reloadData();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Activity activity = mAdapter.getItem(i);
        showActivityReportDialog(activity);
    }

    private void showActivityReportDialog(Activity activity) {
        new ReportStatusSender(getActivity(), activity.getId(), new ReportStatusSender.ReportCallback() {
            @Override
            public void onSuccess() {
                reloadData();
            }

            @Override
            public void onFailed() {
            }
        }).execute(ROOT_URL+"/api/getActivityData");
        View v = View.inflate(getActivity(),R.layout.view_show_activity_report,null);
        TextView report_date=(TextView)v.findViewById(R.id.reportData_date);
        TextView tvSteps =(TextView)v.findViewById(R.id.tvSteps);
        TextView tvDistance =(TextView)v.findViewById(R.id.tvDistance);
        TextView tvCalories =(TextView)v.findViewById(R.id.tvCalories);
        TextView data_report=(TextView)v.findViewById(R.id.reportData_report);
        TextView data_prescription=(TextView)v.findViewById(R.id.reportData_prescription);

        TextView tvDevice = (TextView) v.findViewById(R.id.reportData_device);
        TextView tvDoc = (TextView) v.findViewById(R.id.reportData_doc_name);
        TextView tvUpdated = (TextView) v.findViewById(R.id.reportData_updated);

        report_date.setText(activity.getDateCreated());
        tvSteps.setText(activity.getSteps());
        tvDistance.setText(activity.getDistance());
        tvCalories.setText(activity.getCalories());
        data_report.setText(activity.getReport());
        data_prescription.setText(activity.getPrescription());

        tvDevice.setText(activity.getDevice());
        tvDoc.setText(activity.getDocName());
        tvUpdated.setText(activity.getDateUpdated());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setIcon(R.drawable.ic_info);
        builder.setTitle("Activity Report Detail");
        builder.setPositiveButton("DISMISS",null);
        builder.show();
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
}
