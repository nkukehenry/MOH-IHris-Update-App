package com.moh.ihrisupdatetool.adapaters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moh.ihrisupdatetool.databinding.FacilityListBinding;
import com.moh.ihrisupdatetool.db.entities.DistrictEntity;
import com.moh.ihrisupdatetool.db.entities.FacilityEntity;
import com.moh.ihrisupdatetool.views.FormsActivity;
import com.moh.ihrisupdatetool.views.PersonSearchActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FacilitiesAdapter extends RecyclerView.Adapter<FacilitiesAdapter.FacilityViewHolder> {

    List<FacilityEntity> data;
    Context context;
    private LayoutInflater inflater;

    public FacilitiesAdapter(List<FacilityEntity> data, Context context){
        this.data = data;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public FacilitiesAdapter.FacilityViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if(inflater == null){
            inflater = LayoutInflater.from(parent.getContext());
        }

        FacilityListBinding facilityListBinding =  FacilityListBinding.inflate(inflater,parent,false);
        return new FacilityViewHolder(parent, facilityListBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FacilitiesAdapter.FacilityViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //nested viewholder implementation class
    public static class FacilityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private FacilityListBinding faciltyListBinding;

        public FacilityViewHolder(View view1, FacilityListBinding districtListBinding) {
            super(districtListBinding.getRoot());
            View view = districtListBinding.getRoot();
            view.setOnClickListener(this);
            this.faciltyListBinding = districtListBinding;
        }

        public void bind(FacilityEntity facility){
            this.faciltyListBinding.setFacility(facility);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), PersonSearchActivity.class);
            //intent.putExtra(SELECTED_FACILITY, this.districtListBinding.getFacility());
            v.getContext().startActivity(intent);
        }
    }


}
