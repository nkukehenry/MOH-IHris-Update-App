package com.moh.ihrisupdatetool.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.moh.ihrisupdatetool.db.entities.SessionInfoEntity;

import java.util.List;

@Dao
public interface SessionInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SessionInfoEntity sessionInfoEntity);

    @Query("SELECT * FROM session")
    LiveData<List<SessionInfoEntity>> getSessionInfo();

    @Query("DELETE FROM session")
    void deleteAll();
}
