package com.moh.ihrisupdatetool.adapaters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moh.ihrisupdatetool.databinding.DistrictListBinding;
import com.moh.ihrisupdatetool.db.entities.DistrictEntity;
import com.moh.ihrisupdatetool.utils.AppData;
import com.moh.ihrisupdatetool.views.FormsActivity;
import com.moh.ihrisupdatetool.views.PersonSearchActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DistrictsAdapter extends RecyclerView.Adapter<DistrictsAdapter.DistrictViewHolder> {

    List<DistrictEntity> data;
    Context context;
    private LayoutInflater inflater;

    public DistrictsAdapter(List<DistrictEntity> data, Context context){
        this.data = data;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public DistrictsAdapter.DistrictViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if(inflater == null){
            inflater = LayoutInflater.from(parent.getContext());
        }

        DistrictListBinding districtListBinding =  DistrictListBinding.inflate(inflater,parent,false);
        return new DistrictViewHolder(parent, districtListBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DistrictsAdapter.DistrictViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
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
            Intent intent = new Intent(v.getContext(), PersonSearchActivity.class);
            AppData.selectedDistrict = districtListBinding.getDistrict();
            v.getContext().startActivity(intent);
        }
    }


}
