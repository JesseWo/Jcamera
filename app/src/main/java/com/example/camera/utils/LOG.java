package com.example.camera.utils;

import android.util.Log;

/**
 * Created by xuchdeid on 16/7/14.
 */

public class LOG {

    private static final String TAG = "scan";
    private static final boolean debug = true;

    public static void d(String tag, String info) {
        if (debug) {
            Log.d(TAG, tag + " " + info);
        }
    }

    public static void d(String info) {
        d(getClassName(), info);
    }

    public static void e(String tag, String info) {
        Log.e(TAG, tag + " " + info);
    }

    public static void e(String info) {
        e(getClassName(), info);
    }

    public static void e(String tag, String string, Throwable e) {
        Log.e(TAG, tag + " " + string, e);
    }

    public static void i(String tag, String info) {
        Log.i(TAG, tag + " " + info);
    }

    public static void i(String info) {
        i(getClassName(), info);
    }

    public static void i(String tag, String string, Throwable e) {
        Log.i(TAG, tag + " " + string, e);
    }

    private static String getClassName() {
        String result;
        StackTraceElement thisMethodStack = (new Exception()).getStackTrace()[2];
        result = thisMethodStack.getClassName();
        int lastIndex = result.lastIndexOf(".");
        result = result.substring(lastIndex + 1, result.length());
        return result;
    }
}

