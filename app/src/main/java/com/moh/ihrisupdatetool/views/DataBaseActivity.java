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

import androidx.appcompat.app.AppCompatActivity;
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

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import static com.moh.ihrisupdatetool.utils.AppConstants.SELECTED_FORM;

public class DataBaseActivity extends AppCompatActivity {



    final String TAG = FormDataActivity.class.getSimpleName();

    FormsViewModel formsViewModel;
    FormEntity selectedForm;
    LinearLayout dynamicFieldsWrapper, formNavigator;
    JsonObject postDataObject;
    SubmissionViewModel submissionViewModel;
    List<FormField> formFields;
    Button submitBtn, nextFormButton, prevFormButton;
    final int REQUEST_IMAGE_CAPTURE = 1;
    FormField currentImageField;
    Boolean hasNextForm = true, hasPrevForm = true;
    UIHelper uiHelper;
    TextView formTitle;
    CommunityWorkerEntity selectedCommWorker;
    MinistryWorkerEntity selectedMinWorker;
    int exitCounter=0;
    SimpleDateFormat simpleDateFormat;
    Set<String> imageFields = new HashSet<>();

    AwesomeValidation awesomeValidation;
    final int inputTopMargin = 15;
    final float inputLabelTextSize = 17f;
    final int imagePadding = 5;
    Boolean isUpdateLoaded=false;
    String userId;

    @Inject
    AwesomeCustomValidators customValidators;



    public void prefillHealthWorkerValues() {

        postDataObject.addProperty("user_id", userId);

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

        if(!AppData.isDataUpdate){
            //worker Type is as per selection on mainactivity
            String workerType = (AppData.isCommunityWorker)?"chw":"mhw";
            postDataObject.addProperty("hw_type",workerType);
        }

        Log.e(TAG,"Worker Here");

        if(AppData.selectedDistrict !=null )
        setPostDataField("district",AppData.selectedDistrict.getDistrictName());

        if (selectedCommWorker != null) {

            Log.e(TAG,selectedCommWorker.toString());

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
            setPostDataField("district", selectedCommWorker.getDistrict());

        }else if (selectedMinWorker != null) {

            Log.e(TAG,selectedMinWorker.toString());

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
            setPostDataField("district", selectedMinWorker.getDistrict());
        }


    }

    public void loadOldRecord(){
        isUpdateLoaded = false;
        postDataObject = AppData.updateRecord;
    }

    public void setPostDataField(String dataKey,String dataValue){
        try {

            if (dataValue != null && !dataValue.isEmpty() && postDataObject.get(dataKey) == null)
                postDataObject.addProperty(dataKey, dataValue);

        }catch(Exception ex){
            ex.printStackTrace();
        }

    }


    public void renderTextBasedField(FormField field) {

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
        fieldLabel.setTextSize(inputLabelTextSize);
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

        Integer  maxConstrait  = field.getDb_constraint();
        Integer  minConstrait  = field.getMin_constraint();

        JsonElement element = postDataObject.get(field.getForm_field());

        if (element != null) {
            try {
                String elemValue = element.getAsString();
                EditText thisField = findViewById(Integer.parseInt(field.getId()));

                if(!elemValue.isEmpty())
                    thisField.setText(elemValue);

            }catch(Exception ex){
                // Log.e(TAG,field.getForm_field() +" value not set");
                //ex.printStackTrace();
            }
        }

        if(field.getIs_disabled()) return; // is disabled, don't valdiate anything on it

        if(field.getData_format().equals( InputType.TYPE_CLASS_TEXT ))
            awesomeValidation.addValidation(this, Integer.parseInt(field.getId()), "[a-zA-Z\\s]+",R.string.invalid_characters );

        if(field.getIs_required())
            awesomeValidation.addValidation(this, Integer.parseInt(field.getId()), RegexTemplate.NOT_EMPTY, R.string.not_empty);

        if(maxConstrait > 0)
            awesomeValidation.addValidation(this, Integer.parseInt(field.getId()),customValidators.maxLengthValidator(maxConstrait),R.string.too_short);

        if(minConstrait != null && minConstrait > 0)
            awesomeValidation.addValidation(this, Integer.parseInt(field.getId()),customValidators.minLengthValidator(minConstrait),R.string.input_too_short);


    }

