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
import java.util.HashSet;
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
    private List<Integer> imageFields = new ArrayList<>();
    private List<Integer> formsTracker = new ArrayList<>();

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

            setPostDataField("birth_date",selectedCommWorker.getBirth_date());
            setPostDataField("gender",    selectedCommWorker.getGender());
            setPostDataField("job",       selectedCommWorker.getJob());
            setPostDataField("facility",  selectedCommWorker.getFacility());
            setPostDataField("surname",   selectedCommWorker.getSurname());
            setPostDataField("othername", selectedCommWorker.getOthername());
            setPostDataField("firstname", selectedCommWorker.getFirstname());
            setPostDataField("ihris_pid", selectedCommWorker.getPersonId());
            setPostDataField("primary_mobile_number", selectedCommWorker.getMobile());
            setPostDataField("national_id", selectedCommWorker.getNational_id());

        }
        else if (selectedMinWorker != null) {

            setPostDataField("birth_date",selectedMinWorker.getBirth_date());
            setPostDataField("gender",    selectedMinWorker.getGender());
            setPostDataField("job",       selectedMinWorker.getJob());
            setPostDataField("facility",  selectedMinWorker.getFacility());
            setPostDataField("surname",   selectedMinWorker.getSurname());
            setPostDataField("othername", selectedMinWorker.getOthername());
            setPostDataField("firstname", selectedMinWorker.getFirstname());
            setPostDataField("ihris_pid", selectedMinWorker.getPersonId());
            setPostDataField("primary_mobile_number", selectedMinWorker.getPhone());
            setPostDataField("national_id", selectedMinWorker.getNational_id());
        }

    }

    private void setPostDataField(String dataKey,String dataValue){

        if(dataValue!=null && !dataValue.isEmpty())
         postDataObject.addProperty(dataKey,dataValue);

    }

    private void getFormFields() {

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

                                Log.e(TAG, String.valueOf(field));

                                FormFieldType fieldType = AppUtils.InputType(field.getData_type());

                                if (!field.getIs_visible()) continue;

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
                            Log.e(TAG,"Exception:: "+ex.getMessage());
                            ex.printStackTrace();
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

        JsonElement element = postDataObject.get(field.getForm_field());

        if (element != null) {
            try {
                String elemValue = element.getAsString();
                EditText currentPass = findViewById(Integer.parseInt(field.getId()));

                if(!elemValue.isEmpty())
                  currentPass.setText(elemValue);

            }catch(Exception ex){
               // Log.e(TAG,field.getForm_field() +" value not set");
                //ex.printStackTrace();
            }
        }

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

        imageHolder.setPadding(25, 10, 25, 10);
        imageHolder.setTextColor(getResources().getColor(R.color.grey));
        imageHolder.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageHolder.setBackgroundColor(0x00000000);
        imageHolder.setId(Integer.parseInt(field.getId()));
        imageHolder.setText("Choose " + field.getLabel());

        if( !isTrackedImage(Integer.parseInt(field.getId())) && field.getIs_visible() );
            imageFields.add(Integer.parseInt(field.getId())); //track Image fields

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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(field);
            }
        });

         JsonElement element = postDataObject.get(field.getForm_field());

        if (element != null) {
            Log.e(TAG,field.getForm_field() +" image set");
            try {
                String elemValue = element.getAsString();
                Log.e(TAG,elemValue);
                Bitmap bitmap = AppUtils.base64ToBitmap(elemValue);

                imageView.setImageBitmap(AppUtils.resizeBitmap(bitmap));
                imageView.setVisibility(View.VISIBLE);

            } catch (Exception ex) {
                // Log.e(TAG,field.getForm_field() +" value not set");
                ex.printStackTrace();
            }
        }

        currentField.addView(imageHolder);
        currentField.addView(imageView);

        dynamicFieldsWrapper.addView(currentField);
    }

    private Boolean isTrackedImage(int fieldId) {
        Boolean isTracked = false;
        for(int i=0; i<imageFields.size();i++){

            if(imageFields.get(i) == fieldId)
                isTracked= true;
        }
        return isTracked;
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

        if (spinnerOptions.isEmpty()) return;

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


        currentField.addView(spinnerLabel);
        currentField.addView(spinner);

        View view = new View(this);
        view.setBackgroundColor(getResources().getColor(R.color.grey));
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        currentField.addView(view);

        dynamicFieldsWrapper.addView(currentField);

        //Attempt to set default item
        JsonElement element = postDataObject.get(field.getForm_field());

        if (element != null) {
            try {
                Log.e(TAG, field.getForm_field() + "Spinner Value");
                String elemValue = element.getAsString();

                int selectedIndex = getAutoSelectValueIndex(spinner,elemValue);
                Log.e(TAG, " value :: "+elemValue);

                adapter.notifyDataSetChanged();

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


    }

    private void renderDateField(FormField field) {

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        LinearLayout currentField = new LinearLayout(this);

        //Params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin    = 8;
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

        int maxValue = currentYear+field.getMax_value();
        int minValue = currentYear+field.getMin_value();

        DatePickerDialog datePicker =  new SpinnerDatePickerDialogBuilder()
                .context(this)
                .callback(callback)
                //.spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(minValue, 0, 1)
                .maxDate(maxValue, 11, 1)
                .minDate(minValue, 0, 1)
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



        JsonElement element = postDataObject.get(field.getForm_field());

        if (element != null) {
            try {
                String elemValue = element.getAsString();

                if(!elemValue.isEmpty())
                    dateField.setText(elemValue);

            }catch(Exception ex){
                // Log.e(TAG,field.getForm_field() +" value not set");
                //ex.printStackTrace();
            }
        }


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

        currentField.addView(spinnerLabel);
        currentField.addView(autoCompleteField);
        dynamicFieldsWrapper.addView(currentField);
        awesomeValidation.addValidation(this, Integer.parseInt(field.getId()), RegexTemplate.NOT_EMPTY, R.string.not_empty);


        //Attempt to set default item
        JsonElement element = postDataObject.get(field.getForm_field());

        if (element != null) {
            try {
                String elemValue = element.getAsString();
                int selectedIndex = adapter.getPosition(elemValue);

                Log.e(TAG,"Selected Field");
                Log.e(TAG, String.valueOf(field));

                Log.e(TAG,"Selected Value");
                Log.e(TAG,spinnerOptions.get(selectedIndex));
                autoCompleteField.setText(spinnerOptions.get(selectedIndex));
               AutoCompleteTextView thisTextView = findViewById(Integer.parseInt(field.getId()));
               thisTextView.setText( spinnerOptions.get(selectedIndex) );

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


    }

    private int getAutoSelectValueIndex(Spinner spinner, String myString) {
        int index = 0;
        for(int i = 0; i < spinner.getCount(); i++){
            if(spinner.getItemAtPosition(i).toString().equals(myString)){
                index = i;
                break;
            }
        }
        return index;
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
        //submissionViewModel.cacheData(postDataObject);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            try {

                Bitmap photo = (Bitmap) data.getExtras().get("data");

                ImageView imageView = findViewById(Integer.parseInt(currentImageField.getId()) * 300);
                imageView.setImageBitmap(AppUtils.resizeBitmap(photo));
                imageView.setVisibility(View.VISIBLE);

                String encodedImage = AppUtils.bitmapTobase64(photo);
                postDataObject.addProperty(currentImageField.getForm_field(), encodedImage);

                TextView imageLabel = findViewById(Integer.parseInt(currentImageField.getId()));
                imageLabel.setText(currentImageField.getLabel() + ": Attached Successfully");

                for(int i=0; i<imageFields.size(); i++){

                    if( imageFields.get(i) == Integer.parseInt(currentImageField.getId()) )
                     imageFields.remove(i);
                    Log.e(TAG, "Image Index "+i);
                }
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

        if(  imageFields.size() > 0) {//check if all image fields have been satifiesd
            //Toast.makeText(this, "Provide all images", Toast.LENGTH_LONG).show();
            //return;
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