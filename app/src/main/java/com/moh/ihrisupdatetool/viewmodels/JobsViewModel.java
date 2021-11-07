package com.moh.ihrisupdatetool.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.moh.ihrisupdatetool.db.entities.DistrictEntity;
import com.moh.ihrisupdatetool.db.entities.JobEntity;
import com.moh.ihrisupdatetool.repo.DistrictsRepository;
import com.moh.ihrisupdatetool.repo.JobsRepository;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class JobsViewModel extends AndroidViewModel {

    private JobsRepository jobsRepository;

    @Inject
    public JobsViewModel(@NonNull @NotNull Application application, JobsRepository jobsRepository) {
        super(application);
        this.jobsRepository = jobsRepository;
    }

    public MutableLiveData<List<JobEntity>> observerResponse(){
        return jobsRepository.observerResponse();
    }

    public void getJobs(){
        jobsRepository.fetchJobs();
    }

}
