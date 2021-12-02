package com.moh.ihrisupdatetool.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "cw_workers")
public class CommunityWorkerEntity {

    @PrimaryKey
    @NonNull
    @SerializedName("ihris_pid")
    private String personId;
    private String district_id;
    private String district;
    private String facility_type;
    private String surname;
    private String firstname;
    private String job_id;
    private String job;
    private String dhis2_id;
    private String othername;
    private String mobile;
    private String facility;
    private String national_id;
    private String facility_id;
    private String gender;
    private String birth_date;

    @Ignore
    private String fullName;

    @NonNull
    public String getPersonId() {
        return personId;
    }

    public void setPersonId(@NonNull String personId) {
        this.personId = personId;
    }

    public String getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(String district_id) {
        this.district_id = district_id;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getFacility_type() {
        return facility_type;
    }

    public void setFacility_type(String facility_type) {
        this.facility_type = facility_type;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getDhis2_id() {
        return dhis2_id;
    }

    public void setDhis2_id(String dhis2_id) {
        this.dhis2_id = dhis2_id;
    }

    public String getOthername() {
        return othername;
    }

    public void setOthername(String othername) {
        this.othername = othername;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getNational_id() {
        return national_id;
    }

    public void setNational_id(String national_id) {
        this.national_id = national_id;
    }

    public String getFacility_id() {
        return facility_id;
    }

    public void setFacility_id(String facility_id) {
        this.facility_id = facility_id;
    }

    public String getFullName() {
        return surname+" "+((othername!=null)?othername:"")+" "+firstname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "CommunityWorkerEntity{" +
                "personId='" + personId + '\'' +
                ", district_id='" + district_id + '\'' +
                ", district='" + district + '\'' +
                ", facility_type='" + facility_type + '\'' +
                ", surname='" + surname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", job_id='" + job_id + '\'' +
                ", job='" + job + '\'' +
                ", dhis2_id='" + dhis2_id + '\'' +
                ", othername='" + othername + '\'' +
                ", mobile='" + mobile + '\'' +
                ", facility='" + facility + '\'' +
                ", national_id='" + national_id + '\'' +
                ", facility_id='" + facility_id + '\'' +
                '}';
    }
}
