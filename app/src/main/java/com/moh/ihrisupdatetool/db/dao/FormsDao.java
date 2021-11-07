package com.moh.ihrisupdatetool.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.moh.ihrisupdatetool.db.entities.FormEntity;
import com.moh.ihrisupdatetool.db.entities.JobEntity;

import java.util.List;

@Dao
public interface FormsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<FormEntity> forms);

    @Query("SELECT * FROM forms")
    LiveData<List<FormEntity>> getAllForms();

    @Query("DELETE FROM forms")
    void deleteAll();
}
