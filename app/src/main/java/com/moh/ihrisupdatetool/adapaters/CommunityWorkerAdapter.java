package com.moh.ihrisupdatetool.adapaters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moh.ihrisupdatetool.databinding.CWorkerListBinding;
import com.moh.ihrisupdatetool.db.entities.CommunityWorkerEntity;
import com.moh.ihrisupdatetool.utils.AppData;
import com.moh.ihrisupdatetool.views.FormsActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommunityWorkerAdapter extends RecyclerView.Adapter<CommunityWorkerAdapter.CWorkerViewHolder> {

    List<CommunityWorkerEntity> data;
    Context context;
    private LayoutInflater inflater;

    public CommunityWorkerAdapter(List<CommunityWorkerEntity> data, Context context){
        this.data = data;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public CommunityWorkerAdapter.CWorkerViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if(inflater == null){
            inflater = LayoutInflater.from(parent.getContext());
        }

        CWorkerListBinding workersListBinding =  CWorkerListBinding.inflate(inflater,parent,false);
        return new CWorkerViewHolder(parent, workersListBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CommunityWorkerAdapter.CWorkerViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //nested viewholder implementation class
    public static class CWorkerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private CWorkerListBinding cworkerListBinding;

        public CWorkerViewHolder(View view1, CWorkerListBinding cworkerListBinding) {
            super(cworkerListBinding.getRoot());
            View view = cworkerListBinding.getRoot();
            view.setOnClickListener(this);
            this.cworkerListBinding = cworkerListBinding;
        }

        public void bind(CommunityWorkerEntity person){
            this.cworkerListBinding.setPerson(person);
        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(v.getContext(), FormsActivity.class);
            AppData.selectedCommunityWorker = this.cworkerListBinding.getPerson();
            v.getContext().startActivity(intent);
        }
    }


}
