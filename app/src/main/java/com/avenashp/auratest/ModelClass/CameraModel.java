package com.avenashp.auratest.ModelClass;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CameraModel extends SurfaceView implements SurfaceHolder.Callback {

    Camera camera;
    SurfaceHolder surfaceHolder;

    public CameraModel(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Camera.Parameters params = camera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        Camera.Size msize = null;

        for(Camera.Size size : sizes){
            msize = size;
        }

        if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
            params.set("orientation","portrait");
            camera.setDisplayOrientation(90);
            params.setRotation(90);
        }
        else{
            params.set("orientation","landscape");
            camera.setDisplayOrientation(0);
            params.setRotation(0);
        }

        params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        params.setPictureSize(msize.width,msize.height);
        camera.setParameters(params);

        try{
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }
        catch (IOException e){
            Log.i(TAG, "surfaceDestroyed: "+e);
        }

    }
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        camera.release();
    }
}
