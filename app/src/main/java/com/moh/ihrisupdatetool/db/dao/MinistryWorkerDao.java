package com.moh.ihrisupdatetool.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.moh.ihrisupdatetool.db.entities.CommunityWorkerEntity;
import com.moh.ihrisupdatetool.db.entities.MinistryWorkerEntity;

import java.util.List;

@Dao
public interface MinistryWorkerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<MinistryWorkerEntity> workers);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSingle(MinistryWorkerEntity worker);

    @Query("SELECT * FROM min_workers")
    LiveData<List<MinistryWorkerEntity>> getMinistryWorkers();

    @Query("SELECT * FROM min_workers WHERE surname LIKE '%' || :term || '%' OR firstname LIKE '%' || :term || '%'")
    LiveData<List<MinistryWorkerEntity>> searchWorker(String term);

    @Query("SELECT * FROM min_workers WHERE ( surname LIKE  :term || '%' OR firstname LIKE  :term || '%') AND district like '%' || :districtName || '%'")
    LiveData<List<MinistryWorkerEntity>> searchWorker(String term,String districtName);

    @Query("DELETE FROM min_workers")
    void deleteAll();
}
