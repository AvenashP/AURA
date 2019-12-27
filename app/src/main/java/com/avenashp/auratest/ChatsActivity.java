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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

public class ChatsActivity extends AppCompatActivity implements ChatAdapter.OnChatClickListener{

    private static final String TAG = "❌GIVER-CHAT❌";
    private RecyclerView chatlist;
    private RecyclerView.LayoutManager chatlistLM;
    private TextInputEditText msgInput;
    private Button sendButton;
    private ArrayList<ChatModel> chatModels;
    private ChatAdapter chatAdapter;
    private String xMessage,xUserId,xChatid,xDate,xTime,xMorseCode;
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private DatabaseReference dbChatManager,dbCurrentChat;
    private Map<Character, String> xDict = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        xChatid = getIntent().getExtras().getString("chatid");
        funCreateDictionary();

        funInit();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View view) {
                xMessage = msgInput.getText().toString().toUpperCase().trim();
                xDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                xTime = new SimpleDateFormat("hh:mm a").format(new Date());
                xMorseCode = funConvertToMorseCode(xMessage);
                if(xMorseCode == null){
                    Toast.makeText(ChatsActivity.this,"INVALID CHARACTERS !",Toast.LENGTH_SHORT).show();
                }
                else{
                    ChatModel chm  = new ChatModel(xMessage,xTime,xUserId,xDate,xMorseCode);
                    dbChatManager.child(xChatid).push().setValue(chm);
                    msgInput.setText("");
                }
            }
        });
        funReadChats();
    }

    private String funConvertToMorseCode(String xMessage) {
        char[] ch = xMessage.toCharArray();
        String str = "";
        String sp=" ";
        for(int i=0;i<ch.length;i++){
            if(i == (ch.length) - 1){
                sp="";
            }
            if(xDict.get(ch[i]) == null){
                return null;
            }
            else{
                str = str + xDict.get(ch[i]) + sp;
            }
        }
        return str;
    }

    private void funReadChats() {
        dbCurrentChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    Log.i(TAG, "SNAP_CHAT: "+dataSnapshot);
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

    private void funCreateDictionary() {
        xDict.put(' ', "");
        xDict.put('A', "•−");
        xDict.put('B', "−•••");
        xDict.put('C', "−•−•");
        xDict.put('D', "−••");
        xDict.put('E', "•");
        xDict.put('F', "••−•");
        xDict.put('G', "−−•");
        xDict.put('H', "••••");
        xDict.put('I', "••");
        xDict.put('J', "•−−−");
        xDict.put('K', "−•−");
        xDict.put('L', "•−••");
        xDict.put('M', "−−");
        xDict.put('N', "−•");
        xDict.put('O', "−−−");
        xDict.put('P', "•−−•");
        xDict.put('Q', "−−•−");
        xDict.put('R', "•−•");
        xDict.put('S', "•••");
        xDict.put('T', "−");
        xDict.put('U', "••−");
        xDict.put('V', "•••−");
        xDict.put('W', "•−−");
        xDict.put('X', "−••−");
        xDict.put('Y', "−•−−");
        xDict.put('Z', "−−••");

        xDict.put('0', "−−−−−");
        xDict.put('1', "•−−−−");
        xDict.put('2', "••−−−");
        xDict.put('3', "•••−−");
        xDict.put('4', "••••−");
        xDict.put('5', "•••••");
        xDict.put('6', "−••••");
        xDict.put('7', "−−•••");
        xDict.put('8', "−−−••");
        xDict.put('9', "−−−−•");

        xDict.put('@', "•−−•−•");
        xDict.put('_', "••−−•−");
        xDict.put('&', "•−•••");
        xDict.put('-', "−••••−");
        xDict.put('=', "−•••−");
        xDict.put('+', "•−•−•");
        xDict.put('(', "−•−−•");
        xDict.put(')', "−•−−•−");
        xDict.put('/', "−••−•");
        xDict.put('.', "•−•−•−");
        xDict.put(',', "−−••−−");
        xDict.put('\'', "•−−−−•");
        xDict.put(':', "−−−•••");
        xDict.put(';', "−•−•−•");
        xDict.put('!', "−•−•−−");
        xDict.put('?', "••−−••");
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
