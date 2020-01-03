package com.avenashp.auratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ExistingUserActivity extends AppCompatActivity {

    private static final String TAG = "❌EXISTING❌";
    private TextView name,age,gender,country,usermode,topic;
    private Button continueBt;
    private String xUserId,xMode;
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private DatabaseReference dbUserDetails;
    private String activity;
    private ProgressDialog mProgressDialog;


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
            topic.setText("EXISTING USER");
        }

        continueBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(xMode.equals("CARE SEEKER")){
                    Intent intent = new Intent(ExistingUserActivity.this,mChatsActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(ExistingUserActivity.this,ContactsActivity.class);
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
                    xMode = dataSnapshot.child(xUserId).child("mode").getValue().toString();
                    name.setText(dataSnapshot.child(xUserId).child("name").getValue().toString());
                    age.setText(dataSnapshot.child(xUserId).child("age").getValue().toString());
                    country.setText(dataSnapshot.child(xUserId).child("country").getValue().toString());
                    gender.setText(dataSnapshot.child(xUserId).child("gender").getValue().toString());
                    usermode.setText(xMode);
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
        country = findViewById(R.id.country);
        usermode = findViewById(R.id.usermode);
        continueBt = findViewById(R.id.continueBt);
        mProgressDialog = new ProgressDialog(ExistingUserActivity.this);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(ExistingUserActivity.this);
            builder.setTitle("Exit Application!");
            builder.setMessage("Are you sure?");
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
