package com.avenashp.auratest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.avenashp.auratest.ModelClass.ContactModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Main3Activity extends AppCompatActivity {

    private Button addButton,nextButton;
    private TextInputEditText longField,shortField,numberField;
    private String xLongName,xShortName,xMobileNumber,xUserId;
    int xMode,FLAG=0;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mRootRef;
    private DatabaseReference mCareSeekerRef,mCareGiverRef,mAllUsersRef,mContactRef,mChatManagerRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        xMode = getIntent().getIntExtra("xMode",2);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        xUserId = mUser.getUid();
        mRootRef = FirebaseDatabase.getInstance();

        mCareSeekerRef = mRootRef.getReference("Care Seeker Details");
        mCareGiverRef = mRootRef.getReference("Care Giver Details");
        mAllUsersRef = mRootRef.getReference("All Users ID");
        mChatManagerRef = mRootRef.getReference("Chats Manager");
        if(xMode == 0) {
            mContactRef = mCareSeekerRef.child(xUserId).child("Contacts");
        }
        else {
            mContactRef = mCareGiverRef.child(xUserId).child("Contacts");
        }
        addButton = findViewById(R.id.addButton);
        nextButton = findViewById(R.id.nextButton);
        longField = findViewById(R.id.longField);
        shortField = findViewById(R.id.shortField);
        numberField = findViewById(R.id.numberField);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xLongName = longField.getText().toString();
                xShortName = shortField.getText().toString();
                xMobileNumber = numberField.getText().toString();

                mAllUsersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snap : dataSnapshot.getChildren())
                        {
                            String Uid = snap.getKey().toString();
                            String Cnum = snap.child("number").getValue().toString();
                            String xChatId="";

                            char[] ch1 = xUserId.toCharArray();
                            char[] ch2 = Uid.toCharArray();

                            if(Cnum.equals(xMobileNumber)){
                                if(xMode == 1){
                                    for(int i=14;i<28;i++){
                                        xChatId = xChatId + ch1[i] + ch2[i];
                                    }
                                }
                                else{
                                    for(int i=14;i<28;i++){
                                        xChatId = xChatId + ch2[i] + ch1[i];
                                    }
                                }
                                Log.i("*-*-*-*-*-*-*-*-*-*-*","CHATID = "+xChatId);
                                ContactModel contactModels = new ContactModel(xShortName,xLongName,xMobileNumber,xChatId);
                                mContactRef.child(Uid).setValue(contactModels);
                                longField.setText("");
                                shortField.setText("");
                                numberField.setText("");
                                Toast.makeText(Main3Activity.this,"contact added !",Toast.LENGTH_SHORT).show();
                                FLAG = 0;
                                break;
                            }
                            else{
                                FLAG = 1;
                            }
                        }
                        if(FLAG == 1){
                            Toast.makeText(Main3Activity.this,"not a registered user",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //error
                    }
                });
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main3Activity.this,Main5Activity.class);
                intent.putExtra("xMode",xMode);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(Main3Activity.this,Main5Activity.class));
        super.onBackPressed();
    }
}
