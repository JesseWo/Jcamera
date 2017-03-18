package com.example.camerademo.data;

/**
 * Created by wangzhenxing on 2017/1/9.
 */

public class HttpCacheManager {

    private volatile static HttpCacheManager sInstance;
    private DBManager mDbManager;

    private HttpCacheManager() {
//        mDbManager = DBManager.getInstance();
    }

    public static HttpCacheManager getInstance() {
        if (sInstance == null) {
            synchronized (HttpCacheManager.class) {
                if (sInstance == null) {
                    sInstance = new HttpCacheManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 增加缓存
     *
     * @param result
     */
//    public boolean saveCache(CacheResult result) {
//        return mDbManager.insert(result);
//    }

//    /**
//     * 删除缓存
//     *
//     * @param result
//     */
//    public void deleteCache(CacheResult result) {
//        mDbManager.delete(result);
//    }
//
//    /**
//     * 更新缓存
//     *
//     * @param result
//     */
//    public void updateCache(CacheResult result) {
//        mDbManager.update(result);
//    }
//
//    /**
//     * 根据UIL获取缓存
//     *
//     * @param key
//     * @return
//     */
//    public CacheResult queryCacheBy(String key) {
//        CacheResult cacheResult = null;
//        try {
//            CacheResultDao dao = mDbManager.getDaoSession().getCacheResultDao();
//            QueryBuilder<CacheResult> qb = dao.queryBuilder();
//            qb.where(CacheResultDao.Properties.Url.eq(key));
//            cacheResult = qb.unique();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return cacheResult;
//    }
}
