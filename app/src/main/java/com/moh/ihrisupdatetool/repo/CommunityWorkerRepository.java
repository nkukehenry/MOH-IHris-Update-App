package com.moh.ihrisupdatetool.repo;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.reflect.TypeToken;
import com.moh.ihrisupdatetool.db.dao.CommunityWorkerDao;
import com.moh.ihrisupdatetool.db.entities.CommunityWorkerEntity;
import com.moh.ihrisupdatetool.repo.remote.IGenericAppRepository;
import com.moh.ihrisupdatetool.utils.AppConstants;
import com.moh.ihrisupdatetool.utils.AppUtils;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

public class CommunityWorkerRepository {

    private IGenericAppRepository genericAppRepository;
    private MutableLiveData<List<CommunityWorkerEntity>> communityWorkerResponse;
    private CommunityWorkerDao communitWorkerDao;

    @Inject
    public CommunityWorkerRepository(IGenericAppRepository genericAppRepository,CommunityWorkerDao commuityWorkerDao) {

        this.genericAppRepository = genericAppRepository;
        this.communityWorkerResponse = new MutableLiveData<>();
        this.communitWorkerDao = commuityWorkerDao;
    }

    public MutableLiveData<List<CommunityWorkerEntity>> observerResponse(){
        return communityWorkerResponse;
    }

    public void fetchCommunityWorkers(){

        communitWorkerDao.getCommunityWorkers().observeForever(o->{

            if(o.isEmpty()){ fetchFromApi(); } else {
                this.communityWorkerResponse.postValue(o);
            }

        });

    }

    private void  fetchFromApi(){
        genericAppRepository.get(AppConstants.GET_COMMUNITY_WORKER_DATA_URL()).observeForever(o -> {
            System.out.println(o);
            if(o != null){
                //convert response to required type
                Type genType = new TypeToken<List<CommunityWorkerEntity>>() {}.getType();
                List<CommunityWorkerEntity> response = AppUtils.ToBaseType(o,genType);
                //add values to the observable
                cacheWorkers(response);
                this.communityWorkerResponse.postValue(response);
            }
        });
    }

    private void  cacheWorkers(List<CommunityWorkerEntity> workers){
        System.out.println(workers);
        new CommunityWorkerRepository.InsetAsyncTask(communitWorkerDao).execute(workers);
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
}
