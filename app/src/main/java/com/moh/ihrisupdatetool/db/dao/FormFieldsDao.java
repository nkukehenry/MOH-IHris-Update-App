package com.moh.ihrisupdatetool.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.moh.ihrisupdatetool.db.entities.FormEntity;
import com.moh.ihrisupdatetool.db.entities.FormField;

import java.util.List;

@Dao
public interface FormFieldsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<FormField> formFields);

    @Query("SELECT * FROM form_fields")
    LiveData<List<FormField>> getAllFormFields();

    @Query("SELECT * FROM form_fields WHERE form_id =:formId")
    LiveData<List<FormField>> getAllFormByForm(Integer formId);

    @Query("DELETE FROM form_fields")
    void deleteAll();
}
