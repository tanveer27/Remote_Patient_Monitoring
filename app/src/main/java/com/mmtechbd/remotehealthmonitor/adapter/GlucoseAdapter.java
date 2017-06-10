package com.mmtechbd.remotehealthmonitor.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mmtechbd.remotehealthmonitor.R;
import com.mmtechbd.remotehealthmonitor.model.Glucose;

import org.json.JSONObject;

import java.util.ArrayList;

import static android.support.v4.content.ContextCompat.getColor;

/**
 * Created by Roaim on 16-Nov-16.
 */

public class GlucoseAdapter extends BaseAdapter {
    private ArrayList<Glucose> mList;
    private Context mContext;

    public GlucoseAdapter(Context context) {
        this.mList = new ArrayList<>();
        this.mContext = context;
    }

    public void removeAllGlucose() {
        mList.removeAll(mList);
        notifyDataSetChanged();
    }

    public void addGlucose(JSONObject object) {
        mList.add(new Glucose(object));
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Glucose getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Glucose data = getItem(i);
        if (view == null) {
            view = View.inflate(mContext, R.layout.item_list_report,null);
            TextView tv = (TextView) view.findViewById(R.id.textViewDate);
            tv.setText(data.getDateCreated());
            if (data.isConsult()) view.setBackgroundColor(getColor(mContext,R.color.bg_consult));
        }
        ImageView iv = (ImageView) view.findViewById(R.id.imageViewSeen);
        if (data.getStatus().trim().equals("seen")){
            iv.setImageResource(R.drawable.ic_seen);
        } else iv.setImageResource(R.drawable.ic_unseen);
        return view;
    }
}
