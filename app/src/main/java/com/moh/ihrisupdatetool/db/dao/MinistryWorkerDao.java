package com.moh.ihrisupdatetool.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.moh.ihrisupdatetool.db.entities.MinistryWorkerEntity;

import java.util.List;

@Dao
public interface MinistryWorkerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<MinistryWorkerEntity> workers);

    @Query("SELECT * FROM min_workers")
    LiveData<List<MinistryWorkerEntity>> getMinistryWorkers();

    @Query("DELETE FROM min_workers")
    void deleteAll();
}
