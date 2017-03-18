package com.example.camera.rx;

import com.example.camera.activity.BaseActivity;
import com.example.camera.utils.ToastUtil;
import com.example.camera.api.API;
import com.example.camera.model.Result;

import org.greenrobot.greendao.annotation.NotNull;

import java.lang.ref.SoftReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * 成功回调处理
 * Created by wangzhx on 2016/12/29.
 */
public abstract class HttpCallBack<T> {

    private SoftReference<BaseActivity> mActivity;
    private List<String> mUrls = new ArrayList<>();
    /**
     * 是否弹框
     * */
    private boolean showPorgress = true;
    /**
     * 是否提示错误信息
     */
    private boolean showError = true;

    public HttpCallBack(boolean showProgress) {
        this.showPorgress = showProgress;
    }

    public HttpCallBack(boolean showProgress, boolean showError) {
        this.showPorgress = showProgress;
        this.showError = showError;
    }

    public HttpCallBack() {
    }

    public void setActivity(@NotNull BaseActivity activity) {
        this.mActivity = new SoftReference<>(activity);
    }

    public boolean isShowPorgress() {
        return showPorgress;
    }

    public void setShowPorgress(boolean showPorgress) {
        this.showPorgress = showPorgress;
    }

    public void showProgress() {
        if (showPorgress)
            mActivity.get().showProgress();
    }

    private void dismissProgress() {
        if (showPorgress)
            mActivity.get().dismissProgress();
    }

    public BaseActivity getActivity() {
        return mActivity.get();
    }

    public void addUrl(String url) {
        if (!mUrls.contains(url)) {
            mUrls.add(url);
        }
    }

    public List<String> getUrls() {
        return mUrls;
    }

    public void onStart() {
        showProgress();
    }

    public abstract void onNext(T t);

    /**
     * 读取缓存后回调
     *
     * @param jsons
     */
    public void onCacheNext(List<String> jsons) {

    }

    public void onError(Throwable e) {
        e.printStackTrace();
        dismissProgress();
        if (e instanceof SocketTimeoutException) {
            showErrorMsg("网络中断，请检查您的网络状态!");
        } else if (e instanceof ConnectException) {
            showErrorMsg("网络中断，请检查您的网络状态!");
        } else {
            showErrorMsg("错误: " + e.getMessage());
        }
    }

    private void showErrorMsg(String error) {
        if (!showError) return;
        ToastUtil.show(error);
    }

    public void onComplete() {
        dismissProgress();
    }

    public T filterResult(T t) {
        if (t instanceof Result) {
            Result result = (Result) t;
            switch (result.getStatus()) {
                case API.SUCCESS_CODE:
                    break;
                case API.ERROR_CODE:
                    showErrorMsg(result.getMessage());
                    break;
            }
        }
        return t;
    }
}
