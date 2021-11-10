package com.moh.ihrisupdatetool.services;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.db.entities.FormField;
import com.moh.ihrisupdatetool.views.FormDataActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class DynamicFieldService implements IDynamicFieldService {

    private Context context;

    @Inject
    public DynamicFieldService(Context context) {
        this.context = context;
    }

    @Override
    public TextInputLayout gettextBasedField(FormField field) {
        return null;
    }

    @Override
    public TextView getImageField(FormField field) {
        return null;
    }

    @Override
    public Spinner getSpinnerField(FormField field) {

        System.out.println(field);
        Gson gson = new Gson();
        Type targetType = new TypeToken<List<Map<String,String>>>() {}.getType();
        List<Map<String,String>> options = gson.fromJson(field.getDefault_data(),targetType);

        System.out.println(options);

        List<String> listOptions = new ArrayList<String>();

        /**
         * Extract fields , get there values (key->value)
         * Consider value in position 1 for if there values are >1 in count
         * Else take position 0 if values count ==1
         */
        if(options != null){

            for (Map<String, String> option : options) {

                int count = 0;
                for(String value:option.values()) {
                    if( (count == 1 && option.values().size() >1) || option.values().size() ==1)//use  only second value
                        listOptions.add(value);
                    count ++;
                }
            }
        }


        final List<String> spinnerOptions = listOptions;

        if(spinnerOptions.isEmpty())
            return null;

        LinearLayout currentField =new LinearLayout(context);

        //Params
        LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 8;
        params.bottomMargin = 8;

        currentField.setLayoutParams(params);

        spinnerOptions.add("Choose "+field.getLabel());
        //Create spinner
        Spinner spinner = new Spinner(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( context, R.layout.spinner_item,spinnerOptions);
        spinner.setAdapter(adapter);
        spinner.setLayoutParams(params);
        return spinner;
    }

}
