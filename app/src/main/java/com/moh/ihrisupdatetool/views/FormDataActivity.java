package com.moh.ihrisupdatetool.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.adapaters.FormsAdapter;
import com.moh.ihrisupdatetool.db.entities.FormEntity;
import com.moh.ihrisupdatetool.db.entities.FormField;
import com.moh.ihrisupdatetool.viewmodels.FormsViewModel;

import dagger.hilt.android.AndroidEntryPoint;

import static com.moh.ihrisupdatetool.utils.AppConstants.SELECTED_FORM;

@AndroidEntryPoint
public class FormDataActivity extends AppCompatActivity {

    FormsViewModel formsViewModel;
    FormEntity selectedForm;
    LinearLayout dynamicFieldsWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_fields);
        dynamicFieldsWrapper = findViewById(R.id.dynamicFieldsWrapper);

        Bundle bundle; bundle = getIntent().getExtras();
        selectedForm = (FormEntity) bundle.get(SELECTED_FORM);

        formsViewModel = new ViewModelProvider(this).get(FormsViewModel.class);

        getFormFields();
    }

    private void getFormFields() {

        formsViewModel.observerFormFieldsResponse().observe(this, formsFieldsResponse -> {

             for(FormField field : formsFieldsResponse){
                 TextInputLayout currentField = renderField(field);
                 dynamicFieldsWrapper.addView(currentField);
             }

        });

        formsViewModel.getFormFields(selectedForm.getId());
    }

    private TextInputLayout renderField(FormField field){

        // Create EditText
        TextInputLayout currentField =new TextInputLayout(this);

        //Params
        TextInputLayout.LayoutParams params= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin =8;
        params.bottomMargin=8;

        currentField.setLayoutParams(params);

        //Input
        final EditText edtPass = new EditText(currentField.getContext());
        edtPass.setInputType( getInputType(field.getData_type()) );
        edtPass.setPadding(25,50,25,10);
        edtPass.setTextColor(getResources().getColor(R.color.grey));
        edtPass.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        edtPass.setHint(field.getLabel());
        edtPass.setBackgroundColor(0x00000000);
        edtPass.setId(Integer.parseInt(field.getId()));

        currentField.addView(edtPass);

        return currentField;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private int getInputType(String remoteType){

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

}