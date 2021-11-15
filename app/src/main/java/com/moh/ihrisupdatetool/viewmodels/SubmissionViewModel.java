package com.moh.ihrisupdatetool.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.moh.ihrisupdatetool.repo.DataSubmissionRepository;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SubmissionViewModel extends AndroidViewModel {

    private DataSubmissionRepository dataSubmissionRepository;

    @Inject
    public SubmissionViewModel(@NonNull @NotNull Application application, DataSubmissionRepository dataSubmissionRepository) {
        super(application);
        this.dataSubmissionRepository = dataSubmissionRepository;
    }

    public MutableLiveData<JsonObject> observeResonse(){
        return dataSubmissionRepository.observerResponse();
    }

    public void postData(JsonObject postData){
         dataSubmissionRepository.postData(postData);
    }

    public void cacheData(JsonObject postData){
        dataSubmissionRepository.cacheFormData(postData,false);
    }

    public Boolean syncData(){
        return dataSubmissionRepository.syncData();
    }

}
