package com.moh.ihrisupdatetool.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.adapaters.FacilitiesAdapter;
import com.moh.ihrisupdatetool.adapaters.FormsAdapter;
import com.moh.ihrisupdatetool.db.entities.FormEntity;
import com.moh.ihrisupdatetool.utils.AppData;
import com.moh.ihrisupdatetool.utils.UIHelper;
import com.moh.ihrisupdatetool.viewmodels.FormsViewModel;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

import static com.moh.ihrisupdatetool.utils.AppConstants.SELECTED_FORM;

@AndroidEntryPoint
public class FormsActivity extends AppCompatActivity {

    private RecyclerView formsRecycler;
    private FormsViewModel formsViewModel;
    private ArrayList<FormEntity> forms = new ArrayList<>();
    private UIHelper uiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        uiHelper = new UIHelper(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);

        formsRecycler = findViewById(R.id.formsRecycler);

        formsRecycler.setLayoutManager(linearLayout);
        formsViewModel = new ViewModelProvider(this).get(FormsViewModel.class);

        getForms();
    }

    private void getForms() {

        uiHelper.showLoader();

        formsViewModel.getForms().observe(this, formsResponse -> {

            if (formsResponse != null) {

                for(FormEntity form: formsResponse){
                    forms.add(form);
                }
                AppData.allForms = forms;

                preloadAllForms();

                //FormsAdapter formsAdapter = new FormsAdapter(formsResponse, this);
               // formsRecycler.setAdapter(formsAdapter);
            }

        });
    }

    private void preloadAllForms(){

        for(FormEntity form : AppData.allForms){
            formsViewModel.getFormFields(form.getId());
        }
        uiHelper.hideLoader();

        showFirstForm(AppData.allForms.get(0));
    }

    private void showFirstForm(FormEntity selectedForm) {
        Intent intent = new Intent(this, FormDataActivity.class);
        intent.putExtra(SELECTED_FORM, selectedForm);
        AppData.selectedForm = selectedForm;
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}