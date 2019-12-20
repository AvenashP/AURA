package com.avenashp.auratest.AdapterClass;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.avenashp.auratest.R;
import com.avenashp.auratest.ModelClass.ContactModel;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private ArrayList<ContactModel> contactModels;
    private OnContactClickListener onContactListener;

    public ContactAdapter(ArrayList<ContactModel> contactModels, OnContactClickListener onContactClickListener) {
        this.contactModels = contactModels;
        this.onContactListener = onContactClickListener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_contactlist,parent,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        return new ContactViewHolder(layoutView,onContactListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactViewHolder holder, int position) {
        ContactModel cm = contactModels.get(position);
        holder.contactShort.setText(cm.getShort_name());
        holder.contactLong.setText(cm.getLong_name());
        holder.contactNumber.setText(cm.getNumber());
    }

    @Override
    public int getItemCount() {
        return contactModels.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView contactShort,contactLong,contactNumber;
        private LinearLayout contactLayout;
        OnContactClickListener onContactClickListener;

        private ContactViewHolder(View view, OnContactClickListener onContactClickListener){
            super(view);
            contactShort = view.findViewById(R.id.contactShort);
            contactLong = view.findViewById(R.id.contactLong);
            contactNumber = view.findViewById(R.id.contactNumber);
            contactLayout = view.findViewById(R.id.contactLayout);
            this.onContactClickListener = onContactClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onContactClickListener.onContactClick(getAdapterPosition());
        }
    }

    public interface OnContactClickListener{
        void onContactClick(int position);
    }
}
