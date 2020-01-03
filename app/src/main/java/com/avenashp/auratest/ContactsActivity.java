package com.avenashp.auratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.avenashp.auratest.AdapterClass.ContactAdapter;
import com.avenashp.auratest.ModelClass.ContactModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity implements ContactAdapter.OnContactClickListener{

    private static final String TAG = "❌GIVER-CONTACTS❌";
    private RecyclerView contactList;
    private ArrayList<ContactModel> contactModel;
    private ContactAdapter contactAdapter;
    private ProgressDialog mProgressDialog;
    private String xUserId,xMode,xName,xAge,xCountry,xGender,xType,xNumber;
    private int back=2;
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private DatabaseReference dbUserDetails,dbUserContacts;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        funInit();
        funReadUserDetails();
        funReadContacts();
    }

    private void funReadUserDetails() {
        dbUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(xUserId).exists()){
                    xMode = dataSnapshot.child(xUserId).child("mode").getValue().toString();
                    xName = dataSnapshot.child(xUserId).child("name").getValue().toString();
                    xAge = dataSnapshot.child(xUserId).child("age").getValue().toString();
                    xCountry = dataSnapshot.child(xUserId).child("country").getValue().toString();
                    xGender = dataSnapshot.child(xUserId).child("gender").getValue().toString();
                    xType = dataSnapshot.child(xUserId).child("type").getValue().toString();
                    xNumber = dataSnapshot.child(xUserId).child("number").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void funReadContacts() {
        mProgressDialog.setMessage("Loading Contacts...");
        mProgressDialog.show();
        dbUserContacts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snap : dataSnapshot.getChildren()){

                        ContactModel cm = snap.getValue(ContactModel.class);
                        contactModel.add(cm);
                    }
                    contactAdapter = new ContactAdapter(contactModel,ContactsActivity.this);
                    contactList.setAdapter(contactAdapter);
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void funInit() {
        mProgressDialog = new ProgressDialog(ContactsActivity.this);
        contactList = findViewById(R.id.contactList);
        contactList.setHasFixedSize(true);
        contactList.setLayoutManager(new LinearLayoutManager(this));
        contactModel = new ArrayList<>();
        fireAuth = FirebaseAuth.getInstance();
        fireUser = fireAuth.getCurrentUser();
        xUserId = fireUser.getUid();
        dbUserDetails = FirebaseDatabase.getInstance().getReference("User Details");
        dbUserContacts = dbUserDetails.child(xUserId).child("Contacts");
    }

    @Override
    public void onContactClick(int position) {
        ContactModel cml = contactModel.get(position);
        String chatid = cml.getChat_id();

        Intent intent = new Intent(ContactsActivity.this,ChatsActivity.class);
        intent.putExtra("xMode",xMode);
        intent.putExtra("chatid",chatid);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        if(xMode.equals("CARE SEEKER")){
            super.onBackPressed();
        }
        else{
            if(back == 0){
                Intent intent =  new Intent(ContactsActivity.this,SettingsActivity.class);
                intent.putExtra("xName",xName);
                intent.putExtra("xMode",xMode);
                intent.putExtra("xNumber",xNumber);
                intent.putExtra("xGender",xGender);
                intent.putExtra("xCountry",xCountry);
                intent.putExtra("xAge",xAge);
                intent.putExtra("xType",xType);
                startActivity(intent);
            }
            else{
                final Toast toast = Toast.makeText(ContactsActivity.this,"Press back button "+back+" times", Toast.LENGTH_SHORT);
                toast.show();
                back--;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 500);
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    back=2;
                }
            }, 2000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }
}
