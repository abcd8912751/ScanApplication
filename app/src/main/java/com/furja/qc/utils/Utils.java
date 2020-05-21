package com.furja.qc.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.furja.qc.QcApplication;
import com.furja.qc.R;
import com.furja.qc.databases.BadMaterialLog;
import com.furja.qc.databases.BadTypeConfig;
import com.furja.qc.databases.BadTypeConfigDao;
import com.furja.qc.databases.DaoMaster;
import com.furja.qc.databases.DaoSession;
import com.furja.qc.databases.ProductModel;
import com.furja.qc.databases.ProductModelDao;
import com.furja.qc.jsonbeans.BadTypeConfigJson;
import com.furja.qc.jsonbeans.BadTypeConfigJson.*;
import com.furja.qc.services.NetworkChangeReceiver;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.RequestBody;

import static com.furja.qc.utils.Constants.FURJA_BADTYPEBASIC_URL;
import static com.furja.qc.utils.Constants.LOG_TAG;
import static com.furja.qc.utils.Constants.SYNCOVER_BADTYPE_CONFIG;

/**
 * 常用工具类
 */

public class Utils {
    public static DaoMaster daoMaster;
    public static Handler toastHandler;
    private static HandlerThread handlerThread;
    public static final int TAG_TOAST_MESSAGE = 1232;
    public static void showLog(String msg) {
        Log.i(LOG_TAG,msg);
    }

    /**
     * 显示Toast
     * @param msg
     */
    public static void showToast(final String msg) {
        showLog(msg);
        if (!TextUtils.isEmpty(msg)) {
            if (isPrimaryThread()) {
                showToastView(msg);
            }
            else if (toastHandler != null) {
                Message message = toastHandler.obtainMessage();
                message.what = TAG_TOAST_MESSAGE;
                message.obj = msg;
                toastHandler.sendMessage(message);
            }
        }
    }

