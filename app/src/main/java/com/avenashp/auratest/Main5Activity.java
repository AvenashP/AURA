package com.avenashp.auratest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.avenashp.auratest.AdapterClass.ContactAdapter;
import com.avenashp.auratest.ModelClass.ContactModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Main5Activity extends AppCompatActivity {

    private RecyclerView contactRV;
    private RecyclerView.Adapter contactRV_A;
    private RecyclerView.LayoutManager contactRV_LM;
    private ArrayList<ContactModel> contactModels;

    private String xUserId;
    int xMode;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mRootRef;
    private DatabaseReference mCareSeekerRef,mCareGiverRef,mAllUsersRef,mContactRef,mChatManagerRef;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        contactModels = new ArrayList<>();
        xMode = getIntent().getIntExtra("xMode",2);

        contactRV = findViewById(R.id.contactRV);
        contactRV.setNestedScrollingEnabled(false);
        contactRV.setHasFixedSize(false);
        contactRV_LM = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL,false);
        contactRV.setLayoutManager(contactRV_LM);
        contactRV_A = new ContactAdapter(contactModels);
        contactRV.setAdapter(contactRV_A);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        xUserId = mUser.getUid();
        mRootRef = FirebaseDatabase.getInstance();
        mProgressDialog = new ProgressDialog(Main5Activity.this);
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
        Log.i("############","Contact ref = " + mContactRef);

        mProgressDialog.setMessage("Loading Contacts...");
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        funShowContacts();

    }

    private void funShowContacts() {
        mContactRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                contactModels.clear();
                Log.i("############","On CHILD ADDED");
                if(dataSnapshot.exists()) {
                    Log.i("############", "snap exist");

                    String Ln, Sn, Mn, Ci;
                    //for(DataSnapshot snap : dataSnapshot.getChildren()){
                    Ln = dataSnapshot.child("longName").getValue().toString();
                    Sn = dataSnapshot.child("shortName").getValue().toString();
                    Mn = dataSnapshot.child("mobileNumber").getValue().toString();
                    Ci = dataSnapshot.child("chatId").getValue().toString();

                    Log.i("############", "contact " + Ln + Mn + Sn);

                    ContactModel scm = new ContactModel(Sn, Ln, Mn, Ci);
                    contactModels.add(scm);
                    contactRV_A.notifyDataSetChanged();

                    Log.i("############", "contact " + Ln + Mn + Sn);
                    //}
                    mProgressDialog.dismiss();
                }
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
        /*mContactRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("############","On CHILD ADDED");
                if(dataSnapshot.exists()){
                    Log.i("############","snap exist");

                    String Ln,Sn,Mn,Ci;
                    for(DataSnapshot snap : dataSnapshot.getChildren()){
                        Ln = snap.child("longName").getValue().toString();
                        Sn = snap.child("shortName").getValue().toString();
                        Mn = snap.child("mobileNumber").getValue().toString();
                        Ci = snap.child("chatId").getValue().toString();

                        Log.i("############","contact " +Ln+Mn+Sn);

                        ContactModel scm = new ContactModel(Sn,Ln,Mn,Ci);
                        contactModels.add(scm);
                        contactRV_A.notifyDataSetChanged();

                        Log.i("############","contact " +Ln+Mn+Sn);
                    }
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.addcontactsMenu:
                startActivity(new Intent(Main5Activity.this,Main3Activity.class));
                finish();
                break;

            case R.id.logoutMenu:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Sign Out!");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(Main5Activity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
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
                return(true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        System.exit(0);
        super.onBackPressed();
    }
}
