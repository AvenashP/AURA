package com.avenashp.auratest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
    private String xUserId,xMode,xGender,xNumber,xCountry;
    private int xType = 0;
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
                        xType = 1;
                    }
                    else if(BLIND && DUMB){
                        xType = 2;
                    }
                    else if(BLIND){
                        xType = 3;
                    }
                    else{
                        xType = 0;
                    }
                }
                else{
                    xType = 0;
                }
                funSaveUserDetails();
                Intent intent = new Intent(NewUserActivity.this,AddContactActivity.class);
                intent.putExtra("xMode",xMode);
                startActivity(intent);
                finish();
            }
        });
    }

    private void funSaveUserDetails() {
        dbUserDetails.child(xUserId).child("name").setValue(nameField.getText().toString().toUpperCase());
        dbUserDetails.child(xUserId).child("age").setValue(ageField.getText().toString().toUpperCase());
        dbUserDetails.child(xUserId).child("country").setValue(xCountry);
        dbUserDetails.child(xUserId).child("gender").setValue(xGender);
        dbUserDetails.child(xUserId).child("mode").setValue(xMode);
        dbUserDetails.child(xUserId).child("type").setValue(xType);
        dbUserDetails.child(xUserId).child("number").setValue(xNumber);
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
