package com.moh.ihrisupdatetool.dto;

import com.moh.ihrisupdatetool.db.entities.FacilityEntity;

import java.util.List;

public class FacilitesResponse extends AppResponse{

    private List<FacilityEntity> facilities;

    public List<FacilityEntity> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<FacilityEntity> facilities) {
        this.facilities = facilities;
    }
}
