package com.moh.ihrisupdatetool.repo;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.gson.reflect.TypeToken;
import com.moh.ihrisupdatetool.db.dao.CommunityWorkerDao;
import com.moh.ihrisupdatetool.db.entities.CommunityWorkerEntity;
import com.moh.ihrisupdatetool.repo.remote.IAppRemoteCallRepository;
import com.moh.ihrisupdatetool.utils.AppConstants;
import com.moh.ihrisupdatetool.utils.AppUtils;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

import static java.lang.Thread.sleep;

public class CommunityWorkerRepository {

    private IAppRemoteCallRepository genericAppRepository;
    private CommunityWorkerDao communitWorkerDao;
    private MutableLiveData<List<CommunityWorkerEntity>> communityWorkers;
    private MutableLiveData<List<CommunityWorkerEntity>> cachedWorkers;

    @Inject
    public CommunityWorkerRepository(IAppRemoteCallRepository genericAppRepository, CommunityWorkerDao commuityWorkerDao) {

        this.genericAppRepository = genericAppRepository;
        this.communitWorkerDao = commuityWorkerDao;
        communityWorkers = new MutableLiveData<>();
        cachedWorkers = new MutableLiveData<>();
    }
    public LiveData<List<CommunityWorkerEntity>> fetchCommunityWorkers(String district, Boolean deleteCache){
        cachedWorkers = new MutableLiveData<>();
        fetchFromApi(district,deleteCache);
       return cachedWorkers;
    }

    public LiveData<List<CommunityWorkerEntity>> fetchCommunityWorkers(String district){

        communitWorkerDao.getCommunityWorkers().observeForever(workers -> {

            if( workers.isEmpty() ){
                fetchFromApi(district,false);
            }else{
                communityWorkers.setValue(workers);
            }
        });

        return communityWorkers;
    }

    public MutableLiveData<List<CommunityWorkerEntity>>  searchWorkers(String term,String districtName){

        MutableLiveData<List<CommunityWorkerEntity>> workersResult = new MutableLiveData();

        communitWorkerDao.searchWorker(term,districtName).observeForever(o->{
            if(o.isEmpty()){
                  //fetchFromApi();
                workersResult.setValue(null);
                 } else {
                workersResult.setValue(o);
            }
        });
        return workersResult;
    }


    private void  fetchFromApi(String district,Boolean deleteCache){
        genericAppRepository.get(AppConstants.GET_COMMUNITY_WORKER_DATA_URL(district)).observeForever(o -> {
            if(o != null){
                //convert response to required type
                Type genType = new TypeToken<List<CommunityWorkerEntity>>() {}.getType();
                List<CommunityWorkerEntity> response = AppUtils.objectToType(o,genType);
                //add values to the observable
                cacheWorkers(response);
                if(deleteCache)
                    cachedWorkers.setValue(response);
                else
                communityWorkers.setValue(response);
            }
        });
    }

    private void  cacheWorkers(List<CommunityWorkerEntity> workers){
        new CommunityWorkerRepository.InsetAsyncTask(communitWorkerDao).execute(workers);
    }

    public void  deleteData(){
        new CommunityWorkerRepository.DeleteAsyncTask(communitWorkerDao).execute();
    }

    static class InsetAsyncTask extends AsyncTask<List<CommunityWorkerEntity>, Void, Void> {
        private CommunityWorkerDao communityWorkerDao;

        public InsetAsyncTask(CommunityWorkerDao communityWorkerDao) {
            this.communityWorkerDao = communityWorkerDao;
        }
        @Override
        protected Void doInBackground(List<CommunityWorkerEntity>... workers) {
            communityWorkerDao.insert(workers[0]);
            return null;
        }
    }


    static class DeleteAsyncTask extends AsyncTask<Void, Void, Void> {
        private CommunityWorkerDao communityWorkerDao;

        public DeleteAsyncTask(CommunityWorkerDao communityWorkerDao) {
            this.communityWorkerDao = communityWorkerDao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            communityWorkerDao.deleteAll();
            return null;
        }
    }


}
