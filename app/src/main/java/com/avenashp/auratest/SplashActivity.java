package com.avenashp.auratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "❌SPLASH❌";
    private String xUserId,xMode;
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private DatabaseReference dbUserDetails;
    public int TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        fireAuth = FirebaseAuth.getInstance();
        fireUser = fireAuth.getCurrentUser();
        dbUserDetails = FirebaseDatabase.getInstance().getReference("User Details");

        if(fireUser != null){
            xUserId = fireUser.getUid();
            dbUserDetails.child(xUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    xMode = dataSnapshot.child("mode").getValue().toString();
                    //Log.i(TAG, "onDataChange 111: "+xMode);
                    if(xMode.equals("Care Giver")){
                        startActivity(new Intent(SplashActivity.this, ContactsActivity.class));
                        finish();
                    }
                    else{
                        startActivity(new Intent(SplashActivity.this, mContactsActivity.class));
                        finish();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, PhoneAuthActivity.class));
                    finish();
                }
            }, TIME_OUT);
        }
    }
}
