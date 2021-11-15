package com.moh.ihrisupdatetool.repo;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.moh.ihrisupdatetool.db.dao.DataEntryDao;
import com.moh.ihrisupdatetool.db.dao.FormFieldsDao;
import com.moh.ihrisupdatetool.db.entities.DataEntryTemplate;
import com.moh.ihrisupdatetool.repo.remote.IGenericAppRepository;
import com.moh.ihrisupdatetool.utils.AppConstants;
import com.moh.ihrisupdatetool.utils.AppUtils;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

public class DataSubmissionRepository {

    private IGenericAppRepository genericAppRepository;
    private MutableLiveData<JsonObject> submissionResponse;
    private DataEntryDao dataEntryDao;

    @Inject
    public DataSubmissionRepository(IGenericAppRepository genericAppRepository,FormFieldsDao  formsDao,DataEntryDao dataEntryDao) {

        this.genericAppRepository = genericAppRepository;
        this.submissionResponse = new MutableLiveData<>();
        this.dataEntryDao = dataEntryDao;

    }

    public MutableLiveData<JsonObject> observerResponse(){
        return submissionResponse;
    }

    public Boolean syncData(){

        dataEntryDao.getLocalRecords().observeForever(new Observer<List<DataEntryTemplate>>() {
            @Override
            public void onChanged(List<DataEntryTemplate> records) {

                   JsonObject resp = new JsonObject();
                    if( !records.isEmpty() ) {
                        for (DataEntryTemplate record :records) {
                            postData(record.getFormdata());
                            resp.addProperty("state",true);
                            resp.addProperty("isUploaded",true);
                        }
                    }else{
                        resp.addProperty("state",false);
                    }
                submissionResponse.postValue(resp);
            }
        });
        return true;
    }

    public void  postData(JsonObject postData){

        cacheFormData(postData,false);

        genericAppRepository.post(AppConstants.POST_FORM_DATA_URL(),postData).observeForever(o -> {

            if(o != null){
                //convert response to required type
                Type genType = new TypeToken<JsonObject>() {}.getType();
                JsonObject response = AppUtils.objectToType(o,genType);
                //add values to the observable
                this.submissionResponse.postValue(response);
                //set marked uploaded
                cacheFormData(postData,true);
            }

        });
    }


    public void  cacheFormData(JsonObject data,Boolean isUploaded){

        DataEntryTemplate dataEntryTemplate = new DataEntryTemplate();
        dataEntryTemplate.setFacility_id("9988898");
        dataEntryTemplate.setFormdata(data);
        dataEntryTemplate.setReference(data.get("reference").getAsString());
        dataEntryTemplate.setStatus((isUploaded)?1:0);

        new DataSubmissionRepository.InsetAsyncTask(dataEntryDao).execute(dataEntryTemplate);
    }


    static class InsetAsyncTask extends AsyncTask<DataEntryTemplate, Void, Void> {
        private DataEntryDao dataEntryDao;

        public InsetAsyncTask(DataEntryDao dataEntryDao) {
            this.dataEntryDao = dataEntryDao;
        }
        @Override
        protected Void doInBackground(DataEntryTemplate... dataEntryTemplates) {
            dataEntryDao.insert(dataEntryTemplates[0]);
            return null;
        }
    }

}
