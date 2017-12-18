package com.furja.qc.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.furja.qc.utils.Utils;

import static com.furja.qc.utils.Utils.showLog;

/**
 * Created by zhangmeng on 2017/12/13.
 */

public class UploadServices extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags,int startId) {
        showLog("上传数据");
        Utils.toUploadBackground();
        return super.onStartCommand(intent, flags, startId);
    }
}
