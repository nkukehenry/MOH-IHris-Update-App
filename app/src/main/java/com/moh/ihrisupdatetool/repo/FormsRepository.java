package com.moh.ihrisupdatetool.repo;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.reflect.TypeToken;
import com.moh.ihrisupdatetool.db.dao.FormsDao;
import com.moh.ihrisupdatetool.db.entities.FormEntity;
import com.moh.ihrisupdatetool.repo.remote.IGenericAppRepository;
import com.moh.ihrisupdatetool.utils.AppConstants;
import com.moh.ihrisupdatetool.utils.AppUtils;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

public class FormsRepository {

    private IGenericAppRepository genericAppRepository;
    private MutableLiveData<List<FormEntity>> formsResponse;
    private FormsDao formsDao;

    @Inject
    public FormsRepository(IGenericAppRepository genericAppRepository, FormsDao  formsDao) {

        this.genericAppRepository = genericAppRepository;
        this.formsResponse = new MutableLiveData<>();
        this.formsDao =  formsDao;

    }

    public MutableLiveData<List<FormEntity>> observerResponse(){
        return formsResponse;
    }

    public void fetchForms(){

        formsDao.getAllForms().observeForever(o->{

            if(o.isEmpty()){ fetchFromApi(); } else {
                this.formsResponse.postValue(o);
            }
        });

    }

    private void  fetchFromApi(){

        genericAppRepository.get(AppConstants.GET_FORMS_URL() ).observeForever(o -> {

            System.out.println(o);

            if(o != null){
                //convert response to required type
                Type genType = new TypeToken<List<FormEntity>>() {}.getType();
                List<FormEntity> response = AppUtils.objectToType(o,genType);
                //add values to the observable
                cacheForms(response);
                this.formsResponse.postValue(response);
            }

        });
    }

    private void  cacheForms(List<FormEntity> forms){

        new FormsRepository.InsetAsyncTask(formsDao).execute(forms);

    }

    static class InsetAsyncTask extends AsyncTask<List<FormEntity>, Void, Void> {
        private FormsDao formsDao;

        public InsetAsyncTask(FormsDao formsDao) {
            this.formsDao = formsDao;
        }
        @Override
        protected Void doInBackground(List<FormEntity>... forms) {
            formsDao.insert(forms[0]);
            return null;
        }
    }
}
