package com.mmtechbd.remotehealthmonitor.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mmtechbd.remotehealthmonitor.R;
import com.mmtechbd.remotehealthmonitor.model.Doctor;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Roaim on 16-Nov-16.
 */

public class DoctorAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Doctor> mList;

    public DoctorAdapter(Context context) {
        this.mContext = context;
        this.mList = new ArrayList<>();
    }

    public void removeAllData() {
        mList.removeAll(mList);
        notifyDataSetChanged();
    }

    public void addData(JSONObject object) {
        Doctor doc = new Doctor(object);
        if (!doc.getName().trim().isEmpty()) mList.add(doc);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Doctor getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Doctor doctor = getItem(i);
        if (view == null) {
            view = View.inflate(mContext, R.layout.item_list_report,null);
            TextView tv = (TextView) view.findViewById(R.id.textViewDate);
            tv.setText(doctor.getName());
            ImageView iv = (ImageView) view.findViewById(R.id.imageView4);
            iv.setImageResource(R.drawable.ic_action_user);
            view.findViewById(R.id.imageViewSeen).setVisibility(View.GONE);
        }
        return view;
    }
}
