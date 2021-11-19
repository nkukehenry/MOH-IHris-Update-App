package com.moh.ihrisupdatetool.repo;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.reflect.TypeToken;
import com.moh.ihrisupdatetool.db.dao.FormFieldsDao;
import com.moh.ihrisupdatetool.db.entities.FormField;
import com.moh.ihrisupdatetool.repo.remote.IAppRemoteCallRepository;
import com.moh.ihrisupdatetool.utils.AppConstants;
import com.moh.ihrisupdatetool.utils.AppUtils;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

public class FormFieldsRepository {
    private IAppRemoteCallRepository genericAppRepository;
    private MutableLiveData<List<FormField>> formFieldsResponse;
    private FormFieldsDao formFieldsDoa;

    @Inject
    public FormFieldsRepository(IAppRemoteCallRepository genericAppRepository, FormFieldsDao  formsDao) {

        this.genericAppRepository = genericAppRepository;
        this.formFieldsResponse = new MutableLiveData<>();
        this.formFieldsDoa =  formsDao;

    }

    public MutableLiveData<List<FormField>> observerResponse(){
        return formFieldsResponse;
    }

    public void fetchFormFields(Integer formId){

        formFieldsDoa.getAllFormByFormId(formId).observeForever(o->{

            if(o.isEmpty()){
                fetchFromApi(formId);
            }
            else {
                this.formFieldsResponse.setValue(o);
            }

        });

    }

    private void  fetchFromApi(Integer formId){

        genericAppRepository.get(AppConstants.GET_FORM_FIELDS_URL(formId) ).observeForever(o -> {


            if(o != null){
                //convert response to required type
                Type genType = new TypeToken<List<FormField>>() {}.getType();
                List<FormField> response = AppUtils.objectToType(o,genType);
                //add values to the observable
                cacheFormFields(response);
                this.formFieldsResponse.setValue(response);
            }

        });
    }

    private void  cacheFormFields(List<FormField> formFields){

        new FormFieldsRepository.InsetAsyncTask(formFieldsDoa).execute(formFields);

    }

    static class InsetAsyncTask extends AsyncTask<List<FormField>, Void, Void> {
        private FormFieldsDao formFieldsDao;

        public InsetAsyncTask(FormFieldsDao formFieldsDao) {
            this.formFieldsDao = formFieldsDao;
        }
        @Override
        protected Void doInBackground(List<FormField>... formFields) {
            formFieldsDao.insert(formFields[0]);
            return null;
        }
    }
}
