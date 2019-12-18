package com.avenashp.auratest;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewUserActivity extends AppCompatActivity {

    private TextInputEditText nameField,ageField,genderField,countryField;
    private Button nextButton;
    private ProgressDialog mProgressDialog;
    private String xUserId;
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
                mProgressDialog.setMessage("Verifying Code..!");
                mProgressDialog.show();
                dbUserDetails.child(xUserId).child("name").setValue(nameField.getText().toString());
                dbUserDetails.child(xUserId).child("age").setValue(ageField.getText().toString());
                dbUserDetails.child(xUserId).child("gender").setValue(genderField.getText().toString());
                dbUserDetails.child(xUserId).child("country").setValue(countryField.getText().toString());
                mProgressDialog.dismiss();
                startActivity(new Intent(NewUserActivity.this,UserActivity.class));
                finish();
            }
        });
    }

    private void funInit() {
        nameField = findViewById(R.id.nameField);
        ageField = findViewById(R.id.ageField);
        genderField = findViewById(R.id.genderField);
        countryField = findViewById(R.id.countryField);
        nextButton = findViewById(R.id.nextButton);
        mProgressDialog = new ProgressDialog(NewUserActivity.this);
        fireAuth = FirebaseAuth.getInstance();
        fireUser = fireAuth.getCurrentUser();
        xUserId = fireUser.getUid();
        dbUserDetails = FirebaseDatabase.getInstance().getReference("User Details");
    }
}
