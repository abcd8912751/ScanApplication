package com.furja.qc.beans;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.furja.qc.QcApplication;

import static com.furja.qc.utils.Constants.TYPE_BADLOG_EMPTY;

/**
 * SharedPreference操作类
 */

public class Preferences {
    private static final String KEY_USER = "user";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_SOURSEOFTYPE="sourceOftype";//工作场景
    private static final String KEY_AUTO_LOGIN="autoLogin"; //是否自动登录
    private static final String KEY_ALARM_ON="alarm_On";    //是否已经开启闹钟
    private static Context context;


    /**
     * 设置静态Context
     * @param contex
     */
    public static void setContext(Context contex)
    {
        context=contex;
    }

    /**
     * 获取录入数据的方式,1为ButtonFragment视图
     * 2为KeyBoard代码录入的Fragment视图
     * @return
     */
    public static int getSourceType() {
        if(TextUtils.isEmpty(getString(KEY_SOURSEOFTYPE)))
            return TYPE_BADLOG_EMPTY;  //为空返回-1
        else
            return Integer.valueOf(getString(KEY_SOURSEOFTYPE));
    }

    public static void saveSourceType(String type) {
        saveString(KEY_SOURSEOFTYPE,type);
    }

    public static String getOperatorId() {
        return getString(KEY_USER);
    }

    public static String getOperatorPassword() {
        return getString(KEY_PASSWORD);
    }

    public static boolean isAutoLogin()
    {
        return Boolean.parseBoolean(getString(KEY_AUTO_LOGIN));
    }

    public static void saveAutoLogin(boolean isAutoLogined)
    {
        saveString(KEY_AUTO_LOGIN,isAutoLogined+"");
    }

    public static boolean isAlarmOn()
    {
        return Boolean.parseBoolean(getString(KEY_ALARM_ON));
    }

    public static void saveAlarmOn(boolean isAlarmOn)
    {
        saveString(KEY_ALARM_ON,isAlarmOn+"");
    }

    /**
     *保存当前测试次数
     */
    private static void saveUserName(String user)
    {
        saveString(KEY_USER,user);
    }
    /**
     *保存密码
     */
    private static void saveOperatorPassword(String pass)
    {
        saveString(KEY_PASSWORD,pass);
    }



    private static void saveString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static String getString(String key) {
        return getSharedPreferences().getString(key, null);
    }

    static SharedPreferences getSharedPreferences() {
        if(context==null)
            context= QcApplication.getContext();
        return context.getSharedPreferences("FTP_CONFIG", Context.MODE_PRIVATE);
    }

    public static void clear() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear();
        editor.commit();
    }

    public static void saveUser(User user) {
        saveUserName(user.getUserName());
        saveOperatorPassword(user.getPassword());
    }
}
