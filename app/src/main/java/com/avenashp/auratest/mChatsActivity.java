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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class mChatsActivity extends AppCompatActivity {

    private static final String TAG = "❌SEEKER-CHAT❌";
    private TextView textmsg,morsemsg,morseletter,contact;
    private LinearLayout touch;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_m_chat);

        funInit();
        funReadUserDetails();
        funCreateDictionary();
        vibrator.vibrate(400);

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
                if(!textmsg.getText().toString().equals("")){
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
                    Toast.makeText(mChatsActivity.this,"OPENS CAMERA !",Toast.LENGTH_SHORT).show();
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
                        xRecived = dataSnapshot.child("morse_code").getValue().toString();
                        Toast.makeText(mChatsActivity.this,dataSnapshot.child("message").getValue().toString(),Toast.LENGTH_SHORT).show();
                        char[] morsearr = xRecived.toCharArray();
                        int n = morsearr.length;
                        long[] vibro = new long[(n*2)+1];
                        int in1=0,in2=0;
                        vibro[in2++] = 0;
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
                            vibro[in2++] = 700;
                            in1++;
                        }
                        vibrator.vibrate(vibro,-1);
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
        dbUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(xUserId).exists()){
                    xMode = dataSnapshot.child(xUserId).child("mode").getValue().toString();
                    xName = dataSnapshot.child(xUserId).child("name").getValue().toString();
                    xAge = dataSnapshot.child(xUserId).child("age").getValue().toString();
                    xCountry = dataSnapshot.child(xUserId).child("country").getValue().toString();
                    xGender = dataSnapshot.child(xUserId).child("gender").getValue().toString();
                    xType = dataSnapshot.child(xUserId).child("type").getValue().toString();
                    xNumber = dataSnapshot.child(xUserId).child("number").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
}
