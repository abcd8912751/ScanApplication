package com.furja.qc.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.furja.qc.R;
import com.furja.qc.beans.WorkOrderInfo;
import com.furja.qc.contract.LogBadWithBnContract;
import com.furja.qc.presenter.BadLogWithBtnPresenter;
import com.furja.qc.utils.SharpBus;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.furja.qc.utils.Constants.FRAGMENT_ON_TOUCH;
import static com.furja.qc.utils.Utils.showLog;

/**
 * Created by zhangmeng on 2017/12/3.
 */

public class LogBadWithBtnFragment extends BaseFragment implements LogBadWithBnContract.View{
    @BindView(R.id.recycler_marker)
    RecyclerView markerRecyclerView;
    //上述MarkerRecyclerView与数据交互使用的presenter
    private BadLogWithBtnPresenter mBadLogWithBtnPresenter;
    @BindView(R.id.btn_redo_btnFrag)
    ImageButton redo_button;
    @BindView(R.id.btn_undo_btnFrag)
    ImageButton undo_button;
    @BindView(R.id.btn_submit_btnFrag)
    ImageButton submit_button;
    @BindView(R.id.btn_edit_btnFrag)
    ImageButton edit_button;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_badlogwithbtn,container,false);
        ButterKnife.bind(this,view);

        markerRecyclerView.setLayoutManager(new GridLayoutManager(mContext,4));
        mBadLogWithBtnPresenter =new BadLogWithBtnPresenter(this);
        notifyInitFinish();


        return view;
    }


    @Override
    public void setRecyclerAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        markerRecyclerView.setAdapter(adapter);
    }

    @Override
    public void setButtonClickListener(View.OnClickListener buttonClickListener) {
        redo_button.setOnClickListener(buttonClickListener);
        undo_button.setOnClickListener(buttonClickListener);
        edit_button.setOnClickListener(buttonClickListener);
        submit_button.setOnClickListener(buttonClickListener);
    }


    public void syncAndUpdateBtnBadData(WorkOrderInfo workOrderInfo) {
        if(mBadLogWithBtnPresenter!=null)
            mBadLogWithBtnPresenter.syncAndUpdateData(workOrderInfo);
    }


    /**
     * 将工单信息及
     */
    public void syncData()
    {
        mBadLogWithBtnPresenter.syncData();
        showLog("数据同步完成");
    }

}
