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
import android.widget.Toast;

import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.adapaters.HistoryListAdapter;
import com.moh.ihrisupdatetool.utils.UIHelper;
import com.moh.ihrisupdatetool.viewmodels.HistoryViewModel;
import com.moh.ihrisupdatetool.viewmodels.SubmissionViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HistoryActivity extends AppCompatActivity {

    private HistoryViewModel historyViewModel;
    private SubmissionViewModel submissionViewModel;
    private RecyclerView historyRecycler;
    private UIHelper uiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        uiHelper = new UIHelper(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);

        historyRecycler = findViewById(R.id.historyRecycler);
        historyRecycler.setLayoutManager(linearLayout);
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        submissionViewModel = new ViewModelProvider(this).get(SubmissionViewModel.class);

        initDataObserver();
        fetchHistory();

    }

    private void initDataObserver(){
        this.historyViewModel.observeHistoryData().observe(this,response->{
            uiHelper.hideLoader();
                //bind data to view
            if(response !=null && !response.isEmpty()) {
                HistoryListAdapter adapter = new HistoryListAdapter(response, this);
                historyRecycler.setAdapter(adapter);
            }

        });

        //observer for if upload is clicked
        syncingResponseObserver();
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

    }


    private void fetchHistory(){
        uiHelper.showLoader();
        this.historyViewModel.getAllData();
    }


    private void syncCollectedLocalData(){
        uiHelper.showLoader("Synchronizing data...");
        try {
            submissionViewModel.syncData();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.upload:
                syncCollectedLocalData();
                return true;
            case R.id.delete:
                Toast.makeText(this, "Delete coming soon", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}