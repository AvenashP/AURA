package com.avenashp.auratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    private LinearLayout sProfile, sAddContact,sViewChats,sLearn,sSettings,sSignOut,sUninstall;
    private TextView profileName,profileNumber;
    private String xUserId,xMode,xName,xAge,xCountry,xGender,xNumber,xType;
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private DatabaseReference dbUserDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        xMode = getIntent().getStringExtra("xMode");
        xAge = getIntent().getStringExtra("xAge");
        xCountry = getIntent().getStringExtra("xCountry");
        xGender = getIntent().getStringExtra("xGender");
        xName = getIntent().getStringExtra("xName");
        xNumber = getIntent().getStringExtra("xNumber");
        xType = getIntent().getStringExtra("xType");

        funInit();

        profileName.setText(xName);
        profileNumber.setText(xNumber);

        if(xMode.equals("CARE SEEKER")){
            sViewChats.setVisibility(View.VISIBLE);
        }

        sProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this,ExistingUserActivity.class);
                intent.putExtra("Activity","settings");
                startActivity(intent);
            }
        });

        sViewChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this,ContactsActivity.class);
                startActivity(intent);
            }
        });

        sLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this,LearnActivity.class);
                startActivity(intent);
            }
        });

        sSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this,"Coming Soon...!",Toast.LENGTH_SHORT).show();
            }
        });

        sAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this,AddContactActivity.class);
                intent.putExtra("Activity","settings");
                startActivity(intent);
            }
        });

        sSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Sign Out!");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(SettingsActivity.this, SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        
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
        });

        sUninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:" + "com.avenashp.auratest"));
                startActivity(intent);
            }
        });
    }

    private void funInit() {
        profileName = findViewById(R.id.profileName);
        profileNumber = findViewById(R.id.profileNumber);
        sProfile = findViewById(R.id.sProfile);
        sAddContact = findViewById(R.id.sAddContact);
        sViewChats = findViewById(R.id.sViewChats);
        sLearn = findViewById(R.id.sLearn);
        sSettings = findViewById(R.id.sSettings);
        sSignOut = findViewById(R.id.sSignOut);
        sUninstall = findViewById(R.id.sUninstall);
        fireAuth = FirebaseAuth.getInstance();
        fireUser = fireAuth.getCurrentUser();
        xUserId = fireUser.getUid();
        dbUserDetails = FirebaseDatabase.getInstance().getReference("User Details");
    }

    @Override
    public void onBackPressed() {
        if(xMode.equals("CARE SEEKER")){
            Intent intent = new Intent(SettingsActivity.this,mChatsActivity.class);
            startActivity(intent);
        }
        else if(xMode.equals("CARE GIVER")){
            Intent intent = new Intent(SettingsActivity.this,ContactsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }
}
