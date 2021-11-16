package com.moh.ihrisupdatetool.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.utils.AppData;
import com.moh.ihrisupdatetool.utils.UIHelper;
import com.moh.ihrisupdatetool.viewmodels.SubmissionViewModel;
import com.moh.ihrisupdatetool.viewmodels.WorkersViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private WorkersViewModel workersViewModel;
    private SubmissionViewModel submissionViewModel;
    private UIHelper uiHelper;
    private int exitCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        uiHelper = new UIHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        workersViewModel    = new ViewModelProvider(this).get(WorkersViewModel.class);
        submissionViewModel = new ViewModelProvider(this).get(SubmissionViewModel.class);

        syncingResponseObserver();

    }

    public void searchMinistryWorker(View view) {
        AppData.isCommunityWorker = false;
        Intent intent = new Intent(this,DistrictsActivity.class);
        startActivity(intent);
        finish();
    }

    public void searchCommunityWorker(View view) {
        AppData.isCommunityWorker = true;
        Intent intent = new Intent(this,DistrictsActivity.class);
        startActivity(intent);
        finish();
    }

    private void syncingResponseObserver(){
        //submission
        submissionViewModel.observeResonse().observe( this,submissionResponse->{

           try {
               String msg = "Sync finished successfully";

               if (!submissionResponse.get("state").getAsBoolean())
                   msg = "There wasn't any unsynchronized data";

               uiHelper.hideLoader();
               uiHelper.showDialog(msg);

           }catch (Exception ex){
               ex.printStackTrace();
           }

        });

        //workers observer
        workersViewModel.observeMinistryWorkers().observe( this,submissionResponse->{
            String msg = "Sync finished successfully";
            uiHelper.hideLoader();
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }

    private void syncCollectedLocalData(){
        uiHelper.showLoader("Synchronizing data...");
        try {
            submissionViewModel.syncData();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void syncResources(){
        uiHelper.showLoader("Synchronizing resources...");
        workersViewModel.getCommunityHealthWorkers();
        workersViewModel.getMinistryHealthWorkers();
    }

    public void goToHistory() {
        Intent intent = new Intent(this,HistoryActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sysncData:
                syncCollectedLocalData();
                return true;
            case R.id.syncResources:
                syncResources();
                return true;
            case R.id.history:
                goToHistory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(exitCounter<1){
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            exitCounter++;
        }else {
            finishAffinity();
            System.exit(0);
        }
    }
}