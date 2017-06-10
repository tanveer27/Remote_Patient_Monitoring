package com.mmtechbd.remotehealthmonitor.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Roaim on 16-Nov-16.
 */

public class Doctor {
    private String id, name, degree, speciality, hospital, consultingHour;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Doctor(JSONObject object) {
        try {
            setName(String.format("%s %s",object.getString("doctor_first_name"),object.getString("doctor_last_name")));
            setDegree(object.getString("degree"));
            setSpeciality(object.getString("speciality"));
            setHospital(object.getString("hospital"));
            setConsultingHour(object.getString("consulting_hour"));
            setId(object.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getConsultingHour() {
        return consultingHour;
    }

    public void setConsultingHour(String consultingHour) {
        this.consultingHour = consultingHour;
    }
}
