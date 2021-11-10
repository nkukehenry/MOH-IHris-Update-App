package com.moh.ihrisupdatetool.services;

import okhttp3.Response;

public interface IApiErrorHandlingService {

    Response networkErrorHandling(Response response);
    void showNetworkRequestLoader();
    void closeNetworkRequestLoader();
}
