package com.avenashp.auratest;

import androidx.annotation.NonNull;
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
    private String xUserId;
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private DatabaseReference dbUserDetails,dbUserContacts;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        funInit();

        funReadContacts();
    }

    private void funReadContacts() {
        mProgressDialog.setMessage("Loading Contacts...");
        mProgressDialog.show();
        dbUserContacts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snap : dataSnapshot.getChildren()){

                        //Log.i(TAG, "contacts: "+snap);

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

        //Log.i(TAG, "onContactClick: "+chatid);

        Intent intent = new Intent(ContactsActivity.this,ChatsActivity.class);
        intent.putExtra("chatid",chatid);
        startActivity(intent);

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
                startActivity(new Intent(ContactsActivity.this, AddContactActivity.class));
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
                        Intent intent = new Intent(ContactsActivity.this, SplashActivity.class);
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
