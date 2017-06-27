package com.pejko.portal.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pejko.portal.R;
import com.pejko.portal.camera.CameraPreview;
import com.pejko.portal.utils.CameraUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class CameraActivity extends Activity {

    private ImageView imgPhoto;
    private TextView txtCounter;
    private FrameLayout ltCamera;

    private Camera camera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler = new Handler();
    private boolean canAutofocus = true;
    private boolean hasAutoFocus;
    private int previewFormat = ImageFormat.NV21;

    private Handler handler = new Handler();
    private int cameraWidth, cameraHeight;
    private int rotation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.camera_activity);

        findViews();
    }

    private void findViews() {
        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        txtCounter = (TextView) findViewById(R.id.txtCounter);
        ltCamera = (FrameLayout) findViewById(R.id.ltCamera);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "SPUSTAM", Toast.LENGTH_SHORT).show();
                initCamera();
            }
        }, 3000);
    }

    private void initCamera() {
        /*if (!CameraUtils.checkPermissions(getActivity()))
            return;*/
        if (camera == null) {
            canAutofocus = true;
            autoFocusHandler = new Handler();
            camera = CameraUtils.getCameraInstance();
            if (camera == null) {
                camera = CameraUtils.getCameraForceInstance(Camera.CameraInfo.CAMERA_FACING_BACK);
            }

            if (camera == null) {
                camera = CameraUtils.getCameraForceInstance(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }
            if (camera != null) {
                int[] bestDimens = CameraUtils.determineBestPreviewSize(getApplicationContext(), camera);
                if (bestDimens[0] != 0 && bestDimens[1] != 0) {
                    cameraWidth = bestDimens[0];
                    cameraHeight = bestDimens[1];
                }
                rotation = CameraUtils.getRotation(CameraActivity.this);
                mPreview = new CameraPreview(getApplicationContext(), camera, previewCb, autoFocusCB, previewFormat,
                        cameraWidth, cameraHeight, rotation);

                ltCamera.removeAllViews();
                ltCamera.addView(mPreview);

                List<String> supportedFocusModes = camera.getParameters().getSupportedFocusModes();
                hasAutoFocus = supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO);

                if (camera != null) {
                    Camera.Parameters params = camera.getParameters();
                    params.setPreviewFormat(previewFormat);
                    camera.setDisplayOrientation(rotation);
                    if (cameraWidth != 0 || cameraHeight != 0) {
                        params.setPreviewSize(cameraWidth, cameraHeight);
                    }
                    camera.setParameters(params);
                    startCamera();
                }
            }
        }
    }

    private void startCamera() {
        camera.setPreviewCallback(previewCb);
        camera.startPreview();
        if (hasAutoFocus && canAutofocus) {
            canAutofocus = false;
            try {
                camera.autoFocus(autoFocusCB);
                handler.postDelayed(takePicture, 5000);

                new CountDownTimer(5000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        txtCounter.setText(String.valueOf(millisUntilFinished / 1000));
                    }

                    public void onFinish() {
                        txtCounter.setVisibility(View.GONE);
                    }

                }.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        camera = CameraUtils.releaseCamera(camera);
        super.onPause();
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            System.out.println("### DoFocus " + (camera != null) + " " + hasAutoFocus + " " + canAutofocus);
            if (camera != null && hasAutoFocus && canAutofocus) {
                canAutofocus = false;
                try {
                    camera.autoFocus(autoFocusCB);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
       //     System.out.println("### previewCb");
            /*if (data != null) {
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = parameters.getPreviewSize();

                canScan = true;
            } else {
                canScan = true;
            }*/
        }
    };

    /*Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            System.out.println("### AutoFocusCb");
            canAutofocus = true;
            autoFocusHandler.postDelayed(doAutoFocus, 3000);
            if (success) {
                camera.takePicture(myShutterCallback, myPictureCallbackRaw, myPictureCallbackJPG);
            }
            camera.cancelAutoFocus();
        }
    };*/

    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {

        }
    };

    Runnable takePicture = new Runnable() {
        @Override
        public void run() {
            System.out.println("### takePicture");
            //autoFocusHandler.postDelayed(takePicture, 5000);
            camera.takePicture(myShutterCallback, myPictureCallbackRaw, myPictureCallbackJPG);
        }
    };

    Camera.ShutterCallback myShutterCallback = new Camera.ShutterCallback(){
        @Override
        public void onShutter() {
            System.out.println("### onShutter");

        }};

    Camera.PictureCallback myPictureCallbackRaw = new Camera.PictureCallback(){

        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            System.out.println("### myPictureCallbackRaw onPictureTaken");
        }};

    Camera.PictureCallback myPictureCallbackJPG = new Camera.PictureCallback(){

        @Override
        public void onPictureTaken(byte[] data, Camera arg1) {
            System.out.println("### myPictureCallbackJPG onPictureTaken " + arg1.getParameters().getPreviewSize().width + "x" + arg1.getParameters().getPreviewSize().height);

            System.out.println("### onPreviewFrame");
            if (data != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                if (bitmap != null) {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, os);
                    byte[] array = os.toByteArray();
                    Bitmap bitmapResult = BitmapFactory.decodeByteArray(array, 0, array.length);
                    if (bitmapResult != null) {
                        imgPhoto.setImageBitmap(bitmapResult);
                    }
                }
            }
        }};

}