package com.wzx.camera.rx;

import com.wzx.camera.App;
import com.wzx.camera.api.API;
import com.wzx.camera.utils.NetworkUtil;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by wangzhx on 2016/12/29.
 */
public class CacheSubscriber<T> extends Subscriber<T> {

    /* 软引用回调接口*/
    private SoftReference<HttpCallBack> mOnNextListener;
    private final List<String> mUrls;

    public CacheSubscriber(HttpCallBack listener) {
        this.mOnNextListener = new SoftReference<>(listener);
        this.mUrls = mOnNextListener.get().getUrls();
    }

    /**
     * 订阅开始: 有网络,有缓存,并且缓存未超时 -> 直接获取缓存数据
     */
    @Override
    public void onStart() {
        if (mOnNextListener != null) {
            mOnNextListener.get().onStart();
        }
        if (API.isCache && NetworkUtil.isNetworkAvailable(App.getInstance())) {
            ArrayList<String> cookies = new ArrayList<>();
            Observable.from(mUrls).subscribe(new Subscriber<String>(){

                @Override
                public void onCompleted() {
                    if (mOnNextListener.get() != null && !cookies.isEmpty()) {
                        mOnNextListener.get().onCacheNext(cookies);
                        onCompleted();
                        unsubscribe();
                    }
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(String url) {
//                    CacheResult cookieResult = HttpCacheManager.getInstance().queryCacheBy(url);
//                    if (cookieResult != null) {
//                        long time = (System.currentTimeMillis() - cookieResult.getTime()) / 1000;
//                        if (time < API.cookieNetWorkTime) {
//                            cookies.add(cookieResult.getResult());
//                        }
//                    }
                }
            });
        }
    }

    @Override
    public void onCompleted() {
        if (mOnNextListener.get() != null) {
            mOnNextListener.get().onComplete();
        }
    }

    /**
     * 对错误进行统一处理
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        /*需要緩存并且本地有缓存才返回*/
        if (API.isCache) {
            ArrayList<String> cookies = new ArrayList<>();
            Observable.from(mUrls).subscribe(new Subscriber<String>() {
                @Override
                public void onCompleted() {
                    if (mOnNextListener.get() != null && !cookies.isEmpty()) {
                        mOnNextListener.get().onCacheNext(cookies);
                        CacheSubscriber.this.onCompleted();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    errorDo(e);
                }

                @Override
                public void onNext(String url) {
//                    CacheResult cookieResult = HttpCacheManager.getInstance().queryCacheBy(url);
//                    if (cookieResult == null) {
//                        throw new HttpTimeException("网络错误");
//                    }
//                    long time = (System.currentTimeMillis() - cookieResult.getTime()) / 1000;
//                    if (time < API.cookieNoNetWorkTime) {
//                        cookies.add(cookieResult.getResult());
//                    } else {
//                        HttpCacheManager.getInstance().deleteCache(cookieResult);
//                        throw new HttpTimeException("网络错误");
//                    }
                }
            });
        } else {
            errorDo(e);
        }
    }

    private void errorDo(Throwable e) {
        if (mOnNextListener.get() != null) {
            mOnNextListener.get().onError(e);
        }
    }

    @Override
    public void onNext(T t) {
        if (t == null) throw new HttpTimeException("未知错误");
        if (mOnNextListener.get() != null) {
            mOnNextListener.get().onNext(t);
        }
    }
}