package com.mmtechbd.remotehealthmonitor.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Roaim on 16-Nov-16.
 */

public class Temp {
    String id, temp;
    private String report;
    private String status;
    private String prescription;
    private String dateCreated;
    private boolean isConsult;

    private String dateUpdated;
    private String device;
    private String docName;

    public Temp(JSONObject object) {
        try {
//            JSONObject reportObj = object.getJSONObject("0");
            setTemp(object.getString("body_temperature"));
            setReport(object.getString("temp_report"));
            setConsult(!getReport().equals("ok"));
            setStatus(object.getString("temp_report_status"));
            setPrescription(object.getString("prescription"));
            setDateCreated(object.getString("created_at"));
            setId(object.getString("temperature_id"));

            setDateUpdated(object.getString("updated_at"));
            setDevice(object.getString("device"));
            setDocName(object.getString("doctor_first_name") + " " + object.getString("doctor_last_name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isConsult() {
        return isConsult;
    }

    public void setConsult(boolean consult) {
        isConsult = consult;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }
}
