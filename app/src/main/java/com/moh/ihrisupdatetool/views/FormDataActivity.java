package com.moh.ihrisupdatetool.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.google.gson.JsonObject;
import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.db.entities.FormEntity;
import com.moh.ihrisupdatetool.utils.AppData;
import com.moh.ihrisupdatetool.utils.AppUtils;
import com.moh.ihrisupdatetool.utils.UIHelper;
import com.moh.ihrisupdatetool.viewmodels.FormsViewModel;
import com.moh.ihrisupdatetool.viewmodels.SubmissionViewModel;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import dagger.hilt.android.AndroidEntryPoint;

import static com.moh.ihrisupdatetool.utils.AppConstants.SELECTED_FORM;

@AndroidEntryPoint
public class FormDataActivity extends DataBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        uiHelper = new UIHelper(this);
        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd ", Locale.US);

         userId = String.valueOf(AppData.userId);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_fields);

        try{
            initAcitvity();
        }catch(Exception ex){
            ex.printStackTrace();
            uiHelper.showDialog(ex.getMessage());
        }
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

        submitBtn.setOnClickListener(v -> {
            onSubmitClicked();
        });

        getFormFields(); //form data activity
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