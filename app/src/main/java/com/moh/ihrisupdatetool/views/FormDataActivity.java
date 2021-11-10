package com.moh.ihrisupdatetool.views;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
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
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.db.entities.FormEntity;
import com.moh.ihrisupdatetool.db.entities.FormField;
import com.moh.ihrisupdatetool.dto.FormFieldType;
import com.moh.ihrisupdatetool.viewmodels.FormsViewModel;
import com.moh.ihrisupdatetool.viewmodels.SubmissionViewModel;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    static final int REQUEST_IMAGE_CAPTURE = 1;
    FormField currentImageField;

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

            try {
                submitBtn.setEnabled(true);

                for (FormField field : formsFieldsResponse) {

                    FormFieldType fieldType = InputType(field.getData_type());

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
        edtPass.setPadding(25,100,25,10);
        edtPass.setTextColor(getResources().getColor(R.color.grey));
        edtPass.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        edtPass.setHint(field.getLabel());
        edtPass.setBackgroundColor(0x00000000);
        edtPass.setId(Integer.parseInt(field.getId()));

        currentField.addView(edtPass);

        dynamicFieldsWrapper.addView(currentField);
    }

    private void renderImageField(FormField field){

        // Create EditText
        TextInputLayout currentField =new TextInputLayout(this);

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, FormsActivity.class);
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
            case "blob":
                return FormFieldType.IMAGE_FIELD;
            case "map":
                return FormFieldType.SPINNER_BASED_FIELD;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {


            Bundle bundle =  data.getExtras();

            System.out.println(data.getData());
            System.out.println(bundle.get("data"));
            System.out.println(bundle.get("uri"));

            // final Uri imageUri = (Uri) bundle.get("data");

            InputStream imageStream = null;

            try {

                Bitmap photo = (Bitmap) data.getExtras().get("data");

                ImageView imageView = findViewById(Integer.parseInt(currentImageField.getId())*300);
                imageView.setImageBitmap(photo);
                imageView.setVisibility(View.VISIBLE);

                String encodedImage = toBase64(photo);
                postDataObject.addProperty(currentImageField.getForm_field(),encodedImage);
                TextView imageLabel = findViewById(Integer.parseInt(currentImageField.getId()));
                imageLabel.setText(currentImageField.getLabel() + ": Attached Successfully");

                currentImageField = null;

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    public String toBase64(Bitmap bm) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.NO_WRAP);
    }


}