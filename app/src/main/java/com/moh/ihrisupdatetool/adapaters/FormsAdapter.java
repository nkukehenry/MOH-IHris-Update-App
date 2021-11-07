package com.moh.ihrisupdatetool.adapaters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moh.ihrisupdatetool.databinding.FacilityListBinding;
import com.moh.ihrisupdatetool.databinding.FormsListBinding;
import com.moh.ihrisupdatetool.db.entities.FormEntity;
import com.moh.ihrisupdatetool.views.FormDataActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.moh.ihrisupdatetool.utils.AppConstants.SELECTED_FORM;

public class FormsAdapter extends RecyclerView.Adapter<FormsAdapter.FormViewHolder> {

    List<FormEntity> data;
    Context context;
    private LayoutInflater inflater;

    public FormsAdapter(List<FormEntity> data, Context context){
        this.data = data;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public FormsAdapter.FormViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if(inflater == null){
            inflater = LayoutInflater.from(parent.getContext());
        }

        FormsListBinding formsListBinding =  FormsListBinding.inflate(inflater,parent,false);
        return new FormViewHolder(parent, formsListBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FormsAdapter.FormViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //nested viewholder implementation class
    public static class FormViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private FormsListBinding formsListBinding;

        public FormViewHolder(View view1, FormsListBinding districtListBinding) {
            super(districtListBinding.getRoot());
            View view = districtListBinding.getRoot();
            view.setOnClickListener(this);
            this.formsListBinding = districtListBinding;
        }

        public void bind(FormEntity form){
            this.formsListBinding.setForm(form);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), FormDataActivity.class);
            intent.putExtra(SELECTED_FORM, this.formsListBinding.getForm());
            v.getContext().startActivity(intent);
        }
    }


}
