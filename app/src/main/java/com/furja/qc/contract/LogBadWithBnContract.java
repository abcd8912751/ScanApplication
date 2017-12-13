package com.furja.qc.contract;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.furja.qc.beans.WorkOrderInfo;

/**
 * Created by zhangmeng on 2017/12/3.
 */

public interface LogBadWithBnContract {
    interface Model {
        public int getItemCount();
        public String getOptionTitle(int position);
        public long getMarkerCount(int position);
        public void addMarkerCount(int position);
        public long getTotalBad();
        public void updateData(WorkOrderInfo workOrderInfo);
    }

    interface View {
        public void setRecyclerAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter);
        public void setButtonClickListener(android.view.View.OnClickListener buttonClickListener);
    }

    interface Presenter {
        public void syncAndUpdateData(WorkOrderInfo workOrderInfo);
        public void syncData();
    }
}
