package com.example.camera.data;

/**
 * Created by wangzhenxing on 2016/12/29.
 */

public class DBManager {
//    private volatile static DBManager sInstance;
//    private final static String dbName = "fenqi-mall";
//    private static final String TAG = DBManager.class.getSimpleName();
//    private static final boolean debug = BuildConfig.DEBUG;
//    private DaoMaster.DevOpenHelper mOpenHelper;
//    private DaoMaster mDaoMaster;
//    private DaoSession mDaoSession;
//    public static final String ACCOUNT = "account";
//    public static final String USER = "user";
//    public static final String CONTACT1 = "contact1";
//    public static final String CONTACT2 = "contact2";
//
//
//    private DBManager() {
//        mOpenHelper = new DaoMaster.DevOpenHelper(App.getInstance(), dbName);
//    }
//
//    public static DBManager getInstance() {
//        if (sInstance == null) {
//            synchronized (DBManager.class) {
//                if (sInstance == null) {
//                    sInstance = new DBManager();
//                }
//            }
//        }
//        return sInstance;
//    }
//
//    private SQLiteDatabase getWritableDatabase() {
//        if (mOpenHelper == null) {
//            mOpenHelper = new DaoMaster.DevOpenHelper(App.getInstance(), dbName);
//        }
//        return mOpenHelper.getWritableDatabase();
//    }
//
//    private DaoMaster getDaoMaster() {
//        if (mDaoMaster == null) {
//            mDaoMaster = new DaoMaster(getWritableDatabase());
//        }
//        return mDaoMaster;
//    }
//
//    public DaoSession getDaoSession() {
//        if (mDaoSession == null) {
//            mDaoSession = getDaoMaster().newSession();
//        }
//        return mDaoSession;
//    }
//
//    /**
//     * 关闭数据库
//     */
//    public void closeDataBase() {
//        closeHelper();
//        closeDaoSession();
//    }
//
//    private void closeDaoSession() {
//        if (null != mDaoSession) {
//            mDaoSession.clear();
//            mDaoSession = null;
//        }
//    }
//
//    private void closeHelper() {
//        if (mOpenHelper != null) {
//            mOpenHelper.close();
//            mOpenHelper = null;
//        }
//    }
//    /**************************数据库插入操作***********************/
//    /**
//     * 插入单个对象
//     *
//     * @param object
//     * @return
//     */
//    public <T> boolean insert(T object) {
//        boolean flag = false;
//        try {
//            flag = getDaoSession().insert(object) != -1;
//        } catch (Exception e) {
//            Log.e(TAG, e.toString());
//        }
//        return flag;
//    }
//
//    /**************************数据库更新操作***********************/
//    /**
//     * 以对象形式进行数据修改
//     *
//     * @param object
//     * @return
//     */
//    public <T> boolean update(T object) {
//        boolean flag = false;
//        if (object != null) {
//            try {
//                getDaoSession().update(object);
//                flag = true;
//            } catch (Exception e) {
//                Log.e(TAG, e.toString());
//            }
//        }
//        return flag;
//    }
//
//    /**************************数据库删除操作***********************/
//    /**
//     * 删除某个数据库表
//     *
//     * @param clss
//     * @return
//     */
//    public boolean deleteAll(Class clss) {
//        boolean flag;
//        try {
//            getDaoSession().deleteAll(clss);
//            flag = true;
//        } catch (Exception e) {
//            Log.e(TAG, e.toString());
//            flag = false;
//        }
//        return flag;
//    }
//
//    /**
//     * 删除某个对象
//     *
//     * @param object
//     * @return
//     */
//    public <T> void delete(T object) {
//        try {
//            getDaoSession().delete(object);
//        } catch (Exception e) {
//            Log.e(TAG, e.toString());
//        }
//    }
//
//    /**************************数据库查询操作***********************/
//
//    /**
//     * 获得某个表名
//     *
//     * @return
//     */
//    public String getTablename(Class object) {
//        return getDaoSession().getDao(object).getTablename();
//    }
//
//    public <T> List<T> query(Class object) {
//        Object obj;
//        List<T> objects = null;
//        try {
//            obj = getDaoSession().getDao(object);
//            if (null == obj) {
//                return null;
//            }
//            objects = (List<T>) getDaoSession().getDao(object).queryBuilder().list();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return objects;
//    }
//
//    /**
//     * 缓存model
//     *
//     * @param key
//     * @param object
//     */
//    public boolean save(String key, Object object) {
//        boolean flag = false;
//        if (object == null) {
//            return flag;
//        }
//        String value = new Gson().toJson(object);
//        DBModel dbModel = getDBModel(key);
//        if (dbModel == null) {
//            dbModel = new DBModel(key, value);
//            flag = insert(dbModel);
//        } else {
//            dbModel.setValue(value);
//            flag = update(dbModel);
//        }
//        return flag;
//    }
//
//    /**
//     * 获取model
//     *
//     * @param key
//     * @param clazz
//     * @param <T>
//     * @return
//     */
//    public <T> T load(String key, Class<T> clazz) {
//        T t = null;
//        try {
//            DBModel dbModel = getDBModel(key);
//            if (dbModel != null) {
//                t = new Gson().fromJson(dbModel.getValue(), clazz);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return t;
//    }
//
//    private DBModel getDBModel(String key) {
//        DBModel dbModel = null;
//        try {
//            DBModelDao dao = getDaoSession().getDBModelDao();
//            QueryBuilder<DBModel> queryBuilder = dao.queryBuilder();
//            queryBuilder.where(DBModelDao.Properties.Key.eq(key));
//            dbModel = queryBuilder.unique();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return dbModel;
//    }
//
//    public Account getAccount() {
//        return load(ACCOUNT, Account.class);
//    }
//
//    public boolean setAccount(Account account) {
//        boolean success = save(ACCOUNT, account);
//        if (success) {
//            API.addHeader(API.HEADER_ACCESS_TOKEN, account.getAccessToken());
//            if (PushManager.getInstance().isNotificationOpen())
//                PushManager.getInstance().register(account.getUserId(), null);
//            //广播通知
//            App.getEventBus().post(new AccountChangeEvent(account));
//        }
//        return success;
//    }
//
//    public void updateAccount(Account account) {
//        save(ACCOUNT, account);
//    }
//
//    public void clearAccount(boolean notify) {
//        if (debug) LOG.d(TAG, "clearAccount");
//        deleteAll(DBModel.class);
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
//                || !Environment.isExternalStorageRemovable()) {
//            File externalCacheDir = App.getInstance().getExternalCacheDir();
//            if (externalCacheDir != null) {
//                FileOpt.delFolder(externalCacheDir.getPath());
//            }
//        }
//        FileOpt.delFolder(App.getInstance().getCacheDir().getPath());
//        API.removeHeader(API.HEADER_ACCESS_TOKEN);
//        //Analytics.getInstance().stop();
//        //clearUBTEvents();
//        Account account = getAccount();
//        if (account != null) {
//            //unregister push service
//            if (PushManager.getInstance().isNotificationOpen()) {
//                PushManager.getInstance().unregister(account.getUserId());
//            }
//            //广播通知
//            if (notify) App.getEventBus().post(new AccountChangeEvent(null));
//        }
//    }
}

