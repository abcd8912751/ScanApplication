package com.furja.qc.utils;

import androidx.annotation.NonNull;

import com.furja.qc.services.NetworkChangeReceiver;

/**
 * Created by zhangmeng on 2017/12/4.
 */
public class Constants {
    public static final String LOG_TAG="ScanApplication";
    public static final String FURJA_INNER_URL="http://192.168.8.46:8118";
    public static final String VERTX_INNER_URL ="http://192.168.8.46:8378";
    public static final String VERTX_OUTER_URL ="http://www.nbfurja.com:8378";
    public static final String HTTPS_INNER_URL="https://192.168.8.46:7070";
    public static final String HTTPS_OUTER_URL="https://www.nbfurja.com:7070";
    public static final String VERTX_TEST_URL ="http://192.168.10.115:8378";
    public static final String FURJA_OUTER_URL="http://www.nbfurja.com";
    public static final String FURJA_BARCODEINFO_URL ="/FJCommonInterface/GetBarCodeInfo/";
    public static final String FURJA_UPLOAD_URL ="/FJBadTypeInterface/SendBadTypeLog/";
    public static final String FURJA_BADTYPEBASIC_URL ="/FJBadTypeInterface/GetBadTypeBasicData";
    public static final String FJ_BADTYPETOTAL_WORKPLACE ="/FJBadTypeInterface/GetBadTypeTotal/";
    public static final String ZOOM_EXTRA_NAME="url-List";
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
    public static final String INTERNET_ABNORMAL="网络连接异常";
    public static final String NODATA_AVAILABLE="没有找到符合条件的结果";
    public static final String OPERATOR_LOGIN_ERROR="账号或密码错误";
    public static final String TAG_SCAN_BARCODE="二维码扫描录入完成";
    //为录入异常数据使用视图的标识,-1为没有设置
    public static final int TYPE_BADLOG_WITHBTN=1;  //注塑车间
    public static final int TYPE_BADLOG_WITHKEY=2;  //装配车间
    public static final int TYPE_BADLOG_EMPTY=-1;
    public static final String UPLOAD_FINISH="uploadfinish";
    public static final String RESET_CONFIG="reset_config";
    public static final String MATERIAL_INTERNET_ABNORMAL="获取物料信息网络连接异常";
    public static final String BUTTON_FRAGMENT_TITLE="注塑车间";
    public static final String KEY_FRAGMENT_TITLE="装配车间";
    public static final String INFORMATION_HAS_NUL="INFORMATION_HAS_NULL";
    public static final String TAG_GOT_TOURLOG="获取到注塑巡检数据";
    public static final String TAG_GOT_DIMENLOG="获取到注塑尺寸记录数据";
    public static final String ACTION_UPDATE_APK="UPDATE_APK";
    public static final String DIMENITEM_TOUCH ="点击了RecyclerView的ChildItem";
    public static final int TYPE_RULER_GAUGE =0;  //卡尺测试
    public static final int TYPE_PIN_GAUGE=1;    //针规测试
    public static final int TYPE_NODIMEN_GAUGE =2; //没实测值,试检具判断
    public static final int TYPE_HARDNESS_GAUGE =3; //硬度测试
    public static final int TYPE_FEELER_GAUGE =4;   //塞尺平面度测试
    @NonNull
    public static String getBaseUrl() {
        String url="";
        if(NetworkChangeReceiver.isInnerNet())
            url= FURJA_INNER_URL;
        else
            url= FURJA_OUTER_URL;
        return url;
    }

    @NonNull
    public static String getHttpsUrl() {
        String url="";
        if(NetworkChangeReceiver.isInnerNet())
            url= HTTPS_INNER_URL;
        else
            url= HTTPS_OUTER_URL;
        return url;
    }

    @NonNull
    public static String getVertxUrl() {
        String url="";
        if(NetworkChangeReceiver.isInnerNet())
            url= VERTX_INNER_URL;
        else
            url= VERTX_OUTER_URL;
        return url;
    }
}