    /**
     * 初始化ToastHandler
     */
    public static void initToastHandler() {
        handlerThread = new HandlerThread("ToastHandler");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        toastHandler = new Handler(looper, new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what == TAG_TOAST_MESSAGE) {
                    String message = textOf(msg.obj);
                    showToastView(message);
                }
                return true;
            }
        });
    }

    /**
     * 判断是否为主线程
     * @return
     */
    public static boolean isPrimaryThread(){
        return Looper.getMainLooper() == Looper.myLooper();
    }

    private static void showToastView(String msg) {
        try {
            Context context = QcApplication.getContext();
            if (Build.VERSION.SDK_INT > 24) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            } else {
                AnimToast.makeText(context, msg).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备ID
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getDeviceID(){
        String deviceID = "";
        Context context = QcApplication.getContext();
        try {
            TelephonyManager telephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceID=telephonyMgr.getDeviceId();
        } catch (Exception e) {
        }
        if(!TextUtils.isEmpty(deviceID))
            deviceID= "imei -> "+deviceID;
        else {
            try {
                deviceID= Settings.Secure.getString(context.getContentResolver(), "android_id");
                if(!TextUtils.isEmpty(deviceID))
                    deviceID= "android_id -> "+deviceID;
                else
                    deviceID= "serialnumber -> "+android.os.Build.SERIAL;
            } catch (Exception e) {
            }
        }
        if(TextUtils.isEmpty(deviceID)) {
            String ipAddress=getIPAddress();
            if(!TextUtils.isEmpty(ipAddress))
                deviceID = "ip -> " + ipAddress;
        }
        return deviceID;
    }

    public static String getIPAddress() {
        Context context = QcApplication.getContext();
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        String ipAddress="";
        if(networkInfo!=null&&networkInfo.isAvailable()){
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {    // 当前使用2G/3G/4G网络
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
                                return inetAddress.getHostAddress();
                        }
                    }
                } catch (Exception e) {
                }
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {    // 当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                ipAddress = intIP2StringIP(wifiInfo.getIpAddress());    // 得到IPV4地址
            }
        }
        return ipAddress;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }


    public static DaoSession getDaoSession() {
        initDaoMaster();
        return daoMaster.newSession();
    }

    /**
     * 初始化GreenDao
     */
    private static synchronized void initDaoMaster() {
        if(daoMaster==null){
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(QcApplication.getContext(), "BadMaterialLog.db", null);
            SQLiteDatabase db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
        }
    }

    /**
     * 抖动自己
     * @param view
     */
    public static void shakeOwnSelf(View view) {
        Animation shakeAnimation
                = new TranslateAnimation(0, 4, 0, 0);
        //设置一个循环加速器，使用传入的次数就会出现摆动的效果。
        shakeAnimation.setInterpolator(new CycleInterpolator(2));
        shakeAnimation.setDuration(500);
        view.startAnimation(shakeAnimation);
    }





    public static RequestBody getRequestBody(Object object) {
        String json= JSON.toJSONString(object);
        return RequestBody
                .create(okhttp3.MediaType.parse("application/json;charset=utf-8"),json);
    }

    /**
     * 禁用输入法即只允许条码扫码
     */
    public static void disableShowInput(EditText edit_mainBarCode){
        EditText editText = edit_mainBarCode;
        Class<EditText> cls = EditText.class;
        Method method;
        try {
            method = cls.getMethod("setShowSoftInputOnFocus",boolean.class);
            method.setAccessible(true);
            method.invoke(editText,false);
        }catch (Exception e) {
        }
        try {
            method = cls.getMethod("setSoftInputShownOnFocus",boolean.class);
            method.setAccessible(true);
            method.invoke(editText,false);
        }
        catch (Exception e) {
        }
    }

    /**
     * 同步本地异常类型至本地数据库
     * fromTag为请求同步的界面提供的Tag
     */
    public static void syncBadTypeConfig(final boolean isReset) {
        String badtypeUrl
                = Constants.getBaseUrl()+ FURJA_BADTYPEBASIC_URL;
        final DaoSession daoSession=Utils.getDaoSession();
        final BadTypeConfigDao typeConfigDao=daoSession.getBadTypeConfigDao();
        final ProductModelDao productModelDao=daoSession.getProductModelDao();
        if(isReset) {
            typeConfigDao.deleteAll();
            productModelDao.deleteAll();
        }
        else {
            List<BadTypeConfig> configs=
                    typeConfigDao.loadAll();
            if(configs!=null&&configs.size()>0) {
                SharpBus.getInstance().post(SYNCOVER_BADTYPE_CONFIG,true);
                daoSession.clear();
                return;
            }
        }
        OkHttpUtils
                .get()
                .url(badtypeUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        SharpBus.getInstance().post(SYNCOVER_BADTYPE_CONFIG,"false");
                        showLog(e.toString());
                    }
                    @Override
                    public void onResponse(String responce, int i) {
                        showLog("获得异常类型基础信息");
                        BadTypeConfigJson badTypeConfigJson
                                = JSON.parseObject(responce,BadTypeConfigJson.class);
                        if(badTypeConfigJson.getErrCode()!=110) {
                            Observable.just(badTypeConfigJson.getErrData())
                                    .observeOn(Schedulers.io())
                                    .subscribe(new Consumer<List<ErrDataBean>>() {
                                        @Override
                                        public void accept(List<ErrDataBean> errDataBeans) throws Exception {
                                            for(ErrDataBean configBean:errDataBeans) {
                                                BadTypeConfig config=
                                                        new BadTypeConfig(null,configBean.getSourceType(),configBean.getBadTypeInfo()+"",configBean.getBadTypeDetail());
                                                typeConfigDao.insertOrReplace(config);
                                            }
                                            typeConfigDao.detachAll();
                                            SharpBus.getInstance().post(SYNCOVER_BADTYPE_CONFIG,true);
                                        }
                                    },error->{
                                        typeConfigDao.detachAll();
                                    });
                        }
                    }
                });
        String url = Constants.getVertxUrl();
        RetrofitHelper helper
                =RetrofitBuilder.getHelperByUrl(url,RetrofitHelper.class);
        helper.getProductModel()
                .subscribeOn(Schedulers.io())
                .subscribe(listBaseHttpResponse -> {
                   if(listBaseHttpResponse.getCode()>0) {
                       List<ProductModel> productModelJsons
                               = listBaseHttpResponse.getResult();
                       for(ProductModel model:productModelJsons) {
                           model.setId(null);
                           productModelDao.insertOrReplace(model);
                       }
                       productModelDao.detachAll();
                   }
                },throwable -> {
                    throwable.printStackTrace();
                    productModelDao.detachAll();
                });
    }


    public static MaterialDialog showWaitingDialog(Context context) {
        return showWaitingDialog(context,"        正在提交数据...");
    }
    public static MaterialDialog showWaitingDialog(Context context,String content) {
        MaterialDialog.Builder builder
                =new MaterialDialog.Builder(context);
        MaterialDialog dialog
                =builder
                .iconRes(R.mipmap.ic_wait)
                .title("请稍候")
                .content(content)
                .contentColorRes(R.color.white)
                .titleColorRes(R.color.white)
                .cancelable(false)
                .build();
        dialog.show();
        Window window=dialog.getWindow();
        WindowManager.LayoutParams p =window.getAttributes();  //获取对话框当前的参数值
        p.dimAmount=0.2f;
        p.alpha=30;
        window.setAttributes(p);
        window.setBackgroundDrawableResource(R.drawable.shape_wxdialog_bg);
        return dialog;
    }





    public static  <T> String textOf(T str){
        if (str == null)
            return "";
        else {
            String string=str+"";
            try {
                if (Double.valueOf(string)==0)
                    return "";
            } catch (Exception e) {
            }
            return string;
        }
    }

    /**
     * 获取double值
     * @param str
     * @param <T>
     * @return
     */
    public static  <T> double doubleOf(T str){
        if (str == null)
            return 0;
        else{
            String string=str+"";
            try {
                if(!string.equals("."))
                    return Double.valueOf(string);
            } catch (Exception e) { }
            return 0;
        }
    }


    public static  <T> int intOf(T str){
        return (int) doubleOf(str);
    }

    public static String exceptionToString(Exception e) {
        try {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            printWriter.close();
            return writer.toString();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return e.toString();
    }


    public static int getSampleSizeByProduction(double totalProduction) {
        if(totalProduction>10000)
            return 315;
        if(totalProduction>3200)
            return 200;
        if(totalProduction>1200)
            return 125;
        if(totalProduction>500)
            return 80;
        if(totalProduction>280)
            return 50;
        if(totalProduction>150)
            return 32;
        if(totalProduction>90)
            return 20;
        if(totalProduction>50)
            return 13;
        if(totalProduction>25)
            return 8;
        if(totalProduction>15)
            return 5;
        if(totalProduction>8)
            return 3;
        else if(totalProduction>0)
            return 2;
            else
                return 0;
    }

    public static void destoryHandler(){
        if(handlerThread!=null){
            handlerThread.quit();
            handlerThread = null;
            toastHandler = null;
        }
    }
}
