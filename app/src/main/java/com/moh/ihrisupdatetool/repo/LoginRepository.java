package com.moh.ihrisupdatetool.repo;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.gson.reflect.TypeToken;
import com.moh.ihrisupdatetool.db.dao.JobsDao;
import com.moh.ihrisupdatetool.db.dao.SessionInfoDao;
import com.moh.ihrisupdatetool.db.entities.JobEntity;
import com.moh.ihrisupdatetool.db.entities.SessionInfoEntity;
import com.moh.ihrisupdatetool.repo.remote.IAppRemoteCallRepository;
import com.moh.ihrisupdatetool.utils.AppConstants;
import com.moh.ihrisupdatetool.utils.AppUtils;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

public class LoginRepository {

    private MutableLiveData<SessionInfoEntity> loginResponse;
    private IAppRemoteCallRepository remoteCallRepository;
    private SessionInfoDao sessionInfoDao;
    private Boolean isObserved=false;

    @Inject
    public LoginRepository(IAppRemoteCallRepository remoteCallRepository,SessionInfoDao sessionInfoDao) {
        this.remoteCallRepository = remoteCallRepository;
        this.sessionInfoDao =  sessionInfoDao;
        this.loginResponse = new MutableLiveData<>();
    }

    public LiveData<SessionInfoEntity> doLogin(int userCode) {

        sessionInfoDao.getUserSessionInfo(userCode).observeForever(sessionInfoEntities -> {

            System.out.println(sessionInfoEntities);

               if(sessionInfoEntities == null && !isObserved){
                   doRemoteLogin(userCode);
               }else{
                   loginResponse.setValue(sessionInfoEntities);
               }
        });

        sessionInfoDao.getUserSessionInfo(userCode).removeObserver(formEntities -> { });
        return loginResponse;

    }

    private void doRemoteLogin(int userCode){

        this.remoteCallRepository.get(AppConstants.GET_LOGIN_BYCODE(userCode)).observeForever(o -> {

            if(o != null) {
                isObserved = true;
                //convert response to required type
                Type genType = new TypeToken<SessionInfoEntity>() { }.getType();
                SessionInfoEntity response = AppUtils.objectToType(o, genType);

                if(response.getStatus() == 1){
                    response.setCode(String.valueOf(userCode));
                    cacheSession(response);
                    loginResponse.setValue(response);
                }else{
                    loginResponse.setValue(null);
                }

            }else{
                loginResponse.setValue(null);
            }
        });

    }

    private void  cacheSession(SessionInfoEntity sessionInfoEntity){
        new LoginRepository.InsetAsyncTask(sessionInfoDao).execute(sessionInfoEntity);
    }

    static class InsetAsyncTask extends AsyncTask<SessionInfoEntity, Void, Void> {
        private SessionInfoDao sessionInfoDao;

        public InsetAsyncTask(SessionInfoDao sessionInfoDao) {
            this.sessionInfoDao = sessionInfoDao;
        }
        @Override
        protected Void doInBackground(SessionInfoEntity... sessionInfoEntities) {
            sessionInfoDao.insert(sessionInfoEntities[0]);
            return null;
        }
    }

}
