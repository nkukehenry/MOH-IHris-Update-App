package com.moh.ihrisupdatetool.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.moh.ihrisupdatetool.db.entities.CommunityWorkerEntity;
import com.moh.ihrisupdatetool.db.entities.MinistryWorkerEntity;
import com.moh.ihrisupdatetool.repo.CommunityWorkerRepository;
import com.moh.ihrisupdatetool.repo.MinistryWorkerRepository;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkersViewModel extends AndroidViewModel {

    private CommunityWorkerRepository communityWorkerRepository;
    private MinistryWorkerRepository ministryWorkerRepository;

    @Inject
    public WorkersViewModel(@NonNull @NotNull Application application
            , CommunityWorkerRepository communityWorkerRepository
            ,MinistryWorkerRepository ministryWorkerRepository) {
        super(application);
        this.communityWorkerRepository = communityWorkerRepository;
        this.ministryWorkerRepository = ministryWorkerRepository;
    }


    public LiveData<List<CommunityWorkerEntity>> syncCommunityHealthWorkers(String districtName){
        return communityWorkerRepository.fetchCommunityWorkers(districtName,true);
    }

    public LiveData<List<MinistryWorkerEntity>> syncMinistryHealthWorkers(String districtName){
        return ministryWorkerRepository.fetchMinistryWorkers(districtName,true);
    }

    public LiveData<List<CommunityWorkerEntity>> getCommunityHealthWorkers(String districtName){
       return communityWorkerRepository.fetchCommunityWorkers(districtName);
    }

    public LiveData<List<MinistryWorkerEntity>> getMinistryHealthWorkers(String districtName){
        return ministryWorkerRepository.fetchMinistryWorkers(districtName);
    }

    public void deleteData(){
        ministryWorkerRepository.deleteData();
        communityWorkerRepository.deleteData();
    }

    public LiveData<List<MinistryWorkerEntity>> searchMinistryWorker(String term,String districtName) {
            return ministryWorkerRepository.searchWorkers(term, districtName);
    }

    public LiveData<List<CommunityWorkerEntity>>  searchCommunityWorker(String term,String districtName) {
            return communityWorkerRepository.searchWorkers(term, districtName);
    }
}
