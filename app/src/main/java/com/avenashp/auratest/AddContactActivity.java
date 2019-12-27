package com.avenashp.auratest;

import androidx.appcompat.app.AppCompatActivity;

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
    private TextInputEditText longField,shortField,numberField;
    private String xUserId,xMode;
    private boolean FLAG;
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private DatabaseReference dbUserDetails,dbUserContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        funInit();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = numberField.getText().toString();
                String sname = shortField.getText().toString().toUpperCase();
                String lname = longField.getText().toString().toUpperCase();

                funSaveUserContact(number,sname,lname);
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(xMode.equals("CARE SEEKER")){
                startActivity(new Intent(AddContactActivity.this, mChatsActivity.class));
                finish();
            }
            else{
                startActivity(new Intent(AddContactActivity.this, ContactsActivity.class));
                finish();
            }
            }
        });
    }

    private void funSaveUserContact(final String number, final String sname, final String lname) {
        dbUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                xMode = dataSnapshot.child(xUserId).child("mode").getValue().toString();
                for (DataSnapshot snap : dataSnapshot.getChildren())
                {
                    String userid = snap.getKey();
                    String usernum = snap.child("number").getValue().toString();

                    if(number.equals(usernum)){

                        String chatid = funGenerateChatID(userid);

                        dbUserContacts.child(userid).child("short_name").setValue(sname);
                        dbUserContacts.child(userid).child("long_name").setValue(lname);
                        dbUserContacts.child(userid).child("number").setValue(number);
                        dbUserContacts.child(userid).child("chat_id").setValue(chatid);

                        longField.setText("");
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
        longField = findViewById(R.id.longField);
        shortField = findViewById(R.id.shortField);
        numberField = findViewById(R.id.numberField);
        fireAuth = FirebaseAuth.getInstance();
        fireUser = fireAuth.getCurrentUser();
        xUserId = fireUser.getUid();
        dbUserDetails = FirebaseDatabase.getInstance().getReference("User Details");
        dbUserContacts = dbUserDetails.child(xUserId).child("Contacts");
    }
}
