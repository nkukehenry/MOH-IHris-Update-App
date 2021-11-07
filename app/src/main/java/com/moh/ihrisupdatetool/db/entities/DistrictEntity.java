package com.moh.ihrisupdatetool.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "districts")
public class DistrictEntity {

    @PrimaryKey
    @NonNull
    @SerializedName("district_id")
    private String districtId;
    @SerializedName("district")
    private String districtName;

    public DistrictEntity(@NonNull String districtId, String districtName) {
        this.districtId = districtId;
        this.districtName = districtName;
    }

    @NonNull
    public String getDistrictId() {
        return districtId;
    }

    public String getDistrictName() {
        return districtName;
    }

    @Override
    public String toString() {
        return "DistrictEntity{" +
                "districtId='" + districtId + '\'' +
                ", districtName='" + districtName + '\'' +
                '}';
    }
}
