package com.avenashp.auratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

    private static final String TAG = "❌❌❌❌❌";
    private TextView name,age,gender,country,usermode;
    private Button continueBt;
    private String xUserId;
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private DatabaseReference dbUserDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_user);

        funInit();
        funReadUserDetails();

        continueBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExistingUserActivity.this,ContactsActivity.class));
                finish();
            }
        });
    }

    private void funReadUserDetails() {
        dbUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(xUserId).exists()){
                    name.setText(dataSnapshot.child(xUserId).child("name").getValue().toString());
                    age.setText(dataSnapshot.child(xUserId).child("age").getValue().toString());
                    country.setText(dataSnapshot.child(xUserId).child("country").getValue().toString());
                    gender.setText(dataSnapshot.child(xUserId).child("gender").getValue().toString());
                    usermode.setText(dataSnapshot.child(xUserId).child("mode").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void funInit() {
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        gender = findViewById(R.id.gender);
        country = findViewById(R.id.country);
        usermode = findViewById(R.id.usermode);
        continueBt = findViewById(R.id.continueBt);
        fireAuth = FirebaseAuth.getInstance();
        fireUser = fireAuth.getCurrentUser();
        xUserId = fireUser.getUid();
        dbUserDetails = FirebaseDatabase.getInstance().getReference("User Details");
    }
}
