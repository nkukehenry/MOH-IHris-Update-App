package com.moh.ihrisupdatetool.repo;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.moh.ihrisupdatetool.db.dao.FormFieldsDao;
import com.moh.ihrisupdatetool.db.entities.FormField;
import com.moh.ihrisupdatetool.repo.remote.IGenericAppRepository;
import com.moh.ihrisupdatetool.utils.AppConstants;
import com.moh.ihrisupdatetool.utils.AppUtils;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

public class DataSubmissionRepository {

    private IGenericAppRepository genericAppRepository;
    private MutableLiveData<JsonObject> submissionResponse;

    @Inject
    public DataSubmissionRepository(IGenericAppRepository genericAppRepository, FormFieldsDao  formsDao) {

        this.genericAppRepository = genericAppRepository;
        this.submissionResponse = new MutableLiveData<>();

    }

    public MutableLiveData<JsonObject> observerResponse(){
        return submissionResponse;
    }

    public void  postData(JsonObject postData){

        genericAppRepository.post(AppConstants.POST_FORM_DATA_URL(),postData).observeForever(o -> {

            System.out.println(o);

            if(o != null){
                //convert response to required type
                Type genType = new TypeToken<JsonObject>() {}.getType();
                JsonObject response = AppUtils.ToBaseType(o,genType);
                //add values to the observable
                this.submissionResponse.postValue(response);
            }

        });
    }

}
