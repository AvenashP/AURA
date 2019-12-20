package com.avenashp.auratest.AdapterClass;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avenashp.auratest.ChatsActivity;
import com.avenashp.auratest.R;
import com.avenashp.auratest.ModelClass.ChatModel;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private ArrayList<ChatModel> chatModels;
    private OnChatClickListener onChatListener;

    public ChatAdapter(ArrayList<ChatModel> chatModels, OnChatClickListener onChatClickListener) {
        this.chatModels = chatModels;
        this.onChatListener = onChatClickListener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_chatlist,parent,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        return new ChatViewHolder(layoutView,onChatListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatViewHolder holder, int position) {
        ChatModel cm = chatModels.get(position);
        holder.chatMessage.setText(cm.getMessage());
        holder.chatTime.setText(cm.getTime());
    }

    @Override
    public int getItemCount() {
        return chatModels.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView chatMessage,chatTime;
        OnChatClickListener onChatClickListener;

        private ChatViewHolder(View view, OnChatClickListener onChatClickListener){
            super(view);
            chatMessage = view.findViewById(R.id.chatMessage);
            chatTime = view.findViewById(R.id.chatTime);
            this.onChatClickListener = onChatClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onChatClickListener.onChatClick(getAdapterPosition());
        }
    }

    public interface OnChatClickListener{
        void onChatClick(int position);
    }
}
