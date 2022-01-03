package com.moh.ihrisupdatetool.repo.remote;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.reflect.TypeToken;
import com.moh.ihrisupdatetool.api.AppApi;
import com.moh.ihrisupdatetool.utils.AppUtils;

import java.lang.reflect.Type;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppRemoteCallRepository implements IAppRemoteCallRepository {

    private AppApi appApi;

    @Inject
    public AppRemoteCallRepository(AppApi appApi) {
        this.appApi = appApi;
    }

    @Override
    public <T> T postSync(String url, Object model) {

        final MutableLiveData<T> data = new MutableLiveData<>();

        appApi.post(url, model).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if(!response.isSuccessful()){
                    data.setValue(null);
                }

                Type genType = new TypeToken<T>() {}.getType();
                try {
                    T results = AppUtils.objectToType(response.body(), genType);
                    data.setValue(results);
                }catch(Exception exception){
                    data.setValue(null);
                    exception.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                data.postValue( null );
            }
        });

        return data.getValue();
    }

    public <T> LiveData<T> post(String url, Object model){
        final MutableLiveData<T> data = new MutableLiveData<>();

        appApi.post(url, model).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {

                if(!response.isSuccessful()){
                    data.setValue(null);
                    return;
                }

                Type genType = new TypeToken<T>() {}.getType();
                //assert response.body() != null;
                try {
                    T results = AppUtils.objectToType(response.body(), genType);
                    data.setValue(results);
                }catch(Exception exception){
                    data.setValue(null);
                    exception.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                data.postValue( null );
            }
        });

        return data;
    }

    public <T> LiveData<T> get(String url){
        final MutableLiveData<T> data = new MutableLiveData<>();

        appApi.get(url).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {

                if(!response.isSuccessful()){
                    data.setValue(null);
                    return;
                }

                Type genType = new TypeToken<T>() {}.getType();
                assert response.body() != null;

                T results = AppUtils.objectToType(response.body(),genType);
                data.postValue(results);
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                System.out.println("Response erro in repo");
                System.out.println(call);
                t.printStackTrace();
                data.postValue( null );
            }
        });

        return data;
    }



}
