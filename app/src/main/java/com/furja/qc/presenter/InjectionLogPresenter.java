package com.furja.qc.presenter;

import android.accounts.NetworkErrorException;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.baronzhang.retrofit2.converter.FastJsonConverterFactory;
import com.furja.qc.R;
import com.furja.qc.beans.BadLogEntry;
import com.furja.qc.beans.DimenGroupItem;
import com.furja.qc.beans.MaterialInfo;
import com.furja.qc.databases.BadMaterialLog;
import com.furja.qc.databases.DimenGaugeLog;
import com.furja.qc.databases.TourInspectionLog;
import com.furja.qc.contract.InjectionLogContract;
import com.furja.qc.jsonbeans.UploadDataJson;
import com.furja.qc.model.InjectionLogModel;
import com.furja.qc.ui.DimenLogActivity;
import com.furja.qc.ui.TourInspectionActivity;
import com.furja.qc.utils.Caretaker;
import com.furja.qc.utils.Constants;
import com.furja.qc.utils.HttpCallback;
import com.furja.qc.utils.RetrofitBuilder;
import com.furja.qc.utils.RetrofitHelper;
import com.furja.qc.utils.RetryWhenUtils;
import com.furja.qc.utils.SharpBus;
import com.furja.qc.utils.Utils;
import com.furja.qc.view.LogViewHolder;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.PostFormRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import static com.furja.qc.utils.Constants.FURJA_UPLOAD_URL;
import static com.furja.qc.utils.Constants.INTERNET_ABNORMAL;
import static com.furja.qc.utils.Constants.NODATA_AVAILABLE;
import static com.furja.qc.utils.Constants.TAG_GOT_DIMENLOG;
import static com.furja.qc.utils.Constants.TAG_GOT_TOURLOG;
import static com.furja.qc.utils.Constants.TAG_SCAN_BARCODE;
import static com.furja.qc.utils.Constants.UPDATE_BAD_COUNT;
import static com.furja.qc.utils.Constants.getHttpsUrl;
import static com.furja.qc.utils.Constants.getVertxUrl;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;

public class InjectionLogPresenter implements InjectionLogContract.Presenter {
    InjectionLogContract.View injectlogView;
    InjectionLogModel injectionlogModel;
    Caretaker caretaker;
    SharpBus sharpBus;
    MyRecyclerAdapter myRecyclerAdapter;
    HttpCallback httpCallback;
    Integer[] defetiveReasons;  //造成不良原因的数组
    TourInspectionLog tourInspectionLog;
    DimenGaugeLog dimenGaugeLog;
    LifecycleOwner lifecycleOwner;
    Disposable disposable;
    boolean isEditing = false,isTourInspection = false; //isTourInspection为true时,点击MarkerButton不检测输入

    public InjectionLogPresenter(InjectionLogContract.View view) {
        this.injectlogView = view;
        injectionlogModel = new InjectionLogModel();
        myRecyclerAdapter = new MyRecyclerAdapter();
        caretaker = new Caretaker();
        injectlogView
                .setButtonClickListener(new BtnClickListener());
        injectlogView
                .setRecyclerAdapter(myRecyclerAdapter);
        sharpBus = SharpBus.getInstance();
        lifecycleOwner = view.getLifeCycle();
        resetFieldData();
    }

