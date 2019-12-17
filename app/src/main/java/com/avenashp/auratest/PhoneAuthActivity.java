package com.avenashp.auratest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PhoneAuthActivity extends AppCompatActivity {

    private TextInputEditText numberField, codeField;
    private Button sendButton;
    /*private String xVerificationId, xNumber, xCode, xUserId;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private ProgressDialog mProgressDialog;
    private FirebaseUser mUser;
    private FirebaseDatabase mRootRef;
    private DatabaseReference mCareSeekerRef,mCareGiverRef,mAllUsersRef;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        numberField = findViewById(R.id.numberField);
        codeField = findViewById(R.id.codeField);
        sendButton = findViewById(R.id.sendButton);
    }
}
