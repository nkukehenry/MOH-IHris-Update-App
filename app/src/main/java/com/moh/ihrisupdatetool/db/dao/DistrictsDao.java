package com.moh.ihrisupdatetool.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.moh.ihrisupdatetool.db.entities.DistrictEntity;
import java.util.List;

@Dao
public interface DistrictsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<DistrictEntity> districts);

    @Query("SELECT * FROM districts")
    LiveData<List<DistrictEntity>> getAllDistricts();

    @Query("DELETE FROM districts")
    void deleteAll();
}
