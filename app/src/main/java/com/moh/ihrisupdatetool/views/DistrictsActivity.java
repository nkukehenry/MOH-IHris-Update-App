package com.moh.ihrisupdatetool.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.adapaters.DistrictsAdapter;
import com.moh.ihrisupdatetool.db.entities.DistrictEntity;
import com.moh.ihrisupdatetool.utils.UIHelper;
import com.moh.ihrisupdatetool.viewmodels.DistrictsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dagger.hilt.android.AndroidEntryPoint;
import kotlin.UInt;

@AndroidEntryPoint
public class DistrictsActivity extends AppCompatActivity {

    private RecyclerView districtsRecycler;
    private DistrictsViewModel districtsViewModel;
    private UIHelper uiHelper;
    private DistrictsAdapter districtsAdapter;
    private EditText districtSearch;
    private List<DistrictEntity> districtsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_districts);

        uiHelper = new UIHelper(this);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);

        districtsRecycler = findViewById(R.id.districtsRecycler);
        districtsRecycler.setLayoutManager(linearLayout);

        districtSearch = findViewById(R.id.districtSearch);

        districtsViewModel = new ViewModelProvider(this).get(DistrictsViewModel.class);

        getDistricts();

        onDataFilteredHandler();

    }

    private void onDataFilteredHandler() {

        districtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable searchTearm) {

                List<DistrictEntity> filteredList = new ArrayList<>();

                for(DistrictEntity district:  districtsList){
                    if(district.getDistrictName().toLowerCase().contains(searchTearm.toString().toLowerCase())){
                        filteredList.add(district);
                    }
                }
                districtsAdapter.filterDistricts(filteredList);
            }
        });
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
                districtsList = districtsResponse;
                districtsAdapter = new DistrictsAdapter(districtsResponse,this);
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