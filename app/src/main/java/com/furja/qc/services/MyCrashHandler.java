package com.furja.qc.services;

import android.content.Context;


import com.furja.qc.QcApplication;
import com.furja.qc.utils.RetrofitBuilder;
import com.furja.qc.utils.RetryWhenUtils;
import com.furja.qc.utils.Utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

import static com.furja.qc.utils.Constants.getVertxUrl;
import static com.furja.qc.utils.Utils.showLog;


/**
 * 未捕捉异常的日志上传
 */

public class MyCrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private Context context;
    public static MyCrashHandler crashHandler;
    public static MyCrashHandler getInstance()
    {
        synchronized (MyCrashHandler.class) {
            if(crashHandler==null)
                crashHandler=new MyCrashHandler(QcApplication.getContext());
        }
        return crashHandler;
    }

    public static void init()
    {
        Thread.setDefaultUncaughtExceptionHandler(getInstance());
    }

    public MyCrashHandler(Context context1)
    {
        this.context=context1;
    }
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        String string="QcApplicationAPP>"+QcApplication.getUserID()+":"+getStackTraceInfo(ex);
        upload(string);
//        if(context==null)
//            return;
//        Intent launchIntent = context.getPackageManager()
//                .getLaunchIntentForPackage(context.getPackageName());
//        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        context.startActivity(launchIntent);
//        try {
//            android.os.Process.killProcess(android.os.Process.myPid());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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

    public void upload(String errorDetail){
        try {
            RequestBody jsonBody = Utils.getRequestBody(errorDetail);
            RetrofitBuilder.getHelperByUrl(getVertxUrl())
                    .submitErrorLog(jsonBody)
                    .subscribeOn(Schedulers.io())
                    .retryWhen(RetryWhenUtils.create())
                    .subscribe(responseBody -> {
                    },error->{
                        });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void uploadError(Throwable ex)
    {
        upload(getStackTraceInfo(ex));
    }
}
