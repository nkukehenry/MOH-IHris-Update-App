package com.moh.ihrisupdatetool.api;

import com.google.gson.JsonObject;
import com.moh.ihrisupdatetool.dto.AppResponse;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface AppApi {

    @GET
    Call<Object> get(@Url String url);

    @POST
    Call<Object> post(@Url String url,@Body Object data);
}
