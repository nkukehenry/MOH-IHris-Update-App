package com.moh.ihrisupdatetool.views;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationHolder;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.basgeekball.awesomevalidation.utility.custom.CustomValidation;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.db.entities.CommunityWorkerEntity;
import com.moh.ihrisupdatetool.db.entities.FormEntity;
import com.moh.ihrisupdatetool.db.entities.FormField;
import com.moh.ihrisupdatetool.db.entities.MinistryWorkerEntity;
import com.moh.ihrisupdatetool.dto.FormFieldType;
import com.moh.ihrisupdatetool.utils.AppConstants;
import com.moh.ihrisupdatetool.utils.AppData;
import com.moh.ihrisupdatetool.utils.AppUtils;
import com.moh.ihrisupdatetool.utils.AwesomeCustomValidators;
import com.moh.ihrisupdatetool.utils.UIHelper;
import com.moh.ihrisupdatetool.viewmodels.FormsViewModel;
import com.moh.ihrisupdatetool.viewmodels.SubmissionViewModel;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import static com.moh.ihrisupdatetool.utils.AppConstants.SELECTED_FORM;

@AndroidEntryPoint
public class FormDataActivity extends DataBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        uiHelper = new UIHelper(this);
        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd ", Locale.US);

        userId = String.valueOf(AppData.session.getUserId());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_fields);

        try{
            initAcitvity();
        }catch(Exception ex){
            ex.printStackTrace();
            uiHelper.showDialog(ex.getMessage());
        }
    }

    public void getFormFields() {

        awesomeValidation = null;

        uiHelper.showLoader();

        formsViewModel.getFormFields(selectedForm.getId()).observe(this, formsFieldsResponse -> {

            dynamicFieldsWrapper.removeAllViews();

            if(formsFieldsResponse!=null && !formsFieldsResponse.isEmpty()) {


                prefillHealthWorkerValues();//attempt to populate worker's info

                //instantiate awesome again
                awesomeValidation = new AwesomeValidation(BASIC);

                formFields = formsFieldsResponse;
                submitBtn.setEnabled(false);
                formTitle.setText(selectedForm.getForm_title());

                try {
                    for (FormField field : formsFieldsResponse) {

                        // Log.e(TAG, String.valueOf(field));

                        FormFieldType fieldType = AppUtils.InputType(field.getData_type());

                        if (!field.getIs_visible()) continue;

                        switch (fieldType) {
                            case SPINNER_BASED_FIELD:
                                try {
                                    renderSpinnerBasedField(field);
                                }catch (Exception exception){}
                                break;
                            case DATE_FIELD:
                                try{
                                    renderDateField(field);
                                }catch (Exception exception){}
                                break;
                            case IMAGE_FIELD:
                                try{
                                    renderImageField(field);
                                }catch (Exception exception){}
                                break;
                            case TEXT_AUTOCOMPLETE_FIELD:
                                try{
                                    renderTextAutoCompleteField(field);
                                }catch (Exception exception){}
                                break;
                            case SIGNATURE_FIELD:
                                try{
                                    renderSignatureField(field);
                                }catch (Exception exception){}
                                break;
                            default:
                                try{
                                    renderTextBasedField(field);
                                }catch (Exception exception){}
                                break;
                        }
                    }

                } catch (Exception ex) {
                    Log.e(TAG,"Exception:: "+ex.getMessage());
                    //ex.printStackTrace();
                } finally {
                    if (formsFieldsResponse != null)
                        updateUIOnNavigation();
                }
            }

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    uiHelper.hideLoader();
                }
            }, 1000);


        });
    }

    private void initAcitvity(){

        nextFormButton = findViewById(R.id.nextFormBtn);
        prevFormButton = findViewById(R.id.previousFormBtn);
        formNavigator  = findViewById(R.id.formNavigator);
        dynamicFieldsWrapper = findViewById(R.id.dynamicFieldsWrapper);
        formTitle = findViewById(R.id.formTitle);

        Bundle bundle;
        bundle = getIntent().getExtras();
        selectedForm = (FormEntity) bundle.get(SELECTED_FORM);

        selectedCommWorker = AppData.selectedCommunityWorker;
        selectedMinWorker = AppData.selectedMinistryWorker;

        formsViewModel      = new ViewModelProvider(this).get(FormsViewModel.class);
        submissionViewModel = new ViewModelProvider(this).get(SubmissionViewModel.class);
        submitBtn = findViewById(R.id.submitBtn);


        if(AppData.isDataUpdate && !isUpdateLoaded){ //if update , just load the data
            loadOldRecord();
        }else{

            if(!AppData.isDataUpdate) {
                postDataObject = new JsonObject();
            }

            postDataObject.addProperty("reference", AppUtils.getRandomString(11)+userId);
        }

        postDataObject.addProperty("app_version", AppConstants.APP_VERSION);

        submitBtn.setOnClickListener(v -> {
            onSubmitClicked();
        });

        getFormFields(); //form data activity
    }

    public void preparePostData() {

        try {
            for (FormField field : formFields) {

                FormFieldType fieldType = AppUtils.InputType(field.getData_type());

                if (fieldType.equals(FormFieldType.TEXT_BASED_FIELD)) {

                    EditText textInput = findViewById(Integer.parseInt(field.getId()));
                    if(textInput!=null)
                        postDataObject.addProperty(field.getForm_field(), textInput.getText().toString());

                }else if (fieldType.equals(FormFieldType.TEXT_AUTOCOMPLETE_FIELD)) {

                    AutoCompleteTextView autoCompleteTextView = findViewById(Integer.parseInt(field.getId()));
                    if(autoCompleteTextView!=null)
                        postDataObject.addProperty(field.getForm_field(), autoCompleteTextView.getText().toString());

                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void cacheData() {
        preparePostData();
        //submissionViewModel.cacheData(postDataObject);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            try {

                Bitmap photo = (Bitmap) data.getExtras().get("data");

                LinearLayout wrapper = findViewById(Integer.parseInt(currentImageField.getId())*112);
                wrapper.setBackground(null);
                wrapper.setPadding(0,5,0,5);

                ImageView imageView = findViewById(Integer.parseInt(currentImageField.getId()) * 300);
                imageView.setImageBitmap(AppUtils.resizeBitmap(photo));
                imageView.setVisibility(View.VISIBLE);
                imageView.setPadding(0,0,0,0);
                imageView.setAdjustViewBounds(true);

                String encodedImage = AppUtils.bitmapTobase64(photo);
                postDataObject.addProperty(currentImageField.getForm_field(), encodedImage);

                Log.e(TAG, "Images  "+imageFields.size());

                currentImageField = null;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUIOnNavigation() {

        List<FormEntity> forms = AppData.allForms;
        int currentFormIndex = forms.indexOf(selectedForm);
        int lastIndex = forms.size() - 1;

        //Next btn
        if (hasNextForm && currentFormIndex != lastIndex) {
            nextFormButton.setEnabled(true);
        } else {
            nextFormButton.setEnabled(false);
        }

        //Previous btn hide/show
        if (hasPrevForm && currentFormIndex!=0) {
            prevFormButton.setEnabled(true);
        } else {
            prevFormButton.setEnabled(false);
        }

        //submit enabled on last one
        if (currentFormIndex == lastIndex) {
            submitBtn.setEnabled(true);
        } else {
            submitBtn.setEnabled(false);
        }

    }


    private void onSubmitClicked() {

        if (!awesomeValidation.validate()) return;

        if(  !validateImagesRequired(false) ) {
            //check if all image fields have been satifies
            Toast.makeText(this, "Provide the required "+imageFields.size()+" images ", Toast.LENGTH_LONG).show();
            return;
        }

        preparePostData();
        uiHelper.showLoader("Submitting data...");

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                goHome();
            }
        }, 5000);

        submissionViewModel.postData(postDataObject).observe(this, submissionResponse -> {
            goHome();
        });

    }

    private void goHome(){

        try {
            uiHelper.hideLoader();

            Toast.makeText(this, "Data submitted successfully", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void onNextClick(View view) {

        if (!awesomeValidation.validate()) {
            Toast.makeText(this, "Some fields contain invalid input", Toast.LENGTH_SHORT).show();
            return;
        }

        if(  !validateImagesRequired(true) ) {
            //check if all image fields have been satifies
            Toast.makeText(this, "Provide the required  image(s) ", Toast.LENGTH_LONG).show();
            return;
        }

        cacheData();

        List<FormEntity> forms = AppData.allForms;
        int currentFormIndex = forms.indexOf(selectedForm);

        if (currentFormIndex < (forms.size() - 1)) {
            selectedForm = forms.get(currentFormIndex + 1);
            hasNextForm = true;
            hasPrevForm = true;
            getFormFields();
        } else {
            hasNextForm = false;
            hasPrevForm = true;
        }

    }

    public void onPrevClick(View view) {

        // if (!awesomeValidation.validate()) return;

        cacheData();

        List<FormEntity> forms = AppData.allForms;
        int currentFormIndex   = forms.indexOf(selectedForm);

        int index = currentFormIndex - 1;

        if (index > -1) {
            selectedForm = forms.get(index);
            hasPrevForm = true;
            hasNextForm = true;
            getFormFields();
        } else {
            selectedForm = forms.get(0);
            hasPrevForm = false;
            hasNextForm = true;
        }
    }

    private Boolean validateImagesRequired(Boolean isPartial) {
        Boolean allimagesCaptured = true;

        for (Iterator<String> it = imageFields.iterator(); it.hasNext(); ) {
            String current= it.next();
            if ( postDataObject.get(current) == null)
                allimagesCaptured = false;
            else continue;
        }
        return (isPartial && imageFields.size() >1)?true: allimagesCaptured;
    }

    @Override
    public void onBackPressed() {

        if (exitCounter < 1) {
            Toast.makeText(this, "Press back to abandon form", Toast.LENGTH_LONG).show();
            exitCounter++;

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    exitCounter = 0;
                }
            }, 5000);

        } else {
            Intent intent = new Intent(this,PersonSearchActivity.class);
            startActivity(intent);
            finish();
        }
    }

}