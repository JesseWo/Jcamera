package com.example.camera.api;

import com.example.camera.rx.CacheInterceptor;
import com.example.camerademo.BuildConfig;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wangzhx on 2016/12/28.
 */

public class API {

    private static final String TAG = API.class.getSimpleName();
    private static final boolean debug = BuildConfig.DEBUG;
    //默认不缓存
    public static boolean isCache = false;
    //有网情况下的本地缓存时间
    public static final int cookieNetWorkTime = 60;
    //无网络的情况下本地缓存时间
    public static final int cookieNoNetWorkTime = 24 * 60 * 60 * 15;
    private static final int CONNECT_TIMEOUT = 15;
    private static final long READ_TIMEOUT = 30;
    private static final long WRITE_TIMEOUT = 30;
    public final static String HEADER_APP_VERSION = "app-version";
    public final static String HEADER_ACCESS_TOKEN = "access-token";
    //response status
    public final static int SUCCESS_CODE = 0;
    public final static int ERROR_CODE = 1;
    public final static int SKIP_CODE = 2;
    public final static int RETRY_TIMES = 3;

    private static final Retrofit retrofit;
    private static APIService service;
    private static Gson gson;
    private static CacheInterceptor sCacheInterceptor;

    private static OkHttpClient getClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        sCacheInterceptor = new CacheInterceptor();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addNetworkInterceptor(sCacheInterceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        if (debug) {
            builder.addInterceptor(httpLoggingInterceptor);
        }
        return builder.build();
    }

    static {
        gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                //过滤掉"_"开头的字段
                return f.getName().startsWith("_");
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();

        retrofit = new Retrofit.Builder()
                .baseUrl(Config.HOST)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        service = retrofit.create(APIService.class);
    }

    public static void addHeader(String header, String value) {
        if (sCacheInterceptor != null) {
            sCacheInterceptor.addHeader(header, value);
        }
    }

    public static void removeHeader(String header) {
        if (sCacheInterceptor != null) {
            sCacheInterceptor.removeHeader(header);
        }
    }

//    public static Observable<Result<UserSimpleInfo>> getUserSimpleInfo(String userId){
//        return service.getUserSimpleInfo(userId);
//    }
//
//    public static Observable<Result<UserInfo>> uploadUserBaseInfo(UserInfo info) {
//        return service.uploadUserBaseInfo(info);
//    }

}
