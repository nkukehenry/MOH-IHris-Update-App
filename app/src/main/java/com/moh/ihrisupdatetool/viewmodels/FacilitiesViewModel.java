package com.moh.ihrisupdatetool.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.moh.ihrisupdatetool.db.entities.FacilityEntity;
import com.moh.ihrisupdatetool.repo.FacilitiesRepository;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FacilitiesViewModel extends AndroidViewModel {

    private FacilitiesRepository facilitiesRepository;
    private MutableLiveData<List<FacilityEntity>> facilities;

    @Inject
    public FacilitiesViewModel(@NonNull @NotNull Application application, FacilitiesRepository facilitiesRepository) {
        super(application);
        this.facilitiesRepository = facilitiesRepository;
    }

    public MutableLiveData<List<FacilityEntity>>  observerFacilitiesResponse(){
        return facilitiesRepository.observerResponse();
    }

    public void getFacilities(){
        facilitiesRepository.fetchFacilities();
    }
}
