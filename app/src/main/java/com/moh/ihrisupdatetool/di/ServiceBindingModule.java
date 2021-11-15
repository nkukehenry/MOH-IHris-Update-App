package com.moh.ihrisupdatetool.di;

import com.moh.ihrisupdatetool.services.ApiErrorHandlingService;
import com.moh.ihrisupdatetool.services.IApiErrorHandlingService;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class ServiceBindingModule {

    @Binds
    abstract IApiErrorHandlingService bindApiErrorHandlingService (ApiErrorHandlingService apiErrorHandlingService);

}
