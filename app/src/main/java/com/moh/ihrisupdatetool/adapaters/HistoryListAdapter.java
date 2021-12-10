package com.moh.ihrisupdatetool.adapaters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moh.ihrisupdatetool.R;
import com.moh.ihrisupdatetool.databinding.HistoryListBinding;
import com.moh.ihrisupdatetool.db.entities.DataEntryTemplate;
import com.moh.ihrisupdatetool.utils.AppData;
import com.moh.ihrisupdatetool.views.FormsActivity;
import com.moh.ihrisupdatetool.views.HistoryActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.HistoryItemViewHolder>{


    List<DataEntryTemplate> data;
    static Context context;
    private LayoutInflater inflater;

    public HistoryListAdapter(List<DataEntryTemplate> data, Context context){
        this.data = data;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public HistoryListAdapter.HistoryItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if(inflater == null){
            inflater = LayoutInflater.from(parent.getContext());
        }

        HistoryListBinding historyListBinding =  HistoryListBinding.inflate(inflater,parent,false);
        return new HistoryItemViewHolder(parent, historyListBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HistoryListAdapter.HistoryItemViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //nested viewholder implementation class
    public static class HistoryItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private HistoryListBinding historyListBinding;

        public HistoryItemViewHolder(View view1, HistoryListBinding historyListBinding) {
            super(historyListBinding.getRoot());
            View view = historyListBinding.getRoot();
            view.setOnClickListener(this);
            this.historyListBinding = historyListBinding;

            view.findViewById(R.id.updateRecord).setOnClickListener(v -> {
               AppData.updateRecord = historyListBinding.getRecord().getFormdata();
               AppData.isDataUpdate = true;
               Intent formsIntent = new Intent(context,FormsActivity.class);
               context.startActivity(formsIntent);
            }
            );
        }

        public void bind(DataEntryTemplate row){
            this.historyListBinding.setRecord(row);
        }

        @Override
        public void onClick(View v) {
            //re-render data n form

        }
    }
}
