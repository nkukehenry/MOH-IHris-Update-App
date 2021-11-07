package com.moh.ihrisupdatetool.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.moh.ihrisupdatetool.db.entities.FacilityEntity;

import java.util.List;

@Dao
public interface FacilitiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<FacilityEntity> facilities);

    @Query("SELECT * FROM facilities")
    LiveData<List<FacilityEntity>> getAllFacilities();

    @Query("DELETE FROM facilities")
    void deleteAll();

}
