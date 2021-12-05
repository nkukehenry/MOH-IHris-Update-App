package com.moh.ihrisupdatetool.repo;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.moh.ihrisupdatetool.db.dao.DataEntryDao;
import com.moh.ihrisupdatetool.db.dao.FormFieldsDao;
import com.moh.ihrisupdatetool.db.entities.DataEntryTemplate;
import com.moh.ihrisupdatetool.repo.remote.IAppRemoteCallRepository;
import com.moh.ihrisupdatetool.utils.AppConstants;
import com.moh.ihrisupdatetool.utils.AppUtils;

import java.lang.reflect.Type;

import javax.inject.Inject;

public class DataSubmissionRepository {

    private IAppRemoteCallRepository genericAppRepository;
    private DataEntryDao dataEntryDao;

    @Inject
    public DataSubmissionRepository(IAppRemoteCallRepository genericAppRepository, FormFieldsDao  formsDao, DataEntryDao dataEntryDao) {
        this.genericAppRepository = genericAppRepository;
        this.dataEntryDao = dataEntryDao;
    }


    public Boolean deleteAll() {
            new DeleteAsyncTask(dataEntryDao).execute();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }


    public LiveData<JsonObject> syncData(){

        MutableLiveData<JsonObject> syncResponse = new MutableLiveData<>();

        dataEntryDao.getLocalRecords().observeForever(records -> {

               JsonObject resp = new JsonObject();

                if( !records.isEmpty() ) {

                    for (DataEntryTemplate record :records) {

                        if(record.getStatus() == 0)
                            postData(record.getFormdata());

                        resp.addProperty("state",true);
                        resp.addProperty("isUploaded",true);
                    }

                }else{
                    resp.addProperty("state",false);
                }
            syncResponse.setValue(resp);
        });

        return syncResponse;
    }

    public LiveData<JsonObject>  postData(JsonObject postData){

        MutableLiveData<JsonObject> submissionResponse = new MutableLiveData<>();
        cacheFormData(postData,false);

        genericAppRepository.post(AppConstants.POST_FORM_DATA_URL(),postData).observeForever(o -> {

            if(o != null){
                //convert response to required type
                Type genType = new TypeToken<JsonObject>() {}.getType();
                JsonObject response = AppUtils.objectToType(o,genType);
                //add values to the observable
                submissionResponse.setValue(response);
                //set marked uploaded
                cacheFormData(postData,true);
            }

        });
        return submissionResponse;
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


    static class DeleteAsyncTask extends AsyncTask<Void, Void, Void> {
        private DataEntryDao dataEntryDao;

        public DeleteAsyncTask(DataEntryDao communityWorkerDao) {
            this.dataEntryDao = communityWorkerDao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            dataEntryDao.deleteAll();
            return null;
        }
    }

}
