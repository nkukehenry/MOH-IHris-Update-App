package com.moh.ihrisupdatetool.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.moh.ihrisupdatetool.db.entities.DataEntryTemplate;
import com.moh.ihrisupdatetool.db.entities.FormEntity;

import java.util.List;

@Dao
public interface DataEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DataEntryTemplate form_data);

    @Query("SELECT * FROM form_data")
    LiveData<List<DataEntryTemplate>> getAllFormData();

    @Query("SELECT * FROM form_data WHERE status = 0")
    LiveData<List<DataEntryTemplate>> getLocalRecords();

    @Query("SELECT * FROM form_data WHERE status = 0")
    List<DataEntryTemplate> getLocalRecordsSync();

    @Query("SELECT * FROM form_data")
    LiveData<DataEntryTemplate> getFormData();

    @Query("DELETE FROM form_data")
    void deleteAll();
}
