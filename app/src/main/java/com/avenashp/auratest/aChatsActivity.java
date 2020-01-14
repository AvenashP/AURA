package com.avenashp.auratest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avenashp.auratest.ModelClass.ChatModel;
import com.avenashp.auratest.ModelClass.ContactModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class aChatsActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextView audioMsg,chatName;
    private LinearLayout record;
    private Boolean CONTACT = false;
    private SpeechRecognizer STT;
    private static final String TAG = "❌SEEKER-CHAT❌";
    private String xUserId,xName,xMode,xAge,xCountry,xGender,xType,xNumber,xChatid,xDate,xTime,xMessage,xMorseCode;
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private DatabaseReference dbUserDetails,dbChatManager,dbUserContacts;
    private Query lastquery;
    private Map<String, String> xDict = new HashMap();
    private TextToSpeech TTS;
    private String localName="name",localNumber="number",localAge="age",
            localGender="gender",localCountry="country",localMode="mode",localType="type";
    private Vibrator vibrator;
    private int back=2;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_chats);

        funInit();
        funReadUserDetails();
        funCreateDictionary();


        final Intent STTIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        STTIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        STTIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        STT.startListening(STTIntent);
                        audioMsg.setText("");
                        audioMsg.setHint("Listening....");
                        break;

                    case MotionEvent.ACTION_UP:
                        audioMsg.setText("");
                        audioMsg.setHint("");
                        STT.stopListening();
                        break;
                }
                return false;
            }
        });

        funReadLastMessage();

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

            @SuppressLint("SimpleDateFormat")
            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(matches != null) {
                    xMessage = matches.get(0).toUpperCase().trim();
                    String[] arr = xMessage.split(" ");

                    if (!CONTACT && matches != null) {
                        audioMsg.setText(xMessage);
                        final String cName = TextUtils.join("", arr);

                        if(cName.equals("CAMERA")){
                            Intent intent = new Intent(aChatsActivity.this,CameraActivity.class);
                            audioMsg.setText("");
                            intent.putExtra("Activity","aChats");
                            startActivity(intent);
                        }
                        else {
                            dbUserContacts.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        int EMPTY = 0;
                                        for (DataSnapshot snap : dataSnapshot.getChildren()) {

                                            ContactModel cm = snap.getValue(ContactModel.class);

                                            if (cm.getShort_name().equals(cName)) {
                                                EMPTY = 0;
                                                xChatid = cm.getChat_id();
                                                String Name = cm.getLong_name();
                                                lastquery = dbChatManager.child(xChatid).orderByKey().limitToLast(1);
                                                CONTACT = true;
                                                chatName.setText("To : "+Name);
                                                audioMsg.setText("");
                                                funReadLastMessage();
                                                break;
                                            } else {
                                                EMPTY = 1;
                                            }
                                        }
                                        if (EMPTY == 1) {
                                            vibrator.vibrate(800);
                                            funSpeech("please try again");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    else {
                        if(xMessage.equals("CLOSE")){
                            CONTACT = false;
                            chatName.setText("");
                            audioMsg.setText("");
                            vibrator.vibrate(800);
                        }
                        else if(xMessage.equals("REPEAT")){
                            funReadLastMessage();
                        }
                        else {
                            audioMsg.setText(xMessage);
                            xDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                            xTime = new SimpleDateFormat("hh:mm a").format(new Date());
                            xMorseCode = funConvertToMorseCode(xMessage);

                            ChatModel chm = new ChatModel(xMessage, xTime, xUserId, xDate, xMorseCode);
                            dbChatManager.child(xChatid).push().setValue(chm);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    audioMsg.setText("");
                                    audioMsg.setHint("Message Sent !");
                                }
                            }, 800);
                        }
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {
            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

    }

    private void funSpeech(final String str) {
        final int[] speechStatus = new int[1];
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                speechStatus[0] = TTS.speak(str.toLowerCase(), TextToSpeech.QUEUE_FLUSH, null);
                audioMsg.setText(str);
            }
        }, 800);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                audioMsg.setText("");
            }
        }, 2300);

        if (speechStatus[0] == TextToSpeech.ERROR) {
            Log.e("TTS", "Error in converting Text to Speech!");
        }
    }

    private void funInit() {
        chatName = findViewById(R.id.chatName);
        audioMsg = findViewById(R.id.audioMsg);
        record = findViewById(R.id.record);
        fireAuth = FirebaseAuth.getInstance();
        fireUser = fireAuth.getCurrentUser();
        xUserId = fireUser.getUid();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        dbUserDetails = FirebaseDatabase.getInstance().getReference("User Details");
        dbChatManager = FirebaseDatabase.getInstance().getReference("Chat Manager");
        dbUserContacts = dbUserDetails.child(xUserId).child("Contacts");
        STT = SpeechRecognizer.createSpeechRecognizer(this);
        TTS = new TextToSpeech(aChatsActivity.this, this);
    }

    private void funReadLastMessage() {
        if(CONTACT){
            lastquery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(!xUserId.equals(dataSnapshot.child("sender").getValue().toString())){
                        String RecMsg = dataSnapshot.child("message").getValue().toString();
                        funSpeech(RecMsg);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private String funConvertToMorseCode(String xMessage) {
        char[] ch = xMessage.toCharArray();
        String str = "";
        String sp=" ";
        for(int i=0;i<ch.length;i++){
            if(i == (ch.length) - 1){
                sp="";
            }
            if(xDict.get(Character.toString(ch[i])) == null){
                return null;
            }
            else{
                str = str + xDict.get(Character.toString(ch[i])) + sp;
            }
        }
        return str;
    }

    private void funCreateDictionary() {
        xDict.put(" ", " ");
        xDict.put("A", "•−");
        xDict.put("B", "−•••");
        xDict.put("C", "−•−•");
        xDict.put("D", "−••");
        xDict.put("E", "•");
        xDict.put("F", "••−•");
        xDict.put("G", "−−•");
        xDict.put("H", "••••");
        xDict.put("I", "••");
        xDict.put("J", "•−−−");
        xDict.put("K", "−•−");
        xDict.put("L", "•−••");
        xDict.put("M", "−−");
        xDict.put("N", "−•");
        xDict.put("O", "−−−");
        xDict.put("P", "•−−•");
        xDict.put("Q", "−−•−");
        xDict.put("R", "•−•");
        xDict.put("S", "•••");
        xDict.put("T", "−");
        xDict.put("U", "••−");
        xDict.put("V", "•••−");
        xDict.put("W", "•−−");
        xDict.put("X", "−••−");
        xDict.put("Y", "−•−−");
        xDict.put("Z", "−−••");

        xDict.put("0", "−−−−−");
        xDict.put("1", "•−−−−");
        xDict.put("2", "••−−−");
        xDict.put("3", "•••−−");
        xDict.put("4", "••••−");
        xDict.put("5", "•••••");
        xDict.put("6", "−••••");
        xDict.put("7", "−−•••");
        xDict.put("8", "−−−••");
        xDict.put("9", "−−−−•");

        xDict.put("@", "•−−•−•");
        xDict.put("_", "••−−•−");
        xDict.put("&", "•−•••");
        xDict.put("-", "−••••−");
        xDict.put("=", "−•••−");
        xDict.put("+", "•−•−•");
        xDict.put("(", "−•−−•");
        xDict.put(")", "−•−−•−");
        xDict.put("/", "−••−•");
        xDict.put(".", "•−•−•−");
        xDict.put(",", "−−••−−");
        xDict.put("'", "•−−−−•");
        xDict.put(":", "−−−•••");
        xDict.put(";", "−•−•−•");
        xDict.put("!", "−•−•−−");
        xDict.put("?", "••−−••");
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

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            int ttsLang = TTS.setLanguage(Locale.US);
            if (ttsLang == TextToSpeech.LANG_MISSING_DATA || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language is not supported!");
            } else {
                Log.i("TTS", "Language Supported.");
            }
            Log.i("TTS", "Initialization success.");
        } else {
            Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (TTS != null) {
            TTS.stop();
            TTS.shutdown();
        }
    }

    @Override
    public void onBackPressed() {
        if(back == 0){
            Intent intent =  new Intent(aChatsActivity.this,SettingsActivity.class);
            intent.putExtra("xName",xName);
            intent.putExtra("xMode",xMode);
            intent.putExtra("xNumber",xNumber);
            intent.putExtra("xGender",xGender);
            intent.putExtra("xCountry",xCountry);
            intent.putExtra("xAge",xAge);
            intent.putExtra("xType",xType);
            startActivity(intent);
        }
        else{
            final Toast toast = Toast.makeText(aChatsActivity.this,"Press back button "+back+" times", Toast.LENGTH_SHORT);
            toast.show();
            back--;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 500);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                back=2;
            }
        }, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }
}
