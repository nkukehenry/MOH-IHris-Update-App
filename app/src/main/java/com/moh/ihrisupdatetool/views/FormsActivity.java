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
import com.moh.ihrisupdatetool.viewmodels.FormsViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FormsActivity extends AppCompatActivity {

    RecyclerView formsRecycler;
    FormsViewModel formsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);

        formsRecycler = findViewById(R.id.formsRecycler);
        formsRecycler.setLayoutManager(linearLayout);

        formsViewModel = new ViewModelProvider(this).get(FormsViewModel.class);

        getForms();
    }

    private void getForms() {

        formsViewModel.observerResponse().observe(this, formsResponse -> {

            if (formsResponse != null) {

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