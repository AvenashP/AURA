package com.avenashp.auratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.avenashp.auratest.ModelClass.ChatModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main4Activity extends AppCompatActivity {

    private TextView prevMessage, currMessage;
    private TextInputEditText msgInput;
    private Button sendButton;

    private String xUserId,xChatId, xMessage;
    int xMode;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mRootRef;
    private DatabaseReference mCareSeekerRef,mCareGiverRef,mAllUsersRef,mContactRef,mChatManagerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        xChatId = getIntent().getExtras().getString("chatID");

        prevMessage = findViewById(R.id.prevMessage);
        currMessage = findViewById(R.id.currMessage);
        msgInput = findViewById(R.id.msgInput);
        sendButton = findViewById(R.id.sendButton);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        xUserId = mUser.getUid();
        mRootRef = FirebaseDatabase.getInstance();
        mCareSeekerRef = mRootRef.getReference("Care Seeker Details");
        mCareGiverRef = mRootRef.getReference("Care Giver Details");
        mAllUsersRef = mRootRef.getReference("All Users ID");
        mChatManagerRef = mRootRef.getReference("Chats Manager").child(xChatId);
        final Query lastQuery = mChatManagerRef.orderByKey().limitToLast(1);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View view) {
                xMessage = msgInput.getText().toString();
                //String dt = DateFormat.getDateTimeInstance().format(new Date());
                //SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                //SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                String date = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
                String time = new SimpleDateFormat("hh:mm a").format(new Date());
                Log.i("################","DATE AND TIME = " + date+time);
                ChatModel adc = new ChatModel(xUserId,xMessage,date,time);
                mChatManagerRef.push().setValue(adc);

                lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snap : dataSnapshot.getChildren()) {
                            xMessage = snap.child("message").getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                prevMessage.setText(currMessage.getText().toString());
                currMessage.setText(xMessage);
            }
        });
    }
}
