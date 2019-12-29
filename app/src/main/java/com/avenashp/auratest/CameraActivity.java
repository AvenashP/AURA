package com.avenashp.auratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.avenashp.auratest.AdapterClass.ChatAdapter;
import com.avenashp.auratest.AdapterClass.LabelsAdapter;
import com.avenashp.auratest.ModelClass.ChatModel;
import com.avenashp.auratest.ModelClass.LabelsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends AppCompatActivity implements LabelsAdapter.OnLabelClickListener {

    private static final String TAG = "###########";
    private Button open;
    private ImageView image;
    private RecyclerView labels;
    private RecyclerView.LayoutManager labelsLM;
    private ArrayList<LabelsModel> labelsModels;
    private LabelsAdapter labelsAdapter;
    private static final int pic_id = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        open = findViewById(R.id.open);
        image = findViewById(R.id.image);
        labels = findViewById(R.id.labels);
        labelsModels = new ArrayList<>();
        labels.setHasFixedSize(true);
        labelsLM = new LinearLayoutManager(this);
        labels.setLayoutManager(labelsLM);

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, pic_id);
            }
        });

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == pic_id) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            getLabelsFromDevice(photo);
            image.setImageBitmap(photo);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getLabelsFromDevice(Bitmap photo) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(photo);
        FirebaseVisionImageLabeler detector = FirebaseVision.getInstance().getOnDeviceImageLabeler();

        labelsModels.clear();
        detector.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
            @Override
            public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                for(FirebaseVisionImageLabel label:firebaseVisionImageLabels){
                    Log.i(TAG, "onSuccess: "+label.getEntityId()+" "+label.getText()+" "+label.getConfidence());
                    LabelsModel lm = new LabelsModel(label.getText(),label.getConfidence());
                    labelsModels.add(lm);
                }
                labelsAdapter = new LabelsAdapter(labelsModels,CameraActivity.this);
                labels.setAdapter(labelsAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: "+e);
            }
        });
    }

    @Override
    public void onLabelClick(int position) {
        //DO NOTHING
    }
}
