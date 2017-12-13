package com.furja.qc;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.furja.qc.beans.User;
import com.furja.qc.databases.DaoMaster;
import com.furja.qc.databases.DaoSession;
import com.furja.qc.beans.Preferences;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static com.furja.qc.utils.Utils.showLog;

/**
 * QC_Application
 */

public class QcApplication extends Application {
    private static volatile Context context;
    private static  DaoMaster daoMaster;
    private static User user;   //当前已登录用户
    public static QcApplication getInstance()
    {
        return (QcApplication) context;
    }

    public static Context getContext() {
        return context;
    }

    public static DaoMaster getDaoMaster() {
        return daoMaster;
    }

    public static User getUser() {
        return user;
    }

    /**
     * 将员工用户信息存储
     * @param user
     */
    public static void setUserAndSave(User user) {
        QcApplication.user = user;
        if(user!=null)
            Preferences.saveUser(user);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        initSomeFrameWork();
        initDaoMaster();
        showLog("创建了->"+getClass());

    }

    /**
     * 初始化使用的开源库
     */
    private void initSomeFrameWork() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("marker_event"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)

                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    /**
     * 初始化GreenDao
     */
    private synchronized void initDaoMaster() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(QcApplication.getContext(), "BadMaterialLog.db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        this.daoMaster = new DaoMaster(db);//获取唯一DaoMaster对象
    }

    public static DaoSession getDaoSession()
    {
        return daoMaster.newSession();
    }
}
