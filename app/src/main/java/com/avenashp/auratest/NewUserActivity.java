package com.avenashp.auratest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.FileOutputStream;
import java.util.Arrays;

public class NewUserActivity extends AppCompatActivity {

    private static final String TAG = "❌❌❌❌❌";
    private LinearLayout seekerLayout;
    private TextInputEditText nameField,ageField,modeRadio,genderRadio;
    private CheckBox blindBox,deafBox,dumbBox;
    private Boolean BLIND, DEAF, DUMB;
    private Button nextButton;
    private String xUserId,xName,xAge,xMode,xGender,xNumber,xCountry,xType;
    private String localName="name",localNumber="number",localAge="age",
            localGender="gender",localCountry="country",localMode="mode",localType="type";
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private DatabaseReference dbUserDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        xNumber = getIntent().getStringExtra("xNumber");
        xCountry = getIntent().getStringExtra("xCountry");

        funInit();

        genderRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewUserActivity.this,R.style.AlertBox);
                builder.setTitle("Gender");
                final String[] genders = {"Male","Female","Others"};
                builder.setItems(genders, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        genderRadio.setText(genders[which]);
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(true);
                builder.show();
            }
        });

        modeRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewUserActivity.this,R.style.AlertBox);
                builder.setTitle("Mode");
                final String[] modes = {"Care Giver","Care Seeker"};
                builder.setItems(modes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 1){
                            seekerLayout.setVisibility(View.VISIBLE);
                        }
                        if(which == 0){
                            seekerLayout.setVisibility(View.INVISIBLE);
                        }
                        modeRadio.setText(modes[which]);
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(true);
                builder.show();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xGender = genderRadio.getText().toString().toUpperCase();
                xMode = modeRadio.getText().toString().toUpperCase();
                if(xMode.equals("CARE SEEKER")){
                    BLIND = blindBox.isChecked();
                    DEAF = deafBox.isChecked();
                    DUMB = dumbBox.isChecked();
                    if((DEAF && BLIND && DUMB) || (DEAF && BLIND)){
                        xType = "V_V";
                    }
                    else if(BLIND && DUMB){
                        xType = "V_A";
                    }
                    else if(BLIND){
                        xType = "A_A";
                    }
                    else{
                        xType = "T_T";
                    }
                }
                else{
                    xType = "T_T";
                }
                funSaveUserDetails();
                Intent intent = new Intent(NewUserActivity.this,AddContactActivity.class);
                intent.putExtra("xMode",xMode);
                intent.putExtra("xType",xType);
                startActivity(intent);
                finish();
            }
        });
    }

    private void funSaveUserDetails() {
        xName = nameField.getText().toString().toUpperCase();
        xAge = ageField.getText().toString().toUpperCase();
        dbUserDetails.child(xUserId).child("name").setValue(xName);
        dbUserDetails.child(xUserId).child("age").setValue(xAge);
        dbUserDetails.child(xUserId).child("country").setValue(xCountry);
        dbUserDetails.child(xUserId).child("gender").setValue(xGender);
        dbUserDetails.child(xUserId).child("mode").setValue(xMode);
        dbUserDetails.child(xUserId).child("type").setValue(xType);
        dbUserDetails.child(xUserId).child("number").setValue(xNumber);

        try{
            FileOutputStream f1 = openFileOutput(localName,MODE_PRIVATE);
            f1.write(xName.getBytes());
            f1.close();

            FileOutputStream f2 = openFileOutput(localNumber,MODE_PRIVATE);
            f2.write(xNumber.getBytes());
            f2.close();

            FileOutputStream f3= openFileOutput(localAge,MODE_PRIVATE);
            f3.write(xAge.getBytes());
            f3.close();

            FileOutputStream f4 = openFileOutput(localGender,MODE_PRIVATE);
            f4.write(xGender.getBytes());
            f4.close();

            FileOutputStream f5 = openFileOutput(localCountry,MODE_PRIVATE);
            f5.write(xCountry.getBytes());
            f5.close();

            FileOutputStream f6 = openFileOutput(localMode,MODE_PRIVATE);
            f6.write(xMode.getBytes());
            f6.close();

            FileOutputStream f7 = openFileOutput(localType,MODE_PRIVATE);
            f7.write(xType.getBytes());
            f7.close();

            Log.i(TAG, "onNewUser: SAVED");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void funInit() {
        seekerLayout = findViewById(R.id.seekerLayout);
        nameField = findViewById(R.id.nameField);
        ageField = findViewById(R.id.ageField);
        genderRadio = findViewById(R.id.genderRadio);
        blindBox = findViewById(R.id.blind);
        deafBox = findViewById(R.id.deaf);
        dumbBox = findViewById(R.id.dumb);
        modeRadio = findViewById(R.id.modeRadio);
        nextButton = findViewById(R.id.nextButton);
        fireAuth = FirebaseAuth.getInstance();
        fireUser = fireAuth.getCurrentUser();
        xUserId = fireUser.getUid();
        dbUserDetails = FirebaseDatabase.getInstance().getReference("User Details");
    }

    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(NewUserActivity.this, PhoneAuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
