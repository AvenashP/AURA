package com.avenashp.auratest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.avenashp.auratest.ModelClass.ContactModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddContactActivity extends AppCompatActivity {

    private Button addButton,doneButton;
    private TextInputEditText longField,shortField,numberField;
    private String xUserId;
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private DatabaseReference dbUserDetails,dbUserContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

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


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = numberField.getText().toString();
                dbUserContacts.child("short name").setValue(shortField.getText().toString());
                dbUserContacts.child("long name").setValue(longField.getText().toString());
                dbUserDetails.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snap : dataSnapshot.getChildren())
                        {
                            String Uid = snap.getKey().toString();
                            String Cnum = snap.child("number").getValue().toString();
                            String xChatId="";

                            char[] ch1 = xUserId.toCharArray();
                            char[] ch2 = Uid.toCharArray();

                            if(Cnum.equals(xMobileNumber)){
                                if(xMode == 1){
                                    for(int i=14;i<28;i++){
                                        xChatId = xChatId + ch1[i] + ch2[i];
                                    }
                                }
                                else{
                                    for(int i=14;i<28;i++){
                                        xChatId = xChatId + ch2[i] + ch1[i];
                                    }
                                }
                                Log.i("*-*-*-*-*-*-*-*-*-*-*","CHATID = "+xChatId);
                                ContactModel contactModels = new ContactModel(xShortName,xLongName,xMobileNumber,xChatId);
                                mContactRef.child(Uid).setValue(contactModels);
                                longField.setText("");
                                shortField.setText("");
                                numberField.setText("");
                                Toast.makeText(AddContactActivity.this,"contact added !",Toast.LENGTH_SHORT).show();
                                FLAG = 0;
                                break;
                            }
                            else{
                                FLAG = 1;
                            }
                        }
                        if(FLAG == 1){
                            Toast.makeText(AddContactActivity.this,"not a registered user",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //error
                    }
                });
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddContactActivity.this, ContactsActivity.class);
                intent.putExtra("xMode",xMode);
                startActivity(intent);
                finish();
            }
        });
    }
}
