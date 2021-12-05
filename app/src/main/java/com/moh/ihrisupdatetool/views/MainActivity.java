package com.moh.ihrisupdatetool.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.utils.AppConstants;
import com.moh.ihrisupdatetool.utils.AppData;
import com.moh.ihrisupdatetool.utils.UIHelper;
import com.moh.ihrisupdatetool.viewmodels.FormsViewModel;
import com.moh.ihrisupdatetool.viewmodels.SubmissionViewModel;
import com.moh.ihrisupdatetool.viewmodels.WorkersViewModel;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private WorkersViewModel workersViewModel;
    private SubmissionViewModel submissionViewModel;
    private FormsViewModel formsViewModel;
    private UIHelper uiHelper;
    private int exitCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        uiHelper = new UIHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submissionViewModel = new ViewModelProvider(this).get(SubmissionViewModel.class);
        formsViewModel      = new ViewModelProvider(this).get(FormsViewModel.class);
        workersViewModel = new ViewModelProvider(this).get(WorkersViewModel.class);

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

    private void syncData() {
        //submission
        submissionViewModel.syncData().observe(this, submissionResponse -> {
             uiHelper.hideLoader();
            try {
                String msg = "Sync finished successfully";

                if (!submissionResponse.get("state").getAsBoolean())
                    msg = "There wasn't any unsynchronized data";

                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });
    }

    private void syncCollectedLocalData(){
        uiHelper.showLoader("Synchronizing data...");
                try {
                    syncData();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
    }

    private void syncResources(){
        uiHelper.showLoader("Synchronizing resources...");

        try {
                //forms
           formsViewModel.syncForms().observe(MainActivity.this, rp -> {

                //fields
                formsViewModel.deleteFields();
                workersViewModel.deleteData();

                String msg = "Proceed to sync districts data";
                uiHelper.hideLoader();
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

               Intent intent = new Intent(this, SynchronizationActivity.class);
               startActivity(intent);

            });
        }catch(Exception ex){
            uiHelper.hideLoader();
            ex.printStackTrace();
        }

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
            case R.id.logout:
                 logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logOut(){

        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHAREDPREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(AppConstants.LOGIN_CODE);
        editor.commit();

        Intent  loginIntent=new Intent(MainActivity.this, LoginActivity.class );
        startActivity(loginIntent);
        finishAffinity();

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