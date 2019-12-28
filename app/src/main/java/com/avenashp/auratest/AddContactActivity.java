package com.avenashp.auratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddContactActivity extends AppCompatActivity {

    private static final String TAG = "❌❌❌❌❌";
    private Button addButton,doneButton;
    private TextInputEditText shortField,numberField;
    private String xUserId,xMode;
    private boolean FLAG;
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private DatabaseReference dbUserDetails,dbUserContacts;
    private String activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        activity = getIntent().getStringExtra("Activity");

        funInit();

        if(activity != null && activity.equals("settings")){
            doneButton.setVisibility(View.GONE);
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = numberField.getText().toString();
                String sname = shortField.getText().toString().toUpperCase();
                funSaveUserContact(number,sname);
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(xMode.equals("CARE SEEKER")){
                Intent intent = new Intent(AddContactActivity.this, mChatsActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                Intent intent = new Intent(AddContactActivity.this, ContactsActivity.class);
                startActivity(intent);
                finish();
            }
            }
        });
    }

    private void funSaveUserContact(final String number, final String sname) {
        dbUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                xMode = dataSnapshot.child(xUserId).child("mode").getValue().toString();
                for (DataSnapshot snap : dataSnapshot.getChildren())
                {
                    String usernum = snap.child("number").getValue().toString();

                    if(number.equals(usernum)){

                        String userid = snap.getKey();
                        String chatid = funGenerateChatID(userid);
                        String lname = snap.child("name").getValue().toString();

                        dbUserContacts.child(userid).child("short_name").setValue(sname);
                        dbUserContacts.child(userid).child("long_name").setValue(lname);
                        dbUserContacts.child(userid).child("number").setValue(number);
                        dbUserContacts.child(userid).child("chat_id").setValue(chatid);

                        shortField.setText("");
                        numberField.setText("");

                        Toast.makeText(AddContactActivity.this,"contact added !",Toast.LENGTH_SHORT).show();
                        FLAG = false;
                        break;
                    }
                    else{
                        FLAG = true;
                    }
                }
                if(FLAG){
                    Toast.makeText(AddContactActivity.this,"not a registered user",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //error
            }
        });
    }

    private String funGenerateChatID(String userid) {
        char[] ch1 = xUserId.toCharArray();
        char[] ch2 = userid.toCharArray();
        String chatid = "";
        if(xMode.equals("CARE SEEKER")){
            for(int i=14;i<xUserId.length();i++){
                chatid = chatid + ch1[i] +ch2[i];
            }
        }
        else{
            for(int i=14;i<xUserId.length();i++){
                chatid = chatid + ch2[i] +ch1[i];
            }
        }
        return chatid;
    }

    private void funInit() {
        addButton = findViewById(R.id.addButton);
        doneButton = findViewById(R.id.nextButton);
        shortField = findViewById(R.id.shortField);
        numberField = findViewById(R.id.numberField);
        fireAuth = FirebaseAuth.getInstance();
        fireUser = fireAuth.getCurrentUser();
        xUserId = fireUser.getUid();
        dbUserDetails = FirebaseDatabase.getInstance().getReference("User Details");
        dbUserContacts = dbUserDetails.child(xUserId).child("Contacts");
    }
    @Override
    public void onBackPressed() {
        if(activity != null && activity.equals("settings")){
            super.onBackPressed();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(AddContactActivity.this);
            builder.setTitle("Exit Application!");
            builder.setMessage("Are you sure?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AddContactActivity.this.finish();
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
