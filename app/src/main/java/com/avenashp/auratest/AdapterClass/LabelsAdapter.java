package com.avenashp.auratest.AdapterClass;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.avenashp.auratest.ModelClass.LabelsModel;
import com.avenashp.auratest.R;

import java.util.ArrayList;

public class LabelsAdapter extends RecyclerView.Adapter<LabelsAdapter.LabelViewHolder> {

    private ArrayList<LabelsModel> labelsModels;
    private LabelsAdapter.OnLabelClickListener onLabelListener;

    public LabelsAdapter(ArrayList<LabelsModel> labelsModels, LabelsAdapter.OnLabelClickListener onLabelClickListener) {
        this.labelsModels = labelsModels;
        this.onLabelListener = onLabelClickListener;
    }

    @NonNull
    @Override
    public LabelsAdapter.LabelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_labelslist,parent,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        return new LabelsAdapter.LabelViewHolder(layoutView,onLabelListener);
    }
    @Override
    public void onBindViewHolder(@NonNull final LabelsAdapter.LabelViewHolder holder, int position) {
        LabelsModel cm = labelsModels.get(position);
        holder.label.setText(cm.getLabel());
        holder.accuracy.setText(cm.getAccuracy().toString());
    }

    @Override
    public int getItemCount() {
        return labelsModels.size();
    }

    public class LabelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView label,accuracy;
        LabelsAdapter.OnLabelClickListener onLabelClickListener;

        private LabelViewHolder(View view, LabelsAdapter.OnLabelClickListener onLabelClickListener){
            super(view);
            label = view.findViewById(R.id.label);
            accuracy = view.findViewById(R.id.accuracy);
            this.onLabelClickListener = onLabelClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onLabelClickListener.onLabelClick(getAdapterPosition());
        }
    }

    public interface OnLabelClickListener{
        void onLabelClick(int position);
    }
}
