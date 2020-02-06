package com.avenashp.auratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "❌SPLASH❌";
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private String xMode,xType;
    private String localMode="mode",localType="type";

    public int TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        fireAuth = FirebaseAuth.getInstance();
        fireUser = fireAuth.getCurrentUser();
        try{
            FileInputStream f6 = openFileInput(localType);
            FileInputStream f7 = openFileInput(localMode);
            int c;
            String temp1 ="",temp2="";
            while((c = f6.read())!= -1){
                temp1 = temp1 + Character.toString((char)c);
            }
            while((c = f7.read())!= -1){
                temp2 = temp2 + Character.toString((char)c);
            }
            Log.d(TAG, "SPALSH: "+xType+" "+xMode+" "+fireUser);
            xType = temp1;
            xMode = temp2;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //Toast.makeText(this, xType+" "+xMode+" "+fireUser, Toast.LENGTH_SHORT).show();

        if(fireUser != null){
            if(xType.equals("T_T")){
                Intent intent = new Intent(SplashActivity.this, ContactsActivity.class);
                startActivity(intent);
                finish();
            }
            else if(xType.equals("A_A")){
                Intent intent = new Intent(SplashActivity.this, aChatsActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                Intent intent = new Intent(SplashActivity.this, mChatsActivity.class);
                startActivity(intent);
                finish();
            }
        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, PhoneAuthActivity.class));
                    finish();
                }
            }, TIME_OUT);
        }
    }
}
