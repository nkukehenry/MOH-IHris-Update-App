package com.moh.ihrisupdatetool.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.adapaters.HistoryListAdapter;
import com.moh.ihrisupdatetool.api.AppApi;
import com.moh.ihrisupdatetool.db.dao.DataEntryDao;
import com.moh.ihrisupdatetool.db.entities.DataEntryTemplate;
import com.moh.ihrisupdatetool.repo.DataSubmissionRepository;
import com.moh.ihrisupdatetool.utils.AppConstants;
import com.moh.ihrisupdatetool.utils.AppUtils;
import com.moh.ihrisupdatetool.utils.UIHelper;
import com.moh.ihrisupdatetool.viewmodels.HistoryViewModel;
import com.moh.ihrisupdatetool.viewmodels.SubmissionViewModel;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class HistoryActivity extends AppCompatActivity {

    private HistoryViewModel historyViewModel;
    private SubmissionViewModel submissionViewModel;
    private RecyclerView historyRecycler;
    private UIHelper uiHelper;
    TextView allRecords;

    @Inject
    AppApi appApi;
    @Inject
    DataEntryDao dataEntryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        uiHelper = new UIHelper(this);

        setTitle("Data Collection History");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);

        allRecords = findViewById(R.id.allRecords);

        historyRecycler = findViewById(R.id.historyRecycler);
        historyRecycler.setLayoutManager(linearLayout);
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        submissionViewModel = new ViewModelProvider(this).get(SubmissionViewModel.class);

        fetchHistory();

    }


    private void postSync(){
        //submission
        submissionViewModel.syncData().observe( this,submissionResponse->{

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


    private void postSync2(){
        //submission
        //syncDataSync
        JsonObject submissionResponse = submissionViewModel.syncDataSync();
                //.observe( this,submissionResponse->{
            try {
                String msg = "Sync finished successfully";

                System.out.println(submissionResponse);

                if (submissionResponse == null)
                    msg = "Sysnchronization Failure, Make sure you're connected to the internet";

                uiHelper.hideLoader();
                uiHelper.showDialog(msg);

            }catch (Exception ex){
                ex.printStackTrace();
            }
    }


    private void deleteLocalData(){
        //submission
        submissionViewModel.syncData();
    }


    private void fetchHistory(){
        uiHelper.showLoader();
        this.historyViewModel.getAllData().observe(this,response->{
            uiHelper.hideLoader();
            //bind data to view
            if(response !=null && !response.isEmpty()) {
                allRecords.setText("Records: "+ response.size());
                HistoryListAdapter adapter = new HistoryListAdapter(response, this);
                historyRecycler.setAdapter(adapter);
            }

        });;
    }


    private void syncCollectedLocalData(){

        uiHelper.showLoader("Synchronizing data...");

        try {
            //postSync2();

            List<DataEntryTemplate> records= dataEntryDao.getLocalRecordsSync();

            JsonObject resp = null;

            if( !records.isEmpty() ) {
                int count = 1;
                for (DataEntryTemplate record :records) {

                    Boolean isFinished = (count == records.size());

                    if(!record.getUploaded()) {
                         postSync(AppConstants.POST_FORM_DATA_URL(), record.getFormdata(),isFinished);
                    }
                    else if (isFinished && record.getUploaded()){
                        uiHelper.hideLoader();
                        uiHelper.showDialog("Synchronized successfully");
                    }

                    count++;

                }

            }else{
                uiHelper.hideLoader();
                uiHelper.showDialog("No data to synchronize");
            }
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
                deleteCachedRecords();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteCachedRecords(){
        uiHelper.showLoader("Processing...");
        this.historyViewModel.getAllData();
        uiHelper.hideLoader();
        Toast.makeText(this, "Delete operation started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void postSync(String url, Object model,Boolean isFinished) {

            appApi.post(url, model).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {

                    Type genType = new TypeToken<JsonObject>() {}.getType();
                    try {
                        JsonObject results = AppUtils.objectToType(response.body(), genType);

                        if(response.isSuccessful()){
                            cacheFormData( (JsonObject) model,true);
                        }

                        if(isFinished) {
                            uiHelper.hideLoader();
                            uiHelper.showDialog("Synchronization completed successfully ");
                        }

                    }catch(Exception exception){
                        exception.printStackTrace();
                        if(isFinished){
                            uiHelper.hideLoader();
                            uiHelper.showDialog("Synchronization failed "+exception.getMessage());
                        }
                    }

                }

                @Override
                public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {

                    if(isFinished) {
                        uiHelper.hideLoader();
                        uiHelper.showDialog("Synchronization failed " + t.getMessage());
                    }
                }
            });

        }


    public void  cacheFormData(JsonObject data,Boolean isUploaded){

        DataEntryTemplate dataEntryTemplate = new DataEntryTemplate();
        //dataEntryTemplate.setFacility_id("9988898");
        dataEntryTemplate.setFormdata(data);
        dataEntryTemplate.setReference(data.get("reference").getAsString());
        dataEntryTemplate.setStatus((isUploaded)?1:0);
        dataEntryTemplate.setUploaded(isUploaded);

        new InsetAsyncTask(dataEntryDao).execute(dataEntryTemplate);
    }

    static class InsetAsyncTask extends AsyncTask<DataEntryTemplate, Void, Void> {
        private DataEntryDao dataEntryDao;

        public InsetAsyncTask(DataEntryDao dataEntryDao) {
            this.dataEntryDao = dataEntryDao;
        }
        @Override
        protected Void doInBackground(DataEntryTemplate... dataEntryTemplates) {
            dataEntryDao.insert(dataEntryTemplates[0]);
            return null;
        }
    }


    static class DeleteAsyncTask extends AsyncTask<Void, Void, Void> {
        private DataEntryDao dataEntryDao;

        public DeleteAsyncTask(DataEntryDao communityWorkerDao) {
            this.dataEntryDao = communityWorkerDao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            dataEntryDao.deleteAll();
            return null;
        }
    }


}