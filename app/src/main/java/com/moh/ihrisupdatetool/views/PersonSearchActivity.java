package com.moh.ihrisupdatetool.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.adapaters.CommunityWorkerAdapter;
import com.moh.ihrisupdatetool.adapaters.FormsAdapter;
import com.moh.ihrisupdatetool.adapaters.MinistryWorkerAdapter;
import com.moh.ihrisupdatetool.db.entities.DistrictEntity;
import com.moh.ihrisupdatetool.utils.AppData;
import com.moh.ihrisupdatetool.viewmodels.WorkersViewModel;

import java.util.TreeMap;
import java.util.TreeSet;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PersonSearchActivity extends AppCompatActivity {

    Button searchButton;
    EditText searchTerm;
    WorkersViewModel workersViewModel;
    RecyclerView comunityWorkerRecycler,ministryWorkerRecycler;
    DistrictEntity selectedDistrict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_search);

        selectedDistrict = AppData.selectedDistrict;

        searchButton = findViewById(R.id.searchButton);
        searchTerm =  findViewById(R.id.searchTerm);

        comunityWorkerRecycler = findViewById(R.id.comunityWorkerRecycler);
        ministryWorkerRecycler = findViewById(R.id.ministryWorkerRecycler);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        comunityWorkerRecycler.setLayoutManager(linearLayout);

        workersViewModel = new ViewModelProvider(this).get(WorkersViewModel.class);

        observePersonsResponse();

        searchButton.setOnClickListener(v->{
            String term = searchTerm.getText().toString();
//            Intent intent = new Intent(this, FormsActivity.class);
//            v.getContext().startActivity(intent);
            System.out.println(selectedDistrict);

            if(term!=null && !term.isEmpty())
              workersViewModel.searchWorker(term,selectedDistrict.getDistrictName(),AppData.isCommunityWorker);
        });

    }

    private void observePersonsResponse(){

        workersViewModel.observeCommunityWorkers().observe( this,workersResponse->{
                System.out.println(workersResponse);

                if(!workersResponse.isEmpty()) {
                    CommunityWorkerAdapter formsAdapter = new CommunityWorkerAdapter(workersResponse, this);
                    comunityWorkerRecycler.setAdapter(formsAdapter);
                }
        });

        workersViewModel.observeMinistryWorkers().observe( this,workersResponse->{
            System.out.println(workersResponse);

            if(!workersResponse.isEmpty()) {
                MinistryWorkerAdapter formsAdapter = new MinistryWorkerAdapter(workersResponse, this);
                ministryWorkerRecycler.setAdapter(formsAdapter);
            }
        });



    }

}