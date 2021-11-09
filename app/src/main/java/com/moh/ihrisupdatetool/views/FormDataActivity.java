package com.moh.ihrisupdatetool.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.adapters.AdapterViewBindingAdapter;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.db.entities.FormEntity;
import com.moh.ihrisupdatetool.db.entities.FormField;
import com.moh.ihrisupdatetool.dto.FormFieldType;
import com.moh.ihrisupdatetool.viewmodels.FormsViewModel;
import com.moh.ihrisupdatetool.viewmodels.SubmissionViewModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dagger.hilt.android.AndroidEntryPoint;

import static com.moh.ihrisupdatetool.utils.AppConstants.SELECTED_FORM;

@AndroidEntryPoint
public class FormDataActivity extends AppCompatActivity {

    FormsViewModel formsViewModel;
    FormEntity selectedForm;
    LinearLayout dynamicFieldsWrapper;
    JsonObject postDataObject = new JsonObject();
    SubmissionViewModel submissionViewModel;
    List<FormField> formFields;
    Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_fields);

        dynamicFieldsWrapper  = findViewById(R.id.dynamicFieldsWrapper);

        Bundle bundle; bundle = getIntent().getExtras();
        selectedForm   = (FormEntity) bundle.get(SELECTED_FORM);

        formsViewModel = new ViewModelProvider(this).get(FormsViewModel.class);
        submissionViewModel =  new ViewModelProvider(this).get(SubmissionViewModel.class);
        submitBtn = findViewById(R.id.submitBtn);

        submitBtn.setOnClickListener(v -> {
            onSubmitClicked();
        });

        initObservers();

        getFormFields();
    }

    private void initObservers(){

        formsViewModel.observerFormFieldsResponse().observe(this, formsFieldsResponse -> {

            formFields = formsFieldsResponse;

            for(FormField field : formsFieldsResponse){

                FormFieldType fieldType = InputType(field.getData_type());

                switch(fieldType) {
                    case SPINNER_BASED_FIED:
                        renderSpinnerBasedField(field);
                        break;
                    default:
                        renderTextBasedField(field);
                        break;
                }
            }

        });


        //submission
        submissionViewModel.observeResonse().observe( this,submissionResponse->{
            System.out.println(submissionResponse);
        });
    }

    private void getFormFields() {
        formsViewModel.getFormFields(selectedForm.getId());
    }


    private void renderTextBasedField(FormField field){

        // Create EditText
        TextInputLayout currentField =new TextInputLayout(this);

        //Params
        TextInputLayout.LayoutParams params= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin =8;
        params.bottomMargin=8;

        currentField.setLayoutParams(params);

        //Input
        final EditText edtPass = new EditText(currentField.getContext());
        edtPass.setInputType( getInputTypeClass(field.getData_type()) );
        edtPass.setPadding(25,50,25,10);
        edtPass.setTextColor(getResources().getColor(R.color.grey));
        edtPass.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        edtPass.setHint(field.getLabel());
        edtPass.setBackgroundColor(0x00000000);
        edtPass.setId(Integer.parseInt(field.getId()));

        currentField.addView(edtPass);

        dynamicFieldsWrapper.addView(currentField);
    }


    private void renderSpinnerBasedField(FormField field){

        System.out.println(field);
        Gson gson = new Gson();
        Type targetType = new TypeToken<List<Map<String,String>>>() {}.getType();
        List<Map<String,String>> options = gson.fromJson(field.getDefault_data(),targetType);

        System.out.println(options);

        List<String> listOptions = new ArrayList<String>();

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
            return;

        LinearLayout currentField =new LinearLayout(this);

        //Params
        LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
                params.topMargin = 8;
                params.bottomMargin = 8;

        currentField.setLayoutParams(params);

        spinnerOptions.add("Choose "+field.getLabel());

        Spinner spinner = new Spinner(FormDataActivity.this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FormDataActivity.this,android.R.layout.simple_spinner_item,spinnerOptions);
        spinner.setAdapter(adapter);
        spinner.setLayoutParams(params);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(spinnerOptions.get(position));
                System.out.println(options.get(position));

                postDataObject.addProperty(field.getForm_field(),spinnerOptions.get(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        currentField.addView(spinner);
        dynamicFieldsWrapper.addView(currentField);
    }




    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private int getInputTypeClass(String remoteType){

        switch (remoteType){
            case "date":
                return InputType.TYPE_CLASS_DATETIME;
            case "number":
                return InputType.TYPE_CLASS_NUMBER;
            case "decimal":
                return InputType.TYPE_NUMBER_FLAG_DECIMAL;
            case "phone":
                return InputType.TYPE_CLASS_PHONE;
            case "email":
                return InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
            default:
                return InputType.TYPE_CLASS_TEXT;
        }
    }

    private FormFieldType InputType(String fieldType){

        switch (fieldType){
            case "phone":
            case "decimal":
            case "number":
            case "date":
            case "email":
                return FormFieldType.TEXT_BASED_FIELD;
            case "map":
                return FormFieldType.SPINNER_BASED_FIED;
        }
        return FormFieldType.TEXT_BASED_FIELD;
    }

    private void onSubmitClicked() {

        for (FormField field : formFields) {

            FormFieldType fieldType = InputType(field.getData_type());

            if (fieldType.equals(FormFieldType.TEXT_BASED_FIELD)) {
                EditText textInput = findViewById(Integer.parseInt(field.getId()));
                postDataObject.addProperty(field.getForm_field(), textInput.getText().toString());
            }

        }

        submissionViewModel.postData(postDataObject);
    }

}