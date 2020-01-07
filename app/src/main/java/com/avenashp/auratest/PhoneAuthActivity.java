package com.avenashp.auratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
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
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    private static final String TAG = "❌❌❌❌❌";
    private TextInputEditText numberField, codeField;
    private TextView countryList;
    private Button sendButton,loginButton;
    private ProgressDialog mProgressDialog;
    private String xVerificationId,xCode,xUserId,xNumber,xCountry,xCountryCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private DatabaseReference dbUserDetails;
    private int FLAG = 0, num=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        FirebaseApp.initializeApp(this);

        funInit();
        funGetPermission();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xNumber = numberField.getText().toString();
                xCountry = countryList.getText().toString().toUpperCase();
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
                FLAG = 0;
            }
        });
        countryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PhoneAuthActivity.this);
                builder.setTitle("Choose your Country");
                final String[] countries = PhoneAuthActivity.this.getResources().getStringArray(R.array.country_names);
                final String[] codes = PhoneAuthActivity.this.getResources().getStringArray(R.array.country_codes);
                final int checkedItem;
                if(FLAG == 1) {
                    checkedItem = num;
                }
                else{
                    checkedItem = 0;
                }
                final String[] name = new String[1];
                final String[] code = new String[1];

                builder.setSingleChoiceItems(countries, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        num = which;
                        name[0] = countries[which];
                        code[0] = codes[which];
                        FLAG = 1;
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(name[0]!=null) {
                            countryList.setText(name[0]);
                            xCountryCode = code[0];
                            Log.i(TAG, "onCCCC: " + xCountryCode);
                        }
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int index = Arrays.binarySearch(countries, countryList.getText().toString());
                        num =  (index < 0) ? -1 : index;
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(true);
                builder.show();
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
                sendButton.setVisibility(View.GONE);
                mProgressDialog.dismiss();
            }
        };
    }

    private void funGetPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.INTERNET},1);
        }
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
                                intent.putExtra("xCountry",xCountry);
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
        countryList = findViewById(R.id.countryView);
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
