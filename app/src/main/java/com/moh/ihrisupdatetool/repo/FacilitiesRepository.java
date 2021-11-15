package com.moh.ihrisupdatetool.repo;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.reflect.TypeToken;
import com.moh.ihrisupdatetool.db.dao.FacilitiesDao;
import com.moh.ihrisupdatetool.db.entities.FacilityEntity;
import com.moh.ihrisupdatetool.repo.remote.IGenericAppRepository;
import com.moh.ihrisupdatetool.utils.AppConstants;
import com.moh.ihrisupdatetool.utils.AppUtils;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

public class FacilitiesRepository {

    private IGenericAppRepository genericAppRepository;
    private MutableLiveData<List<FacilityEntity>> facilitiesResponse;
    private FacilitiesDao facilitiesDao;

    @Inject
    public FacilitiesRepository(IGenericAppRepository genericAppRepository,FacilitiesDao facilitiesDao) {
        this.genericAppRepository = genericAppRepository;
        this.facilitiesResponse = new MutableLiveData<>();
        this.facilitiesDao = facilitiesDao;
    }

    public MutableLiveData<List<FacilityEntity>> observerResponse(){
        return facilitiesResponse;
    }

    public void fetchFacilities(){

        facilitiesDao.getAllFacilities().observeForever( o->{

            if(o.isEmpty()){
                fetchFromApi();
            }else {
                this.facilitiesResponse.postValue(o);
            }
        });

    }

    private void  fetchFromApi(){
        genericAppRepository.get(AppConstants.GET_FACILITIES_URL() ).observeForever(o -> {
            System.out.println(o);
            if(o != null){
                //convert response to required type
                Type genType = new TypeToken<List<FacilityEntity>>() {}.getType();
                List<FacilityEntity> response = AppUtils.objectToType(o,genType);
                //add values to the observable
                cacheFacilities(response);
                this.facilitiesResponse.postValue(response);
            }

        });
    }

     private void  cacheFacilities(List<FacilityEntity> facilities){
        new FacilitiesRepository.InsetAsyncTask(facilitiesDao).execute(facilities);
    }


    static class InsetAsyncTask extends AsyncTask<List<FacilityEntity>, Void, Void> {
        private FacilitiesDao facilitiesDao;

        public InsetAsyncTask(FacilitiesDao facilitiesDao) {
            this.facilitiesDao = facilitiesDao;
        }
        @Override
        protected Void doInBackground(List<FacilityEntity>... facilities) {
            facilitiesDao.insert(facilities[0]);
            return null;
        }
    }
}
