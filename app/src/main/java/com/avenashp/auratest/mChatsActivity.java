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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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

public class mChatsActivity extends AppCompatActivity implements TextToSpeech.OnInitListener  {

    private static final String TAG = "❌SEEKER-CHAT❌";
    private TextView textmsg,morsemsg,morseletter,contact;
    private LinearLayout touch,audio;
    private Vibrator vibrator;
    private String[] ch = new String[1000];
    private String[] str = new String[1000];
    private String mMsg,tMsg,xMessage="",xRecived;
    private int index=0,DSPACE=0,back=2;
    private Map<String, String> xDict = new HashMap();
    private String xUserId,xChatid,xDate,xTime,xMorseCode,xName,xMode,xAge,xCountry,xGender,xType,xNumber;
    private FirebaseAuth fireAuth;
    private FirebaseUser fireUser;
    private DatabaseReference dbUserDetails,dbChatManager,dbUserContacts;
    private Query lastquery;
    private Boolean CONTACT = false;
    private String arrLabel = "";
    private int arrAccuracy;
    private TextToSpeech TTS;
    private String localName="name",localNumber="number",localAge="age",
            localGender="gender",localCountry="country",localMode="mode",localType="type";



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_m_chat);

        arrLabel = getIntent().getStringExtra("arrLabel");
        arrAccuracy = getIntent().getIntExtra("arrAccuracy",0);

        funInit();
        funReadUserDetails();
        funCreateDictionary();


        if(arrLabel != null && arrAccuracy != 0){
            String mor = funConvertToMorseCode(arrLabel);
            final long[] vibe = funCreateVibrationPattern(mor);
            vibrator.vibrate(vibe,-1);
            Toast.makeText(mChatsActivity.this,arrLabel,Toast.LENGTH_LONG).show();
        }
        else{
            vibrator.vibrate(400);
        }

        morseletter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence cS, int i, int i1, int i2) {

                if(getKey(xDict,cS.toString()) == null){
                    ch[index] = "";
                    str[index] = morseletter.getText().toString();
                }
                else{
                    ch[index] = getKey(xDict,cS.toString());
                    str[index] = morseletter.getText().toString();
                }
                tMsg = TextUtils.join("",ch);
                if(index>0)
                    mMsg = TextUtils.join(" ",str);
                else
                    mMsg = TextUtils.join("",str);
                textmsg.setText(tMsg);
                morsemsg.setText(mMsg);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        touch.setOnTouchListener(new OnSwipeTouchListener(this) {
            @SuppressLint("SimpleDateFormat")
            public void onSwipeTop() {
                xMessage = "";
                xMessage = textmsg.getText().toString().trim();
                xMorseCode = morsemsg.getText().toString().trim();
                Arrays.fill(ch,"");
                Arrays.fill(str,"");
                index=0;
                morseletter.setText("");
                if(CONTACT && !xMessage.equals("")){
                    xDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                    xTime = new SimpleDateFormat("hh:mm a").format(new Date());

                    ChatModel chm  = new ChatModel(xMessage,xTime,xUserId,xDate,xMorseCode);
                    dbChatManager.child(xChatid).push().setValue(chm);
                    vibrator.vibrate(400);
                }
                else{
                    dbUserContacts.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                int EMPTY = 0;
                                for(DataSnapshot snap: dataSnapshot.getChildren()){

                                    ContactModel cm = snap.getValue(ContactModel.class);

                                    if(cm.getShort_name().equals(xMessage)){
                                        EMPTY = 0;
                                        xChatid = cm.getChat_id();
                                        String Name = cm.getLong_name();
                                        lastquery = dbChatManager.child(xChatid).orderByKey().limitToLast(1);
                                        CONTACT = true;
                                        contact.setText(Name);
                                        funReadLastMessage();
                                        break;
                                    }
                                    else{
                                        EMPTY = 1;
                                    }
                                }
                                if(EMPTY == 1){
                                    vibrator.vibrate(1000);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            public void onSwipeRight() {
                if(!morsemsg.getText().toString().equals("") && DSPACE<2){
                    vibrator.vibrate(25);
                    if(DSPACE == 1){
                        morseletter.setText(" ");
                    }
                    index++;
                    morseletter.setText("");
                    DSPACE++;
                }
            }

            public void onSwipeLeft() {
                if(!textmsg.getText().toString().equals("") || !morsemsg.getText().toString().equals("")){
                    vibrator.vibrate(25);
                    if(str[index].equals("")){
                        index--;
                        str[index] = "";
                    }
                    else{
                        str[index] = "";
                    }
                    morseletter.setText(str[index]);
                }
            }

            public void onSwipeBottom() {
                CONTACT = false;
                contact.setText("");
                Arrays.fill(ch,"");
                Arrays.fill(str,"");
                index=0;
                morseletter.setText("");
                vibrator.vibrate(1000);
            }

        });

        funReadLastMessage();
    }


    class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        OnSwipeTouchListener(Context ctx) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                DSPACE=0;
                morseletter.append("•");
                vibrator.vibrate(100);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                DSPACE=0;
                morseletter.append("−");
                vibrator.vibrate(400);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if(CONTACT){
                    funReadLastMessage();
                }
                else{
                    //Toast.makeText(mChatsActivity.this,"OPENS CAMERA !",Toast.LENGTH_SHORT).show();
                    morseletter.setText("");
                    morsemsg.setText("");
                    textmsg.setText("");
                    Intent intent = new Intent(mChatsActivity.this,CameraActivity.class);
                    intent.putExtra("Activity","mChats");
                    startActivity(intent);
                }
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                final int SWIPE_THRESHOLD = 300;
                final int SWIPE_VELOCITY_THRESHOLD = 300;
                final int SWIPE_THRESHOLD1 = 600;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD1 && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }
        public void onSwipeRight() {}
        public void onSwipeLeft() {}
        public void onSwipeTop() {}
        public void onSwipeBottom() {}
    }

    private void funReadLastMessage() {
        if(CONTACT){
            lastquery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(!xUserId.equals(dataSnapshot.child("sender").getValue().toString())){
                        String RecMsg = dataSnapshot.child("message").getValue().toString();
                        xRecived = dataSnapshot.child("morse_code").getValue().toString();
                        String[] arr = xRecived.split(" ");
                        long[] vibro = funCreateVibrationPattern(xRecived);
                        if(xType.equals("V_V")){
                            if(morsemsg.getText().toString().equals("") && textmsg.getText().toString().equals("")){
                                long VTIME = 0;
                                for(long l:vibro){
                                    VTIME = VTIME + l;
                                }
                                for(String str : arr){
                                    morseletter.setText(str);
                                    index++;
                                }
                                textmsg.setText(RecMsg);
                                vibrator.vibrate(vibro,-1);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Arrays.fill(ch,"");
                                        Arrays.fill(str,"");
                                        index=0;
                                        morseletter.setText("");
                                    }
                                }, VTIME);
                            }
                            else{
                                vibrator.vibrate(vibro,-1);
                                Toast.makeText(mChatsActivity.this,RecMsg,Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            int speechStatus = TTS.speak(RecMsg.toLowerCase(), TextToSpeech.QUEUE_FLUSH, null);
                            Toast.makeText(mChatsActivity.this,RecMsg,Toast.LENGTH_LONG).show();
                            if (speechStatus == TextToSpeech.ERROR) {
                                Log.e("TTS", "Error in converting Text to Speech!");
                            }
                        }
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

    private String funConvertToMorseCode(String Message) {
        char[] ch = Message.toCharArray();
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

    private long[] funCreateVibrationPattern(String xRecived) {
        char[] morsearr = xRecived.toCharArray();
        int n = morsearr.length;
        long[] vibro = new long[(n*2)+1];
        int in1=0,in2=0;
        vibro[in2++] = 800;
        while(in1 < n){
            if(morsearr[in1] == '•'){
                vibro[in2++] = 100;
            }
            else if(morsearr[in1] == '−'){
                vibro[in2++] = 400;
            }
            else if(morsearr[in1] == ' '){
                vibro[in2++] = 0;
            }
            vibro[in2++] = 800;
            in1++;
        }
        return vibro;
    }

    private static <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
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

    private void funInit() {
        textmsg = findViewById(R.id.textmsg);
        morsemsg = findViewById(R.id.morsemsg);
        morseletter =findViewById(R.id.morseletter);
        contact = findViewById(R.id.contact);
        touch = findViewById(R.id.touch);
        Arrays.fill(ch,"");
        Arrays.fill(str,"");
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        morseletter.setVisibility(TextView.INVISIBLE);
        fireAuth = FirebaseAuth.getInstance();
        fireUser = fireAuth.getCurrentUser();
        xUserId = fireUser.getUid();
        dbUserDetails = FirebaseDatabase.getInstance().getReference("User Details");
        dbChatManager = FirebaseDatabase.getInstance().getReference("Chat Manager");
        dbUserContacts = dbUserDetails.child(xUserId).child("Contacts");
        TTS = new TextToSpeech(mChatsActivity.this, this);

    }

    @Override
    public void onBackPressed() {
        if(back == 0){
            Intent intent =  new Intent(mChatsActivity.this,SettingsActivity.class);
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
            final Toast toast = Toast.makeText(mChatsActivity.this,"Press back button "+back+" times", Toast.LENGTH_SHORT);
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
}
