package com.furja.qc.utils;

/**
 * Created by zhangmeng on 2017/12/4.
 */

public class Constants {
    public static final String LOG_TAG="marker_event";
    public static final String FURIA_LOGIN_URL ="http://192.168.10.5:5050/bcwebservice/Service.asmx/";
    public static final String FURIA_BARCODEINFO_URL ="http://192.168.8.46:8118/FJCommonInterface/GetBarCodeInfo/";
    public static final String FURIA_UPLOAD_URL ="http://192.168.8.46:8118/FJBadTypeInterface/SendBadTypeLog/";
    public static final String FURIA_BADTYPEBASIC_URL ="http://192.168.8.46:8118/FJBadTypeInterface/GetBadTypeBasicData";
    public static final String FJ_BADTYPETOTAL_WORKPLACE ="http://192.168.8.46:8118/FJBadTypeInterface/GetBadTypeTotal/";
    //当点击Button或提交时传递该Tag事件
    public static final String UPDATE_BAD_COUNT ="UPDATE_BAD_COUNT";
    public static final String FRAGMENT_ON_TOUCH ="FRAGMENT_ON_TOUCH";
    public static final String BADLOG_FRAGMENT_INITFINISH ="updateBadLogWithBtn_Data";
    //根据物料信息更新工单列表
    public static final String UPDATE_WORKORDER_BY_MATERIAL ="updateBY_MATERIAL_INFO";
    //更新Button计数所在Fragment数据
    public static final String UPDATE_BADLOGWITHBTN="freshTheButtonFragment";
    //更新KeyBoard所在Fragment数据
    public static final String UPDATE_BADLOGWITHKEY="freshTheKeyBoardFragment";
    //同步异常类型配置完成后传递此事件
    public static final String SYNCOVER_BADTYPE_CONFIG="freshTheKeyBoardFragment";
    public static final String ALARM_ACTION_ON="ui.splash.alarm";
    public static final String INTER_SPLIT =" -> ";
    //为录入异常数据使用视图的标识,-1为没有设置
    public static final int TYPE_BADLOG_WITHBTN=1;
    public static final int TYPE_BADLOG_WITHKEY=2;
    public static final int TYPE_BADLOG_EMPTY=-1;
    public static final String UPLOAD_FINISH="uploadfinish";
    public static final String RESET_CONFIG="reset_config";
    public static final String INTERNET_ABNORMAL="网络连接异常";
    public static final String MATERIAL_INTERNET_ABNORMAL="获取物料信息网络连接异常";
    public static final String OPERATOR_LOGIN_ERROR="账号或密码错误";
    public static final String BUTTON_FRAGMENT_TITLE="注塑车间";
    public static final String KEY_FRAGMENT_TITLE="装配车间";
    public static final String INFORMATION_HAS_NUL="INFORMATION_HAS_NULL";
    }
