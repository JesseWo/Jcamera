package com.example.camera.api;


/**
 * Created by wangzhx on 2016/11/17.
 */

public interface APIService {

    String URL_ACCOUNT_CENTER = "/superapp/openAccountCenter";
    String WX_BASE_URL = "https://api.weixin.qq.com/sns";
    String WX_TOKEN_URL = WX_BASE_URL + "/oauth2/access_token";
    String WX_USER_INFO_URL = WX_BASE_URL + "/userinfo";


}

