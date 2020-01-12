package com.avenashp.auratest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;

public class NewUserActivity extends AppCompatActivity {

    private static final String TAG = "❌❌❌❌❌";
    private LinearLayout seekerLayout;
    private TextInputEditText nameField,ageField;
    private RadioGroup modeRadio,genderRadio;
    private RadioButton giver, seeker;
    private CheckBox blindBox,deafBox,dumbBox;
    private Boolean BLIND, DEAF, DUMB;
    private Button nextButton;
    private ProgressDialog mProgressDialog;
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

        seekerLayout.setVisibility(View.GONE);

        seeker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekerLayout.setVisibility(View.VISIBLE);
            }
        });

        giver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekerLayout.setVisibility(View.GONE);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(genderRadio.indexOfChild(findViewById(genderRadio.getCheckedRadioButtonId()))){
                    case 0:
                        xGender = "MALE";
                        break;
                    case 1:
                        xGender = "FEMALE";
                        break;
                    case 2:
                        xGender = "OTHERS";
                        break;
                }
                switch(modeRadio.indexOfChild(findViewById(modeRadio.getCheckedRadioButtonId()))){
                    case 0:
                        xMode = "CARE GIVER";
                        break;
                    case 1:
                        xMode = "CARE SEEKER";
                        break;
                }
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
        seeker = findViewById(R.id.seekerRadio);
        giver = findViewById(R.id.giverRadio);
        nextButton = findViewById(R.id.nextButton);
        mProgressDialog = new ProgressDialog(NewUserActivity.this);
        fireAuth = FirebaseAuth.getInstance();
        fireUser = fireAuth.getCurrentUser();
        xUserId = fireUser.getUid();
        dbUserDetails = FirebaseDatabase.getInstance().getReference("User Details");
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewUserActivity.this);
        builder.setTitle("Exit Application!");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NewUserActivity.this.finish();
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
