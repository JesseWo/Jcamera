/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wzx.camera.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.wzx.camera.utils.LOG;

import java.io.IOException;

/**
 * This object wraps the Camera service object and expects to be the only one talking to it. The
 * implementation encapsulates the steps needed to take preview-sized images, which are used for
 * both preview and decoding.
 */
public final class CameraManager {

    private static final String TAG = CameraManager.class.getSimpleName();

    private static CameraManager cameraManager;

    private final CameraConfigurationManager configManager;
    private Camera camera;
    private boolean previewing;
    private boolean mFlashLight = false;
    /**
     * Preview frames are delivered here, which we pass on to the registered handler. Make sure to
     * clear the handler so it will only receive one message.
     */
    private final PreviewCallback previewCallback;
    /**
     * Autofocus callbacks arrive here, and are dispatched to the Handler which requested them.
     */
    private final AutoFocusCallback autoFocusCallback;

    /**
     * Initializes this static object with the Context of the calling Activity.
     *
     * @param context The Activity which wants to use the camera.
     */
    public static void init(Context context) {
        if (cameraManager == null) {
            cameraManager = new CameraManager();
        }
    }

    /**
     * Gets the CameraManager singleton instance.
     *
     * @return A reference to the CameraManager singleton.
     */
    public static CameraManager get() {
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
            //FIXME
            //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            //if (prefs.getBoolean(PreferencesActivity.KEY_FRONT_LIGHT, false)) {
            //    FlashlightManager.enableFlashlight();
            //}
            FlashlightManager.enableFlashlight();
        }
    }

    private int cameraFacingType = -1;

    public int getCameraFacingType() {
        return cameraFacingType;
    }

    public void setCameraFacingType(int type) {
        this.cameraFacingType = type;
    }

    public Camera getCamera() {
        return camera;
    }

    /**
     * Closes the camera driver if still in use.
     */
    public void closeDriver() {
        if (camera != null) {
            FlashlightManager.disableFlashlight();
            camera.release();
            camera = null;
        }
    }

    /**
     * Asks the camera hardware to begin drawing preview frames to the screen.
     */
    public void startPreview() {
        if (camera != null && !previewing) {
            camera.startPreview();
            previewing = true;
        }
    }

    /**
     * Tells the camera to stop drawing preview frames.
     */
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

    /**
     * Asks the camera hardware to perform an autofocus.
     *
     * @param handler The Handler to notify when the autofocus completes.
     * @param message The message to deliver.
     */
    public void requestAutoFocus(Handler handler, int message) {
        if (camera != null && previewing) {
            autoFocusCallback.setHandler(handler, message);
            //Log.d(TAG, "Requesting auto-focus callback");
            camera.autoFocus(autoFocusCallback);
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

    public boolean isSupportFlashLight() {
        return camera != null && camera.getParameters().getSupportedFlashModes() != null;
    }

    public boolean isSupportAutoFocus() {
        return false;
    }
}