    public void renderImageField(FormField field) {

        // Create EditText
        LinearLayout currentField = new LinearLayout(this);

        //Params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 8;
        params.bottomMargin = 8;

        currentField.setLayoutParams(params);
        currentField.setId(Integer.parseInt(field.getId())*112);
        currentField.setOrientation(LinearLayout.VERTICAL);

        //Input
        final TextView imageHolder = new TextView(currentField.getContext());

        imageHolder.setPadding(10, 10, 10, 10);
        imageHolder.setTextColor(getResources().getColor(R.color.grey));
        imageHolder.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageHolder.setBackgroundColor(0x00000000);
        imageHolder.setId(Integer.parseInt(field.getId()));
        imageHolder.setText("Choose " + field.getLabel());
        imageHolder.setTextSize(inputLabelTextSize);

        //data already captured
        JsonElement capturedData = postDataObject.get(field.getForm_field());

        if( field.getIs_visible() && capturedData == null) {
            imageFields.add(field.getForm_field()); //track Image fields
        }

        imageHolder.setOnClickListener(v -> dispatchTakePictureIntent(field));

        ImageView imageView = new ImageView(currentField.getContext());
        imageView.setId(Integer.parseInt(field.getId()) * 300);
        imageView.setVisibility(View.GONE);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setPadding(10, imagePadding, 10, 5);
        imageView.setAdjustViewBounds(true);

        currentField.setBackground( getResources().getDrawable(R.drawable.placeholder));

        currentField.setOnClickListener(v -> dispatchTakePictureIntent(field));


        if (capturedData != null) {
            Log.e(TAG,field.getForm_field() +" image set");
            try {
                String elemValue = capturedData.getAsString();
                //Log.e(TAG,elemValue);
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

    public void dispatchTakePictureIntent(FormField field) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            this.currentImageField = field;
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    public void renderSpinnerBasedField(FormField field) {

        Gson gson = new Gson();
        Type targetType = new TypeToken<List<Map<String, String>>>() {
        }.getType();
        List<Map<String, String>> options = gson.fromJson(field.getDefault_data(), targetType);

        List<String> listOptions = new ArrayList<>();

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
        params.topMargin = inputTopMargin;
        params.bottomMargin = 8;

        currentField.setLayoutParams(params);
        currentField.setOrientation(LinearLayout.VERTICAL);

        TextView spinnerLabel = new TextView(this);
        spinnerLabel.setText(field.getLabel());
        spinnerLabel.setTextSize(inputLabelTextSize);

        //Create spinner
        Spinner spinner = new Spinner(this);
        //Attach adapter to spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, spinnerOptions);
        spinner.setAdapter(adapter);
        spinner.setLayoutParams(params);
        spinner.setId(Integer.parseInt(field.getId()));

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

                String elemValue = element.getAsString();
                int selectedIndex = adapter.getPosition(elemValue);

                spinner.setSelection(selectedIndex);

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

        if(field.getIs_required()){
            try {
                spinnerValidation(Integer.parseInt(field.getId()));
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }


    }

    public void spinnerValidation(int spinnerId){

        awesomeValidation.addValidation(this, spinnerId, new CustomValidation() {
            @Override
            public boolean compare(ValidationHolder validationHolder) {
                String selectedValue = ((Spinner) validationHolder.getView()).getSelectedItem().toString();
                Log.e(TAG,"Spinner Selected Value:: "+selectedValue);
                if (selectedValue.equals("Choose Option") || selectedValue.isEmpty()) {
                    return false;
                } else {
                    return true;
                }
            }
        }, validationHolder -> {

            TextView textViewError = (TextView) ((Spinner) validationHolder.getView()).getSelectedView();
            textViewError.setError(validationHolder.getErrMsg());
            textViewError.setTextColor(Color.RED);

        }, validationHolder -> {

            TextView textViewError = (TextView) ((Spinner) validationHolder.getView()).getSelectedView();
            textViewError.setError(null);
            textViewError.setTextColor(Color.BLACK);

        }, R.string.not_empty);
    }

    public void renderDateField(FormField field) {

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        LinearLayout currentField = new LinearLayout(this);

        //Params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin    = inputTopMargin;
        params.bottomMargin = 8;

        currentField.setLayoutParams(params);
        currentField.setOrientation(LinearLayout.VERTICAL);

        TextView fieldLabel = new TextView(this);
        fieldLabel.setText(field.getLabel());
        fieldLabel.setTextSize(inputLabelTextSize);
        //spinnerOptions.add();
        //Create spinner
        TextView dateField = new TextView(this);
        dateField.setPadding(25, 10, 25, 10);
        dateField.setTextColor(getResources().getColor(R.color.grey));
        dateField.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //edtPass.setHint(field.getLabel());
        dateField.setBackgroundColor(0x00000000);
        dateField.setId(Integer.parseInt(field.getId()));
        dateField.setTextAppearance(this, R.style.dateFieldStyle);

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

    public void renderTextAutoCompleteField(FormField field) {

        Gson gson = new Gson();
        Type targetType = new TypeToken<List<Map<String, String>>>() {
        }.getType();
        List<Map<String, String>> options = gson.fromJson(field.getDefault_data(), targetType);

        List<String> listOptions = new ArrayList<>();

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
        params.topMargin = inputTopMargin;
        params.bottomMargin = 8;

        currentField.setLayoutParams(params);
        currentField.setOrientation(LinearLayout.VERTICAL);

        TextView spinnerLabel = new TextView(this);
        spinnerLabel.setText(field.getLabel());
        spinnerLabel.setTextSize(inputLabelTextSize);
        //spinnerOptions.add();
        //Create spinner
        AutoCompleteTextView autoCompleteField = new AutoCompleteTextView(this);
        autoCompleteField.setId(Integer.parseInt(field.getId()));
        //Attach adapter to spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, spinnerOptions);
        autoCompleteField.setAdapter(adapter);
        autoCompleteField.setLayoutParams(params);

        currentField.addView(spinnerLabel);
        currentField.addView(autoCompleteField);
        dynamicFieldsWrapper.addView(currentField);
        awesomeValidation.addValidation(this, Integer.parseInt(field.getId()), RegexTemplate.NOT_EMPTY, R.string.not_empty);


        //Attempt to set default item
        JsonElement element = postDataObject.get(field.getForm_field());

        //Log.e(TAG,"Selected Field");
        //Log.e(TAG, element.getAsString());

        if (element != null) {
            try {
                String elemValue = element.getAsString();
                int selectedIndex = adapter.getPosition(elemValue);

                // Log.e(TAG,"Selected Field");
                //Log.e(TAG, String.valueOf(field));

                // Log.e(TAG,"Selected Value");
                //Log.e(TAG,spinnerOptions.get(selectedIndex));
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

    public void renderSignatureField(FormField field) {

        // Create EditText
        LinearLayout currentField = new LinearLayout(this);

        //Params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin    = inputTopMargin;
        params.bottomMargin = 8;

        currentField.setLayoutParams(params);
        currentField.setOrientation( LinearLayout.VERTICAL );
        // currentField.setBackgroundColor(getResources().getColor(R.color.black));

        //Input
        final TextView signatureLabel = new TextView(currentField.getContext());

        signatureLabel.setPadding(25, 10, 25, 10);
        signatureLabel.setTextColor(getResources().getColor(R.color.grey));
        signatureLabel.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        signatureLabel.setBackgroundColor(0x00000000);
        signatureLabel.setText(field.getLabel());
        signatureLabel.setId( Integer.parseInt(field.getId()) *300);
        signatureLabel.setTextSize(inputLabelTextSize);

        SignaturePad signaturePad = new SignaturePad(currentField.getContext(),null);
        signaturePad.setId(Integer.parseInt(field.getId()));
        // signaturePad.setVisibility(View.GONE);
        signaturePad.setLayoutParams( new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600));
        signaturePad.setPadding(10, 30, 10, 10);
        signaturePad.setPenColor( getResources().getColor(R.color.black) );
        signaturePad.setBackgroundColor( getResources().getColor(R.color.signature_grey) );

        JsonElement element = postDataObject.get(field.getForm_field());

        if (element != null) {
            Log.e(TAG,field.getForm_field() +" signature set");
            try {
                String elemValue = element.getAsString();
                Log.e(TAG,elemValue);

                postDataObject.addProperty(field.getForm_field(), elemValue);
                //Bitmap bitmap = AppUtils.base64ToBitmap(elemValue);

                // signaturePad.setSignatureBitmap(AppUtils.resizeBitmap(bitmap));
                //  signaturePad.setVisibility(View.VISIBLE);

            } catch (Exception ex) {
                // Log.e(TAG,field.getForm_field() +" value not set");
                ex.printStackTrace();
            }
        }


        final TextView clearTrigger = new TextView(currentField.getContext());

        clearTrigger.setPadding(25, 5, 5, 5);
        clearTrigger.setTextColor(getResources().getColor(R.color.grey));
        clearTrigger.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        clearTrigger.setBackgroundColor(0x00000000);
        clearTrigger.setText("Clear Signature");
        clearTrigger.setVisibility(View.GONE);
        clearTrigger.setTextColor(getResources().getColor(R.color.design_default_color_error));

        clearTrigger.setOnClickListener(v -> signaturePad.clear());

        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
                clearTrigger.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSigned() {
                //Event triggered when the pad is signed
                Bitmap signature =  signaturePad.getSignatureBitmap();
                String encodedSignatureImage = AppUtils.bitmapTobase64(signature);
                Log.e(TAG,"Signature:: "+encodedSignatureImage);
                postDataObject.addProperty(field.getForm_field(), encodedSignatureImage);
            }

            @Override
            public void onClear() {
                //Event triggered when the pad is cleared
                postDataObject.remove(field.getForm_field());
                clearTrigger.setVisibility(View.GONE);
                //signaturePad.setSignatureBitmap(null);
            }
        });


        currentField.addView(signatureLabel);
        currentField.addView(signaturePad);
        currentField.addView(clearTrigger);

        dynamicFieldsWrapper.addView(currentField);
    }






}
