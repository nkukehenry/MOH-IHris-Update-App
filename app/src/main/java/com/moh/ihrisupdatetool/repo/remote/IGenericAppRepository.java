package com.moh.ihrisupdatetool.repo.remote;

import androidx.lifecycle.LiveData;

public interface IGenericAppRepository {
    <T> LiveData<T> post(String url, Object model);
    <T> LiveData<T> get(String url);
}
