package com.wzx.camera.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.wzx.camera.utils.LOG;

import java.io.IOException;

public final class CameraManager {

    private static final String TAG = CameraManager.class.getSimpleName();

    private final CameraConfigurationManager configManager;

    private Camera camera;
    private boolean previewing;
    private boolean mFlashLight = false;

    private final PreviewCallback previewCallback;
    private final AutoFocusCallback autoFocusCallback;

    private static Context sContext;
    private volatile static CameraManager cameraManager;
    public static void init(Context context) {
        sContext = context;
    }

    public static CameraManager get() {
        if (cameraManager == null) {
            synchronized (CameraManager.class) {
                if (cameraManager == null) {
                    cameraManager = new CameraManager();
                }
            }
        }
        return cameraManager;
    }

    private CameraManager() {
        this.configManager = new CameraConfigurationManager();
        previewCallback = new PreviewCallback(configManager);
        autoFocusCallback = new AutoFocusCallback();
    }

    /**
     * Opens the camera driver and initializes the hardware parameters.
     *
     * @param holder The surface object which the camera will draw preview frames into.
     * @throws IOException Indicates the camera driver failed to open.
     */
    public void openDriver(SurfaceHolder holder) throws IOException {
        if (camera == null) {
            if (cameraFacingType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                int numberOfCameras = Camera.getNumberOfCameras();
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                for (int i = 0; i < numberOfCameras; i++) {
                    Camera.getCameraInfo(i, cameraInfo);
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        cameraFacingType = cameraInfo.facing;
                        camera = Camera.open(i);
                    }
                }
            }

            if (camera == null) {
                camera = Camera.open();
                cameraFacingType = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
            if (camera == null) {
                throw new IOException();
            }
            camera.setPreviewDisplay(holder);

            configManager.initFromCameraParameters(camera, holder);

            configManager.setDesiredCameraParameters(camera);
            FlashlightManager.enableFlashlight();
        }
    }

    public void closeDriver() {
        if (camera != null) {
            FlashlightManager.disableFlashlight();
            camera.release();
            camera = null;
        }
    }

    private int cameraFacingType = -1;

    public int getCameraFacingType() {
        return cameraFacingType;
    }

    public void setCameraFacingType(int type) {
        this.cameraFacingType = type;
    }

    public void startPreview() {
        if (camera != null && !previewing) {
            camera.startPreview();
            previewing = true;
        }
    }

    public void stopPreview() {
        if (camera != null && previewing) {
            camera.stopPreview();
            previewCallback.setHandler(null, 0);
            autoFocusCallback.setHandler(null, 0);
            previewing = false;
        }
    }

    public void takePicture(Camera.ShutterCallback shutter, Camera.PictureCallback jpg) {
        if (camera != null && previewing) {
            try {
                camera.takePicture(shutter, null, jpg);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    public void requestAutoFocus(Handler handler, int message) {
        if (camera != null && previewing) {
            autoFocusCallback.setHandler(handler, message);
            try {
                camera.autoFocus(autoFocusCallback);
            } catch (RuntimeException e) {
                e.printStackTrace();

            }
        }
    }

    /**
     * A single preview frame will be returned to the handler supplied. The data will arrive as byte[]
     * in the message.obj field, with width and height encoded as message.arg1 and message.arg2,
     * respectively.
     *
     * @param handler The handler to send the message to.
     * @param message The what field of the message to be sent.
     */
    public void requestPreviewFrame(Handler handler, int message) {
        if (camera != null && previewing) {
            previewCallback.setHandler(handler, message);
            camera.setOneShotPreviewCallback(previewCallback);

        }
    }

    public Point getScreenResolutionDifference() {
        return configManager.getScreenResolutionDifference();
    }

    public float getScale() {
        return configManager.getScale();
    }

    public void configSurfaceView(SurfaceView surfaceView) {
        /*Point cameraResolution = configManager.getCameraResolution();

        ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
        float scaleX = (float) configManager.getScreenResolution().x  / (float) cameraResolution.y;
        float scaleY = (float) configManager.getScreenResolution().y / (float) cameraResolution.x;

        float scale = scaleX < scaleY ? scaleY : scaleX;

        params.height = (int) (scale * cameraResolution.x);
        params.width = (int) (scale * cameraResolution.y);

        float dx = (params.width - configManager.getScreenResolution().x)/2.0f;
        float dy = (params.height - configManager.getScreenResolution().y)/2.0f;

        surfaceView.layout((int)dx, (int)dy, (int)(params.width - dx), (int)(params.height - dy));
        */

        float width = surfaceView.getWidth();
        float height = surfaceView.getHeight();

        float cameraWidth = configManager.getScreenResolution().x;
        float cameraHeight = configManager.getScreenResolution().y;
        LOG.d(TAG, "configSurfaceView width:" + width + " height:" + height);
        LOG.d(TAG, "configSurfaceView cameraWidth:" + cameraWidth + " cameraHeight:" + cameraHeight);

        float dx = (width - cameraWidth) / 2.0f;
        float dy = (height - cameraHeight) / 2.0f;
        LOG.d(TAG, "configSurfaceView " + " dx:" + dx + " dy:" + dy);
        ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
        params.width = (int) cameraWidth;
        params.height = (int) cameraHeight;
        Rect rect = new Rect((int) dx, (int) dy, (int) (width - dx), (int) (height - dy));
        LOG.d(TAG, "rect:" + rect + " size:" + camera.getParameters().getPreviewSize().height + " " +
                camera.getParameters().getPreviewSize().width);

        surfaceView.layout(rect.left, rect.top, rect.right, rect.bottom);
        surfaceView.setLayoutParams(params);
        surfaceView.requestLayout();
    }

    /**
     * 闪光灯
     * @return
     */
    public boolean isSupportFlashLight() {
        return sContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void setFlashLight(boolean light) {
        if (camera != null) {
            mFlashLight = light;
            Camera.Parameters parameters = camera.getParameters();
            if (light) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            camera.setParameters(parameters);
        }
    }

    public boolean isFlashLight() {
        return mFlashLight;
    }

    /**
     * 自动对焦
     * @return
     */
    public boolean isSupportAutoFocus() {
        return sContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);
    }

    public Camera getCamera() {
        return camera;
    }
}
