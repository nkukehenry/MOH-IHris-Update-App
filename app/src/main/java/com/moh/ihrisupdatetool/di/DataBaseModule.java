package com.moh.ihrisupdatetool.di;

import android.app.Application;

import androidx.room.Room;

import com.moh.ihrisupdatetool.db.AppDatabase;
import com.moh.ihrisupdatetool.db.dao.CommunityWorkerDao;
import com.moh.ihrisupdatetool.db.dao.DataEntryDao;
import com.moh.ihrisupdatetool.db.dao.DistrictsDao;
import com.moh.ihrisupdatetool.db.dao.FacilitiesDao;
import com.moh.ihrisupdatetool.db.dao.FormFieldsDao;
import com.moh.ihrisupdatetool.db.dao.FormsDao;
import com.moh.ihrisupdatetool.db.dao.JobsDao;
import com.moh.ihrisupdatetool.db.dao.MinistryWorkerDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DataBaseModule {

    @Singleton
    @Provides
    public AppDatabase getAppDatabase(Application application){
        return Room.databaseBuilder(application.getApplicationContext(),AppDatabase.class,"qt_app_database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }

    @Provides
    @Singleton
    public static FacilitiesDao facilitiesDao(AppDatabase appDatabase){
        return appDatabase.facilitiesDao();
    }

    @Provides
    @Singleton
    public static DistrictsDao districtsDao(AppDatabase appDatabase){
        return appDatabase.districtsDao();
    }

    @Provides
    @Singleton
    public static JobsDao jobsDao(AppDatabase appDatabase){
        return appDatabase.jobsDao();
    }

    @Provides
    @Singleton
    public static CommunityWorkerDao communityWorkerDao(AppDatabase appDatabase){
        return appDatabase.communityWorkerDao();
    }

    @Provides
    @Singleton
    public static MinistryWorkerDao ministryWorkerDao(AppDatabase appDatabase){
        return appDatabase.ministryWorkerDao();
    }

    @Provides
    @Singleton
    public static FormsDao formsDao(AppDatabase appDatabase){
        return appDatabase.formsDao();
    }

    @Provides
    @Singleton
    public static FormFieldsDao formFieldsDao(AppDatabase appDatabase){
        return appDatabase.formFieldsDao();
    }


    @Provides
    @Singleton
    public static DataEntryDao dataEntryDao(AppDatabase appDatabase){
        return appDatabase.dataEntryDao();
    }


}
