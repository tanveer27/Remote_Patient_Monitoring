package com.mmtechbd.remotehealthmonitor.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Roaim on 16-Nov-16.
 */

public class BP {
    String id, systolic, diastolic;
    private String report;
    private String status;
    private String prescription;
    private String dateCreated;
    private boolean isConsult;

    private String dateUpdated;
    private String note;
    private String device;
    private String docName;

    public BP(JSONObject object) {
        try {
//            JSONObject reportObj = object.getJSONObject("0");
            setSystolic(object.getString("systolic"));
            setDiastolic(object.getString("diastolic"));
            setReport(object.getString("bp_report"));
            setConsult(!getReport().equals("ok"));
            setStatus(object.getString("bp_report_status"));
            setPrescription(object.getString("prescription"));
            setDateCreated(object.getString("created_at"));
            setId(object.getString("bp_id"));

            setDateUpdated(object.getString("updated_at"));
            setNote(object.getString("note"));
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

    public String getSystolic() {
        return systolic;
    }

    public void setSystolic(String systolic) {
        this.systolic = systolic;
    }

    public String getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(String diastolic) {
        this.diastolic = diastolic;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
