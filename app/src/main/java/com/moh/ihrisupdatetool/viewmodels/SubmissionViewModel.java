package com.moh.ihrisupdatetool.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
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

    public LiveData<JsonObject> postData(JsonObject postData){
        return dataSubmissionRepository.postData(postData);
    }

    public void cacheData(JsonObject postData){
        dataSubmissionRepository.cacheFormData(postData,false);
    }

    public LiveData<JsonObject> syncData(){
        return dataSubmissionRepository.syncData();
    }

    public Boolean deleteData(){
        return dataSubmissionRepository.deleteAll();
    }

}
