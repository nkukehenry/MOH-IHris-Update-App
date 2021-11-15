package com.moh.ihrisupdatetool.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.adapaters.DistrictsAdapter;
import com.moh.ihrisupdatetool.utils.UIHelper;
import com.moh.ihrisupdatetool.viewmodels.DistrictsViewModel;

import java.util.Timer;
import java.util.TimerTask;

import dagger.hilt.android.AndroidEntryPoint;
import kotlin.UInt;

@AndroidEntryPoint
public class DistrictsActivity extends AppCompatActivity {

    private RecyclerView districtsRecycler;
    private DistrictsViewModel districtsViewModel;
    private UIHelper uiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_districts);

        uiHelper = new UIHelper(this);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);

        districtsRecycler = findViewById(R.id.districtsRecycler);
        districtsRecycler.setLayoutManager(linearLayout);

        districtsViewModel = new ViewModelProvider(this).get(DistrictsViewModel.class);

        getDistricts();
    }

    private void getDistricts(){

        districtsViewModel.observerResponse().observe( this,districtsResponse->{

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    uiHelper.hideLoader();
                }
            }, 1000);

            if(districtsResponse !=null){

                DistrictsAdapter districtsAdapter = new DistrictsAdapter(districtsResponse,this);
                districtsRecycler.setAdapter(districtsAdapter);
            }
        });

        uiHelper.showLoader();
        districtsViewModel.getDistricts();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}