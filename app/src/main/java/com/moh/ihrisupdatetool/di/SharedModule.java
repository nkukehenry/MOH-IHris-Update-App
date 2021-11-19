package com.moh.ihrisupdatetool.di;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.moh.ihrisupdatetool.utils.AwesomeCustomValidators;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class SharedModule {

    @Provides
    public static AwesomeValidation provideAwesomeValidation() {
        AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        return mAwesomeValidation;
    }

    @Provides
    public static AwesomeCustomValidators provideAwesomeCustomValidators() {
        return new AwesomeCustomValidators();
    }
}

