package com.moh.ihrisupdatetool.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.moh.ihrisupdatetool.db.entities.DataEntryTemplate;
import com.moh.ihrisupdatetool.repo.HistoryRepository;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HistoryViewModel extends AndroidViewModel {

    private HistoryRepository historyRepository;

    @Inject
    public HistoryViewModel(@NonNull @NotNull Application application,HistoryRepository historyRepository) {
        super(application);
        this.historyRepository = historyRepository;
    }

    public MutableLiveData<List<DataEntryTemplate>> observeHistoryData(){
        return this.historyRepository.observeHistoryData();
    }

    public void getAllData(){
        this.historyRepository.getAllHistory();
    }

}
