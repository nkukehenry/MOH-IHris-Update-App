package com.moh.ihrisupdatetool.views;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
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
import com.moh.ihrisupdatetool.utils.AppData;
import com.moh.ihrisupdatetool.utils.AppUtils;
import com.moh.ihrisupdatetool.utils.UIHelper;
import com.moh.ihrisupdatetool.viewmodels.FormsViewModel;
import com.moh.ihrisupdatetool.viewmodels.SubmissionViewModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

import static com.moh.ihrisupdatetool.utils.AppConstants.SELECTED_FORM;

@AndroidEntryPoint
public class FormDataActivity extends AppCompatActivity {

    private FormsViewModel formsViewModel;
    private FormEntity selectedForm;
    private LinearLayout dynamicFieldsWrapper,formNavigator;
    private JsonObject postDataObject;
    private SubmissionViewModel submissionViewModel;
    private List<FormField> formFields;
    private Button submitBtn,nextFormButton,prevFormButton;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private FormField currentImageField;
    private Boolean hasNextForm = true,hasPrevForm = true;
    private UIHelper uiHelper;
    private TextView formTitle;
    private CommunityWorkerEntity selectedCommWorker;
    private MinistryWorkerEntity selectedMinWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        uiHelper = new UIHelper(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_fields);

        postDataObject = new JsonObject();
        postDataObject.addProperty("reference",AppUtils.getRandomString(12));

        nextFormButton = findViewById(R.id.nextFormBtn);
        prevFormButton = findViewById(R.id.previousFormBtn);
        formNavigator = findViewById(R.id.formNavigator);
        dynamicFieldsWrapper  = findViewById(R.id.dynamicFieldsWrapper);
        formTitle = findViewById(R.id.formTitle);
        prevFormButton.setEnabled(false);

        Bundle bundle; bundle = getIntent().getExtras();
        selectedForm   = (FormEntity) bundle.get(SELECTED_FORM);

        selectedCommWorker = AppData.selectedCommunityWorker;
        selectedMinWorker  = AppData.selectedMinistryWorker;

        prefillIndividulValues();

        formsViewModel = new ViewModelProvider(this).get(FormsViewModel.class);
        submissionViewModel =  new ViewModelProvider(this).get(SubmissionViewModel.class);
        submitBtn = findViewById(R.id.submitBtn);

        submitBtn.setOnClickListener(v -> {
            onSubmitClicked();
        });

