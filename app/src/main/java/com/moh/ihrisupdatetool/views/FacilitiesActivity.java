package com.moh.ihrisupdatetool.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.adapaters.DistrictsAdapter;
import com.moh.ihrisupdatetool.adapaters.FacilitiesAdapter;
import com.moh.ihrisupdatetool.db.entities.DistrictEntity;
import com.moh.ihrisupdatetool.db.entities.FacilityEntity;
import com.moh.ihrisupdatetool.viewmodels.DistrictsViewModel;
import com.moh.ihrisupdatetool.viewmodels.FacilitiesViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FacilitiesActivity extends AppCompatActivity {

    RecyclerView facilitiesRecycler;
    FacilitiesViewModel facilitiesViewModel;
    private FacilitiesAdapter districtsAdapter;
    private EditText facilitySearch;
    private List<FacilityEntity> facilityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facilities);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);

        facilitiesRecycler = findViewById(R.id.facilitiesRecycler);
        facilitiesRecycler.setLayoutManager(linearLayout);
        facilitySearch = findViewById(R.id.facilitySearch);

        facilitiesViewModel = new ViewModelProvider(this).get(FacilitiesViewModel.class);

        getFacilities();

        onDataFilteredHandler();

    }

    private void onDataFilteredHandler() {

        facilitySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable searchTearm) {

                List<FacilityEntity> filteredList = new ArrayList<>();

                for(FacilityEntity facility:  facilityList){
                    if(facility.getFacilityName().toLowerCase().contains(searchTearm.toString().toLowerCase())){
                        filteredList.add(facility);
                    }
                }
                districtsAdapter.filterFacilities(filteredList);
            }
        });

    }

    private void getFacilities(){

        facilitiesViewModel.observerFacilitiesResponse().observe( this, facilityResponse->{

            if(facilityResponse !=null){

                 districtsAdapter = new FacilitiesAdapter(facilityResponse,this);
                facilitiesRecycler.setAdapter(districtsAdapter);
            }

        });

        facilitiesViewModel.getFacilities();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}