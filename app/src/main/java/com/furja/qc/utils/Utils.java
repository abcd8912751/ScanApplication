package com.furja.qc.utils;

import android.content.Context;
import android.os.Build;
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
import com.furja.qc.databases.BadMaterialLogDao;
import com.furja.qc.databases.BadTypeConfig;
import com.furja.qc.databases.BadTypeConfigDao;
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
import java.util.ArrayList;
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
    public static void showLog(String msg) {
        Log.i(LOG_TAG,msg);
    }
    public static void showToast(final String msg) {
        showLog(msg);
        try {
            Context toastContext = QcApplication.getContext();
            if (Build.VERSION.SDK_INT>24) {
                Toast toast=Toast.makeText(toastContext,null,Toast.LENGTH_SHORT);
                toast.setText(msg);toast.show();
            }
            else
                AnimToast.makeText(toastContext,msg).show();
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * 将 BadMaterialLog 数据存至本地
     * @param badMaterialLog
     */
    public static void saveToLocal(BadMaterialLog badMaterialLog) {
        DaoSession daoSession= QcApplication.getDaoSession();
        BadMaterialLogDao dao=daoSession.getBadMaterialLogDao();
        dao.insertOrReplace(badMaterialLog);
        showLog(badMaterialLog+": 已保存");
        daoSession.clear();
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
        final DaoSession daoSession=QcApplication.getDaoSession();
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

    /**
     * 从数据索取未上传的Log进行上传
     * @return
     */
    public static List<BadMaterialLog> queryNotUploadLog() {
        DaoSession daoSession= QcApplication.getDaoSession();
        BadMaterialLogDao dao=daoSession.getBadMaterialLogDao();
        List<BadMaterialLog> badLogs,
                uploadLogs=new ArrayList<BadMaterialLog>();
        badLogs=dao.loadAll();
        if(badLogs!=null) {
            for(BadMaterialLog badlog:badLogs) {
                if (badlog.isUploaded())
                    delete(badlog);
                else
                    uploadLogs.add(badlog);
            }
        }
        daoSession.clear();
        showLog("待上传的数据条数:"+uploadLogs.size());
        return uploadLogs;
    }



    public static void delete(BadMaterialLog badMaterialLog) {
        try {
            DaoSession daoSession= QcApplication.getDaoSession();
            BadMaterialLogDao dao=daoSession.getBadMaterialLogDao();
            dao.delete(badMaterialLog);
            showLog("已删除本次记录"+badMaterialLog.getMaterialISN());
            daoSession.clear();
        } catch (Exception e){
            e.printStackTrace();
        }
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


    public static void fixInputMethodMemoryLeak(Context context) {
        if (context == null)
            return;
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager == null)
            return;
        String[] viewArr = new String[]{"mCurRootView", "mServedView", "mNextServedView", "mLastSrvView"};
        Field field;
        Object fieldObj;
        for (String view : viewArr) {
            try {
                field = inputMethodManager.getClass().getDeclaredField(view);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                fieldObj = field.get(inputMethodManager);
                if (fieldObj != null && fieldObj instanceof View) {
                    View fieldView = (View) fieldObj;
                    if (fieldView.getContext() == context) {
                        field.set(inputMethodManager, null);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
