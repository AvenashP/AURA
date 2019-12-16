package com.avenashp.auratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.avenashp.auratest.ModelClass.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class NewUserActivity extends AppCompatActivity {

    private TextInputEditText nameField,ageField,genderField,numberField, codeField;
    private Button loginButton,sendButton;
    private int xMode;
    private String xVerificationId, xName,xAge,xGender,xNumber, xError, xCode, xUserId;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private ProgressDialog mProgressDialog;
    private FirebaseUser mUser;
    private FirebaseDatabase mRootRef;
    private DatabaseReference mCareSeekerRef,mCareGiverRef,mAllUsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance();
        mCareSeekerRef = mRootRef.getReference("Care Seeker Details");
        mCareGiverRef = mRootRef.getReference("Care Giver Details");
        mAllUsersRef = mRootRef.getReference("All Users ID");
        mProgressDialog = new ProgressDialog(NewUserActivity.this);
        nameField = findViewById(R.id.nameField);
        ageField = findViewById(R.id.ageField);
        genderField = findViewById(R.id.genderField);
        numberField = findViewById(R.id.numberField);
        codeField = findViewById(R.id.codeField);
        loginButton = findViewById(R.id.loginButton);
        sendButton = findViewById(R.id.sendButton);

        xMode = getIntent().getIntExtra("xMode",2);
        //Log.i("########","-------> " + xMode);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xName = nameField.getText().toString();
                xAge = ageField.getText().toString();
                xGender = genderField.getText().toString();
                xNumber = numberField.getText().toString();

                Toast.makeText(NewUserActivity.this,"Sending Code...",Toast.LENGTH_LONG).show();
                funSendVerificationCode(xNumber);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xCode = codeField.getText().toString();
                funVerifyCode(xCode);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                xCode = phoneAuthCredential.getSmsCode();
                if (xCode != null) {
                    codeField.setText(xCode);
                    funVerifyCode(xCode);
                }
                else{
                    Toast.makeText(NewUserActivity.this,"enter the code",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(NewUserActivity.this,"Invalid Request !",Toast.LENGTH_SHORT).show();
                }
                else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(NewUserActivity.this,"SMS Exceeded",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(NewUserActivity.this,"Try again later",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                xVerificationId = s;
            }
        };
    }

    private void funSendVerificationCode(String xNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+xNumber,60, TimeUnit.SECONDS, NewUserActivity.this, mCallbacks);
    }

    private void funVerifyCode(String mCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(xVerificationId, mCode);
        funSignInWithPhoneAuth(credential);
    }

    private void funSignInWithPhoneAuth(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(NewUserActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mUser = mAuth.getCurrentUser();
                    xUserId = mUser.getUid();
                    //Log.i("#######","-----> " +mUserId);
                    UserModel userModel = new UserModel(xName,xAge,xGender,xNumber);
                    funStoreUserData(userModel);
                    mProgressDialog.dismiss();
                    Intent intent = new Intent(NewUserActivity.this, AddContactActivity.class);
                    intent.putExtra("xMode",xMode);
                    startActivity(intent);
                    finish();
                }
                else{
                    xError = "Check your Connection !";
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        xError = "Invalid Code";
                    }
                    Toast.makeText(NewUserActivity.this,xError,Toast.LENGTH_SHORT).show();
                }
            }
        });
        mProgressDialog.setMessage("Verifying Code..!");
        mProgressDialog.show();
    }

    private void funStoreUserData(UserModel userModel) {
        if(xMode == 0){
            mCareSeekerRef.child(xUserId).setValue(userModel);
        }
        else{
            mCareGiverRef.child(xUserId).setValue(userModel);
        }
        mAllUsersRef.child(xUserId).setValue(userModel);
    }
}
