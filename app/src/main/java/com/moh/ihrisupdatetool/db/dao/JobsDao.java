package com.moh.ihrisupdatetool.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.moh.ihrisupdatetool.db.entities.JobEntity;

import java.util.List;

@Dao
public interface JobsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<JobEntity> jobs);

    @Query("SELECT * FROM jobs")
    LiveData<List<JobEntity>> getAllJobs();

    @Query("DELETE FROM jobs")
    void deleteAll();
}
