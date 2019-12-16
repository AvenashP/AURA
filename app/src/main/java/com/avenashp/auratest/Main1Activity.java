package com.avenashp.auratest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Main1Activity extends AppCompatActivity {
    private Button modeButton;
    private RadioGroup modeRadio;
    private int xMode = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        funInitializeVariables();

        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xMode = modeRadio.indexOfChild(findViewById(modeRadio.getCheckedRadioButtonId()));
                //Log.i("########","-------> " + xMode);
                if(xMode == 2){
                    Toast.makeText(Main1Activity.this,"select a Mode !",Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(Main1Activity.this, Main2Activity.class);
                    intent.putExtra("xMode",xMode);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }

    private void funInitializeVariables() {
        modeButton = findViewById(R.id.modeButton);
        modeRadio = findViewById(R.id.modeRadio);
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
        super.onBackPressed();
    }
}
