package com.moh.ihrisupdatetool.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.moh.ihrisupdatetool.db.entities.SessionInfoEntity;
import com.moh.ihrisupdatetool.repo.LoginRepository;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginViewModel extends AndroidViewModel {

    private LoginRepository loginRepository;

    @Inject
    public LoginViewModel(@NonNull @NotNull Application application, LoginRepository loginRepository) {
        super(application);
        this.loginRepository = loginRepository;
    }

    public MutableLiveData<SessionInfoEntity> observeLoginReponse(){
        return this.loginRepository.observeLoginResponse();
    }

    public void doLogin(String userCode){
        this.loginRepository.doLogin(userCode);
    }
}
