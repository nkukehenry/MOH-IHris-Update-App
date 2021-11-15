package com.moh.ihrisupdatetool.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
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

    public MutableLiveData<List<CommunityWorkerEntity>> observeCommunityWorkers(){
       return this.communityWorkerRepository.observerResponse();
    }

    public MutableLiveData<List<MinistryWorkerEntity>> observeMinistryWorkers(){
        return this.ministryWorkerRepository.observerResponse();
    }

    public void getCommunityHealthWorkers(){
        communityWorkerRepository.fetchCommunityWorkers();
    }

    public void getMinistryHealthWorkers(){
        ministryWorkerRepository.fetchMinistryWorkers();
    }

    public void searchWorker(String term,String districtName,Boolean isCommunity) {

        if(isCommunity) {
            communityWorkerRepository.searchWorkers(term, districtName);
        }else{
            ministryWorkerRepository.searchWorkers(term, districtName);
        }
    }
}
