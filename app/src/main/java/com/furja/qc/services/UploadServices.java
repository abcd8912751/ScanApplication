package com.furja.qc.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

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
        return super.onStartCommand(intent, flags, startId);
    }
}
