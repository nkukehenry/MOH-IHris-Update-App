package com.moh.ihrisupdatetool.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.adapaters.FacilitiesAdapter;
import com.moh.ihrisupdatetool.adapaters.FormsAdapter;
import com.moh.ihrisupdatetool.db.entities.FormEntity;
import com.moh.ihrisupdatetool.utils.AppData;
import com.moh.ihrisupdatetool.utils.UIHelper;
import com.moh.ihrisupdatetool.viewmodels.FormsViewModel;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

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

        formsViewModel.observerResponse().observe(this, formsResponse -> {
            uiHelper.hideLoader();

            if (formsResponse != null) {

                for(FormEntity form: formsResponse){
                    forms.add(form);
                }
                AppData.allForms = forms;

                FormsAdapter formsAdapter = new FormsAdapter(formsResponse, this);
                formsRecycler.setAdapter(formsAdapter);
            }

        });

        formsViewModel.getForms();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}