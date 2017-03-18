package com.wzx.camera.model;

import java.io.Serializable;

/**
 * Created by wangzhenxing on 2016/11/17.
 */

public class Result<T> implements Serializable {
    /**
     * status : 0成功, 1失败
     * message :
     * data :
     */
    private static final long serialVersionUID = 1234;
    private int status;
    private String msg;
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String message) {
        this.msg = message;
    }
}
