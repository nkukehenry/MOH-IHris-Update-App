package com.moh.ihrisupdatetool.adapaters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moh.ihrisupdatetool.databinding.MinWorkerListBinding;
import com.moh.ihrisupdatetool.db.entities.MinistryWorkerEntity;
import com.moh.ihrisupdatetool.utils.AppData;
import com.moh.ihrisupdatetool.views.FormsActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MinistryWorkerAdapter extends RecyclerView.Adapter<MinistryWorkerAdapter.MinWorkerViewHolder> {

    List<MinistryWorkerEntity> data;
    Context context;
    private LayoutInflater inflater;

    public MinistryWorkerAdapter(List<MinistryWorkerEntity> data, Context context){
        this.data = data;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MinistryWorkerAdapter.MinWorkerViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if(inflater == null){
            inflater = LayoutInflater.from(parent.getContext());
        }

        MinWorkerListBinding workersListBinding =  MinWorkerListBinding.inflate(inflater,parent,false);
        return new MinWorkerViewHolder(parent, workersListBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MinistryWorkerAdapter.MinWorkerViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //nested viewholder implementation class
    public static class MinWorkerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private MinWorkerListBinding minWorkerListBinding;

        public MinWorkerViewHolder(View view1, MinWorkerListBinding cworkerListBinding) {
            super(cworkerListBinding.getRoot());
            View view = cworkerListBinding.getRoot();
            view.setOnClickListener(this);
            this.minWorkerListBinding = cworkerListBinding;
        }

        public void bind(MinistryWorkerEntity person){
            this.minWorkerListBinding.setPerson(person);
        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(v.getContext(), FormsActivity.class);
            AppData.selectedMinistryWorker = this.minWorkerListBinding.getPerson();
            v.getContext().startActivity(intent);
        }
    }


}
