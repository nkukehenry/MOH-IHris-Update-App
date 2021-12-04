
package com.moh.ihrisupdatetool.repo;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.reflect.TypeToken;
import com.moh.ihrisupdatetool.db.dao.CommunityWorkerDao;
import com.moh.ihrisupdatetool.db.dao.MinistryWorkerDao;
import com.moh.ihrisupdatetool.db.entities.CommunityWorkerEntity;
import com.moh.ihrisupdatetool.db.entities.MinistryWorkerEntity;
import com.moh.ihrisupdatetool.repo.remote.IAppRemoteCallRepository;
import com.moh.ihrisupdatetool.utils.AppConstants;
import com.moh.ihrisupdatetool.utils.AppUtils;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

import static java.lang.Thread.sleep;

public class MinistryWorkerRepository {

    private IAppRemoteCallRepository genericAppRepository;
    private MutableLiveData<List<MinistryWorkerEntity>> ministryWorkerResponse;
    private MinistryWorkerDao ministryWorkerDao;

    @Inject
    public MinistryWorkerRepository(IAppRemoteCallRepository genericAppRepository, MinistryWorkerDao ministryWorkerDao) {

        this.genericAppRepository = genericAppRepository;
        this.ministryWorkerResponse = new MutableLiveData<>();
        this.ministryWorkerDao = ministryWorkerDao;
    }

    public LiveData<List<MinistryWorkerEntity>> fetchMinistryWorkers(Boolean deleteCache){
        try {
            fetchFromApi();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return ministryWorkerResponse;
    }

    public LiveData<List<MinistryWorkerEntity>> fetchMinistryWorkers(){

        ministryWorkerDao.getMinistryWorkers().observeForever(o->{

            if(o.isEmpty()){ fetchFromApi(); } else {
                this.ministryWorkerResponse.setValue(o);
            }
        });
        return ministryWorkerResponse;
    }

    public LiveData<List<MinistryWorkerEntity>> searchWorkers(String term,String districtName){

        MutableLiveData<List<MinistryWorkerEntity>> workers = new MutableLiveData();

        ministryWorkerDao.searchWorker(term,districtName).observeForever(o->{
            if(o.isEmpty()){
                //fetchFromApi();
                workers.setValue(null);
            } else {
                workers.setValue(o);
            }
        });

        return workers;
    }


    private void  fetchFromApi(){
        genericAppRepository.get(AppConstants.GET_MINISTRY_WORKER_DATA_URL()).observeForever(o -> {
            if(o != null){
                //convert response to required type
                Type genType = new TypeToken<List<MinistryWorkerEntity>>() {}.getType();
                List<MinistryWorkerEntity> response = AppUtils.objectToType(o,genType);
                //add values to the observable
                cacheWorkers(response);
                this.ministryWorkerResponse.setValue(response);
            }
        });
    }

    private void  cacheWorkers(List<MinistryWorkerEntity> workers){
        new MinistryWorkerRepository.InsetAsyncTask(ministryWorkerDao).execute(workers);
    }

    static class InsetAsyncTask extends AsyncTask<List<MinistryWorkerEntity>, Void, Void> {
        private MinistryWorkerDao minWorkerDao;

        public InsetAsyncTask(MinistryWorkerDao minWorkerDao) {
            this.minWorkerDao = minWorkerDao;
        }
        @Override
        protected Void doInBackground(List<MinistryWorkerEntity>... workers) {
            minWorkerDao.insert(workers[0]);
            return null;
        }
    }

    static class DeleteAsyncTask extends AsyncTask<Void, Void, Void> {
        private MinistryWorkerDao ministryWorkerDao;

        public DeleteAsyncTask(MinistryWorkerDao ministryWorkerDao) {
            this.ministryWorkerDao = ministryWorkerDao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            ministryWorkerDao.deleteAll();
            return null;
        }
    }

}

