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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
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
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    private static final String TAG = "❌❌❌❌❌";
    private TextInputEditText numberField, codeField,countryView;
    private TextView userHint;
    private Button verifyButton;
    private ProgressDialog mProgressDialog;
    private String xVerificationId,xCode,xUserId,xNumber,xCountry,xCountryCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private DatabaseReference dbUserDetails;
    private int FLAG = 0, num=0, VERIFY=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        FirebaseApp.initializeApp(this);

        funInit();
        funGetPermission();

        countryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PhoneAuthActivity.this,R.style.AlertBox);
                builder.setTitle("Choose your Country:");
                final String[] countries = PhoneAuthActivity.this.getResources().getStringArray(R.array.country_names);
                final String[] codes = PhoneAuthActivity.this.getResources().getStringArray(R.array.country_codes);
                builder.setItems(countries, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        countryView.setText(countries[which]);
                        xCountryCode = codes[which];
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(true);
                builder.show();
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(VERIFY == 0){
                    xNumber = numberField.getText().toString();
                    xCountry = countryView.getText().toString().toUpperCase();
                    mProgressDialog.setMessage("Sending Code...");
                    mProgressDialog.show();

                    //  SEND VERIFICATION CODE TO PHONE NUMBER
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(xCountryCode+xNumber,60, TimeUnit.SECONDS, PhoneAuthActivity.this, mCallbacks);
                }
                else{
                    xCode  = codeField.getText().toString();
                    funVerifyCode(xCode);
                    VERIFY = 0;
                    FLAG = 0;
                }
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
                    Toast.makeText(PhoneAuthActivity.this,"Invalid entry!",Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
                else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(PhoneAuthActivity.this,"SMS limit Exceeded, try after 3 hours. ",Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
                else{
                    Toast.makeText(PhoneAuthActivity.this,"Try again later",Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                xVerificationId = s;
                userHint.setText("We have sent you a Verification code\nvia SMS to the number "+xNumber);
                countryView.setVisibility(View.INVISIBLE);
                numberField.setVisibility(View.INVISIBLE);
                codeField.setVisibility(View.VISIBLE);
                VERIFY = 1;
                mProgressDialog.dismiss();
            }
        };
    }

    private void funGetPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.DELETE_PACKAGES,
            },1);
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
        countryView = findViewById(R.id.countryView);
        userHint = findViewById(R.id.userHint);
        codeField = findViewById(R.id.codeField);
        verifyButton = findViewById(R.id.verifyButton);
        mProgressDialog = new ProgressDialog(PhoneAuthActivity.this,R.style.AlertBox);
        fireAuth = FirebaseAuth.getInstance();
        fireUser = fireAuth.getCurrentUser();
        if(fireUser != null)
            xUserId = fireUser.getUid();
        dbUserDetails = FirebaseDatabase.getInstance().getReference("User Details");
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PhoneAuthActivity.this,R.style.AlertBox);
        builder.setTitle("Exit Application?");
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
