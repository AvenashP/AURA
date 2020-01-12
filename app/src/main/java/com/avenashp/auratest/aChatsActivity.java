package com.avenashp.auratest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class aChatsActivity extends AppCompatActivity {

    private TextView audioMsg,chatName;
    private LinearLayout record;
    private Boolean CONTACT = false;
    private SpeechRecognizer STT;
    private static final String TAG = "❌SEEKER-CHAT❌";
    private Vibrator vibrator;
    private String[] ch = new String[1000];
    private String[] str = new String[1000];
    private String mMsg,tMsg,xMessage="",xRecived;
    private Map<String, String> xDict = new HashMap();
    private String xUserId,xChatid,xDate,xTime,xMorseCode,xName,xMode,xAge,xCountry,xGender,xType,xNumber;
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private DatabaseReference dbUserDetails,dbChatManager,dbUserContacts;
    private Query lastquery;
    private String arrLabel = "";
    private int arrAccuracy;
    private TextToSpeech TTS;
    private String localName="name",localNumber="number",localAge="age",
            localGender="gender",localCountry="country",localMode="mode",localType="type";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_chats);

        funReadUserDetails();


        chatName = findViewById(R.id.chatName);
        audioMsg = findViewById(R.id.audioMsg);
        record = findViewById(R.id.record);
        fireAuth = FirebaseAuth.getInstance();
        fireUser = fireAuth.getCurrentUser();
        xUserId = fireUser.getUid();
        dbUserDetails = FirebaseDatabase.getInstance().getReference("User Details");
        dbChatManager = FirebaseDatabase.getInstance().getReference("Chat Manager");
        dbUserContacts = dbUserDetails.child(xUserId).child("Contacts");
        STT = SpeechRecognizer.createSpeechRecognizer(this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        final Intent STTIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        STTIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        STTIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        STT.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null)
                    audioMsg.setText(matches.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.i(TAG, "onTouch: ");
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        audioMsg.setText("");
                        STT.stopListening();
                        break;

                    case MotionEvent.ACTION_DOWN:
                        STT.startListening(STTIntent);
                        audioMsg.setText("");
                        break;
                }
                return false;
            }
        });
    }

    private void funReadUserDetails() {
        try{
            FileInputStream f1 = openFileInput(localName);
            FileInputStream f2 = openFileInput(localNumber);
            FileInputStream f3 = openFileInput(localAge);
            FileInputStream f4 = openFileInput(localGender);
            FileInputStream f5 = openFileInput(localCountry);
            FileInputStream f6 = openFileInput(localMode);
            FileInputStream f7 = openFileInput(localType);
            int c;
            String temp1 ="",temp2="",temp3 ="",temp4="",temp5 ="",temp6="",temp7 ="";
            while((c = f1.read())!= -1){
                temp1 = temp1 + Character.toString((char)c);
            }
            while((c = f2.read())!= -1){
                temp2 = temp2 + Character.toString((char)c);
            }
            while((c = f3.read())!= -1){
                temp3 = temp3 + Character.toString((char)c);
            }
            while((c = f4.read())!= -1){
                temp4 = temp4 + Character.toString((char)c);
            }
            while((c = f5.read())!= -1){
                temp5 = temp5 + Character.toString((char)c);
            }
            while((c = f6.read())!= -1){
                temp6 = temp6 + Character.toString((char)c);
            }
            while((c = f7.read())!= -1){
                temp7 = temp7 + Character.toString((char)c);
            }
            xName = temp1;
            xNumber = temp2;
            xAge = temp3;
            xGender = temp4;
            xCountry = temp5;
            xMode = temp6;
            xType = temp7;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
