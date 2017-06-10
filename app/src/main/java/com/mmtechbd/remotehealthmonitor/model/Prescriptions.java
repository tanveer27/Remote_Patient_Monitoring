package com.mmtechbd.remotehealthmonitor.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Roaim on 09-Dec-16.
 */

public class Prescriptions {
    private String activity;
    private String bp;
    private String spo2;
    private String weight;
    private String glucose;
    private boolean ok;

    public Prescriptions(String data) {
        try {
            JSONObject obj = new JSONObject(data);
            setActivity(obj.getString("activity"));
            setBp(obj.getString("bp"));
            setGlucose(obj.getString("glucose"));
            setWeight(obj.getString("weight"));
            setTemp(obj.getString("temp"));
            setSpo2(obj.getString("spo2"));
            setOk(true);
        } catch (JSONException e) {
            e.printStackTrace();
            setOk(false);
        }
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getBp() {
        return bp;
    }

    public void setBp(String bp) {
        this.bp = bp;
    }

    public String getSpo2() {
        return spo2;
    }

    public void setSpo2(String spo2) {
        this.spo2 = spo2;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getGlucose() {
        return glucose;
    }

    public void setGlucose(String glucose) {
        this.glucose = glucose;
    }

    private String temp;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
}
