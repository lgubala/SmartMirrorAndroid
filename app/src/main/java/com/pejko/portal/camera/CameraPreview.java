package com.pejko.portal.camera;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.casperise.common.debug.Log;

import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private int previewFormat, cameraWidth, cameraHeight, rotation;
    //private List<Camera.Size> mSupportedPreviewSizes;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private PreviewCallback previewCallback;
    private AutoFocusCallback autoFocusCallback;

    //private Camera.Size mPreviewSize;

    //private static final double ASPECT_RATIO = 3.0 / 4.0;
    private boolean hasAutoFocus;
    private boolean isEnabled;

    public CameraPreview(Context context, Camera camera,
                         PreviewCallback previewCb,
                         AutoFocusCallback autoFocusCb,
                         int previewFormat, int cameraWidth, int cameraHeight, int rotation) {
        super(context);
        //Log.e("### Camera CameraPreview");
        this.previewFormat = previewFormat;
        this.cameraWidth = cameraWidth;
        this.cameraHeight = cameraHeight;
        this.rotation = rotation;
        mCamera = camera;
        //mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        previewCallback = previewCb;
        autoFocusCallback = autoFocusCb;

        /*
         * Set camera to continuous focus if supported, otherwise use
         * software auto-focus. Only works for API level >=9.
         */
        /*
        Camera.Parameters parameters = camera.getParameters();
        for (String f : parameters.getSupportedFocusModes()) {
            if (f == Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
                mCamera.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                autoFocusCallback = null;
                break;
            }
        }
        */

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        //Log.e("### Camera surfaceCreated");
        if (mCamera == null)
            return;

        if (!isEnabled)
            return;

        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewFormat(previewFormat);
            /*if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                mCamera.setDisplayOrientation(90);
                parameters.setRotation(90);
            }
            else {
                // This is an undocumented although widely known feature
                parameters.set("orientation", "landscape");
                // For Android 2.2 and above
                mCamera.setDisplayOrientation(0);
                // Uncomment for Android 2.0 and above
                parameters.setRotation(0);
            }*/
            List<String> supportedFocusModes = mCamera.getParameters().getSupportedFocusModes();
            hasAutoFocus = supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO);

            //parameters.set("orientation", "portrait");
            //mCamera.setDisplayOrientation(90);
            //parameters.setRotation(rotation);
            if (cameraWidth != 0 || cameraHeight != 0)
                parameters.setPreviewSize(cameraWidth, cameraHeight);
            mCamera.setParameters(parameters);
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            Log.d("Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        //Log.e("### Camera surfaceDestroyed");
        // Camera preview released in activity
    }

    /*@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }*/

    /*@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        if (width > height * ASPECT_RATIO) {
            width = (int) (height * ASPECT_RATIO + .5);
        } else {
            height = (int) (width / ASPECT_RATIO + .5);
        }

        setMeasuredDimension(width, height);
    }*/

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //Log.e("### Camera surfaceChanged");
        /*
         * If your preview can change or rotate, take care of those events here.
         * Make sure to stop the preview before resizing or reformatting it.
         */
        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        try {
            // Hard code camera surface rotation 90 degs to match Activity view in portrait
            //mCamera.setDisplayOrientation(90);
            //Camera.Parameters parameters = mCamera.getParameters();
            //parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            //mCamera.setParameters(parameters);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewFormat(previewFormat);
            /*if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                mCamera.setDisplayOrientation(90);
                parameters.setRotation(90);
            } else {
                // This is an undocumented although widely known feature
                parameters.set("orientation", "landscape");
                // For Android 2.2 and above
                mCamera.setDisplayOrientation(0);
                // Uncomment for Android 2.0 and above
                parameters.setRotation(0);
            }*/

            //parameters.setPictureSize(cameraWidth, cameraHeight);
            //mCamera.getParameters().getSupportedPreviewSizes()
            //parameters.setRotation(0);
            //parameters.set("orientation", "portrait");
            mCamera.setDisplayOrientation(rotation);
            //parameters.setRotation(rotation);
            if (cameraWidth != 0 || cameraHeight != 0)
                parameters.setPreviewSize(cameraWidth, cameraHeight);
            mCamera.setParameters(parameters);
            mCamera.setPreviewDisplay(mHolder);
            System.out.println("### " + parameters.getPictureSize().width + " - " + parameters.getPictureSize().height);
            if (previewCallback != null) {
                mCamera.setPreviewCallback(previewCallback);
            }

            mCamera.startPreview();
            if (hasAutoFocus) {
                mCamera.autoFocus(autoFocusCallback);
            }
        } catch (Exception e){
            Log.d("DBG", "Error starting camera preview: " + e.getMessage());
        }
    }

    public void isCameraEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}