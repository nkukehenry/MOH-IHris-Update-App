package com.moh.ihrisupdatetool.repo;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.reflect.TypeToken;
import com.moh.ihrisupdatetool.db.dao.JobsDao;
import com.moh.ihrisupdatetool.db.entities.JobEntity;
import com.moh.ihrisupdatetool.repo.remote.IGenericAppRepository;
import com.moh.ihrisupdatetool.utils.AppConstants;
import com.moh.ihrisupdatetool.utils.AppUtils;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

public class JobsRepository {

    private IGenericAppRepository genericAppRepository;
    private MutableLiveData<List<JobEntity>> jobsResponse;
    private JobsDao jobsDao;

    @Inject
    public JobsRepository(IGenericAppRepository genericAppRepository,JobsDao jobsDao) {

        this.genericAppRepository = genericAppRepository;
        this.jobsResponse = new MutableLiveData<>();
        this.jobsDao = jobsDao;

    }

    public MutableLiveData<List<JobEntity>> observerResponse(){
        return jobsResponse;
    }

    public void fetchJobs(){

        jobsDao.getAllJobs().observeForever(o->{

            if(o.isEmpty()){ fetchFromApi(); } else {
                this.jobsResponse.postValue(o);
            }
        });

    }

    private void  fetchFromApi(){

        genericAppRepository.get(AppConstants.GET_JOBS_URL() ).observeForever(o -> {

            System.out.println(o);

            if(o != null){
                //convert response to required type
                Type genType = new TypeToken<List<JobEntity>>() {}.getType();
                List<JobEntity> response = AppUtils.objectToType(o,genType);
                //add values to the observable
                cacheJobs(response);
                this.jobsResponse.postValue(response);
            }

        });
    }

    private void  cacheJobs(List<JobEntity> districts){

        new JobsRepository.InsetAsyncTask(jobsDao).execute(districts);

    }

    static class InsetAsyncTask extends AsyncTask<List<JobEntity>, Void, Void> {
        private JobsDao jobsDao;

        public InsetAsyncTask(JobsDao jobsDao) {
            this.jobsDao = jobsDao;
        }
        @Override
        protected Void doInBackground(List<JobEntity>... jobs) {
            jobsDao.insert(jobs[0]);
            return null;
        }
    }
}
