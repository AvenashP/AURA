package com.avenashp.auratest;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewUserActivity extends AppCompatActivity {

    private static final String TAG = "❌❌❌❌❌";
    private TextInputEditText nameField,ageField,countryField;
    private RadioGroup modeRadio,genderRadio;
    private Button nextButton;
    private ProgressDialog mProgressDialog;
    private String xUserId,xMode,xGender;
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private DatabaseReference dbUserDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        funInit();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog.setMessage("Saving data...");
                mProgressDialog.show();
                switch(genderRadio.indexOfChild(findViewById(genderRadio.getCheckedRadioButtonId()))){
                    case 0:
                        xGender = "Male";
                        break;
                    case 1:
                        xGender = "Female";
                        break;
                    case 2:
                        xGender = "Others";
                        break;
                }
                switch(modeRadio.indexOfChild(findViewById(modeRadio.getCheckedRadioButtonId()))){
                    case 0:
                        xMode = "Care Seeker";
                        break;
                    case 1:
                        xMode = "Care Giver";
                        break;
                }
                funSaveUserDetails();
                mProgressDialog.dismiss();
                Intent intent =new Intent(NewUserActivity.this,AddContactActivity.class);
                intent.putExtra("xMode",xMode);
                startActivity(intent);
                finish();
            }
        });
    }

    private void funSaveUserDetails() {
        dbUserDetails.child(xUserId).child("name").setValue(nameField.getText().toString());
        dbUserDetails.child(xUserId).child("age").setValue(ageField.getText().toString());
        dbUserDetails.child(xUserId).child("country").setValue(countryField.getText().toString());
        dbUserDetails.child(xUserId).child("gender").setValue(xGender);
        dbUserDetails.child(xUserId).child("mode").setValue(xMode);
    }

    private void funInit() {
        nameField = findViewById(R.id.nameField);
        ageField = findViewById(R.id.ageField);
        genderRadio = findViewById(R.id.genderRadio);
        modeRadio = findViewById(R.id.modeRadio);
        countryField = findViewById(R.id.countryField);
        nextButton = findViewById(R.id.nextButton);
        mProgressDialog = new ProgressDialog(NewUserActivity.this);
        fireAuth = FirebaseAuth.getInstance();
        fireUser = fireAuth.getCurrentUser();
        xUserId = fireUser.getUid();
        dbUserDetails = FirebaseDatabase.getInstance().getReference("User Details");
    }
}
