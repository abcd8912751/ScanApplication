package com.furja.qc.view;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.furja.qc.R;
import com.furja.qc.beans.BadLogEntry;
import com.furja.qc.databases.BadTypeConfig;
import com.furja.qc.utils.LocalBadTypeQuery;
import com.furja.qc.utils.SharpBus;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import q.rorbin.badgeview.QBadgeView;

import static com.furja.qc.utils.Constants.UPDATE_BAD_COUNT;

/**
 * Created by zhangmeng on 2017/12/14.
 */
public class KeyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<BadLogEntry> mData;
    private SharpBus sharpBus;
    private boolean isEditing;
    LocalBadTypeQuery localBadTypeQuery;
    public KeyRecyclerAdapter() {
        mData =new ArrayList<BadLogEntry>();
        sharpBus=SharpBus.getInstance();
        setEditing(false);
        localBadTypeQuery = new LocalBadTypeQuery() ;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_marker_key,parent,false);
        MyKeyHolder viewHolder=new MyKeyHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final MyKeyHolder myKeyHolder=(MyKeyHolder)holder;
        final BadLogEntry badLogEntry =getItem(position);
        myKeyHolder.setCodeCount(badLogEntry.getCodeCount());
        myKeyHolder.setLabel(badLogEntry.getBadCode());
        RxView.clicks(myKeyHolder.button)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if(isEditing()) {
                            showEditorDialog(myKeyHolder.itemView.getContext(),position);
                        }
                        else {
                            badLogEntry.addCount();
                            freshItem(position, badLogEntry);
                        }
                    }
                });
        RxView.longClicks(myKeyHolder.button)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        showEditorDialog(myKeyHolder.itemView.getContext(),position);
                    }
                });
    }

    /**
     * 更新当前Item
     * @param position
     * @param badLogEntry
     */
    private void freshItem(int position, BadLogEntry badLogEntry) {
        if(badLogEntry.getCodeCount()>0)
            mData.set(position, badLogEntry);
        else
            mData.remove(position);
        notifyView();
    }

    private BadLogEntry getItem(int position)
    {
        return mData.get(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * 显示编辑个数的对话框
     * @param context
     * @param position
     */
    private void showEditorDialog(Context context, final int position) {
        final BadLogEntry badLogEntry =getItem(position);
        String prefill="";
        prefill=prefill+ badLogEntry.getCodeCount();
        View customView
                =LayoutInflater.from(context).inflate(R.layout.layout_edit_dialog,null);
        final EditText itemNum_edit=customView.findViewById(R.id.itemNum_edit);
        final EditText itemRemark_edit=customView.findViewById(R.id.itemRemark_edit);
        itemNum_edit.setText(prefill);
        itemRemark_edit.setText(badLogEntry.getRemark());
        List<BadTypeConfig> badTypeConfigs
                =localBadTypeQuery.queryLocal(badLogEntry.getBadCode());
        String title="设定该异常个数及备注";
        if(!badTypeConfigs.isEmpty())
            title="编辑类型 "+badTypeConfigs.get(0).getTypeDesp();
        new MaterialDialog.Builder(context)
                .title(title)
                .customView(customView,false)
                .autoDismiss(false)
                .positiveText("确定").onPositive((dialog, which) -> {
                    long count=0;
                    CharSequence input=itemNum_edit.getText();
                    if(!TextUtils.isEmpty(input))
                    {
                        count= Long.valueOf(input.toString());
                    }
                    input=itemRemark_edit.getText();
                    if(!TextUtils.isEmpty(input))
                    {
                        badLogEntry.setRemark(input.toString());
                    }
                    else
                        badLogEntry.setRemark("");
                    badLogEntry.setCodeCount(count);
                    freshItem(position, badLogEntry);
                    dialog.cancel();
        }).show().getWindow()
                .setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
    }

    public long getTotalCount() {
        long totalCount =0;
        for(BadLogEntry item:mData)
            totalCount = totalCount +item.getCodeCount();
        return totalCount;
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
    }

    /**
     * 清空data数据
     */
    public void clearData() {
        io.reactivex.Observable.just("clearData")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mData.clear();
                        notifyDataSetChanged();
                        setEditing(false);
                    }
                });
    }


    class MyKeyHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.marker_button_key)
        Button button;
        QBadgeView badgeView;
        public MyKeyHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            Context context=view.getContext();
            badgeView=new QBadgeView(context);
            badgeView.bindTarget(button);
            badgeView.setBadgeTextColor(context.getResources().getColor(R.color.holo_purple));
            badgeView.setBadgeTextSize(15,true);
            badgeView.setBadgeBackgroundColor(context.getResources().getColor(R.color.colorBottomBg));
        }

        /**
         * 针对当前ButtonLabel所示的异常代码计数
         * @param count
         */
        public void setCodeCount(long count)
        {
            badgeView.setBadgeNumber((int) count);
            badgeView.setExactMode(true);
        }
        public void setLabel(String label)
        {
            button.setText(label);
        }
    }

    /**
     * 向mData里添加对象
     * @param code
     */
    public int addItem(String code)
    {
        int position=-1;
        for(int i=0;i<mData.size();i++)
        {
            if(getItem(i).getBadCode().equals(code))
            {
                position=i;
                break;
            }
        }
        if (position<0)
        {
            position=mData.size();
            mData.add(new BadLogEntry(code,1).setRemark(""));
        }
        else
        {
            BadLogEntry item=getItem(position);
            item.addCount();
            mData.set(position,item);
        }
        notifyView();
        return position;
    }

    /**
     * 通知页面刷新
     */
    private void notifyView() {
        sharpBus.post(UPDATE_BAD_COUNT, getTotalCount());
        notifyDataSetChanged();
    }
    public List<BadLogEntry> getmData() {
        return mData;
    }

}
