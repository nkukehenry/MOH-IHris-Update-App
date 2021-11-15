package com.moh.ihrisupdatetool.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.JsonObject;
import com.moh.ihrisupdatetool.db.typeconverters.JsonObjectConverter;

import java.io.Serializable;

@Entity (tableName = "form_data")
public class DataEntryTemplate implements Serializable {

    @PrimaryKey
    @NonNull
    private String reference;
    private String ihris_pid;
    private String facility_id;
    private String job_id;
    private Integer status = 0;

    @TypeConverters(JsonObjectConverter.class)
    private JsonObject formdata;
    private Boolean isUploaded=false;

    public String getIhris_pid() {
        return ihris_pid;
    }

    public void setIhris_pid(String ihris_pid) {
        this.ihris_pid = ihris_pid;
    }

    public String getFacility_id() {
        return facility_id;
    }

    public void setFacility_id(String facility_id) {
        this.facility_id = facility_id;
    }

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public JsonObject getFormdata() {
        return formdata;
    }

    public void setFormdata(JsonObject formdata) {
        this.formdata = formdata;
    }

    public Boolean getUploaded() {
        return isUploaded;
    }

    public void setUploaded(Boolean uploaded) {
        isUploaded = uploaded;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
