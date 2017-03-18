package com.wzx.camera.activity;

import android.os.Bundle;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzx.camera.BuildConfig;
import com.wzx.camera.utils.ToastUtil;

/**
 * Created by wangzhx on 16/1/24.
 */
public class BaseActivity extends RxAppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();
    private static final boolean debug = BuildConfig.DEBUG;

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
