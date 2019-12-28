package com.avenashp.auratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    private static final String TAG = "❌❌❌❌❌";
    private TextInputEditText numberField, codeField;
    private Button sendButton,loginButton;
    private ProgressDialog mProgressDialog;
    private String xVerificationId,xCode,xUserId,xNumber;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private DatabaseReference dbUserDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        FirebaseApp.initializeApp(this);

        funInit();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xNumber = numberField.getText().toString();
                mProgressDialog.setMessage("Sending Code...");
                mProgressDialog.show();

                //  SEND VERIFICATION CODE TO PHONE NUMBER
                PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+xNumber,60, TimeUnit.SECONDS, PhoneAuthActivity.this, mCallbacks);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xCode  = codeField.getText().toString();
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
            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(PhoneAuthActivity.this,"Invalid Request !",Toast.LENGTH_SHORT).show();
                }
                else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(PhoneAuthActivity.this,"SMS Exceeded",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(PhoneAuthActivity.this,"Try again later",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                xVerificationId = s;
                loginButton.setVisibility(View.VISIBLE);
                mProgressDialog.dismiss();
            }
        };
    }

    private void funVerifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(xVerificationId, code);
        funSignInWithPhoneAuth(credential);
    }

    private void funSignInWithPhoneAuth(PhoneAuthCredential credential) {
        fireAuth.signInWithCredential(credential).addOnCompleteListener(PhoneAuthActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    fireUser = fireAuth.getCurrentUser();
                    xUserId = fireUser.getUid();
                    dbUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child(xUserId).exists()){
                                mProgressDialog.dismiss();
                                Intent intent = new Intent(PhoneAuthActivity.this,ExistingUserActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                mProgressDialog.dismiss();
                                Intent intent = new Intent(PhoneAuthActivity.this,NewUserActivity.class);
                                intent.putExtra("xNumber",xNumber);
                                startActivity(intent);
                                finish();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        Toast.makeText(PhoneAuthActivity.this,"Invalid Code",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        mProgressDialog.setMessage("Verifying Code..!");
        mProgressDialog.show();
    }

    private void funInit() {
        numberField = findViewById(R.id.numberField);
        codeField = findViewById(R.id.codeField);
        sendButton = findViewById(R.id.sendButton);
        loginButton = findViewById(R.id.loginButton);
        mProgressDialog = new ProgressDialog(PhoneAuthActivity.this);
        fireAuth = FirebaseAuth.getInstance();
        fireUser = fireAuth.getCurrentUser();
        if(fireUser != null)
            xUserId = fireUser.getUid();
        dbUserDetails = FirebaseDatabase.getInstance().getReference("User Details");
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PhoneAuthActivity.this);
        builder.setTitle("Exit Application!");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PhoneAuthActivity.this.finish();
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
