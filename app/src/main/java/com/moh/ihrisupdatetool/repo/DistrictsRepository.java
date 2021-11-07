
package com.moh.ihrisupdatetool.repo;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.reflect.TypeToken;
import com.moh.ihrisupdatetool.db.dao.DistrictsDao;
import com.moh.ihrisupdatetool.db.entities.DistrictEntity;
import com.moh.ihrisupdatetool.repo.remote.IGenericAppRepository;
import com.moh.ihrisupdatetool.utils.AppConstants;
import com.moh.ihrisupdatetool.utils.AppUtils;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

public class DistrictsRepository {

    private IGenericAppRepository genericAppRepository;
    private MutableLiveData<List<DistrictEntity>> districtsResponse;
    private DistrictsDao districtsDao;

    @Inject
    public DistrictsRepository(IGenericAppRepository genericAppRepository,DistrictsDao districtsDao) {
        this.genericAppRepository = genericAppRepository;
        this.districtsResponse = new MutableLiveData<>();
        this.districtsDao = districtsDao;
    }

    public MutableLiveData<List<DistrictEntity>> observerResponse(){
        return districtsResponse;
    }

    public void fetchDistricts(){

        districtsDao.getAllDistricts().observeForever( o->{

            if(o.isEmpty()){ fetchFromApi(); } else {
                this.districtsResponse.postValue(o);
            }
        });

    }

    private void  fetchFromApi(){
        genericAppRepository.get(AppConstants.GET_DISTRICTS_URL() ).observeForever(o -> {
            System.out.println(o);
            if(o != null){
                //convert response to required type
                Type genType = new TypeToken<List<DistrictEntity>>() {}.getType();
                List<DistrictEntity> response = AppUtils.ToBaseType(o,genType);
                //add values to the observable
                cacheDistricts(response);
                this.districtsResponse.postValue(response);
            }

        });
    }

    private void  cacheDistricts(List<DistrictEntity> districts){
        new DistrictsRepository.InsetAsyncTask(districtsDao).execute(districts);
    }

    static class InsetAsyncTask extends AsyncTask<List<DistrictEntity>, Void, Void> {
        private DistrictsDao districtsDao;

        public InsetAsyncTask(DistrictsDao districtsDao) {
            this.districtsDao = districtsDao;
        }
        @Override
        protected Void doInBackground(List<DistrictEntity>... districts) {
            districtsDao.insert(districts[0]);
            return null;
        }
    }
}
