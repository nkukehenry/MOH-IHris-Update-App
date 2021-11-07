package com.moh.ihrisupdatetool.repo;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.reflect.TypeToken;
import com.moh.ihrisupdatetool.db.dao.FormFieldsDao;
import com.moh.ihrisupdatetool.db.entities.FormField;
import com.moh.ihrisupdatetool.repo.remote.IGenericAppRepository;
import com.moh.ihrisupdatetool.utils.AppConstants;
import com.moh.ihrisupdatetool.utils.AppUtils;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

public class FormFieldsRepository {
    private IGenericAppRepository genericAppRepository;
    private MutableLiveData<List<FormField>> formFieldsResponse;
    private FormFieldsDao formFieldsDoa;

    @Inject
    public FormFieldsRepository(IGenericAppRepository genericAppRepository, FormFieldsDao  formsDao) {

        this.genericAppRepository = genericAppRepository;
        this.formFieldsResponse = new MutableLiveData<>();
        this.formFieldsDoa =  formsDao;

    }

    public MutableLiveData<List<FormField>> observerResponse(){
        return formFieldsResponse;
    }

    public void fetchFormFields(Integer formId){

        formFieldsDoa.getAllFormByForm(formId).observeForever(o->{

            if(o.isEmpty()){ fetchFromApi(formId); } else {
                this.formFieldsResponse.postValue(o);
            }
        });

    }

    private void  fetchFromApi(Integer formId){

        genericAppRepository.get(AppConstants.GET_FORM_FIELDS_URL(formId) ).observeForever(o -> {

            System.out.println(o);

            if(o != null){
                //convert response to required type
                Type genType = new TypeToken<List<FormField>>() {}.getType();
                List<FormField> response = AppUtils.ToBaseType(o,genType);
                //add values to the observable
                cacheFormFields(response);
                this.formFieldsResponse.postValue(response);
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
