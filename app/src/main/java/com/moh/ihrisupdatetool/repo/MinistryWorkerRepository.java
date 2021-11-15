
package com.moh.ihrisupdatetool.repo;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.reflect.TypeToken;
import com.moh.ihrisupdatetool.db.dao.MinistryWorkerDao;
import com.moh.ihrisupdatetool.db.entities.CommunityWorkerEntity;
import com.moh.ihrisupdatetool.db.entities.MinistryWorkerEntity;
import com.moh.ihrisupdatetool.repo.remote.IGenericAppRepository;
import com.moh.ihrisupdatetool.utils.AppConstants;
import com.moh.ihrisupdatetool.utils.AppUtils;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

public class MinistryWorkerRepository {

    private IGenericAppRepository genericAppRepository;
    private MutableLiveData<List<MinistryWorkerEntity>> ministryWorkerResponse;
    private MinistryWorkerDao ministryWorkerDao;

    @Inject
    public MinistryWorkerRepository(IGenericAppRepository genericAppRepository,MinistryWorkerDao ministryWorkerDao) {

        this.genericAppRepository = genericAppRepository;
        this.ministryWorkerResponse = new MutableLiveData<>();
        this.ministryWorkerDao = ministryWorkerDao;
    }

    public MutableLiveData<List<MinistryWorkerEntity>> observerResponse(){
        return ministryWorkerResponse;
    }

    public void fetchMinistryWorkers(){

        ministryWorkerDao.getMinistryWorkers().observeForever(o->{

            if(o.isEmpty()){ fetchFromApi(); } else {
                this.ministryWorkerResponse.postValue(o);
            }

        });

    }

    public void searchWorkers(String term,String districtName){

        ministryWorkerDao.searchWorker(term,districtName).observeForever(o->{
            if(o.isEmpty()){
                fetchFromApi();
            } else {
                this.ministryWorkerResponse.postValue(o);
            }
        });

    }


    private void  fetchFromApi(){
        genericAppRepository.get(AppConstants.GET_MINISTRY_WORKER_DATA_URL()).observeForever(o -> {
            System.out.println(o);
            if(o != null){
                //convert response to required type
                Type genType = new TypeToken<List<MinistryWorkerEntity>>() {}.getType();
                List<MinistryWorkerEntity> response = AppUtils.objectToType(o,genType);
                //add values to the observable
                cacheWorkers(response);
                this.ministryWorkerResponse.postValue(response);
            }
        });
    }

    private void  cacheWorkers(List<MinistryWorkerEntity> workers){
        System.out.println(workers);
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
}

