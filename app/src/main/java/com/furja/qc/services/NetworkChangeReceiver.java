package com.furja.qc.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.furja.qc.utils.RetrofitBuilder;
import com.furja.qc.utils.RetrofitHelper;

import java.util.concurrent.Callable;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.furja.qc.utils.Constants.VERTX_INNER_URL;
import static com.furja.qc.utils.Constants.VERTX_OUTER_URL;
import static com.furja.qc.utils.Utils.showLog;

/**
 * 网络切换接收
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    private static boolean isInnerNet=true;
    private static Context context;
    private static NetworkChangeReceiver networkChangeReceiver;
    public static void init(Context applicationContext) {
        context=applicationContext;
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        getInstance();
        context.registerReceiver(networkChangeReceiver,intentFilter);
    }

    public static NetworkChangeReceiver getInstance() {
        if(networkChangeReceiver==null)
            networkChangeReceiver=new NetworkChangeReceiver();
        return networkChangeReceiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null||!networkInfo.isAvailable()){
            showLog("网络不可用");
        } else{
            checkNetworkDomain();
        }
    }

    /**
     * 检测终端是在内网还是外网
     */
    public void checkNetworkDomain() {
        RetrofitHelper helper = RetrofitBuilder.getHelperByUrl(VERTX_OUTER_URL);
        helper.request(VERTX_OUTER_URL)
                .subscribeOn(Schedulers.io())
                .subscribe(responseBody -> {
                    NetworkChangeReceiver.isInnerNet = false;
                    showLog("网络状态变化为外网");
                    checkInnerNetwork();
                }, error->{
                    NetworkChangeReceiver.isInnerNet = true;
                    showLog("网络状态变化为内网");
                });
    }

    /**
     * 检测终端是在内网还是外网
     */
    public void checkInnerNetwork() {
        RetrofitHelper helper = RetrofitBuilder.getHelperByUrl(VERTX_INNER_URL);
        helper.request(VERTX_INNER_URL)
                .subscribe(responseBody -> {
                    NetworkChangeReceiver.isInnerNet = true;
                    showLog("网络状态变化为内网");
                }, error->{
                    NetworkChangeReceiver.isInnerNet = false;
                    showLog("网络状态变化为外网");
                });
    }

    /**
     * ping 指定IP返回成功与否,此为linux下的方法
     * @param host
     * @return
     */
    public  boolean ping(String host) {
        String line = null;
        Process process = null;
        int pingCount=1;
        String command = "ping -c " + pingCount + " " + host;
        boolean isSuccess = false;
        try {
            process = Runtime.getRuntime().exec(command);
            if (process == null) {
                return false;
            }
            int status = process.waitFor();
            if (status == 0) {
                isSuccess = true;
            } else {
                isSuccess = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return isSuccess;
    }

    public static boolean isInnerNet() {
        return isInnerNet;
    }

    public static void setIsInnerNet(boolean isInnerNet) {
        NetworkChangeReceiver.isInnerNet = isInnerNet;
    }

    public static void unregister() {
        if(context!=null&&networkChangeReceiver!=null)
            context.unregisterReceiver(networkChangeReceiver);
    }

}
