package com.moh.ihrisupdatetool.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.moh.ihrisupdatetool.db.entities.CommunityWorkerEntity;
import com.moh.ihrisupdatetool.db.entities.DistrictEntity;

import java.util.List;

@Dao
public interface CommunityWorkerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<CommunityWorkerEntity> workers);

    @Query("SELECT * FROM cw_workers")
    LiveData<List<CommunityWorkerEntity>> getCommunityWorkers();

    @Query("DELETE FROM cw_workers")
    void deleteAll();

}
