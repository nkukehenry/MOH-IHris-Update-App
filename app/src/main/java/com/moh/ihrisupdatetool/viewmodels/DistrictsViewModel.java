package com.moh.ihrisupdatetool.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.moh.ihrisupdatetool.db.entities.DistrictEntity;
import com.moh.ihrisupdatetool.db.entities.FacilityEntity;
import com.moh.ihrisupdatetool.repo.DistrictsRepository;
import com.moh.ihrisupdatetool.repo.FacilitiesRepository;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DistrictsViewModel extends AndroidViewModel {

    private DistrictsRepository districtsRepository;

    @Inject
    public DistrictsViewModel(@NonNull @NotNull Application application, DistrictsRepository districtsRepository) {
        super(application);
        this.districtsRepository = districtsRepository;
    }

    public MutableLiveData<List<DistrictEntity>>  observerResponse(){
        return districtsRepository.observerResponse();
    }

    public void getDistricts(){
        districtsRepository.fetchDistricts();
    }

}