    /**
     * 显示确定提交的对话框
     */
    public void submitWithConfirm() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(injectlogView.getContext())
                .title("提交数据")
                .content("确定要提交数据吗?")
                .positiveText("确定")
                .negativeText("取消")
                .autoDismiss(false)
                .onPositive((dialog, which) -> {
                    dialog.setContent("        正在提交数据...");
                    submitData(dialog);
                    dialog.getActionButton(DialogAction.POSITIVE)
                            .setVisibility(View.GONE);
                    dialog.getActionButton(DialogAction.NEGATIVE)
                            .setVisibility(View.GONE);
                }).onNegative((dialog, which) -> {
                    dialog.cancel();
                }).show();
        materialDialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
    }

    /**
     * 提交数据,这里最好在Presenter里建立个对象为数据实体
     * 通过injectlogView是哪个实例的方法提交不同的数据
     */
    private void submitData(MaterialDialog dialog) {
        RetrofitHelper helper = RetrofitBuilder.getHelperByUrl(getVertxUrl());
        if(injectlogView instanceof TourInspectionActivity) {
            RequestBody requestBody = Utils.getRequestBody(tourInspectionLog);
            helper.postTourInspectionLog(requestBody)
                    .subscribeOn(Schedulers.io())
                    .retryWhen(RetryWhenUtils.create())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(stringResponse -> {
                        dialog.cancel();
                        showToast(stringResponse.getResult());
                        if (stringResponse.getCode() > 0) {
                            injectlogView.resetView();
                        }
                        if (disposable != null) {
                            showLog("解注册TourInspectLogActivity");
                            disposable.dispose();
                        }
                    }, throwable -> {
                        if (throwable instanceof NetworkErrorException) {
                            showToast(INTERNET_ABNORMAL);
                        }
                        dialog.cancel();
                    }, new Action() {
                        @Override
                        public void run() throws Exception {
                        }
                    }, new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable d) throws Exception {
                            disposable = d;
                        }
                    });
        }
        else if(injectlogView instanceof DimenLogActivity) {
            RequestBody requestBody = Utils.getRequestBody(dimenGaugeLog);
            helper.postDimenGaugeLog(requestBody)
                    .subscribeOn(Schedulers.io())
                    .retryWhen(RetryWhenUtils.create())
                    .observeOn(AndroidSchedulers.mainThread())
                    .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner)))
                    .subscribe(stringResponse->{
                        if(stringResponse.getCode()>0)
                            injectlogView.resetView();
                        showToast(stringResponse.getResult());
                        dialog.cancel();
                    },throwable -> {
                        if(throwable instanceof NetworkErrorException)
                            showToast(INTERNET_ABNORMAL);
                        dialog.cancel();
                    });
        }
    }


    public long getTotalBadCount() {
        return injectionlogModel.getTotalBad();
    }

    public void setBadLogEntries(List<BadLogEntry> badLogEntries) {
        injectionlogModel.setBadLogEntries(badLogEntries);
        myRecyclerAdapter.notifyDataSetChanged();
    }

    public void acquireDimenLog(String materialISN, String produceDate,
                                String timePeriod, String moldNo, String type) {
        RetrofitHelper helper
                = RetrofitBuilder.getHelperByUrl(getVertxUrl());
        helper.getDimenGaugeLog(materialISN,produceDate,timePeriod,moldNo,type)
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWhenUtils.create())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner)))
                .subscribe(dimenLogResponse->{
                    if(dimenLogResponse.getCode()>0) {
                        DimenGaugeLog dimenLog = dimenLogResponse.getResult();
                        if (dimenLog.getFID() > 0) {
                            MaterialDialog materialDialog = new MaterialDialog.Builder(injectlogView.getContext())
                                    .title("该产品(含模号)在所示时段已作记录")
                                    .content("确定呈现历史记录吗?")
                                    .positiveText("确定").negativeText("取消")
                                    .onPositive((dialog, which) -> {
                                        dialog.cancel();
                                        sharpBus.post(TAG_GOT_DIMENLOG, dimenLog);
                                    })
                                    .onNegative((dialog, which) -> {
                                        dialog.cancel();
                                        dimenLog.setFID(0);
                                        List<DimenGroupItem> dimenGroupItems
                                                =dimenLog.getDimenGroupItems(),newItems=new ArrayList<>();
                                        if(dimenGroupItems!=null){
                                            for(DimenGroupItem groupItem:dimenGroupItems){
                                                groupItem.setDimenJson(null);
                                                newItems.add(groupItem);
                                            }
                                            dimenLog.setDimenGroupItems(newItems);
                                            sharpBus.post(TAG_GOT_DIMENLOG, dimenLog);
                                        }
                                    })
                                    .show();
                            materialDialog.getWindow()
                                    .setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
                        } else
                            sharpBus.post(TAG_GOT_DIMENLOG, dimenLog);
                    }
                },error->{
                    error.printStackTrace();
                    showToast("网络连接异常");
                });
    }

    private class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_markerview,parent,false);
            LogViewHolder viewHolder=new LogViewHolder(view);
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final LogViewHolder viewHolder=(LogViewHolder)holder;
            viewHolder.setText(injectionlogModel.getOptionTitle(position));
            viewHolder.setMarkNum(injectionlogModel.getMarkerCount(position));
            viewHolder.getMarkerButton().setOnClickListener(v -> {
                if(!isTourInspection&&injectionlogModel.infoHasNull())
                    return;
                if(isEditing)
                    showEditDialog(viewHolder,position);
                else {
                    if (!isTourInspection&&caretaker.isEmpty())
                        caretaker.appendUndo(injectionlogModel.getMarkCountString());
                    injectionlogModel.addMarkerCount(position);
                    caretaker.appendUndo(injectionlogModel.getMarkCountString());  //保存这个记录
                    viewHolder.setMarkNum(injectionlogModel.getMarkerCount(position));
                    sharpBus.post(UPDATE_BAD_COUNT, injectionlogModel.getTotalBad());
                }
            });
            viewHolder.getMarkerButton().setOnLongClickListener(v -> {
                if(!isTourInspection&&injectionlogModel.infoHasNull())
                    return true;
                if(isEditing)
                    return true;
                showEditDialog(viewHolder,position);
                return true;
            });
        }

        private void showEditDialog(final LogViewHolder viewHolder, final int position) {
            String prefill="";
            if(viewHolder.getMarkNum()>0)
                prefill=viewHolder.getMarkNum()+"";
            new MaterialDialog.Builder(viewHolder.getContext())
                    .title("设定该异常类型个数")
                    .inputType(InputType.TYPE_CLASS_NUMBER)
                    .autoDismiss(false)
                    .input("不设置将清空该异常计数", prefill, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            long count=0;
                            if(!TextUtils.isEmpty(input)){
                                count= Long.valueOf(input.toString());
                            }
                            if(!isTourInspection)
                                caretaker.appendUndo(injectionlogModel.getMarkCountString());  //保存这个记录
                            injectionlogModel.setMarkerCount(position,count);
                            viewHolder.setMarkNum(injectionlogModel.getMarkerCount(position));
                            sharpBus.post(UPDATE_BAD_COUNT,injectionlogModel.getTotalBad());
                            dialog.cancel();
                        }
                    }).show();
        }

        @Override
        public int getItemCount() {
            return injectionlogModel.getItemCount();
        }
    }

    /**
     * 撤销、重做、提交 按钮的监听
     */
    public class BtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_undo_btnFrag:
                    List<Long> codeCounts=caretaker.getUndoMemo();
                    if(codeCounts!=null) {
                        injectionlogModel.setMarkCounts(codeCounts);
                        sharpBus.post(UPDATE_BAD_COUNT,injectionlogModel.getTotalBad());
                        myRecyclerAdapter.notifyDataSetChanged();
                    }
                    break;
                case R.id.btn_redo_btnFrag:
                    List<Long> counts=caretaker.getRedoMemo(injectionlogModel.getMarkCountString());
                    if(counts!=null) {
                        injectionlogModel.setMarkCounts(counts);
                        sharpBus.post(UPDATE_BAD_COUNT,injectionlogModel.getTotalBad());
                        myRecyclerAdapter.notifyDataSetChanged();
                    }
                    break;
                //编辑按钮
                case R.id.btn_edit_btnFrag:
                    try{
                        if(!isTourInspection&&injectionlogModel.infoHasNull())
                            return;
                        if(!isEditing) {
                            ((ImageButton)v).setImageResource(R.mipmap.ic_editing_src);
                            showToast("点击异常按钮快速编辑计数");
                        }
                        else
                            ((ImageButton)v).setImageResource(R.mipmap.ic_edit_src);
                        isEditing=!isEditing;
                    }catch(Exception e){e.printStackTrace();}
                    break;
                //submit按钮、传递上传完成
                case R.id.btn_submit_btnFrag:
                    if(!injectionlogModel.infoHasNull()) {
                        new MaterialDialog.Builder(injectlogView.getContext())
                                .title("提交数据")
                                .content("确定要提交数据吗?")
                                .positiveText("确定")
                                .negativeText("取消")
                                .onPositive((dialog, which) -> {
                                    dialog.setContent("        正在提交数据...");
                                    dialog.getActionButton(DialogAction.POSITIVE)
                                            .setVisibility(View.GONE);
                                    dialog.getActionButton(DialogAction.NEGATIVE)
                                            .setVisibility(View.GONE);
                                    uploadLog(dialog);
                                }).show().getWindow()
                                .setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
                    }
                    break;
            }
        }
    }

    public void acquireTourLog(String materialISN,String produceDate,
                               String timePeriod,String moldNo) {
        RetrofitHelper helper
                = RetrofitBuilder.getHelperByUrl(getVertxUrl(), RetrofitHelper.class);
        helper.getTourInspectionLog(materialISN,produceDate,timePeriod,moldNo)
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWhenUtils.create())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner)))
                .subscribe(tourLogResponse->{
                    if(tourLogResponse.getCode() > 0) {
                        TourInspectionLog tourLog = tourLogResponse.getResult();
                        if (tourLog.getFID()>0) {
                            MaterialDialog materialDialog = new MaterialDialog.Builder(injectlogView.getContext())
                                    .title("该物料在所示时段已作记录")
                                    .content("确定呈现历史记录吗?")
                                    .positiveText("确定").negativeText("取消")
                                    .onPositive((dialog, which) -> {
                                        dialog.cancel();
                                        sharpBus.post(TAG_GOT_TOURLOG, tourLog);
                                    }).show();
                            materialDialog.getWindow()
                                    .setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
                        }
                        else {
                            sharpBus.post(TAG_GOT_TOURLOG, tourLog);
                        }
                    }
                },error->{
                    error.printStackTrace();
                });
    }

    /**
     * 上传InjectionLog日志
     * @param finalDialog
     */
    public void uploadLog(final MaterialDialog finalDialog) {
        BadMaterialLog badMaterialLog = injectionlogModel.getBadMaterialLog();
        Map<String, String> uploadParams = badMaterialLog.getUploadParams();
        Map<String, RequestBody> requestBodyMap = generateRequestBody(uploadParams);
        String baseUrl = Constants.getBaseUrl();
        RetrofitHelper helper = RetrofitBuilder.getHelperByUrl(baseUrl);
        helper.postInjectionLog(requestBodyMap)
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWhenUtils.create())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner)))
                .subscribe(responseBody->{
                    finalDialog.cancel();
                    String response = responseBody.string();
                    checkUploadSuccessAndSave(response);
                }, error -> {
                    showToast(INTERNET_ABNORMAL);
                    finalDialog.cancel();
                    if (httpCallback != null)
                        httpCallback.onFail(INTERNET_ABNORMAL);
                });
    }

    /**
     * 转换为 form-data
     * @param requestDataMap
     * @return
     */
    public static Map<String, RequestBody> generateRequestBody(Map<String, String> requestDataMap) {
        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        for (String key : requestDataMap.keySet()) {
            String value = requestDataMap.get(key);
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),
                    value == null ? "" : value);
            requestBodyMap.put(key, requestBody);
        }
        return requestBodyMap;
    }

    /**
     * 根据返回的Json检验是否上传成功
     *  如果上传成功则更新存入本地数据库
     *  在网络异常时未上传成功需考虑定时上传
     */
    private void checkUploadSuccessAndSave(String responce) {
        UploadDataJson uploadDataJson
                = JSON.parseObject(responce,UploadDataJson.class);
        if(uploadDataJson!=null&&uploadDataJson.getErrCode()==100) {
            showToast(uploadDataJson.getErrMsg());
            if(httpCallback!=null)
                httpCallback.onSuccess("ok");
            else {
                injectlogView.resetView();
                myRecyclerAdapter.notifyDataSetChanged();
            }
        }
        else {
            showToast("上传失败,请重试");
            if(httpCallback!=null)
                httpCallback.onFail("上传失败");
        }}

    /**
     * 重置部分字段数据
     */
    @Override
    public void resetFieldData() {
        myRecyclerAdapter.notifyDataSetChanged();
        caretaker.clear();
        dimenGaugeLog = new DimenGaugeLog();
        tourInspectionLog = new TourInspectionLog();
        injectionlogModel.resetFieldData();
    }


    public DimenGaugeLog getDimenGaugeLog() {
        return dimenGaugeLog;
    }

    public void setWorkPlace(String input) {
        injectionlogModel.setWorkPlace(input);
    }

    public void setOperator(String input) {
        injectionlogModel.setOperator(input.toUpperCase());
    }

    public void acquireMaterialInfo(String scanString) {
        RetrofitHelper helper
                =RetrofitBuilder.getHelperByUrl(getHttpsUrl(), RetrofitHelper.class);
        helper.getMaterialJson(scanString)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWhenUtils())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner)))
                .subscribe(materialJson -> {
                    MaterialInfo info=new MaterialInfo(materialJson);
                    injectionlogModel.setMaterialISN(info.getMaterialISN());
                    injectlogView.showMaterialInfo(info);
                },throwable ->{
                    if(throwable instanceof NetworkErrorException)
                        sharpBus.post(TAG_SCAN_BARCODE,INTERNET_ABNORMAL);
                    else
                        sharpBus.post(TAG_SCAN_BARCODE,NODATA_AVAILABLE);
                });
    }

    public void confirmLeave() {
        MaterialDialog materialDialog
                =new MaterialDialog.Builder(injectlogView.getContext())
                .title("离开这里")
                .content("确定舍弃已输入的数据吗?")
                .positiveText("确定")
                .negativeText("取消")
                .onPositive((dialog, which) -> {
                    injectlogView.onBackPressed();
                })
                .show();
        materialDialog.getWindow()
                .setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
    }

    public HttpCallback getHttpCallback() {
        return httpCallback;
    }
    public void setHttpCallback(HttpCallback httpCallback) {
        this.httpCallback = httpCallback;
    }
    public void setTourInspection(boolean tourInspection) {
        isTourInspection = tourInspection;
    }
    public Integer[] getDefetiveReasons() {
        if(defetiveReasons==null)
            defetiveReasons=new Integer[]{};
        return defetiveReasons;
    }

    /**
     * 更新时会将外观检验RecyclerView里的值设定好
     * @param tourInspectionLog
     */
    public void setTourInspectionAndBadLog(TourInspectionLog tourInspectionLog) {
        tourInspectionLog.setBadLogEntries(injectionlogModel.getBadLogEntries());
        this.tourInspectionLog = tourInspectionLog;
    }

    public void setTourInspectionLog(TourInspectionLog tourInspectionLog) {
        this.tourInspectionLog = tourInspectionLog;
    }

    public void setDimenGaugeLog(DimenGaugeLog dimenGaugeLog) {
        this.dimenGaugeLog = dimenGaugeLog;
    }

    public List<String> getMoldNoLst() {
        List<String> moldList=new ArrayList<>();
        List<String> letterLst
                =Arrays.asList("A#","B#","C#","D#","E#","F#","G#");
        for (int i = 1; i < 17; i++) {
            moldList.add(i + "#");
        }
        moldList.add(1,"1#A");
        moldList.add(3,"2#A");
        moldList.add(5,"3#A");
        moldList.add(7,"4#A");
        moldList.addAll(letterLst);
        return moldList;
    }
    public List<String> getClassLst() {
        return Arrays
                .asList("甲1班","甲2班","甲3班","乙1班","乙2班","乙3班");
    }

    public List<String> getMoldCavityLst()
    {
        return Arrays.asList("1*1","1*2","1*4","1*8");
    }
    public TourInspectionLog getTourInspectionLog() {
        return tourInspectionLog;
    }
    public void setDefetiveReasons(Integer[] defetiveReasons) {
        this.defetiveReasons = defetiveReasons;
    }
}