        initObservers();
        getFormFields();

    }

    private void prefillIndividulValues(){

        if(selectedCommWorker!=null){

            postDataObject.addProperty("surname",selectedCommWorker.getSurname());
            postDataObject.addProperty("othername",selectedCommWorker.getOthername());
            postDataObject.addProperty("firstname",selectedCommWorker.getFirstname());
            postDataObject.addProperty("ihris_pid",selectedCommWorker.getPersonId());
            postDataObject.addProperty("primary_mobile_number",selectedCommWorker.getMobile());
        }
        else if(selectedMinWorker!=null){
            postDataObject.addProperty("surname",selectedMinWorker.getSurname());
            postDataObject.addProperty("othername",selectedMinWorker.getOthername());
            postDataObject.addProperty("firstname",selectedMinWorker.getFirstname());
            postDataObject.addProperty("ihris_pid",selectedMinWorker.getPersonId());
            postDataObject.addProperty("primary_mobile_number",selectedMinWorker.getPhone());
        }

    }

    private void initObservers(){

        formsViewModel.observerFormFieldsResponse().observe(this, formsFieldsResponse -> {

            uiHelper.hideLoader();
            dynamicFieldsWrapper.removeAllViews();
            formFields = formsFieldsResponse;
            submitBtn.setEnabled(false);
            formTitle.setText(selectedForm.getForm_title());

            try {
                for (FormField field : formsFieldsResponse) {

                    FormFieldType fieldType = AppUtils.InputType(field.getData_type());

                    switch (fieldType) {
                        case SPINNER_BASED_FIELD:
                            renderSpinnerBasedField(field);
                            break;
                        case IMAGE_FIELD:
                            renderImageField(field);
                            break;
                        default:
                            renderTextBasedField(field);
                            break;
                    }
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }

        });

        //submission
        submissionViewModel.observeResonse().observe( this,submissionResponse->{
           try {
               uiHelper.hideLoader();
               uiHelper.showDialog("Entry finished successfully");
           }catch (Exception ex){
                ex.printStackTrace();
           }finally {
               Intent intent = new Intent(this, MainActivity.class);
               startActivity(intent);
               finish();
           }

        });
    }

    private void getFormFields() {
        uiHelper.showLoader();
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
        edtPass.setInputType( AppUtils.getInputTypeClass(field.getData_type()) );
        edtPass.setPadding(25,100,25,10);
        edtPass.setTextColor(getResources().getColor(R.color.grey));
        edtPass.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        edtPass.setHint(field.getLabel());
        edtPass.setBackgroundColor(0x00000000);
        edtPass.setId(Integer.parseInt(field.getId()));

        JsonElement element = postDataObject.get(field.getForm_field());

        if( element !=null) {
            String elemValue = element.getAsString();
            edtPass.setText(elemValue.toString());
        }

        currentField.addView(edtPass);

        dynamicFieldsWrapper.addView(currentField);
    }

    private void renderImageField(FormField field){

        // Create EditText
        TextInputLayout currentField = new TextInputLayout(this);

        //Params
        TextInputLayout.LayoutParams params= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin =8;
        params.bottomMargin=8;

        currentField.setLayoutParams(params);

        //Input
        final TextView imageHolder = new TextView(currentField.getContext());

        imageHolder.setPadding(25,25,25,25);
        imageHolder.setTextColor(getResources().getColor(R.color.grey));
        imageHolder.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageHolder.setBackgroundColor(0x00000000);
        imageHolder.setId(Integer.parseInt(field.getId()));
        imageHolder.setText("Choose "+field.getLabel());

        imageHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureIntent(field);
            }
        });

        ImageView imageView= new ImageView(currentField.getContext());
        imageView.setId(Integer.parseInt(field.getId())*300);
        imageView.setVisibility(View.GONE);
        imageView.setLayoutParams( new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setPadding(10,30,10,10);

        currentField.addView(imageHolder);
        currentField.addView(imageView);

        dynamicFieldsWrapper.addView(currentField);
    }

    private void dispatchTakePictureIntent(FormField field) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            this.currentImageField = field;
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    private void renderSpinnerBasedField(FormField field){

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
        //Create spinner
        Spinner spinner = new Spinner(FormDataActivity.this);
        //Attach adapter to spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FormDataActivity.this,R.layout.spinner_item,spinnerOptions);
        spinner.setAdapter(adapter);
        spinner.setLayoutParams(params);

        /**
         * Hook a an item selected handler for that particular input
         */
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * Postions should always be less than list size,else outof range
                 */
                if( position < options.size()) {

                    System.out.println(spinnerOptions.get(position));
                    System.out.println(options.get(position));
                    postDataObject.addProperty(field.getForm_field(), spinnerOptions.get(position));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        currentField.addView(spinner);
        dynamicFieldsWrapper.addView(currentField);
    }

    private void preparePostData(){

        try {
            for (FormField field : formFields) {

                FormFieldType fieldType = AppUtils.InputType(field.getData_type());

                if (fieldType.equals(FormFieldType.TEXT_BASED_FIELD)) {
                    EditText textInput = findViewById(Integer.parseInt(field.getId()));
                    postDataObject.addProperty(field.getForm_field(), textInput.getText().toString());
                }

            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void cacheData(){
        preparePostData();
        submissionViewModel.cacheData(postDataObject);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            try {

                Bitmap photo = (Bitmap) data.getExtras().get("data");

                ImageView imageView = findViewById(Integer.parseInt(currentImageField.getId())*300);
                imageView.setImageBitmap(photo);
                imageView.setVisibility(View.VISIBLE);

                String encodedImage = AppUtils.bitmapTobase64(photo);
                postDataObject.addProperty(currentImageField.getForm_field(),encodedImage);
                TextView imageLabel = findViewById(Integer.parseInt(currentImageField.getId()));
                imageLabel.setText(currentImageField.getLabel() + ": Attached Successfully");

                currentImageField = null;

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void onBackPressed() {

      /*  Intent intent = new Intent(this, FormsActivity.class);
        startActivity(intent);
        finish();
       */
        uiHelper.showDialog("Please complete this survey before attempting to close");
    }

    private void updateUIOnNavigation(){

        List<FormEntity> forms = AppData.allForms;
        int currentFormIndex = forms.indexOf(selectedForm);

        //submit enabled on last one
        if(currentFormIndex == (forms.size() -1) ) {
            submitBtn.setEnabled(true);
        }else{
            submitBtn.setEnabled(false);
        }

        //Next btn
        if( hasNextForm){
            nextFormButton.setEnabled(true);
        }else{
            nextFormButton.setEnabled(false);
        }

        //Previous btn hide/show
        if(hasPrevForm){
            prevFormButton.setEnabled(true);
        }else {
            prevFormButton.setEnabled(false);
        }



    }

    private void onSubmitClicked() {
        preparePostData();
        submissionViewModel.postData(postDataObject);
    }

    public void onNextClick(View view) {

        cacheData();

        List<FormEntity> forms = AppData.allForms;
        int currentFormIndex = forms.indexOf(selectedForm);

        if(currentFormIndex < (forms.size()-1)) {
            selectedForm = forms.get(currentFormIndex + 1);
            hasNextForm  = true;
            hasPrevForm  = true;
            getFormFields();
        }else {
            hasNextForm = false;
            hasPrevForm  = true;
        }

        updateUIOnNavigation();
    }

    public void onPrevClick(View view) {
        cacheData();

        List<FormEntity> forms = AppData.allForms;
        int currentFormIndex = forms.indexOf(selectedForm);

        int index = currentFormIndex - 1;

        if(index > -1) {
            selectedForm = forms.get(index);
            hasPrevForm  = true;
            hasNextForm = true;
            getFormFields();
        }else {
            selectedForm = forms.get(0);
            hasPrevForm = false;
            hasNextForm = true;
        }
        updateUIOnNavigation();
    }
}