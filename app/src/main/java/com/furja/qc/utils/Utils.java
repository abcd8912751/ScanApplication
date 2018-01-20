package com.furja.qc.utils;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.furja.qc.QcApplication;
import com.furja.qc.databases.BadMaterialLog;
import com.furja.qc.databases.BadMaterialLogDao;
import com.furja.qc.databases.BadTypeConfig;
import com.furja.qc.databases.BadTypeConfigDao;
import com.furja.qc.databases.DaoSession;
import com.furja.qc.jsonbeans.BadTypeConfigJson;
import com.furja.qc.jsonbeans.BadTypeConfigJson.*;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;

import static com.furja.qc.utils.Constants.FURIA_BADTYPEBASIC_URL;
import static com.furja.qc.utils.Constants.LOG_TAG;
import static com.furja.qc.utils.Constants.SYNCOVER_BADTYPE_CONFIG;

/**
 * 常用工具类
 */

public class Utils {
    public static void showLog(String msg)
    {
        Log.i(LOG_TAG,msg);
    }

    public static void showToast(final String msg)
    {
        showLog(msg);
        new Thread(){
            public void run(){
                try {
                    Looper.prepare();
                    Context context= QcApplication.getContext();
                    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
     * 将 BadMaterialLog 数据存至本地
     * @param badMaterialLog
     */
    public static void saveToLocal(BadMaterialLog badMaterialLog)
    {
        DaoSession daoSession= QcApplication.getDaoSession();
        BadMaterialLogDao dao=daoSession.getBadMaterialLogDao();
        dao.save(badMaterialLog);
    }

    /**
     * 将 BadMaterialLog 数据存至本地并上传
     * @param badMaterialLog
     */
    public static void saveAndUpload(BadMaterialLog badMaterialLog)
    {
        saveToLocal(badMaterialLog);
        badMaterialLog.uploadToRemote();
    }



    /**
     * 同步本地异常类型至本地数据库
     * fromTag为请求同步的界面提供的Tag
     */
    public static void syncBadTypeConfig(final boolean isReset)
    {
        String getBadtypeUrl=FURIA_BADTYPEBASIC_URL;
        final DaoSession daoSession=QcApplication.getDaoSession();
        final BadTypeConfigDao typeConfigDao=daoSession.getBadTypeConfigDao();

        if(isReset)
            typeConfigDao.deleteAll();
        else
        {
            List<BadTypeConfig> configs=
                    typeConfigDao.loadAll();
            if(configs!=null&&configs.size()>0)
            {
                SharpBus.getInstance().post(SYNCOVER_BADTYPE_CONFIG,true);
                return;
            }
        }
        OkHttpUtils
                .get()
                .url(getBadtypeUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        SharpBus.getInstance().post(SYNCOVER_BADTYPE_CONFIG,"false");
                        showLog(e.toString());
                    }
                    @Override
                    public void onResponse(String responce, int i) {
                        showLog("获取异常类型基础信息:"+responce);
                        BadTypeConfigJson badTypeConfigJson
                                = JSON.parseObject(responce,BadTypeConfigJson.class);
                        if(badTypeConfigJson.getErrCode()!=110)
                        {
                            Observable.just(badTypeConfigJson.getErrData())
                                    .observeOn(Schedulers.io())
                                    .subscribe(new Consumer<List<ErrDataBean>>() {
                                        @Override
                                        public void accept(List<ErrDataBean> errDataBeans) throws Exception {
                                            for(ErrDataBean configBean:errDataBeans)
                                            {
                                                BadTypeConfig config=
                                                        new BadTypeConfig((long) configBean.getBadTypeID(),configBean.getSourceType(),configBean.getBadTypeInfo()+"",configBean.getBadTypeDetail());
                                                typeConfigDao.insertOrReplace(config);
                                            }
                                            SharpBus.getInstance().post(SYNCOVER_BADTYPE_CONFIG,true);
                                        }
                                    });
                        }
                    }
                });
    }




    /**
     * 上传数据使用
     */
    public static void toUpload()
    {
        try {
            Observable.fromCallable(new Callable<List<BadMaterialLog>>() {
                @Override
                public List<BadMaterialLog> call() throws Exception {
                    List<BadMaterialLog> badMaterialLogs =queryNotUploadLog();
                    return badMaterialLogs;
                }}) .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Consumer<List<BadMaterialLog>>() {
                        @Override
                        public void accept(List<BadMaterialLog> badMaterialLogs) throws Exception {
                            for(BadMaterialLog badLog: badMaterialLogs)
                            {
                                badLog.uploadToRemote();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 后台上传数据使用
     */
    public static void toUploadBackground()
    {
        try {
            Observable.fromCallable(new Callable<List<BadMaterialLog>>() {
                @Override
                public List<BadMaterialLog> call() throws Exception {
                    List<BadMaterialLog> badMaterialLogs =queryNotUploadLog();
                    return badMaterialLogs;
                }}) .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Consumer<List<BadMaterialLog>>() {
                        @Override
                        public void accept(List<BadMaterialLog> badMaterialLogs) throws Exception {
                            for(BadMaterialLog badLog: badMaterialLogs)
                            {
                                badLog.uploadToRemoteBackGround();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 从数据索取未上传的Log进行上传
     * @return
     */
    public static List<BadMaterialLog> queryNotUploadLog()
    {
        DaoSession daoSession= QcApplication.getDaoSession();
        BadMaterialLogDao dao=daoSession.getBadMaterialLogDao();
        List<BadMaterialLog> badLogs,
                uploadLogs=new ArrayList<BadMaterialLog>();
        badLogs=dao.loadAll();
        if(badLogs!=null)
        {
            for(BadMaterialLog badlog:badLogs)
            {
                if (badlog.isUploaded()||TextUtils.isEmpty(badlog.getMaterialISN()))
                {
                    delete(badlog);
                }
                else
                {
                    uploadLogs.add(badlog);
                }
            }
        }
        showLog("待上传的数据条数:"+uploadLogs.size());
        return uploadLogs;
    }

    public static void delete(BadMaterialLog badMaterialLog) {
        try
        {
            DaoSession daoSession= QcApplication.getDaoSession();
            BadMaterialLogDao dao=daoSession.getBadMaterialLogDao();
            dao.delete(badMaterialLog);
            showLog("已删除本次记录"+badMaterialLog.getMaterialISN());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
