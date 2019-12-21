package com.avenashp.auratest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.avenashp.auratest.AdapterClass.ChatAdapter;
import com.avenashp.auratest.AdapterClass.ChatAdapter;
import com.avenashp.auratest.ModelClass.ChatModel;
import com.avenashp.auratest.ModelClass.ChatModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatsActivity extends AppCompatActivity implements ChatAdapter.OnChatClickListener{

    private static final String TAG = "❌❌❌❌❌";
    private RecyclerView chatlist;
    private RecyclerView.LayoutManager chatlistLM;
    private TextInputEditText msgInput;
    private Button sendButton;
    private ArrayList<ChatModel> chatModels;
    private ChatAdapter chatAdapter;
    private String xMessage,xUserId,xChatid,xDate,xTime;
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private DatabaseReference dbChatManager,dbCurrentChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        xChatid = getIntent().getExtras().getString("chatid");

        funInit();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xMessage = msgInput.getText().toString();
                msgInput.setText("");
                xDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                xTime = new SimpleDateFormat("hh:mm a").format(new Date());

                ChatModel chm  = new ChatModel(xMessage,xTime,xUserId,xDate);
                dbChatManager.child(xChatid).push().setValue(chm);
            }
        });
        funReadChats();
    }

    private void funReadChats() {
        dbCurrentChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    Log.i(TAG, "CHAT: "+dataSnapshot);
                    ChatModel chm1 = dataSnapshot.getValue(ChatModel.class);
                    chatModels.add(chm1);
                }
                chatAdapter = new ChatAdapter(chatModels,ChatsActivity.this);
                chatlist.setAdapter(chatAdapter);
                chatlistLM.scrollToPosition(chatModels.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onChatClick(int position) {
        //DO SOMETHING
    }

    private void funInit() {
        sendButton = findViewById(R.id.sendButton);
        msgInput = findViewById(R.id.msgInput);
        chatModels = new ArrayList<>();
        chatlist = findViewById(R.id.chatlist);
        chatlist.setHasFixedSize(true);
        chatlistLM = new LinearLayoutManager(this);
        chatlist.setLayoutManager(chatlistLM);
        fireAuth = FirebaseAuth.getInstance();
        fireUser = fireAuth.getCurrentUser();
        xUserId = fireUser.getUid();
        dbChatManager = FirebaseDatabase.getInstance().getReference("Chat Manager");
        dbCurrentChat = dbChatManager.child(xChatid);
    }
}
