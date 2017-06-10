package com.mmtechbd.remotehealthmonitor.fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mmtechbd.remotehealthmonitor.BuildConfig;
import com.mmtechbd.remotehealthmonitor.R;
import com.mmtechbd.remotehealthmonitor.User_Panel;
import com.mmtechbd.remotehealthmonitor.adapter.TempAdapter;
import com.mmtechbd.remotehealthmonitor.model.Temp;
import com.mmtechbd.remotehealthmonitor.net.DataDownloader;
import com.mmtechbd.remotehealthmonitor.net.ReportStatusSender;
import com.mmtechbd.remotehealthmonitor.net.TempUploader;
import com.mmtechbd.remotehealthmonitor.utils.TempInputWatcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.mmtechbd.remotehealthmonitor.utils.Universal.getToken;


/**
 * A simple {@link Fragment} subclass.
 */
public class Temperature extends Fragment implements View.OnClickListener{
    private static final String ROOT_URL = BuildConfig.ROOT_URL;
    private ListView datalistView;
    private TempAdapter mAdapter;
    private EditText body_temperature;
    private FloatingActionButton fab;
    private Button submit;
    private String token;
    private ProgressBar mProgress;

    public Temperature() {
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
        View view=inflater.inflate(R.layout.fragment_temperature, container, false);
        mProgress = (ProgressBar) view.findViewById(R.id.progressBar);
        datalistView=(ListView)view.findViewById(R.id.report_List_temp);
        mAdapter = new TempAdapter(getActivity());
        datalistView.setAdapter(mAdapter);
        submit=(Button)view.findViewById(R.id.Temperature_Submit);
        submit.setOnClickListener(this);
        body_temperature=(EditText)view.findViewById(R.id.body_temperature);
        body_temperature.addTextChangedListener(new TempInputWatcher(submit));
        datalistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Temp temp = mAdapter.getItem(position);
                showTempReportDialog(temp);
            }
        });
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        reloadData();
        return view;
    }

    private void showTempReportDialog(Temp temp) {
        new ReportStatusSender(getActivity(), temp.getId(), new ReportStatusSender.ReportCallback() {
            @Override
            public void onSuccess() {
                reloadData();
            }

            @Override
            public void onFailed() {
            }
        }).execute(ROOT_URL+"/api/getTemperatureData");
        View v = View.inflate(getActivity(),R.layout.activity_show_temp_reports,null);
        TextView report_date=(TextView)v.findViewById(R.id.tempData_date);
        TextView temperature=(TextView)v.findViewById(R.id.tempData);
        TextView temp_report=(TextView)v.findViewById(R.id.temp_report);
        TextView temp_prescription=(TextView)v.findViewById(R.id.temp_prescription);

        TextView tvDevice = (TextView) v.findViewById(R.id.reportData_device);
        TextView tvDoc = (TextView) v.findViewById(R.id.reportData_doc_name);
        TextView tvUpdated = (TextView) v.findViewById(R.id.reportData_updated);

        report_date.setText(temp.getDateCreated());
        temperature.setText(temp.getTemp());
        temp_report.setText(temp.getReport());
        temp_prescription.setText(temp.getPrescription());

        tvDevice.setText(temp.getDevice());
        tvDoc.setText(temp.getDocName());
        tvUpdated.setText(temp.getDateUpdated());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setIcon(R.drawable.ic_info);
        builder.setTitle("Temp. Report Detail");
        builder.setPositiveButton("DISMISS",null);
        builder.show();
    }

    public void saveTempData(){
        //Implement data saving method
        String userDataString=body_temperature.getText().toString();
        if (userDataString.isEmpty()) return;
        else {
            new TempUploader(getActivity(), userDataString, new TempUploader.TempUploadCallback() {
                @Override
                public void onSuccess(String s) {
                    Toast.makeText(getActivity(),s.trim(),Toast.LENGTH_LONG).show();
                    body_temperature.setText("");
                }

                @Override
                public void onFailed() {
                    Toast.makeText(getActivity(),"Could not upload temp. data",Toast.LENGTH_SHORT).show();
                }
            }).execute(ROOT_URL+"/api/enterTemperatureData");
        }

    }

    @Override
    public void onClick(View v) {
        if(v==submit){
            saveTempData();
        } else if (v==fab) {
            reloadData();
        }
    }

    public void reloadData() {
        DataDownloader.download(getActivity(), ROOT_URL + "/api/getTemperatureReports", token, /*progress,*/ new DataDownloader.DataCallback() {
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
}
