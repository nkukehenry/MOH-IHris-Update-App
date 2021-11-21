package com.moh.ihrisupdatetool.views;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.collect.Range;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import static com.moh.ihrisupdatetool.utils.AppConstants.SELECTED_FORM;

@AndroidEntryPoint
public class FormDataActivity extends AppCompatActivity {

    private final String TAG = FormDataActivity.class.getSimpleName();

    private FormsViewModel formsViewModel;
    private FormEntity selectedForm;
    private LinearLayout dynamicFieldsWrapper, formNavigator;
    private JsonObject postDataObject;
    private SubmissionViewModel submissionViewModel;
    private List<FormField> formFields;
    private Button submitBtn, nextFormButton, prevFormButton;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private FormField currentImageField;
    private Boolean hasNextForm = true, hasPrevForm = true;
    private UIHelper uiHelper;
    private TextView formTitle;
    private CommunityWorkerEntity selectedCommWorker;
    private MinistryWorkerEntity selectedMinWorker;
    private int exitCounter=0;
    private SimpleDateFormat simpleDateFormat;

    //@Inject

    private AwesomeValidation awesomeValidation;

    @Inject
    AwesomeCustomValidators customValidators;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        uiHelper = new UIHelper(this);
        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd ", Locale.US);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_fields);
        try{
            initAcitvity();
        }catch(Exception ex){
            uiHelper.showDialog(ex.getMessage());
        }
    }

    private void initAcitvity(){

        String userId = String.valueOf(AppData.userId);

        postDataObject = new JsonObject();
        postDataObject.addProperty("reference", AppUtils.getRandomString(11)+userId);
        postDataObject.addProperty("user_id",userId);

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

        formsViewModel = new ViewModelProvider(this).get(FormsViewModel.class);
        submissionViewModel = new ViewModelProvider(this).get(SubmissionViewModel.class);
        submitBtn = findViewById(R.id.submitBtn);

        submitBtn.setOnClickListener(v -> {
            onSubmitClicked();
        });

        getFormFields();
    }

    private void prefillHealthWorkerValues() {

        //worker Type is as per selection on mainactivity
        String workerType = (AppData.isCommunityWorker)?"chw":"mhw";
        postDataObject.addProperty("hw_type",workerType);

        List<FormEntity> forms = AppData.allForms;
        int currentFormIndex = forms.indexOf(selectedForm);
        int lastIndex = forms.size() - 1;

        Log.e(TAG,"Selected form: " + currentFormIndex);

        if(currentFormIndex == 0) {
            prevFormButton.setEnabled(false);
        }
        else if(currentFormIndex == lastIndex ){
            prevFormButton.setEnabled(true);
            nextFormButton.setEnabled(false);
        }
        else{
            prevFormButton.setEnabled(true);
        }


        if (selectedCommWorker != null) {

            postDataObject.addProperty("surname", selectedCommWorker.getSurname());
            postDataObject.addProperty("othername", selectedCommWorker.getOthername());
            postDataObject.addProperty("firstname", selectedCommWorker.getFirstname());
            postDataObject.addProperty("ihris_pid", selectedCommWorker.getPersonId());
            postDataObject.addProperty("primary_mobile_number", selectedCommWorker.getMobile());
        }
        else if (selectedMinWorker != null) {

            postDataObject.addProperty("surname", selectedMinWorker.getSurname());
            postDataObject.addProperty("othername", selectedMinWorker.getOthername());
            postDataObject.addProperty("firstname", selectedMinWorker.getFirstname());
            postDataObject.addProperty("ihris_pid", selectedMinWorker.getPersonId());
            postDataObject.addProperty("primary_mobile_number", selectedMinWorker.getPhone());
        }

    }

    private void getFormFields() {

        awesomeValidation = null;

        uiHelper.showLoader();
        formsViewModel.getFormFields(selectedForm.getId()).observe(this, formsFieldsResponse -> {

                    dynamicFieldsWrapper.removeAllViews();

                    if(formsFieldsResponse!=null && !formsFieldsResponse.isEmpty()) {

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                uiHelper.hideLoader();
                            }
                        }, 1000);

                        prefillHealthWorkerValues();//attempt to populate worker's info

                        //instantiate awesome again
                        awesomeValidation = new AwesomeValidation(BASIC);

                        formFields = formsFieldsResponse;
                        submitBtn.setEnabled(false);
                        formTitle.setText(selectedForm.getForm_title());

                        try {
                            for (FormField field : formsFieldsResponse) {

                                FormFieldType fieldType = AppUtils.InputType(field.getData_type());

                                if (!field.getIs_visible()) return;

                                switch (fieldType) {
                                    case SPINNER_BASED_FIELD:
                                        renderSpinnerBasedField(field);
                                        break;
                                    case DATE_FIELD:
                                        renderDateField(field);
                                        break;
                                    case IMAGE_FIELD:
                                        renderImageField(field);
                                        break;
                                    case TEXT_AUTOCOMPLETE_FIELD:
                                        renderTextAutoCompleteField(field);
                                        break;
                                    default:
                                        renderTextBasedField(field);
                                        break;
                                }
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            if (formsFieldsResponse != null)
                                updateUIOnNavigation();
                        }
                    }

                });
    }

    private void renderTextBasedField(FormField field) {

        // Create EditText
        LinearLayout currentField = new LinearLayout(this);

        //Params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 8;
        params.bottomMargin = 8;

        currentField.setLayoutParams(params);

        currentField.setOrientation(LinearLayout.VERTICAL);

        TextView fieldLabel = new TextView(this);
        fieldLabel.setText(field.getLabel());
        currentField.addView(fieldLabel);

        //Input
        final EditText edtPass = new EditText(currentField.getContext());
        edtPass.setInputType(AppUtils.getInputTypeClass(field.getData_format()));
        edtPass.setPadding(25, 10, 25, 10);
        edtPass.setTextColor(getResources().getColor(R.color.grey));
        edtPass.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        edtPass.setEnabled(!field.getIs_disabled());
        edtPass.setBackgroundColor(0x00000000);
        edtPass.setId(Integer.parseInt(field.getId()));

        JsonElement element = postDataObject.get(field.getForm_field());

        if (element != null) {
            try {
                String elemValue = element.getAsString();
                edtPass.setText(elemValue.toString());
            }catch(Exception ex){
                Log.e(TAG,field.getForm_field() +" value not set");
                ex.printStackTrace();
            }
        }


        currentField.addView(edtPass);

        View view = new View(this);
        view.setBackgroundColor(getResources().getColor(R.color.grey));
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        currentField.addView(view);

        dynamicFieldsWrapper.addView(currentField);

       Integer  lengthConstrait  = field.getDb_constraint();

        if(field.getIs_disabled()) return; // is disabled, don't valdiate anything on it

        if(field.getData_format().equals( InputType.TYPE_CLASS_TEXT ))
        awesomeValidation.addValidation(this, Integer.parseInt(field.getId()), "[a-zA-Z\\s]+",R.string.invalid_characters );

        if(field.getIs_required())
        awesomeValidation.addValidation(this, Integer.parseInt(field.getId()), RegexTemplate.NOT_EMPTY, R.string.not_empty);

        if(lengthConstrait > 0)
        awesomeValidation.addValidation(this, Integer.parseInt(field.getId()),customValidators.maxLengthValidator(lengthConstrait),R.string.too_short);

    }

    private void renderImageField(FormField field) {

        // Create EditText
        TextInputLayout currentField = new TextInputLayout(this);

        //Params
        TextInputLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 8;
        params.bottomMargin = 8;

        currentField.setLayoutParams(params);

        //Input
        final TextView imageHolder = new TextView(currentField.getContext());

        imageHolder.setPadding(25, 25, 25, 35);
        imageHolder.setTextColor(getResources().getColor(R.color.grey));
        imageHolder.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageHolder.setBackgroundColor(0x00000000);
        imageHolder.setId(Integer.parseInt(field.getId()));
        imageHolder.setText("Choose " + field.getLabel());

        imageHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureIntent(field);
            }
        });

        ImageView imageView = new ImageView(currentField.getContext());
        imageView.setId(Integer.parseInt(field.getId()) * 300);
        imageView.setVisibility(View.GONE);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setPadding(10, 30, 10, 10);

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

    private void renderSpinnerBasedField(FormField field) {

        Gson gson = new Gson();
        Type targetType = new TypeToken<List<Map<String, String>>>() {
        }.getType();
        List<Map<String, String>> options = gson.fromJson(field.getDefault_data(), targetType);

        List<String> listOptions = new ArrayList<String>();

        /**
         * Extract fields , get there values (key->value)
         * Consider value in position 1 for if there values are >1 in count
         * Else take position 0 if values count ==1
         */
        if (options != null) {

            for (Map<String, String> option : options) {

                int count = 0;
                for (String value : option.values()) {
                    if ((count == 1 && option.values().size() > 1) || option.values().size() == 1)//use  only second value
                        listOptions.add(value);
                    count++;
                }
            }
        }

        final List<String> spinnerOptions = listOptions;

        if (spinnerOptions.isEmpty())
            return;

        LinearLayout currentField = new LinearLayout(this);

        //Params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 8;
        params.bottomMargin = 8;

        currentField.setLayoutParams(params);
        currentField.setOrientation(LinearLayout.VERTICAL);

        TextView spinnerLabel = new TextView(this);
        spinnerLabel.setText(field.getLabel());
        //spinnerOptions.add();
        //Create spinner
        Spinner spinner = new Spinner(FormDataActivity.this);
        //Attach adapter to spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FormDataActivity.this, R.layout.spinner_item, spinnerOptions);
        spinner.setAdapter(adapter);
        spinner.setLayoutParams(params);

        //Attempt to set default item
        JsonElement element = postDataObject.get(field.getForm_field());

        if (element != null) {
            try {
                String elemValue = element.getAsString();
                int selectedValue = adapter.getPosition(elemValue);
                spinner.setSelection(selectedValue);
            } catch (Exception ex) {
                Log.e(TAG, field.getForm_field() + " value not set");
                ex.printStackTrace();
            }
        }


                /**
                 * Hook a an item selected handler for that particular input
                 */
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * Postions should always be less than list size,else outof range
                 */
                if (position < options.size()) {
                    postDataObject.addProperty(field.getForm_field(), spinnerOptions.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        currentField.addView(spinnerLabel);
        currentField.addView(spinner);

        View view = new View(this);
        view.setBackgroundColor(getResources().getColor(R.color.grey));
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        currentField.addView(view);


        dynamicFieldsWrapper.addView(currentField);
    }

    private void renderDateField(FormField field) {

        LinearLayout currentField = new LinearLayout(this);

        //Params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 8;
        params.bottomMargin = 8;

        currentField.setLayoutParams(params);
        currentField.setOrientation(LinearLayout.VERTICAL);

        TextView fieldLabel = new TextView(this);
        fieldLabel.setText(field.getLabel());
        //spinnerOptions.add();
        //Create spinner
        TextView dateField = new TextView(FormDataActivity.this);
        dateField.setPadding(25, 10, 25, 10);
        dateField.setTextColor(getResources().getColor(R.color.grey));
        dateField.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //edtPass.setHint(field.getLabel());
        dateField.setBackgroundColor(0x00000000);
        dateField.setId(Integer.parseInt(field.getId()));
        //Attach adapter to spinner
        dateField.setLayoutParams(params);
        dateField.setEnabled(false);

        //Attempt to set default item
        JsonElement element = postDataObject.get(field.getForm_field());

        currentField.addView(fieldLabel);
        currentField.addView(dateField);

        View dividerView = new View(this);
        dividerView.setBackgroundColor(getResources().getColor(R.color.grey));
        dividerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        currentField.addView(dividerView);

        DatePickerDialog.OnDateSetListener callback = (view, year, monthOfYear, dayOfMonth)
                -> {
            Log.e(TAG, String.valueOf(year));
            Log.e(TAG, String.valueOf(monthOfYear));

            Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
            String dateData = simpleDateFormat.format(calendar.getTime());

           // String dateData = String.format("%s%s%s%s%s",year,"/", monthOfYear,"/",dayOfMonth);
            postDataObject.addProperty(field.getForm_field(),dateData);
            dateField.setText(dateData);
        };

        DatePickerDialog datePicker =  new SpinnerDatePickerDialogBuilder()
                .context(this)
                .callback(callback)
                //.spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                //.defaultDate(1990, 0, 1)
                //.maxDate(2021, 0, 1)
                // .minDate(2000, 0, 1)
                .build();


        currentField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show();
            }
        });

        dynamicFieldsWrapper.addView(currentField);

        if(field.getIs_required())
        awesomeValidation.addValidation(this, Integer.parseInt(field.getId()), RegexTemplate.NOT_EMPTY, R.string.not_empty);

    }


    private void renderTextAutoCompleteField(FormField field) {

        Gson gson = new Gson();
        Type targetType = new TypeToken<List<Map<String, String>>>() {
        }.getType();
        List<Map<String, String>> options = gson.fromJson(field.getDefault_data(), targetType);

        List<String> listOptions = new ArrayList<String>();

        /**
         * Extract fields , get there values (key->value)
         * Consider value in position 1 for if there values are >1 in count
         * Else take position 0 if values count ==1
         */
        if (options != null) {

            for (Map<String, String> option : options) {

                int count = 0;
                for (String value : option.values()) {
                    if ((count == 1 && option.values().size() > 1) || option.values().size() == 1)//use  only second value
                        listOptions.add(value);
                    count++;
                }
            }
        }

        final List<String> spinnerOptions = listOptions;

        if (spinnerOptions.isEmpty())
            return;

        LinearLayout currentField = new LinearLayout(this);

        //Params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 8;
        params.bottomMargin = 8;

        currentField.setLayoutParams(params);
        currentField.setOrientation(LinearLayout.VERTICAL);

        TextView spinnerLabel = new TextView(this);
        spinnerLabel.setText(field.getLabel());
        //spinnerOptions.add();
        //Create spinner
        AutoCompleteTextView autoCompleteField = new AutoCompleteTextView(FormDataActivity.this);
        autoCompleteField.setId(Integer.parseInt(field.getId()));
        //Attach adapter to spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FormDataActivity.this, R.layout.spinner_item, spinnerOptions);
        autoCompleteField.setAdapter(adapter);
        autoCompleteField.setLayoutParams(params);

        //Attempt to set default item
        JsonElement element = postDataObject.get(field.getForm_field());

        if (element != null) {
            try {
                String elemValue = element.getAsString();
                autoCompleteField.setText(elemValue.toString());
            }catch(Exception ex){
                Log.e(TAG,field.getForm_field() +" value not set");
                ex.printStackTrace();
            }
        }

        /**
         * Hook a an item selected handler for that particular input
         */
        autoCompleteField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * Postions should always be less than list size,else outof range
                 */
                if (position < options.size()) {
                    postDataObject.addProperty(field.getForm_field(), spinnerOptions.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        currentField.addView(spinnerLabel);
        currentField.addView(autoCompleteField);
        dynamicFieldsWrapper.addView(currentField);
        awesomeValidation.addValidation(this, Integer.parseInt(field.getId()), RegexTemplate.NOT_EMPTY, R.string.not_empty);

    }

    private void preparePostData() {

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

    private void cacheData() {
        preparePostData();
        submissionViewModel.cacheData(postDataObject);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            try {

                System.out.println( data.getExtras() );

                Bitmap photo = (Bitmap) data.getExtras().get("data");

                ImageView imageView = findViewById(Integer.parseInt(currentImageField.getId()) * 300);
                imageView.setImageBitmap(photo);
                imageView.setVisibility(View.VISIBLE);

                String encodedImage = AppUtils.bitmapTobase64(photo);
                postDataObject.addProperty(currentImageField.getForm_field(), encodedImage);
                TextView imageLabel = findViewById(Integer.parseInt(currentImageField.getId()));
                imageLabel.setText(currentImageField.getLabel() + ": Attached Successfully");

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
        if (hasNextForm) {
            nextFormButton.setEnabled(true);
        } else {
            nextFormButton.setEnabled(false);
        }

        //Previous btn hide/show
        if (hasPrevForm) {
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

        preparePostData();
        uiHelper.showLoader("Submitting data...");
        submissionViewModel.postData(postDataObject).observe(this, submissionResponse -> {
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

        });
    }

    public void onNextClick(View view) {

      if (!awesomeValidation.validate()) return;

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

        if (!awesomeValidation.validate()) return;

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
            Intent intent = new Intent(this,FormsActivity.class);
            startActivity(intent);
            finish();
        }
    }

}