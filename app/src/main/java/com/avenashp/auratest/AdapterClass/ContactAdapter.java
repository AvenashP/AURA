package com.avenashp.auratest.AdapterClass;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avenashp.auratest.Main4Activity;
import com.avenashp.auratest.R;
import com.avenashp.auratest.ModelClass.ContactModel;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ShowContactViewHolder> {

    ArrayList<ContactModel> contactModels;

    public ContactAdapter(ArrayList<ContactModel> contactModels) {
        this.contactModels = contactModels;
    }

    public class ShowContactViewHolder extends RecyclerView.ViewHolder {

        public TextView contactShort,contactLong,contactNumber;
        public LinearLayout contactLayout;

        public ShowContactViewHolder(View view){
            super(view);
            contactShort = view.findViewById(R.id.contactShort);
            contactLong = view.findViewById(R.id.contactLong);
            contactNumber = view.findViewById(R.id.contactNumber);
            contactLayout = view.findViewById(R.id.contactLayout);
        }
    }

    @NonNull
    @Override
    public ShowContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_contactlist,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        ShowContactViewHolder showContactVH = new ShowContactViewHolder(layoutView);
        return showContactVH;
    }

    @Override
    public void onBindViewHolder(@NonNull final ShowContactViewHolder holder, int position) {
        holder.contactShort.setText(contactModels.get(position).getShortName());
        holder.contactLong.setText(contactModels.get(position).getLongName());
        holder.contactNumber.setText(contactModels.get(position).getMobileNumber());

        holder.contactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Main4Activity.class);
                Bundle bundle = new Bundle();
                bundle.putString("chatID", contactModels.get(holder.getAdapterPosition()).getChatId());
                Log.i("##################","CHAT ID bundle = "+ contactModels.get(holder.getAdapterPosition()).getChatId());
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);
            }
        });
        holder.contactLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactModels.size();
    }
}
