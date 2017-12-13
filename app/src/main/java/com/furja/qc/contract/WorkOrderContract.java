package com.furja.qc.contract;

import android.widget.BaseAdapter;
import android.widget.ListView;

import com.furja.qc.beans.WorkOrderInfo;

/**
 * Created by zhangmeng on 2017/12/3.
 */

public interface WorkOrderContract {
    interface Model {
        public int getItemCount();
        public String getTitle(int position);
        public String getContent(int position);
    }

    interface View {
        public void setListAdapter(BaseAdapter baseAdapter);
        public ListView getAdapterView();
        public void syncAndUpdateBadData();
        public void setSelection(int position);

        void requestFocus();

        void clearFocus();
    }

    interface Presenter {
        public void setListAdapter();
        public WorkOrderInfo getWorkOrderInfo();
    }
}
