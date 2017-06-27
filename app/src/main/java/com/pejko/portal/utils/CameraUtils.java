package com.pejko.portal.utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.support.v4.app.FragmentActivity;
import android.view.Surface;

import com.casperise.common.debug.Log;
import com.casperise.common.utils.ScreenUtils;

import java.util.List;

public class CameraUtils
{
    /*public static final int PERMISSIONS_REQUEST_CAMERA = 0;
    private static Boolean permisionGranted = null;

    public static boolean checkPermissions(final Activity activity) {
        if (isPermissionGranted())
            return true;
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(activity, R.string.toast_camera_required, Toast.LENGTH_LONG).show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSIONS_REQUEST_CAMERA);
            }
        }
        return false;
    }

    public static boolean isPermissionGranted()
    {
        return permisionGranted != null && permisionGranted == Boolean.TRUE;
    }

    public static void permissionGranted()
    {
        permisionGranted = Boolean.TRUE;
    }

    public static void permissionDenied()
    {
        permisionGranted = Boolean.FALSE;
    }*/

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
            /*LogUtils.saveForceTextToFile("ScanActivity", "getCameraInstance - " + e.getMessage());
            Answers.getInstance().logCustom(new CustomEvent("getCameraInstance")
                    .putCustomAttribute("Error", cutText(e.getMessage())));*/
        }
        return c;
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraForceInstance(int cameraId){
        Camera c = null;
        try {
            c = Camera.open(cameraId);
        } catch (Exception e){
            Log.d("Cannot open camera " + cameraId, e);
        }
        return c;
    }

    public static Camera releaseCamera(Camera camera) {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
        return camera;
    }

    public static int[] determineBestPreviewSize(Context context, Camera camera) {
        int[] coords = ScreenUtils.getScreenCoords(context);
        List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
        int[] dimens = new int[]{0, 0};
        for (Camera.Size size : sizes) {
            //System.out.println("### determineBestPreviewSize: " + size.width + "x" + size.height);
            if (size.width == coords[0]) {
                if (size.height == coords[1]) {
                    dimens[0] = size.width;
                    dimens[1] = size.height;
                    break;
                }
            } else if (size.width == coords[1]) {
                if (size.height == coords[0]) {
                    dimens[0] = size.width;
                    dimens[1] = size.height;
                    break;
                }
            }
        }
        return dimens;
    }

    public static int getRotation(Activity fragmentActivity) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        int rotation = fragmentActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break; //Natural orientation
            case Surface.ROTATION_90: degrees = 90; break; //Landscape left
            case Surface.ROTATION_180: degrees = 180; break;//Upside down
            case Surface.ROTATION_270: degrees = 270; break;//Landscape right
        }
        int rotate = (info.orientation - degrees + 360) % 360;
        System.out.println("### getRotation: " + rotation + " " + degrees + " " + rotate);
        //rotate = 0;
        return rotate;
    }

}

// eof
