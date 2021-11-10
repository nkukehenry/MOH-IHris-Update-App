package com.moh.ihrisupdatetool.services;

import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.moh.ihrisupdatetool.db.entities.FormField;

public interface IDynamicFieldService {

    TextInputLayout gettextBasedField(FormField field);
    TextView getImageField(FormField field);
    Spinner getSpinnerField(FormField field);

}
