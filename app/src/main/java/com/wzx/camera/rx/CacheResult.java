package com.wzx.camera.rx;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * post請求緩存数据
 * Created by wangzhx on 2016/10/26.
 */
@Entity
public class CacheResult {
    @Id
    private long id;
    /*url*/
    private String url;
    /*返回结果*/
    private String result;
    /*时间*/
    private long time;

    public CacheResult(String url, String result, long time) {
        this.url = url;
        this.result = result;
        this.time = time;
    }

    @Generated(hash = 1606684740)
    public CacheResult(long id, String url, String result, long time) {
        this.id = id;
        this.url = url;
        this.result = result;
        this.time = time;
    }

    @Generated(hash = 340389037)
    public CacheResult() {
    }

    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getResult() {
        return this.result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }
}
