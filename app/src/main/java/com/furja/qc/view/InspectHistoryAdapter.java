package com.furja.qc.view;

import android.content.res.Resources;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.furja.qc.R;
import com.furja.qc.beans.InspectionHistory;

public class InspectHistoryAdapter extends BaseQuickAdapter<InspectionHistory,BaseViewHolder> {
    public InspectHistoryAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, InspectionHistory item) {
        Resources resources = helper.itemView.getResources();
        TextView text_type = helper.getView(R.id.item_type);
        if (item.getLogType()==1) {
            int textColor = resources.getColor(R.color.color_tourType);
            text_type.setText("巡检");
            text_type.setBackgroundResource(R.drawable.shape_tourtype_bg);
            text_type.setTextColor(textColor);
        }
        else {
            int textColor = resources.getColor(R.color.color_dimenType);
            text_type.setText("尺寸");
            text_type.setBackgroundResource(R.drawable.shape_dimentype_bg);
            text_type.setTextColor(textColor);
        }
        helper.setText(R.id.item_content,"时段: "+item.getProduceDate()+" "+item.getTimePeriod());
        helper.setText(R.id.item_note,"物料名称: "+item.getNote());
    }

}
