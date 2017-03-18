package com.example.camera.rx;

import com.example.camera.data.HttpCacheManager;
import com.example.camerademo.BuildConfig;
import com.example.camera.api.API;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * gson持久化截取保存数据
 * Created by wangzhenxing on 2016/12/29.
 */
public class CacheInterceptor implements Interceptor {

    private HttpCacheManager mCacheDao;
    private String mAccessToken;
    private final Map<String, String> mHeaderMap;

    public CacheInterceptor() {
        mCacheDao = HttpCacheManager.getInstance();
        mHeaderMap = new HashMap<>();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader(API.HEADER_APP_VERSION, BuildConfig.VERSION_NAME);
//        builder.addHeader(HTTP.USER_AGENT, Phone.getUserAgent());
        for (Map.Entry<String, String> entry : mHeaderMap.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request = builder.build();
        Response response = chain.proceed(request);
        if (API.isCache) {
            // Buffer the entire body.
//            ResponseBody body = response.body();
//            BufferedSource source = body.source();
//            source.request(Long.MAX_VALUE);
//            Buffer buffer = source.buffer();
//            Charset charset = Charset.defaultCharset();
//            MediaType contentType = body.contentType();
//            if (contentType != null) {
//                charset = contentType.charset(charset);
//            }
//            String bodyString = buffer.clone().readString(charset);
//            String url = request.url().toString();
//            CacheResult result = mCacheDao.queryCacheBy(url);
//            long time = System.currentTimeMillis();
//            /*保存和更新本地数据*/
//            if (result == null) {
//                result = new CacheResult(url, bodyString, time);
//                mCacheDao.saveCache(result);
//            } else {
//                result.setResult(bodyString);
//                result.setTime(time);
//                mCacheDao.updateCache(result);
//            }
        }
        return response;
    }

    public void addHeader(String header, String value) {
        mHeaderMap.put(header, value);
    }

    public void removeHeader(String header) {
        mHeaderMap.remove(header);
    }
}
