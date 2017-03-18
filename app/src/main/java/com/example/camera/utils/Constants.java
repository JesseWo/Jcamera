package com.example.camera.utils;

/**
 * Created by xuchdeid on 1/25/2016.
 * ________________________________________
 * /~~\__/~~\__/~~~~~~\__/~~~~\_/~~~~~~~~\_
 * _/~~\/~~\__/~~\__/~~\__/~~\__/~~\_______
 * __/~~~~\___/~~\________/~~\__/~~~~~~\___
 * _/~~\/~~\__/~~\__/~~\__/~~\__/~~\_______
 * /~~\__/~~\__/~~~~~~\__/~~~~\_/~~\_______
 * ________________________________________
 */
public interface Constants {
    String TYPE = "type";
    int TYPE_JOB = 10;
    int TYPE_MARRIAGE_STATUS = 20;
    int TYPE_RELATIONSHIP_DIRECT = 30;
    int TYPE_RELATIONSHIP_COMMON = 40;
    int TYPE_WORK_CERTIFICATE = 50;

    String SELECT_ITEM = "select_item";
    String RESULT_VALUE = "result_value";
    String CONTACTS_FIRST = "contacts_first";
    String CONTACTS_SECOND = "contacts_second";
    String UPLOAD_ALL_CONTACTS = "upload_all_contacts";
    String JXL_SUPPORT_INFO = "jxl_support_info";
    String FACTORY_NAME = "factory_name";
    String SIMPLE_USER_INFO = "simple_user_info";
    String MOBILE = "mobile";
    //juxinli support code
    int UNSUPPORT_PSW_RESET = 0;
    int JUST_CAPTCHA = 10;
    int WITH_NEW_PSW = 20;

    //10002 输入动态密码; 10007 简单密码或初始密码无法登录
    String STATUS_NEED_CAPTCHA1 = "10002";
    String STATUS_NEED_CAPTCHA2 = "10007";
    //10003 密码错误 11001 新密码格式错误 参考新密码设置格式重新设置（JS验证）
    String STATUS_PSW_ERROR = "10003";
    String STATUS_PSW_FORMAT_ERROR = "11001";
    //10004 动态密码错误;
    String STATUS_CAPTCHA_ERROR = "10004";
    //10006 动态密码失效系统已自动重新下发(仅北京移动会出现）
    String STATUS_CAPTCHA_INVALID = "10006";
    String STEP_INDEX = "step_index";
    String ID_FRONT = "id_front";
    String ID_CARD_RESULT = "ExIDCardResult";
    String IS_MODIFY = "is_modify";
    String IS_FINISH = "is_finish";
    String FACE = "face";
    String IMG_PATH = "img_path";
    String DATA = "data";
    //10008 开始采集行为数据 结束采集交互流程
    //11000 重置密码成功 结束密码重置流程（可以进行采集流程）
    //30000 错误信息 网络异常、运营商异常或当天无法下发短信验证码所导致的无法登陆（建议结束流程）
    //31000 重置密码失败 建议到营业厅重置密码



}
