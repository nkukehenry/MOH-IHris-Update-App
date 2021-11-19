package com.moh.ihrisupdatetool.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.moh.ihrisupdatetool.db.dao.CommunityWorkerDao;
import com.moh.ihrisupdatetool.db.dao.DataEntryDao;
import com.moh.ihrisupdatetool.db.dao.DistrictsDao;
import com.moh.ihrisupdatetool.db.dao.FacilitiesDao;
import com.moh.ihrisupdatetool.db.dao.FormFieldsDao;
import com.moh.ihrisupdatetool.db.dao.FormsDao;
import com.moh.ihrisupdatetool.db.dao.JobsDao;
import com.moh.ihrisupdatetool.db.dao.MinistryWorkerDao;
import com.moh.ihrisupdatetool.db.dao.SessionInfoDao;
import com.moh.ihrisupdatetool.db.entities.CommunityWorkerEntity;
import com.moh.ihrisupdatetool.db.entities.DataEntryTemplate;
import com.moh.ihrisupdatetool.db.entities.DistrictEntity;
import com.moh.ihrisupdatetool.db.entities.FacilityEntity;
import com.moh.ihrisupdatetool.db.entities.FormEntity;
import com.moh.ihrisupdatetool.db.entities.FormField;
import com.moh.ihrisupdatetool.db.entities.JobEntity;
import com.moh.ihrisupdatetool.db.entities.MinistryWorkerEntity;
import com.moh.ihrisupdatetool.db.entities.SessionInfoEntity;

@Database(entities = {
         FacilityEntity.class
        ,DistrictEntity.class
        ,JobEntity.class
        ,CommunityWorkerEntity.class
        ,MinistryWorkerEntity.class
        ,FormEntity.class
        ,FormField.class
        ,DataEntryTemplate.class
        , SessionInfoEntity.class
     }, version = 22,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FacilitiesDao facilitiesDao();
    public abstract DistrictsDao districtsDao();
    public abstract JobsDao jobsDao();
    public abstract MinistryWorkerDao ministryWorkerDao();
    public abstract CommunityWorkerDao communityWorkerDao();
    public abstract FormsDao formsDao();
    public abstract  FormFieldsDao formFieldsDao();
    public abstract DataEntryDao dataEntryDao();
    public abstract SessionInfoDao sessionInfoDao();
}
