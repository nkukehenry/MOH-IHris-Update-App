package com.moh.ihrisupdatetool.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.moh.ihrisupdatetool.db.entities.FormEntity;
import com.moh.ihrisupdatetool.db.entities.FormField;
import com.moh.ihrisupdatetool.db.entities.JobEntity;
import com.moh.ihrisupdatetool.repo.FormFieldsRepository;
import com.moh.ihrisupdatetool.repo.FormsRepository;
import com.moh.ihrisupdatetool.repo.JobsRepository;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FormsViewModel extends AndroidViewModel {

    private FormsRepository formsRepository;
    private FormFieldsRepository formFieldsRepository;

    @Inject
    public FormsViewModel(@NonNull @NotNull Application application
            ,FormsRepository formsRepository
            ,FormFieldsRepository formFieldsRepository ) {

        super(application);
        this.formsRepository = formsRepository;
        this.formFieldsRepository = formFieldsRepository;
    }

    public LiveData<List<FormEntity>> getForms(){
        return formsRepository.fetchForms();
    }

    public LiveData<List<FormEntity>> syncForms(){
        return formsRepository.fetchForms(true);
    }

    public void deleteFields(){
        formFieldsRepository.deleteFields();
    }

    public LiveData<List<FormField>> getFormFields(Integer formId){
        return formFieldsRepository.fetchFormFields(formId);
    }

}
