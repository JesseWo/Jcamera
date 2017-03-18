package com.example.camera.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

/**
 * Created by wzx on 2017/2/19.
 */

public class GravityManager {

    private static final String TAG = GravityManager.class.getSimpleName();
    private static final boolean debug = false;

    private volatile static GravityManager sInstance;
    private SensorEventListener sensorListener;
    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private static final int ORIENTATION_DOWN = 0;
    private static final int ORIENTATION_LEFT = 1;
    private static final int ORIENTATION_RIGHT = 2;
    private static final int ORIENTATION_UP = 3;
    private int orientation = ORIENTATION_DOWN;
    private ValueAnimator animator;
    private static final int ANIM_DURATION = 600;

    private GravityManager() {

    }

    public static GravityManager getInstance() {
        if (sInstance == null) {
            synchronized (GravityManager.class) {
                if (sInstance == null) {
                    sInstance = new GravityManager();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context, View ...views){
        if (views == null) return;
        initAnim(views);
        //获得重力感应硬件控制器
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        //添加重力感应侦听，并实现其方法，
        sensorListener = new SensorEventListener() {
            public void onSensorChanged(SensorEvent se) {
                if (se.sensor.getType() == Sensor.TYPE_GRAVITY) {
                    float x = se.values[SensorManager.DATA_X];
                    float y = se.values[SensorManager.DATA_Y];
                    float z = se.values[SensorManager.DATA_Z];
//                    LOG.d(TAG, "x = " + x + ", y = " + y + ", z = " + z);
                    if (z > 0 && z < 9) {
                        float value = views[0].getRotation();
                        if (Math.abs(x) > Math.abs(y)) {
                            if (x > 0) {
                                if(debug) LOG.d(TAG, "屏幕左边朝下");
                                switch (orientation) {
                                    case ORIENTATION_DOWN:
                                        animator.cancel();
                                        animator.setFloatValues(value, 90);
                                        animator.start();
                                        break;
                                    case ORIENTATION_UP:
                                        animator.cancel();
                                        animator.setFloatValues(value,90);
                                        animator.start();
                                        break;
                                    case ORIENTATION_RIGHT:
                                        animator.cancel();
                                        animator.setFloatValues(value, 0, 90);
                                        animator.start();
                                        break;
                                }
                                orientation = ORIENTATION_LEFT;
                            } else {
                                if(debug) LOG.d(TAG, "屏幕右边朝下");
                                switch (orientation) {
                                    case ORIENTATION_DOWN:
                                        animator.cancel();
                                        animator.setFloatValues(value,-90);
                                        animator.start();
                                        break;
                                    case ORIENTATION_UP:
                                        animator.cancel();
                                        animator.setFloatValues(value,-90);
                                        animator.start();
                                        break;
                                    case ORIENTATION_LEFT:
                                        animator.cancel();
                                        animator.setFloatValues(value, 0, -90);
                                        animator.start();
                                        break;
                                }
                                orientation = ORIENTATION_RIGHT;
                            }
                        } else {
                            if (y > 0) {
                                if(debug) LOG.d(TAG, "屏幕底部朝下");
                                switch (orientation) {
                                    case ORIENTATION_RIGHT:
                                        animator.cancel();
                                        animator.setFloatValues(value,0);
                                        animator.start();
                                        break;
                                    case ORIENTATION_UP:
                                        animator.cancel();
                                        animator.setFloatValues(value, -90, 0);//??????
                                        animator.start();
                                        break;
                                    case ORIENTATION_LEFT:
                                        animator.cancel();
                                        animator.setFloatValues(value,0);
                                        animator.start();
                                        break;
                                }
                                orientation = ORIENTATION_DOWN;
                            } else {
                                if(debug) LOG.d(TAG, "屏幕顶部朝下");
                                switch (orientation) {
                                    case ORIENTATION_DOWN:
                                        animator.cancel();
                                        animator.setFloatValues(value, 90, 180);//?????
                                        animator.start();
                                        break;
                                    case ORIENTATION_RIGHT:
                                        animator.cancel();
                                        animator.setFloatValues(value,-180);
                                        animator.start();
                                        break;
                                    case ORIENTATION_LEFT:
                                        animator.cancel();
                                        animator.setFloatValues(value,180);
                                        animator.start();
                                        break;
                                }
                                orientation = ORIENTATION_UP;
                            }
                        }
                    }
                }
            }

            public void onAccuracyChanged(Sensor arg0, int arg1) {
            }
        };
    }

    private void initAnim(View ...views) {
        animator = new ValueAnimator().setDuration(ANIM_DURATION);
        animator.addUpdateListener((animation) -> {
            float value = (float) animation.getAnimatedValue();
            for (View view : views) {
                view.setRotation(value);
            }
        });
    }

    public void register() {
        sensorManager.registerListener(sensorListener, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister() {
        sensorManager.unregisterListener(sensorListener);
    }
}
