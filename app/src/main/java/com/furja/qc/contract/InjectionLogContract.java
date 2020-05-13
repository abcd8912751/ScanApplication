package com.furja.qc.contract;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import com.furja.qc.beans.MaterialInfo;
import com.furja.qc.beans.WorkOrderInfo;

public interface InjectionLogContract {
    interface Model {
        int getItemCount();

        String getOptionTitle(int position);

        long getMarkerCount(int position);

        void addMarkerCount(int position);

        long getTotalBad();

        void updateData(WorkOrderInfo workOrderInfo);
    }

    interface View {
        void resetView();
        void showMaterialInfo(MaterialInfo materialInfo);
        default void setRecyclerAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter){};
        default void setButtonClickListener(android.view.View.OnClickListener buttonClickListener){};
        default void onBackPressed(){};
        Context getContext();
    }

    interface Presenter {
        void resetFieldData();
    }
}
