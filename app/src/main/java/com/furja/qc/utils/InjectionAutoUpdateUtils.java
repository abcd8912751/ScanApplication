package com.furja.qc.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.provider.Settings;
import androidx.core.content.FileProvider;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.baronzhang.retrofit2.converter.FastJsonConverterFactory;
import com.furja.qc.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;

import static com.furja.qc.utils.Constants.LOG_TAG;
import static com.furja.qc.utils.Constants.getVertxUrl;
import static com.furja.qc.utils.Utils.doubleOf;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;

/**
 * 自动更新工具
 */
public class InjectionAutoUpdateUtils {
    private Context context;
    private boolean backGround;

    public InjectionAutoUpdateUtils(Context uiContext, boolean isbackGround) {
        this.context=uiContext;
        this.backGround=isbackGround;
    }

    public InjectionAutoUpdateUtils(Context uiContext) {
        this.context=uiContext;
        this.backGround=false;
    }

    /**
     * 获取 当前安装APP的VersionCode
     * @return
     */
    public  int getVerCode() {
        int verCode = -1;
        try{
            verCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        }
        catch (Exception e) {
            showLog(e.getMessage());
        }
        return verCode;
    }

    /**
     * 检查更新
     */
    public void checkUpdate() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor(LOG_TAG))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build();
        Retrofit.Builder builder=new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(FastJsonConverterFactory.create())
                .client(okHttpClient);
        UpdateHelper helper = builder
                .baseUrl(getVertxUrl())
                .build()
                .create(UpdateHelper.class);
        helper.getVersionInfo()
                .subscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, List<AutoUpdateJson>>() {
                    @Override
                    public List<AutoUpdateJson> apply(ResponseBody responseBody) throws Exception {
                        String bodyString=getBodyAString(responseBody);
                        return JSON
                                .parseArray(bodyString,AutoUpdateJson.class);
                    }
                    @androidx.annotation.NonNull
                    private String getBodyAString(ResponseBody response) {
                        Reader in=response.charStream();
                        StringBuffer stringBuffer=new StringBuffer();
                        int bufferSize = 1024;
                        char[] buffer = new char[bufferSize];
                        for (; ; ) {
                            int rsz = 0;
                            try {
                                rsz = in.read(buffer, 0, buffer.length);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (rsz < 0)
                                break;
                            stringBuffer.append(buffer, 0, rsz);
                        }
                        return stringBuffer.toString();
                    }
                })
                .subscribe(new Consumer<List<AutoUpdateJson>>() {
                    @Override
                    public void accept(List<AutoUpdateJson> autoUpdateJsons) throws Exception {
                        for (AutoUpdateJson json : autoUpdateJsons) {
                            if (getPackageName().equals(json.getPackageName())) {
                                showLog("找到了包名一致的ApkUrl");
                                executeUpdateJson(json);
                                return;
                            }
                        }
                        showNoUpdateToast();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        if(throwable instanceof JSONException)
                            showLog("没有适合的数据");
                        showNoUpdateToast();
                    }
                });
    }

    private void showNoUpdateToast() {
        if(!backGround)
            showToast("已经是最新版本");
        else
            showLog("已经是最新版本");
    }


    /**
     * 处理 服务器返回的与本包相符的 AutoUpdateJson 数据
     * @param json
     */
    private void executeUpdateJson(AutoUpdateJson json) {
        double serverVerName
                = doubleOf(json.getVersionName());
        int serverVerCode=json.getVersionCode();
        if(serverVerCode >= getVerCode()) {
            if(serverVerCode > getVerCode())
                showUpdateDialog(json);
            else if(serverVerName > getVerName()) {
                showUpdateDialog(json);
            }
            else {
                showNoUpdateToast();
            }
        }
        else {
            showNoUpdateToast();
        }
    }

    /**
     * 显示 更新对话框
     * @param json
     */
    private void showUpdateDialog(final AutoUpdateJson json) {
        Looper.prepare();
        MaterialDialog.Builder builder
                =new MaterialDialog.Builder(context);
        builder.title("检测到新版本");
        builder.content(json.getUpdateInfo());
        builder.positiveText("立即更新")
                .cancelable(!json.isForceUpdate())
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@androidx.annotation.NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.cancel();
                        String url=json.getLatestApkUrl();
                        if(!TextUtils.isEmpty(url))
                            downloadApkFromUrl(url);
                    }
                });
        if(!json.isForceUpdate())
            builder.negativeText("下次再说");
        MaterialDialog dialog = builder.show();
        dialog.getWindow()
                .setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
        Looper.loop();
    }

    /**
     * 从服务器下载 Apk
     * @param latestApkUrl
     */
    private void downloadApkFromUrl(String latestApkUrl) {
        String path=Environment.getExternalStorageDirectory().getPath()+"/Download";
        String apkName=getNameFromUrl(latestApkUrl);
        File file=new File(path,apkName);
        if(file.exists())
            showLog("已经下载完成");
        final MaterialDialog downLoadDialog
                =new MaterialDialog.Builder(context)
                .progress(false,100,false)
                .title("下载完成后自行安装")
                .autoDismiss(false)
                .build();
        downLoadDialog.show();
        downLoadDialog.getWindow()
                .setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
        latestApkUrl=getVertxUrl()+latestApkUrl;
        OkHttpUtils.get()
                .url(latestApkUrl)
                .build()
                .execute(new FileCallBack(path,apkName) {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        showToast("下载异常");
                        e.printStackTrace();
                        downLoadDialog.cancel();
                    }
                    @Override
                    public void onResponse(File response, int id) {
                        showLog("下载完成:"+response.getAbsolutePath());
                        downLoadDialog.cancel();
                        toUpdateApk(response);
                    }
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        int progressValue=(int) (progress*100);
                        if(progressValue!=downLoadDialog.getCurrentProgress())
                            downLoadDialog.setProgress(progressValue);
                    }
                });
    }

    /**
     * 从下载的Url里获取文件名
     * @param latestApkUrl
     * @return
     */
    private String getNameFromUrl(String latestApkUrl) {
        String fileName="latest.apk";
        if(!TextUtils.isEmpty(latestApkUrl)) {
            int index=latestApkUrl.lastIndexOf("/");
            if(index>-1)
                fileName=latestApkUrl.substring(index+1,latestApkUrl.length());
        }
        return fileName;
    }

    /**
     * 安装最新的APK,仅限于Android7.0以下
     */
    private void toUpdateApk(File file) {
        if(file==null||!file.exists()) {
            showLog("更新");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            boolean installAllowed=context.getPackageManager().canRequestPackageInstalls();
            if (installAllowed)
                installApkFile(file);
            else {
                Intent intent1=new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
                installApkFile(file);
            }
        }
        else {
            installApkFile(file);
        }
    }

    private void installApkFile(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String packageName = getPackageName();
            Uri contentUri
                    = FileProvider.getUriForFile(context,"com.furja.qc.fileprovider", file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    /**
     * 获取包名
     * @return
     */
    private String getPackageName()
    {
        return "com.furja.qc:injection";
    }

    /**
     * 获取当前安装APP的版本名
     * @return VersionName
     */
    public  double getVerName() {
        String verName = "";
        double versionName=0;
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
            versionName = doubleOf(verName);
        } catch (Exception e) {
            showLog(e.getMessage());
        }
        return versionName;
    }


    public static class AutoUpdateJson {

        private String latestApkUrl;
        private String packageName;
        private int versionCode;
        private String versionName;
        private String updateInfo;
        private boolean forceUpdate;
        public String getLatestApkUrl() {
            return latestApkUrl;
        }

        public boolean isForceUpdate() {
            return forceUpdate;
        }

        public void setForceUpdate(boolean forceUpdate) {
            this.forceUpdate = forceUpdate;
        }

        public void setLatestApkUrl(String latestApkUrl) {
            this.latestApkUrl = latestApkUrl;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getUpdateInfo() {
            return updateInfo;
        }

        public void setUpdateInfo(String updateInfos) {
            this.updateInfo = updateInfos;
        }
    }

    /**
     * 更新的 Helper
     */
    interface UpdateHelper {
        @GET("files/apk/version.txt")
        Observable<ResponseBody> getVersionInfo();
    }
}
