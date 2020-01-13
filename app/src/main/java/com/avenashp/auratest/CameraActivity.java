package com.avenashp.auratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.avenashp.auratest.AdapterClass.ChatAdapter;
import com.avenashp.auratest.AdapterClass.LabelsAdapter;
import com.avenashp.auratest.ModelClass.CameraModel;
import com.avenashp.auratest.ModelClass.ChatModel;
import com.avenashp.auratest.ModelClass.LabelsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private static final String TAG = "###########";
    private FrameLayout cameraFrame;
    private LinearLayout captureLayout;
    private CameraModel cameraModel;
    private Camera camera;
    private Vibrator vibrator;
    private ArrayList<String> arrLabel = new ArrayList<>();
    private ArrayList<Integer> arrAccuracy = new ArrayList<>();
    private String xActivity;
    private TextToSpeech TTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        xActivity = getIntent().getStringExtra("Activity");

        funInit();

        vibrator.vibrate(400);

        captureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(camera != null){
                    camera.takePicture(null,null,pictureCallbacks);
                }
            }
        });
    }

    Camera.PictureCallback pictureCallbacks = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            Bitmap photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(photo);

            funOnCloudLabeler(image);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        try{
            camera = Camera.open();
            cameraModel = new CameraModel(this,camera);
            cameraFrame.removeAllViews();
            cameraFrame.addView(cameraModel);

        }
        catch (RuntimeException ex){
            //something
        }
    }

    private void funOnCloudLabeler(final FirebaseVisionImage image) {
        FirebaseVisionImageLabeler cloudDetector = FirebaseVision.getInstance().getCloudImageLabeler();
        //labelsModels.clear();
        cloudDetector.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
            @Override
            public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                for(FirebaseVisionImageLabel label:firebaseVisionImageLabels){
                    Log.i(TAG, "onSuccess: "+label.getEntityId()+" "+label.getText()+" "+label.getConfidence());

                    arrLabel.add(label.getText().toUpperCase());
                    int n = Math.round((label.getConfidence())*100);
                    arrAccuracy.add(n);
                    //LabelsModel lm = new LabelsModel(label.getText(),label.getConfidence());
                    //labelsModels.add(lm);
                }
                Intent intent = new Intent(CameraActivity.this,mChatsActivity.class);
                intent.putExtra("arrLabel",arrLabel.get(0).toUpperCase());
                intent.putExtra("arrAccuracy",arrAccuracy.get(0));
                startActivity(intent);
                //labelsAdapter = new LabelsAdapter(labelsModels,CameraActivity.this);
                //labels.setAdapter(labelsAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: "+e);
                funOnDeviceLabeler(image);
            }
        });
    }

    private void funOnDeviceLabeler(final FirebaseVisionImage image) {
        FirebaseVisionImageLabeler deviceDetector = FirebaseVision.getInstance().getOnDeviceImageLabeler();

        deviceDetector.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
            @Override
            public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                for(FirebaseVisionImageLabel label:firebaseVisionImageLabels){
                    Log.i(TAG, "onSuccess: "+label.getEntityId()+" "+label.getText()+" "+label.getConfidence());

                    arrLabel.add(label.getText().toUpperCase());
                    int n = Math.round((label.getConfidence())*100);
                    arrAccuracy.add(n);
                }
                if(xActivity.equals("mChats")){
                    Intent intent = new Intent(CameraActivity.this,mChatsActivity.class);
                    intent.putExtra("arrLabel",arrLabel.get(0).toUpperCase());
                    intent.putExtra("arrAccuracy",arrAccuracy.get(0));
                    startActivity(intent);
                }
                else{
                    final Intent intent = new Intent(CameraActivity.this,aChatsActivity.class);
                    String str = arrLabel+"";
                    int speechStatus = TTS.speak(str.toLowerCase(), TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(CameraActivity.this,str,Toast.LENGTH_LONG).show();

                    if (speechStatus == TextToSpeech.ERROR) {
                        Log.e("TTS", "Error in converting Text to Speech!");
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(intent);
                        }
                    },1000);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: "+e);
            }
        });
    }

    private void funInit() {
        cameraFrame = findViewById(R.id.cameraFrame);
        captureLayout = findViewById(R.id.captureLayout);
        camera = Camera.open();
        cameraModel = new CameraModel(this,camera);
        cameraFrame.addView(cameraModel);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        TTS = new TextToSpeech(CameraActivity.this, this);
    }

    @Override
    public void onBackPressed() {

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
