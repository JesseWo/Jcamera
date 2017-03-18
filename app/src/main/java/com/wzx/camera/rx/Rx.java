package com.wzx.camera.rx;


import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by wangzhx on 2016/11/17.
 */

public class Rx {

//    public static <T> void doApi(Observable<T> observable, BaseActivity activity, Action0 onSubscribe, Func1 func, final Action1<T> onNext, final Action1<Throwable> onError, final Action0 onCompleted) {
//        observable.subscribeOn(Schedulers.io())
//                .doOnSubscribe(onSubscribe)
//                .observeOn(AndroidSchedulers.mainThread())
//                .compose(activity.bindToLifecycle())
//                .map(func)
//                .subscribe(onNext, onError, onCompleted);
//    }
//
//    public static <T> void doApi(Observable<T> observable, BaseActivity activity, final Action1<T> onNext) {
//        doApi(observable, activity, () -> activity.showProgress(), t -> activity.filterResult(t), onNext, throwable -> activity.onError(throwable), () -> activity.onCompleted());
//    }
//
//    public static <T> void doApi(Observable<T> observable, BaseFragment fragment, Action0 onSubscribe, Func1 func, final Action1<T> onNext, final Action1<Throwable> onError, final Action0 onCompleted) {
//        observable.subscribeOn(Schedulers.io())
//                .doOnSubscribe(onSubscribe)
//                .observeOn(AndroidSchedulers.mainThread())
//                .compose(fragment.bindToLifecycle())
//                .map(func)
//                .subscribe(onNext, onError, onCompleted);
//    }
//
//    public static <T> void doApi(Observable<T> observable, BaseFragment fragment, final Action1<T> onNext) {
//        doApi(observable, fragment, () -> fragment.showProgress(), t -> fragment.filterResult(t), onNext, throwable -> fragment.onError(throwable), () -> fragment.onCompleted());
//    }
//
//    public static <T> void doApi(Observable<T> observable, BaseFragment fragment, Action0 onSubscribe, final Action1<T> onNext) {
//        doApi(observable, fragment, onSubscribe, t -> fragment.filterResult(t), onNext, throwable -> fragment.onError(throwable), () -> fragment.onCompleted());
//    }
//
//
//    public static <T> void doApi(Observable<T> observable, BaseActivity activity, HttpCallBack<T> httpCallBack) {
//        httpCallBack.setActivity(activity);
//        observable
//                //                .retryWhen(new RetryWhenNetworkException())
//                .compose(activity.bindToLifecycle())
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .map(t -> httpCallBack.filterResult(t))
//                .subscribe(new CacheSubscriber<>(httpCallBack));
//    }
//
//    public static <T> void doApi(Observable<T> observable, BaseFragment fragment, HttpCallBack<T> httpCallBack) {
//        FragmentActivity activity = fragment.getActivity();
//        if (activity instanceof BaseActivity) {
//            BaseActivity baseActivity = (BaseActivity) activity;
//            httpCallBack.setActivity(baseActivity);
//        }
//        observable
//                //                .retryWhen(new RetryWhenNetworkException())
//                .compose(fragment.bindToLifecycle())
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .map(t -> httpCallBack.filterResult(t))
//                .subscribe(new CacheSubscriber<>(httpCallBack));
//    }

    /**
     * 合并两个接口 异步请求
     *
     */
//    public static <T, R> void doApiTwoDiff(Observable<Result<T>> observable1, Observable<Result<R>> observable2, BaseActivity activity, HttpCallBack<MixResult<T, R>> httpCallBack) {
//        httpCallBack.setActivity(activity);
//        Observable
//                .zip(observable1, observable2, (result1, result2) -> new MixResult(result1, result2))
//                //                .retryWhen(new RetryWhenNetworkException())
//                .compose(httpCallBack.getActivity().bindToLifecycle())
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .map(mixResult -> httpCallBack.filterResult(mixResult))
//                .subscribe(new CacheSubscriber<>(httpCallBack));
//    }

    //view点击事件，500毫秒过滤重复点击
    public static void clicks(View view, final Action1<Void> onNext) {
        if (view==null) return;
        RxView.clicks(view)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext);
    }

    //延时操作
    public static void timer(long delay, Action1<Long> action) {
        Observable.timer(delay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action);
    }

    //TextView watcher
    public static void afterTextChangeEvents(TextView textView, Action1<TextViewAfterTextChangeEvent> action) {
        RxTextView.afterTextChangeEvents(textView)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action);
    }
}
