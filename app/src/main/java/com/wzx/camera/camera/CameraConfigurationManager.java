/*
 * Copyright (C) 2010 ZXing authors
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

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.wzx.camera.utils.LOG;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;


final class CameraConfigurationManager {

    private static final String TAG = CameraConfigurationManager.class.getSimpleName();

    private static final int TEN_DESIRED_ZOOM = 27;
    private static final int DESIRED_SHARPNESS = 30;

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    private Point screenResolution;
    private Point screenResolutionDifference;
    private float scale;
    private Point cameraResolution;
    private Point cameraPictureSize;
    private int previewFormat;
    private String previewFormatString;
    private int previewMax = 1280;
    private int previewMin = 800;

    private static final int DEFAULT_PREVIEW_WIDTH = 1920;
    private static final int DEFAULT_PREVIEW_HEIGHT = 1080;

    CameraConfigurationManager() {
    }

    /**
     * Reads, one time, values from the camera that are needed by the app.
     */
    void initFromCameraParameters(Camera camera, SurfaceHolder holder) {
        Camera.Parameters parameters = camera.getParameters();
        previewFormat = parameters.getPreviewFormat();
        previewFormatString = parameters.get("preview-format");
        LOG.d(TAG, "Default preview format: " + previewFormat + '/' + previewFormatString);
        //WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //Display display = manager.getDefaultDisplay();
        Rect rect = holder.getSurfaceFrame();
        screenResolution = new Point(rect.width(), rect.height());
        LOG.d(TAG, "Screen resolution: " + screenResolution);
        //cameraResolution = getCameraResolution(parameters, screenResolution);
        cameraResolution = findBestPreviewSizeValue(parameters.getSupportedPreviewSizes(), screenResolution);
        cameraPictureSize = getMaxValue(parameters.getSupportedPictureSizes());

        float scaleX = (float) screenResolution.x / (float) cameraResolution.y;
        float scaleY = (float) screenResolution.y / (float) cameraResolution.x;

        scale = scaleX < scaleY ? scaleY : scaleX;

        screenResolution.y = (int) (scale * cameraResolution.x);
        screenResolution.x = (int) (scale * cameraResolution.y);

        screenResolutionDifference = new Point(screenResolution.x - rect.width(), screenResolution.y - rect.height());

        //holder.setFixedSize(cameraResolution.x, cameraResolution.y);
        LOG.d(TAG, "Camera resolution: " + screenResolution);
    }

    private Point getMaxValue(List<Camera.Size> sizes) {
        Collections.sort(sizes, (Camera.Size size1, Camera.Size size2) -> size2.width - size1.width);
        Camera.Size size = sizes.get(0);
        return new Point(size.width, size.height);
    }

    /**
     * Sets the camera up to take preview images which are used for both preview and decoding.
     * We detect the preview format here so that buildLuminanceSource() can build an appropriate
     * LuminanceSource subclass. In the future we may want to force YUV420SP as it's the smallest,
     * and the planar Y can be used for barcode scanning without a copy in some cases.
     */
    void setDesiredCameraParameters(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        LOG.d(TAG, "Setting preview size: " + cameraResolution);
        parameters.setPictureSize(cameraPictureSize.x, cameraPictureSize.y);
        parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
        setFlash(parameters);
        //setZoom(parameters);
        //setSharpness(parameters);
        //modify here
        camera.setDisplayOrientation(90);
        camera.setParameters(parameters);
    }

    Point getCameraResolution() {
        return cameraResolution;
    }

    Point getScreenResolution() {
        return screenResolution;
    }

    Point getScreenResolutionDifference() {
        return screenResolutionDifference;
    }

    float getScale() {
        return scale;
    }

    void setScreenResolution(int x, int y) {
        if (screenResolution == null) {
            screenResolution = new Point();
        }
        screenResolution.x = x;
        screenResolution.y = y;
    }

    int getPreviewFormat() {
        return previewFormat;
    }

    String getPreviewFormatString() {
        return previewFormatString;
    }

    private Point findBestPreviewSizeValue(List<Camera.Size> sizes, Point screenResolution) {
        //倒序排列
        Collections.sort(sizes, (Camera.Size size1, Camera.Size size2) -> size2.width - size1.width);
        for (Camera.Size size : sizes) {
            int newX = size.width;
            int newY = size.height;
            LOG.d(TAG, "width: " + newX + ", height: " + newY);
//            if (newX == DEFAULT_PREVIEW_WIDTH && newY == DEFAULT_PREVIEW_HEIGHT) {
//                return new Point(newX, newY);
//            }
            if (newX / newY == 16 / 9) {
                return new Point(newX, newY);
            }
        }
        Camera.Size maxSize = sizes.get(0);
        return new Point(maxSize.width, maxSize.height);
    }

    private Point findBestPictureSizeValue(List<Camera.Size> imageSizes, Point screenResolution) {
        int bestX = 0;
        int bestY = 0;
        int maxX = 0;
        int maxY = 0;
        int diff = Integer.MAX_VALUE;
        for (Camera.Size size : imageSizes) {
            int newX;
            int newY;

            newX = size.width;
            newY = size.height;

            if (newX > maxX) {
                maxX = newX;
                maxY = newY;
            }
            if (newX > previewMax || newX < previewMin) {
                continue;
            }

            int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
            if (newDiff == 0) {
                bestX = newX;
                bestY = newY;
                break;
            } else if (newDiff < diff) {
                bestX = newX;
                bestY = newY;
                diff = newDiff;
            }
        }

        if (bestX > 0 && bestY > 0) {
            LOG.d(TAG, "bestX " + bestX + " bestY " + bestY);
            return new Point(bestX, bestY);
        } else {
            LOG.d(TAG, "maxX " + maxX + " maxY " + maxY);
            return new Point(maxX, maxY);
        }
    }

    private static int findBestMotZoomValue(CharSequence stringValues, int tenDesiredZoom) {
        int tenBestValue = 0;
        for (String stringValue : COMMA_PATTERN.split(stringValues)) {
            stringValue = stringValue.trim();
            double value;
            try {
                value = Double.parseDouble(stringValue);
            } catch (NumberFormatException nfe) {
                return tenDesiredZoom;
            }
            int tenValue = (int) (10.0 * value);
            if (Math.abs(tenDesiredZoom - value) < Math.abs(tenDesiredZoom - tenBestValue)) {
                tenBestValue = tenValue;
            }
        }
        return tenBestValue;
    }

    private void setFlash(Camera.Parameters parameters) {
        // This is the standard setting to turn the flash off that all devices should honor.
        parameters.set("flash-mode", "off");
    }

    private void setZoom(Camera.Parameters parameters) {

        String zoomSupportedString = parameters.get("zoom-supported");
        if (zoomSupportedString != null && !Boolean.parseBoolean(zoomSupportedString)) {
            return;
        }

        int tenDesiredZoom = TEN_DESIRED_ZOOM;

        String maxZoomString = parameters.get("max-zoom");
        if (maxZoomString != null) {
            try {
                int tenMaxZoom = (int) (10.0 * Double.parseDouble(maxZoomString));
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (NumberFormatException nfe) {
                LOG.e(TAG, "Bad max-zoom: " + maxZoomString);
            }
        }

        String takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max");
        if (takingPictureZoomMaxString != null) {
            try {
                int tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (NumberFormatException nfe) {
                LOG.e(TAG, "Bad taking-picture-zoom-max: " + takingPictureZoomMaxString);
            }
        }

        String motZoomValuesString = parameters.get("mot-zoom-values");
        if (motZoomValuesString != null) {
            tenDesiredZoom = findBestMotZoomValue(motZoomValuesString, tenDesiredZoom);
        }

        String motZoomStepString = parameters.get("mot-zoom-step");
        if (motZoomStepString != null) {
            try {
                double motZoomStep = Double.parseDouble(motZoomStepString.trim());
                int tenZoomStep = (int) (10.0 * motZoomStep);
                if (tenZoomStep > 1) {
                    tenDesiredZoom -= tenDesiredZoom % tenZoomStep;
                }
            } catch (NumberFormatException nfe) {
                // continue
            }
        }

        // Set zoom. This helps encourage the user to pull back.
        // Some devices like the Behold have a zoom parameter
        if (maxZoomString != null || motZoomValuesString != null) {
            parameters.set("zoom", String.valueOf(tenDesiredZoom / 10.0));
        }

        // Most devices, like the Hero, appear to expose this zoom parameter.
        // It takes on values like "27" which appears to mean 2.7x zoom
        if (takingPictureZoomMaxString != null) {
            parameters.set("taking-picture-zoom", tenDesiredZoom);
        }
    }

    public static int getDesiredSharpness() {
        return DESIRED_SHARPNESS;
    }
}
