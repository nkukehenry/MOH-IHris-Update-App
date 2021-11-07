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

public class GenericAppRepository implements IGenericAppRepository {

    private AppApi appApi;

    @Inject
    public GenericAppRepository(AppApi appApi) {
        this.appApi = appApi;
    }

    public <T> LiveData<T> post(String url, Object model){
        final MutableLiveData<T> data = new MutableLiveData<>();

        appApi.post(url, model).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {

//               System.out.println(response.body() );

                if(!response.isSuccessful()){
                    data.postValue(null);
                    return;
                }

                Type genType = new TypeToken<T>() {}.getType();
                assert response.body() != null;

                T results = AppUtils.ToBaseType(response.body(),genType);
                data.postValue(results);

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

                System.out.println("Response in repo");
               System.out.println(response);

                if(!response.isSuccessful()){
                    data.postValue(null);
                    return;
                }

                Type genType = new TypeToken<T>() {}.getType();
                assert response.body() != null;

                T results = AppUtils.ToBaseType(response.body(),genType);
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
