package com.moh.ihrisupdatetool.repo;

import androidx.lifecycle.MutableLiveData;

import com.moh.ihrisupdatetool.db.dao.DataEntryDao;
import com.moh.ihrisupdatetool.db.entities.DataEntryTemplate;

import java.util.List;

import javax.inject.Inject;

public class HistoryRepository {

    private MutableLiveData<List<DataEntryTemplate>> historyResponse;
    private DataEntryDao dataEntryDao;

    @Inject
    public HistoryRepository(DataEntryDao dataEntryDao) {
        this.historyResponse = new MutableLiveData<>();
        this.dataEntryDao  = dataEntryDao;
    }

    public MutableLiveData<List<DataEntryTemplate>> observeHistoryData(){
        return historyResponse;
    }

    public void getAllHistory(){
        dataEntryDao.getAllFormData().observeForever(o->{
                this.historyResponse.postValue(o);
        });
    }

}
