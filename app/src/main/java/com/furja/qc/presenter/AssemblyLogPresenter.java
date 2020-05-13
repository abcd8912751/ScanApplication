package com.furja.qc.presenter;

import android.accounts.NetworkErrorException;
import android.text.TextUtils;
import com.afollestad.materialdialogs.MaterialDialog;
import com.furja.qc.R;
import com.furja.qc.beans.MaterialInfo;
import com.furja.qc.beans.WorkOrderInfo;
import com.furja.qc.contract.AssemblyLogContract;
import com.furja.qc.databases.BadAssemblyLog;
import com.furja.qc.utils.HttpCallback;
import com.furja.qc.utils.RetrofitBuilder;
import com.furja.qc.utils.RetrofitHelper;
import com.furja.qc.utils.RetryWhenUtils;
import com.furja.qc.utils.SharpBus;
import com.furja.qc.utils.Utils;
import com.furja.qc.beans.BadLogEntry;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

import static com.furja.qc.utils.Constants.FURJA_INNER_URL;
import static com.furja.qc.utils.Constants.VERTX_INNER_URL;
import static com.furja.qc.utils.Constants.INTERNET_ABNORMAL;
import static com.furja.qc.utils.Constants.NODATA_AVAILABLE;
import static com.furja.qc.utils.Constants.TAG_SCAN_BARCODE;
import static com.furja.qc.utils.Utils.showToast;

public class AssemblyLogPresenter implements AssemblyLogContract.Presenter {
    AssemblyLogContract.View assemblyLogView;
    private BadAssemblyLog badAssemblyLog;  //界面执有的数据库实例
    private WorkOrderInfo workOrderInfo;
    private HttpCallback httpCallback;
    MaterialInfo materialInfo;
    public AssemblyLogPresenter(AssemblyLogContract.View view)
    {
        assemblyLogView=view;
        resetBadAssemblyLog();
    }
    public BadAssemblyLog getBadAssemblyLog() {
        return badAssemblyLog;
    }

    public void setBadAssemblyLog(BadAssemblyLog badAssemblyLog) {
        this.badAssemblyLog = badAssemblyLog;
    }


    public boolean inputHasNull() {
        if(badAssemblyLog ==null)
        {
            showToast("设置员工号、线别及产品型号后方可继续");
            return true;
        }
        if(TextUtils.isEmpty(badAssemblyLog.getProductModel()))
        {
            showToast("选择产品型号后方可继续");
            return true;
        }
        if(TextUtils.isEmpty(badAssemblyLog.getOperator()))
        {
            showToast("设置员工号后方可继续");
            return true;
        }
        if(TextUtils.isEmpty(badAssemblyLog.getLineNumber()))
        {
            showToast("设置线别后方可继续");
            return true;
        }
        return false;
    }


    /**
     * 上传数据后重置当前 执有的数据库实例
     */
    private void resetBadAssemblyLog() {
        badAssemblyLog =
                new BadAssemblyLog();
    }
    public void toUpload() {
        if(!inputHasNull())
        {
            new MaterialDialog.Builder(assemblyLogView.getContext())
                    .title("提交数据")
                    .content("确定要提交数据吗?")
                    .positiveText("确定")
                    .negativeText("取消")
                    .onPositive((dialog, which) -> {
                        dialog.cancel();
                        uploadLog();
                    }).show().getWindow()
                    .setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
        }
    }

    public void setOperatorID(String input) {
        badAssemblyLog.setOperator(input);
    }

    public void setLineNumber(String input) {
        badAssemblyLog.setLineNumber(input);
    }

    public void setNote(String input) {
        badAssemblyLog.setNote(input.toUpperCase());
    }

    public HttpCallback getHttpCallback() {
        return httpCallback;
    }

    public void setHttpCallback(HttpCallback httpCallback) {
        this.httpCallback = httpCallback;
    }

    public void setProductModel(String input) {
        badAssemblyLog.setProductModel(input);
    }

    /**
     * 上传记录
     */
    public void uploadLog() {
        RetrofitHelper helper
                =RetrofitBuilder.getHelperByUrl(VERTX_INNER_URL,RetrofitHelper.class);
        final MaterialDialog dialog
                =Utils.showWaitingDialog(assemblyLogView.getContext());
        List<BadLogEntry> datas=assemblyLogView.getDatas();
        badAssemblyLog.setBadLogEntries(datas);
        RequestBody requestBody=Utils.getRequestBody(badAssemblyLog);
        helper.postAssemblyLog(requestBody)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWhenUtils())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stringBaseHttpResponse -> {
                    showToast(stringBaseHttpResponse.getResult());
                    if(stringBaseHttpResponse.getCode()>0)
                    {
                        dialog.cancel();
                        if(httpCallback!=null)
                            httpCallback.onSuccess("上传成功");
                        else
                            assemblyLogView.resetView();
                    }
                },throwable -> {
                    dialog.cancel();
                    if(httpCallback!=null)
                        httpCallback.onFail("上传失败");
                    showToast(throwable.getMessage());
                });
    }

    @Override
    public void resetFieldData() {
        resetBadAssemblyLog();
    }

    public void acquireMaterialInfo(String scanString) {
        RetrofitHelper helper
                =RetrofitBuilder.getHelperByUrl(FURJA_INNER_URL,RetrofitHelper.class);
        helper.getMaterialJson(scanString)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWhenUtils())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(materialJson -> {
                    materialInfo=new MaterialInfo(materialJson);
                    badAssemblyLog.setMaterialISN(materialInfo.getMaterialISN());
                    assemblyLogView.showMaterialInfo(materialInfo);
                },throwable -> {
                    if(throwable instanceof NetworkErrorException)
                    {
                        SharpBus.getInstance()
                                .post(TAG_SCAN_BARCODE,INTERNET_ABNORMAL);
                    }
                    else
                    {
                        SharpBus.getInstance()
                                .post(TAG_SCAN_BARCODE,NODATA_AVAILABLE);
                    }
                });
    }

    public MaterialInfo getMaterialInfo() {
        return materialInfo;
    }

    /**
     * 更新MaterialInfo
     * @param materialInfo
     */
    public void setMaterialInfo(MaterialInfo materialInfo) {
        this.materialInfo = materialInfo;
        if(materialInfo!=null)
            badAssemblyLog.setMaterialISN(materialInfo.getMaterialISN());
        else
            badAssemblyLog.setMaterialISN("");
    }

    public void showAbortDialog(int id) {
        new MaterialDialog.Builder(assemblyLogView.getContext())
                .title("离开这个星换一个时空")
                .iconRes(R.mipmap.ic_planet)
                .content("有数据尚未上传,就这样走吗?")
                .positiveText("上传数据")
                .negativeText("舍弃它们")
                .onPositive((dialog, which) -> {
                    setHttpCallback(new HttpCallback() {
                        @Override
                        public void onSuccess(Object message) {
                            assemblyLogView.performAction(id);
                        }

                        @Override
                        public void onFail(String errorMsg) {
                        }
                    });
                    uploadLog();
                }).onNegative((dialog, which) -> {
                    dialog.cancel();
                    assemblyLogView.performAction(id);
        }).show().getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
    }
}
