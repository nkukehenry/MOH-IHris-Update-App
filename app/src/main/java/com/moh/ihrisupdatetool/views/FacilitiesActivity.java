package com.moh.ihrisupdatetool.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.adapaters.DistrictsAdapter;
import com.moh.ihrisupdatetool.adapaters.FacilitiesAdapter;
import com.moh.ihrisupdatetool.viewmodels.DistrictsViewModel;
import com.moh.ihrisupdatetool.viewmodels.FacilitiesViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FacilitiesActivity extends AppCompatActivity {

    RecyclerView facilitiesRecycler;
    FacilitiesViewModel facilitiesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facilities);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);

        facilitiesRecycler = findViewById(R.id.facilitiesRecycler);
        facilitiesRecycler.setLayoutManager(linearLayout);

        facilitiesViewModel = new ViewModelProvider(this).get(FacilitiesViewModel.class);

        getFacilities();
    }

    private void getFacilities(){

        facilitiesViewModel.observerFacilitiesResponse().observe( this, facilityResponse->{

            if(facilityResponse !=null){

                FacilitiesAdapter districtsAdapter = new FacilitiesAdapter(facilityResponse,this);
                facilitiesRecycler.setAdapter(districtsAdapter);
            }

        });

        facilitiesViewModel.getFacilities();
    }
}