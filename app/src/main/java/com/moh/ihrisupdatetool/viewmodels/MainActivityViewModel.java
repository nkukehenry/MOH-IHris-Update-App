package com.moh.ihrisupdatetool.viewmodels;

import android.app.Application;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.db.entities.CommunityWorkerEntity;
import com.moh.ihrisupdatetool.db.entities.FacilityEntity;
import com.moh.ihrisupdatetool.dto.WorkerMenuModel;
import com.moh.ihrisupdatetool.repo.CommunityWorkerRepository;
import com.moh.ihrisupdatetool.repo.FacilitiesRepository;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainActivityViewModel extends AndroidViewModel {

    private List<WorkerMenuModel> workMenu;

    @Inject
    public MainActivityViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

}

