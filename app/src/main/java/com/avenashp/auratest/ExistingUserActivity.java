package com.avenashp.auratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileOutputStream;

public class ExistingUserActivity extends AppCompatActivity {

    private static final String TAG = "❌EXISTING❌";
    private TextView topic;
    private TextInputEditText name,age,gender,usermode,usertype;
    private Button continueBt;
    private String xUserId,xMode;
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private DatabaseReference dbUserDetails;
    private String activity;
    private ProgressDialog mProgressDialog;
    private String xName,xAge,xGender,xNumber,xCountry,xType;
    private String localName="name",localNumber="number",localAge="age",
            localGender="gender",localCountry="country",localMode="mode",localType="type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_user);

        activity = getIntent().getStringExtra("Activity");

        funInit();
        mProgressDialog.setMessage("Please wait....");
        mProgressDialog.show();
        funReadUserDetails();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
            }
        }, 1000);

        if(activity != null && activity.equals("settings")){
            topic.setText("PROFILE");
            continueBt.setVisibility(View.GONE);
        }
        else{
            topic.setText("WELCOME BACK !");
        }

        continueBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(xType.equals("A_A")){
                    Intent intent = new Intent(ExistingUserActivity.this,aChatsActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(xType.equals("T_T")){
                    Intent intent = new Intent(ExistingUserActivity.this,ContactsActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(ExistingUserActivity.this,mChatsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void funReadUserDetails() {
        dbUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(xUserId).exists()){
                    xName = dataSnapshot.child(xUserId).child("name").getValue().toString();
                    xNumber = dataSnapshot.child(xUserId).child("number").getValue().toString();
                    xAge = dataSnapshot.child(xUserId).child("age").getValue().toString();
                    xGender = dataSnapshot.child(xUserId).child("gender").getValue().toString();
                    xCountry = dataSnapshot.child(xUserId).child("country").getValue().toString();
                    xMode = dataSnapshot.child(xUserId).child("mode").getValue().toString();
                    xType = dataSnapshot.child(xUserId).child("type").getValue().toString();


                    name.setText(xName);
                    age.setText(xAge);
                    gender.setText(xGender);
                    usermode.setText(xMode);
                    if(xType.equals("V_V")){
                        usertype.setText("Deaf, Blind,Dumb".toUpperCase());
                    }
                    else if(xType.equals("V_A")){
                        usertype.setText("Dumb, Blind".toUpperCase());
                    }
                    else if(xType.equals("A_A")){
                        usertype.setText("Blind".toUpperCase());
                    }
                    else if(xType.equals("T_T") && xMode.equals("CARE SEEKER")){
                        usertype.setText("Deaf or Dumb".toUpperCase());
                    }
                    else{
                        usertype.setText("Normal".toUpperCase());
                    }

                    try{
                        FileOutputStream f1 = openFileOutput(localName,MODE_PRIVATE);
                        f1.write(xName.getBytes());
                        f1.close();

                        FileOutputStream f2 = openFileOutput(localNumber,MODE_PRIVATE);
                        f2.write(xNumber.getBytes());
                        f2.close();

                        FileOutputStream f3= openFileOutput(localAge,MODE_PRIVATE);
                        f3.write(xAge.getBytes());
                        f3.close();

                        FileOutputStream f4 = openFileOutput(localGender,MODE_PRIVATE);
                        f4.write(xGender.getBytes());
                        f4.close();

                        FileOutputStream f5 = openFileOutput(localCountry,MODE_PRIVATE);
                        f5.write(xCountry.getBytes());
                        f5.close();

                        FileOutputStream f6 = openFileOutput(localMode,MODE_PRIVATE);
                        f6.write(xMode.getBytes());
                        f6.close();

                        FileOutputStream f7 = openFileOutput(localType,MODE_PRIVATE);
                        f7.write(xType.getBytes());
                        f7.close();

                        Log.i(TAG, "onExistingUser: SAVED");
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void funInit() {
        topic = findViewById(R.id.topic);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        gender = findViewById(R.id.gender);
        usermode = findViewById(R.id.usermode);
        usertype =findViewById(R.id.usertype);
        continueBt = findViewById(R.id.continueButton);
        mProgressDialog = new ProgressDialog(ExistingUserActivity.this,R.style.AlertBox);
        fireAuth = FirebaseAuth.getInstance();
        fireUser = fireAuth.getCurrentUser();
        xUserId = fireUser.getUid();
        dbUserDetails = FirebaseDatabase.getInstance().getReference("User Details");
    }

    @Override
    public void onBackPressed() {
        if(activity != null && activity.equals("settings")){
            super.onBackPressed();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ExistingUserActivity.this,R.style.AlertBox);
            builder.setTitle("Exit Application!");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ExistingUserActivity.this.finish();
                    System.exit(0);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setCancelable(true);
            builder.show();
        }
    }
}
