package com.moh.ihrisupdatetool.adapaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moh.ihrisupdatetool.databinding.DistrictListBinding;
import com.moh.ihrisupdatetool.db.entities.DistrictEntity;
import com.moh.ihrisupdatetool.utils.AppData;
import com.moh.ihrisupdatetool.views.DistrictsActivity;
import com.moh.ihrisupdatetool.views.SynchronizationActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SyncAdapter extends RecyclerView.Adapter<SyncAdapter.DistrictViewHolder> {

    List<DistrictEntity> data;
    List<DistrictEntity> unFilteredData;
    private static Context context;
    private LayoutInflater inflater;

    public SyncAdapter(List<DistrictEntity> data, Context context){
        this.data = data;
        this.unFilteredData = new ArrayList<>(data);
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public SyncAdapter.DistrictViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if(inflater == null){
            inflater = LayoutInflater.from(parent.getContext());
        }

        DistrictListBinding districtListBinding =  DistrictListBinding.inflate(inflater,parent,false);
        return new DistrictViewHolder(parent, districtListBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SyncAdapter.DistrictViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void filterDistricts(List<DistrictEntity> filteredList) {
        data = filteredList;
        notifyDataSetChanged();
    }

    //nested viewholder implementation class
    public static class DistrictViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private DistrictListBinding districtListBinding;

        public DistrictViewHolder(View view1, DistrictListBinding districtListBinding) {
            super(districtListBinding.getRoot());
            View view = districtListBinding.getRoot();
            view.setOnClickListener(this);
            this.districtListBinding = districtListBinding;
        }

        public void bind(DistrictEntity district){
            this.districtListBinding.setDistrict(district);
        }

        @Override
        public void onClick(View v) {

            ((SynchronizationActivity) context ).selectDistrict(districtListBinding.getDistrict().getDistrictName());

        }
    }


}
