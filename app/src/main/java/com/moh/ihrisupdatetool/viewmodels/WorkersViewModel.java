package com.moh.ihrisupdatetool.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.moh.ihrisupdatetool.db.entities.CommunityWorkerEntity;
import com.moh.ihrisupdatetool.repo.CommunityWorkerRepository;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkersViewModel extends AndroidViewModel {

    private CommunityWorkerRepository communityWorkerRepository;

    @Inject
    public WorkersViewModel(@NonNull @NotNull Application application, CommunityWorkerRepository communityWorkerRepository) {
        super(application);
        this.communityWorkerRepository = communityWorkerRepository;
    }

    public MutableLiveData<List<CommunityWorkerEntity>> observerResponse(){
        return communityWorkerRepository.observerResponse();
    }

    public void getHealthWorkers(){
        communityWorkerRepository.fetchCommunityWorkers();
    }

}
