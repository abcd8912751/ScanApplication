package com.furja.qc.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.furja.qc.QcApplication;
import com.furja.qc.beans.Preferences;
import com.furja.qc.utils.SharpBus;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.furja.qc.utils.Constants.TAG_SCAN_BARCODE;
import static com.furja.qc.utils.Utils.showLog;

/**
 * 定时任务接收器
 */
public class ScanAndAlarmReceiver extends BroadcastReceiver {
    int requestCount=0;
    int errorCount=0;
    int requestOrderCount=0;
    @Override
    public void onReceive(Context context, Intent intent) {
        showLog("接收到广播");
        if(intent!=null) {
            analyseIntent(intent);
        }
    }

    /**
     * 解析Intent
     * @param intent
     */
    private void analyseIntent(Intent intent) {
        String action=intent.getAction();
        if(!TextUtils.isEmpty(action)){
            if(action.contains("ALARM")) {
                Preferences.saveAlarmOn(false);
                Preferences.clearUser();
                Observable.just(QcApplication.getContext())
                        .delay(1, TimeUnit.MINUTES) //闹钟响了之后,在一分钟之后唤醒新的闹钟
                        .subscribe(context -> {
                            registerAlarm(context);
                        });
            } else if(action.contains("barcode")){
                String barcode=intent.getStringExtra("BARCODE");
                if(barcode!=null&&!barcode.isEmpty()) {
                    barcode = barcode.toUpperCase();
                    barcode = barcode.replace("\n", "");
                    barcode = barcode.replace("\r", "");
                    SharpBus.getInstance().post(TAG_SCAN_BARCODE, barcode);
                }
            }
        }
    }


    /**
     * 设定闹钟
     */
    public static void registerAlarm(Context context) {
        if (!Preferences.isAlarmOn()) {
            showLog("设定闹钟");
            AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent
                    =new Intent(context, ScanAndAlarmReceiver.class);
            intent.setAction("com.furja.qc.services.ALARM");
            PendingIntent pendingIntent=PendingIntent.getBroadcast(context,0,intent,FLAG_UPDATE_CURRENT);
            am.setExact(AlarmManager.RTC_WAKEUP,getTriggerMillis(),pendingIntent);
            Preferences.saveAlarmOn(true);
        }
    }


    public static long getTriggerMillis(){
        Calendar calendar = Calendar.getInstance();
        Calendar triggerCal = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        triggerCal.set(Calendar.HOUR_OF_DAY,7);
        triggerCal.set(Calendar.MINUTE,30);
        if (!calendar.before(triggerCal)) {
            triggerCal.set(Calendar.HOUR_OF_DAY,19);
            triggerCal.set(Calendar.MINUTE,30);
            if (!calendar.before(triggerCal)) {
                triggerCal.set(Calendar.HOUR_OF_DAY,7);
                triggerCal.add(Calendar.DAY_OF_MONTH,1);
            }
        }
        return triggerCal.getTimeInMillis();
    }
}
