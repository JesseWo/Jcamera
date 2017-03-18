package com.wzx.camera;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.multidex.MultiDexApplication;

import com.squareup.otto.Bus;
import com.wzx.camera.camera.CameraManager;
import com.wzx.camera.utils.LOG;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by wangzhx on 4/5/2016.
 */
public class App extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {

    protected static Context sApplicationContext;
    private static Bus eventBus;

    private ArrayList<Activity> mActivityList;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplicationContext = getApplicationContext();
        eventBus = new Bus();

        mActivityList = new ArrayList<>();
        registerActivityLifecycleCallbacks(this);
        //相机初始化
        CameraManager.init(this);
    }

    public static Context getInstance() {
        return sApplicationContext;
    }

    public static Bus getEventBus() {
        return eventBus;
    }

    public void AppExit() {
        try {
            for (Activity activity : mActivityList) {
                activity.finish();
            }
        } catch (Exception e) {
            LOG.d(e.toString());
        }
        mActivityList.clear();
    }

    public String getDiskCacheDir() {
        String cachePath = getCacheDir().getPath();
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            File externalCacheDir = getExternalCacheDir();
            if (externalCacheDir != null) {
                cachePath = externalCacheDir.getPath();
            }
        }
        return cachePath;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mActivityList.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mActivityList.remove(activity);
    }
}
