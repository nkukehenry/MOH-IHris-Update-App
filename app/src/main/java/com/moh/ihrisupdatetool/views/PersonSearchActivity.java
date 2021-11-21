package com.moh.ihrisupdatetool.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.adapaters.CommunityWorkerAdapter;
import com.moh.ihrisupdatetool.adapaters.FormsAdapter;
import com.moh.ihrisupdatetool.adapaters.MinistryWorkerAdapter;
import com.moh.ihrisupdatetool.db.entities.DistrictEntity;
import com.moh.ihrisupdatetool.utils.AppData;
import com.moh.ihrisupdatetool.utils.UIHelper;
import com.moh.ihrisupdatetool.viewmodels.WorkersViewModel;

import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PersonSearchActivity extends AppCompatActivity {

    private Button searchButton;
    private EditText searchTerm;
    private WorkersViewModel workersViewModel;
    private RecyclerView comunityWorkerRecycler,ministryWorkerRecycler;
    private DistrictEntity selectedDistrict;
    private UIHelper uiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_search);

        //Reset workers
        AppData.selectedMinistryWorker  = null;
        AppData.selectedCommunityWorker = null;

        selectedDistrict = AppData.selectedDistrict;
        uiHelper = new UIHelper(this);

        searchButton = findViewById(R.id.searchButton);
        searchTerm =  findViewById(R.id.searchTerm);

        comunityWorkerRecycler = findViewById(R.id.comunityWorkerRecycler);
        ministryWorkerRecycler = findViewById(R.id.ministryWorkerRecycler);

        LinearLayoutManager communitylinearLayout = new LinearLayoutManager(this);
        LinearLayoutManager ministrylinearLayout = new LinearLayoutManager(this);
        comunityWorkerRecycler.setLayoutManager(communitylinearLayout);
        ministryWorkerRecycler.setLayoutManager(ministrylinearLayout);

        workersViewModel = new ViewModelProvider(this).get(WorkersViewModel.class);

        searchButton.setOnClickListener(v->{

            String term = searchTerm.getText().toString();

            if(term!=null && !term.isEmpty()) {

                uiHelper.showLoader();

                if ((AppData.isCommunityWorker)) {
                    searchCommunityWorker(term);
                } else {
                    searchMinistryWorker(term);
                }
            }
            });


    }

    private void searchCommunityWorker(String term) {

        //community workers observer
        workersViewModel.searchCommunityWorker(term, selectedDistrict.getDistrictName()).observe(this, workersResponse -> {

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    uiHelper.hideLoader();
                }
            }, 1000);

            if (workersResponse == null) {
                Toast.makeText(this, "No records found", Toast.LENGTH_LONG).show();
            } else if (!workersResponse.isEmpty()) {
                CommunityWorkerAdapter formsAdapter = new CommunityWorkerAdapter(workersResponse, this);
                comunityWorkerRecycler.setAdapter(formsAdapter);
            }
        });
    }

    private void searchMinistryWorker(String term) {
        //ministry workers observer
        workersViewModel.searchMinistryWorker(term, selectedDistrict.getDistrictName()).observe( this,workersResponse->{

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    uiHelper.hideLoader();
                }
            }, 1000);

            if(workersResponse == null) {
                Toast.makeText(this, "No records found", Toast.LENGTH_LONG).show();
            }
            else if(!workersResponse.isEmpty()) {
                MinistryWorkerAdapter formsAdapter = new MinistryWorkerAdapter(workersResponse, this);
                ministryWorkerRecycler.setAdapter(formsAdapter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.newRecord:
                  goToFormWithNewRecord();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToFormWithNewRecord() {
        Intent intent = new Intent(this,FormsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
      Intent intent = new Intent(this,MainActivity.class);
      startActivity(intent);
      finish();
    }

}