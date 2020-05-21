package com.furja.qc;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.text.TextUtils;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.furja.qc.beans.User;
import com.furja.qc.databases.DaoMaster;
import com.furja.qc.databases.DaoSession;
import com.furja.qc.beans.Preferences;
import com.furja.qc.services.ScanAndAlarmReceiver;
import com.furja.qc.services.MyCrashHandler;
import com.furja.qc.services.NetworkChangeReceiver;
import com.furja.qc.utils.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.OkHttpClient;
import static com.furja.qc.utils.Constants.LOG_TAG;
import static com.furja.qc.utils.Constants.TYPE_BADLOG_WITHBTN;
import static com.furja.qc.utils.Utils.showLog;

/**
 * QC_Application
 */

public class QcApplication extends Application {
    private static volatile Context context;
    private static User user;   //当前已登录用户
    private static DaoMaster daoMaster;
    public static QcApplication getInstance()
    {
        return (QcApplication) context;
    }

    public static Context getContext() {
        return context;
    }

    public static User getUser() {
        return user;
    }

    public static String getUserID() {
        String userID="";
        if(user!=null)
            userID = user.getUserId();
        if(TextUtils.isEmpty(userID)) {
            if(TextUtils.isEmpty(Preferences.getUserID()))
                userID = "0";
            else
                userID = Preferences.getUserID();
        }
        return userID;
    }

    /**
     * 将用户信息存储
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
        context = getApplicationContext();
        //懒加载
        Observable.just(context)
                .delay(1L,TimeUnit.SECONDS)
                .subscribe(new Consumer<Context>() {
                    @Override
                    public void accept(Context context) throws Exception {
                        initSomeFrameWork();
                    }
                });
    }

    /**
     * 初始化使用的开源库
     */
    private void initSomeFrameWork() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor(LOG_TAG))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
        MyCrashHandler.init(context);
        NetworkChangeReceiver.init(this);   //注册网络接收器
        if (Preferences.getSourceType() == TYPE_BADLOG_WITHBTN) {
            ScanAndAlarmReceiver.registerAlarm(context);
        }
        RxJavaPlugins.setErrorHandler(throwable -> {
            MyCrashHandler.getInstance().postError(throwable);
        });
        Utils.initToastHandler();
    }

    /**
     * APP异常时记录日志并重启
     */
    public void restartApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Observable.just(launchIntent)
                .delay(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Intent>() {
                    @Override
                    public void accept(Intent intent) throws Exception {
                        startActivity(intent);
                        android.os.Process
                                .killProcess(android.os.Process.myPid());
                    }
                },error->{
                    error.printStackTrace();
                });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        NetworkChangeReceiver.unregister();
        Utils.destoryHandler();
    }

}
