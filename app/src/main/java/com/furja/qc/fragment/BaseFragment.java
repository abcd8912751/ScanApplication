package com.furja.qc.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.furja.qc.beans.WorkOrderInfo;
import com.furja.qc.utils.SharpBus;

import static com.furja.qc.utils.Constants.BADLOG_FRAGMENT_INITFINISH;

/**
 * Created by zhangmeng on 2017/12/3.
 */

public abstract class BaseFragment extends Fragment {
    protected Context mContext;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext=getContext();
    }

    /**
     * 通知Activity本Fragment已经加载完成
     * 让其传递工单信息过来
     */
    public  void notifyInitFinish()
    {
        SharpBus.getInstance()
                .post(BADLOG_FRAGMENT_INITFINISH,"initFinish");
    };


}
