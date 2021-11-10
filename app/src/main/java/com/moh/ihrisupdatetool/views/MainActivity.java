package com.moh.ihrisupdatetool.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.db.entities.CommunityWorkerEntity;
import com.moh.ihrisupdatetool.db.entities.FacilityEntity;
import com.moh.ihrisupdatetool.viewmodels.FacilitiesViewModel;
import com.moh.ihrisupdatetool.viewmodels.MainActivityViewModel;
import com.moh.ihrisupdatetool.viewmodels.WorkersViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    WorkersViewModel workersViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        workersViewModel = new ViewModelProvider(this).get(WorkersViewModel.class);
        //get workes fron new thread
        //new Handler(getApplicationContext().getMainLooper()).post(() -> getHealthWorkers());

    }


    private void getHealthWorkers(){
        workersViewModel.getHealthWorkers();
    }


    public void searchMinistryWorker(View view) {

        Intent intent = new Intent(this,DistrictsActivity.class);
        startActivity(intent);
        finish();
    }

    public void searchCommunityWorker(View view) {

        Intent intent = new Intent(this,FacilitiesActivity.class);
        startActivity(intent);
        finish();
    }
}