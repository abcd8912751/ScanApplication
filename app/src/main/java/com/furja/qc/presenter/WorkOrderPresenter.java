package com.furja.qc.presenter;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.furja.qc.R;
import com.furja.qc.beans.WorkOrderInfo;
import com.furja.qc.contract.WorkOrderContract;
import com.furja.qc.model.WorkOrderModel;
import com.furja.qc.utils.SharpBus;
import com.furja.qc.view.MyViewHolder;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.furja.qc.utils.Constants.FRAGMENT_ON_TOUCH;
import static com.furja.qc.utils.Constants.MATERIAL_INTERNET_ABNORMAL;
import static com.furja.qc.utils.Constants.UPDATE_BAD_COUNT;
import static com.furja.qc.utils.Constants.UPDATE_WORKORDER_BY_MATERIAL;
import static com.furja.qc.utils.Constants.UPLOAD_FINISH;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;

/**
 * 用来作为显示工单信息的视图与数据交互的桥
 */

public class WorkOrderPresenter implements WorkOrderContract.Presenter {
    private WorkOrderContract.View mWorkOrderView;
    private WorkOrderModel mWorkOrderModel;
    private OrderListAdapter mOrderListAdapter;
    private Context mContext;
    private int requestCounts=0;    //请求次数
    private int currPosition=0;
    public WorkOrderPresenter(WorkOrderContract.View workOrderView)
    {
        this.mWorkOrderView=workOrderView;
        mWorkOrderModel=new WorkOrderModel();
        mOrderListAdapter=new OrderListAdapter();
        setListAdapter();
        setListener();

        Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Thread.sleep(1000);
                return "";
            }})
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mWorkOrderView.setSelection(0);
                        showLog("延时到了");
                    }
                });
    }


    /**
     * 设定各Button点击的监听器并注册物料信息更新后回调
     */
    private void setListener() {
        //注册网络异常的观察者
        registerObserver();
        ListView view=mWorkOrderView.getAdapterView();
        if(view==null)
        {
            showLog("没有获取到WorkListView");
            return;
        }
        this.mContext=view.getContext();
    }

    private void registerObserver() {
        SharpBus.getInstance()
                .register(MATERIAL_INTERNET_ABNORMAL)
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object value) {
                        //从服务器获取物料信息后获取响应更新视图
                        requestCounts++;
                        if(requestCounts<3)
                            mWorkOrderModel.updateWorkOrderByBarCode(mWorkOrderModel.getMaterialBarCode());
                        else
                            showToast("网络连接异常,请重试");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        //注册获取物料信息的观察者
        SharpBus.getInstance()
                .register(UPDATE_WORKORDER_BY_MATERIAL)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object value) {
                        //从服务器获取物料信息后获取响应更新视图
                        showLog("获取到物料信息");
                        notifyAndUpdateBadData();
                        requestCounts=0;
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        //注册上传成功的观察者
        SharpBus.getInstance()
                .register(UPLOAD_FINISH)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object value) {
                        currPosition=0; //上传成功后置光标为0
                        mWorkOrderModel.setMaterialNull();
                        mWorkOrderView.requestFocus();
                        mWorkOrderView.setSelection(0);
                        showLog("收到了上传完成的指示");
                        if(mOrderListAdapter!=null)
                        {
                            mOrderListAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        Observable<Long> observable= SharpBus.getInstance().register(FRAGMENT_ON_TOUCH);
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object value) {
                        showLog("我来了"+value.toString());
                        if(currPosition<0)
                            return;
                        currPosition=-1;
                        if(mOrderListAdapter!=null)
                            mOrderListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }

                });
    }


    /**
     * 更新信息
     */
    private void notifyAndUpdateBadData() {
        mOrderListAdapter.notifyDataSetChanged();
        mWorkOrderView.syncAndUpdateBadData();
    }


    @Override
    public void setListAdapter() {
        mWorkOrderView.setListAdapter(mOrderListAdapter);
    }

    @Override
    public WorkOrderInfo getWorkOrderInfo() {
        return mWorkOrderModel.getWorkOrderInfo();
    }

    /**
     * 储存当前工单信息至本地
     */
    public void syncLocalWorkOrderData() {
        mWorkOrderModel.syncDataBase();
    }



    /**
     * 存放物料、工位等信息的ListView 适配器
     */
    private class OrderListAdapter extends BaseAdapter{
        public OrderListAdapter()
        {
            //注册监听器
            Observable<Long> observable= SharpBus.getInstance().register(UPDATE_BAD_COUNT);
            observable.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Long>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(Long value) {
                    mWorkOrderModel.updateDefectCount(value);
                    notifyDataSetChanged();
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }

            });

        }

        @Override
        public int getCount() {
            return mWorkOrderModel.getItemCount();
        }

        @Override
        public String getItem(int i) {
            return mWorkOrderModel.getContent(i);
        }
        public String getTitle(int pos)
        {
            return mWorkOrderModel.getTitle(pos);
        }


        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            final MyViewHolder myViewHolder;
            if(convertView==null)
            {
                convertView= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.workinfo_item,null);
                myViewHolder=new MyViewHolder(convertView);
                convertView.setTag(myViewHolder);
            }
            else
            {
                myViewHolder=(MyViewHolder)convertView.getTag();
            }
            myViewHolder.getEditText()
                    .setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if(keyEvent.getKeyCode()==KeyEvent.KEYCODE_ENTER)
                    {
                        showLog("回车了");
                        if(myViewHolder.getContent().length()>0)
                            readInput(position, myViewHolder);
                    }
                    return false;
                }
            });

            myViewHolder.getEditText().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    currPosition=position;
                    if(myViewHolder.onTouch(motionEvent))
                    {
                        if(position==4)
                        {
                            mWorkOrderModel.updateWorkOrderByOperatorId(myViewHolder.getContent());
                        }
                        else if(position==5)
                        {
                            mWorkOrderModel.updateWorkOrderByWorkPlaceId(myViewHolder.getContent());
                        }
                    }
                    return false;
                }
            });
            myViewHolder.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    showLog("编辑了Action");
                    if (i == EditorInfo.IME_ACTION_DONE)
                    {
                        showLog("Done over");
                        readInput(position, myViewHolder);
                    }
                    return false;
                }
            });
            myViewHolder.setInfo_content(getItem(position));
            myViewHolder.setInfo_title(getTitle(position));
            myViewHolder.setCurrPosition(currPosition);
            myViewHolder.setPosition(position);
            return convertView;
        }

        private void readInput(int position, MyViewHolder myViewHolder) {
            showLog("我进来读数了:"+position);
            if(position==0)
            {
                myViewHolder.clearFocus();
                currPosition=4;
                updateMaterialCode(myViewHolder.getContent());
                mWorkOrderView.setSelection(4);
            }
            else if(position==4)
            {
                myViewHolder.clearFocus();
                currPosition=5;
                updateOperatorId(myViewHolder.getContent());
                Observable.fromCallable(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        Thread.sleep(300);
                        return "finish";
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(Object o) throws Exception {

                                mWorkOrderView.setSelection(5);
                            }
                        });

            }
            else if(position==5)
            {
                myViewHolder.clearFocus();
                currPosition=-1;
                updateWorkPlaceId(myViewHolder.getContent());
                myViewHolder.hideKeyBoard();
            }
        }

    }

    /**
     * 更新物料信息
     * @param currContent
     */
    public void updateMaterialCode(String currContent)
    {
        showLog("你输入的是:"+currContent);
        if(TextUtils.isEmpty(currContent))
        {
            showLog("条码为空");
            return;
        }
        mWorkOrderModel.setMaterialBarCode(currContent);
        mWorkOrderModel.checkExistOrUpdateByBarCode(currContent);
    }

    /**
     * 更新员工信息
     */
    public void updateOperatorId(String currContent)
    {
        showLog("输入员工号");
        syncLocalWorkOrderData();
        mWorkOrderModel.updateWorkOrderByOperatorId(currContent);
        notifyAndUpdateBadData();
    }
    /**
     * 更新机台信息
     */
    public void updateWorkPlaceId(String currContent)
    {
        showLog("输入机台号");
        syncLocalWorkOrderData();
        mWorkOrderModel.updateWorkOrderByWorkPlaceId(currContent);
        notifyAndUpdateBadData();
    }
}
