package com.moh.ihrisupdatetool.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.adapaters.DistrictsAdapter;
import com.moh.ihrisupdatetool.viewmodels.DistrictsViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DistrictsActivity extends AppCompatActivity {

    RecyclerView districtsRecycler;
    DistrictsViewModel districtsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_districts);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);

        districtsRecycler = findViewById(R.id.districtsRecycler);
        districtsRecycler.setLayoutManager(linearLayout);

        districtsViewModel = new ViewModelProvider(this).get(DistrictsViewModel.class);

        getDistricts();
    }

    private void getDistricts(){

        districtsViewModel.observerResponse().observe( this,districtsResponse->{
            if(districtsResponse !=null){

                DistrictsAdapter districtsAdapter = new DistrictsAdapter(districtsResponse,this);
                districtsRecycler.setAdapter(districtsAdapter);
            }
        });

        districtsViewModel.getDistricts();

    }
}