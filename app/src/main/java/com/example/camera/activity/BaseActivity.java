package com.example.camera.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.camera.utils.ToastUtil;
import com.example.camerademo.BuildConfig;

/**
 * Merge ProgressActivity + BaseActivity
 * <p>
 * Created by xuchdeid on 16/1/24.
 * Modified by E-boy on 16/12/1.
 */
public class BaseActivity extends AppCompatActivity {

    protected static final String PROGRESS = "activity.progress";
    private final static int PROGRESS_DELAY = 0;
    private final static int MSG_WHAT_SHOWPROGRESS = 1;
    private final static int MSG_WHAT_CLOSEPROGRESS = 2;
    private static final String TAG = BaseActivity.class.getSimpleName();
    private static final boolean debug = BuildConfig.DEBUG;
    private boolean mbShowProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void showProgress() {

    }

    public void dismissProgress() {

    }

    public void showMessage(String msg) {
        ToastUtil.show(msg);
    }
}
