package com.furja.qc.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


import com.furja.qc.QcApplication;
import com.furja.qc.beans.User;
import com.furja.qc.utils.RetrofitBuilder;
import com.furja.qc.utils.RetryWhenUtils;
import com.furja.qc.utils.Utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.sentry.Sentry;
import io.sentry.SentryClientFactory;
import io.sentry.android.AndroidSentryClientFactory;
import okhttp3.RequestBody;
import static com.furja.qc.utils.Constants.getVertxUrl;
import static com.furja.qc.utils.Utils.getDeviceID;
import static com.furja.qc.utils.Utils.getIPAddress;
import static com.furja.qc.utils.Utils.showLog;


/**
 * 未捕捉异常的日志上传
 */

public class MyCrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    public static MyCrashHandler crashHandler;

    public static MyCrashHandler getInstance() {
        synchronized (MyCrashHandler.class) {
            if(crashHandler==null)
                crashHandler=new MyCrashHandler();
        }
        return crashHandler;
    }

    public static void init(Context applicationContext) {
        Thread.setDefaultUncaughtExceptionHandler(getInstance());
        SentryClientFactory factory = new AndroidSentryClientFactory(applicationContext);
        Sentry.init("http://70391f05564d4ffbabffc090a6a464c0@192.168.8.202:9000/5", factory);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        postError(ex);
        QcApplication.getInstance().restartApp();
    }

    /**
     * 提交Error
     * @param ex
     */
    public void postError(Throwable ex) {
        User user = QcApplication.getUser();
        String userName = QcApplication.getUserID();
        if(user!=null)
            userName = user.getUserName();
        io.sentry.event.User sentryUser = new io.sentry.event.User(getDeviceID(), userName, getIPAddress(), "");
        Sentry.getContext().setUser(sentryUser);
        Sentry.capture(ex);
    }


    private  String getStackTraceInfo(final Throwable throwable) {
        String trace = "";
        try {
            Writer writer = new StringWriter();
            PrintWriter pw = new PrintWriter(writer);
            throwable.printStackTrace(pw);
            trace = writer.toString();
            pw.close();
        } catch (Exception e) {
            return "";
        }
        showLog(trace);
        return trace;
    }

}
