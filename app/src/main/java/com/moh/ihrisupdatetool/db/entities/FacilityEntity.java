package com.moh.ihrisupdatetool.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity( tableName = "facilities")
public class FacilityEntity {

    @PrimaryKey
    @NonNull
    @SerializedName("facility_id")
    private String facilityId;
    @SerializedName("facility")
    private String facilityName;

    public FacilityEntity(@NonNull String facilityId, String facilityName) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
    }

    @NonNull
    public String getFacilityId() {
        return facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    @Override
    public String toString() {
        return "FacilityEntity{" +
                "facilityId='" + facilityId + '\'' +
                ", facilityName='" + facilityName + '\'' +
                '}';
    }
}
