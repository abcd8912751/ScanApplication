package com.furja.qc.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.furja.qc.R;
import com.furja.qc.utils.SharpBus;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import q.rorbin.badgeview.QBadgeView;

import static com.furja.qc.utils.Constants.FRAGMENT_ON_TOUCH;
import static com.furja.qc.utils.Constants.UPDATE_BAD_COUNT;
import static com.furja.qc.utils.Utils.showLog;

/**
 * Created by zhangmeng on 2017/12/14.
 */

public class KeyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private List<BadItem> mData;
    private SharpBus sharpBus;
    private boolean isEditing;
    public KeyRecyclerAdapter() {
        mData =new ArrayList<BadItem>();
        sharpBus=SharpBus.getInstance();
        setEditing(false);
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
        final BadItem badItem=getItem(position);
        myKeyHolder.setCodeCount(badItem.getCodeCount());
        myKeyHolder.setLabel(badItem.getBadCode());
        RxView.clicks(myKeyHolder.button)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if(isEditing())
                        {
                            showEditorDialog(myKeyHolder.itemView.getContext(),position);
                        }
                        else
                        {
                            badItem.addCount();
                            freshItem(position, badItem);
                            sharpBus.post(FRAGMENT_ON_TOUCH,"TOUCH");
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
     * @param badItem
     */
    private void freshItem(int position, BadItem badItem) {
        if(badItem.getCodeCount()>0)
            mData.set(position,badItem);
        else
            mData.remove(position);
        notifyView();
    }

    private BadItem getItem(int position)
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
        final BadItem badItem=getItem(position);
        String prefill="";
        prefill=prefill+badItem.getCodeCount();
        new MaterialDialog.Builder(context)
                .title("设定该异常类型个数")
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .autoDismiss(false)
                .input("不设置将清空该异常计数", prefill, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        long count=0;
                        if(!TextUtils.isEmpty(input))
                        {
                            count= Long.valueOf(input.toString());
                        }
                        badItem.setCodeCount(count);
                        freshItem(position, badItem);
                        dialog.cancel();
                    }
                }).show();
    }

    public long getTotalCount() {
        long totalCount =0;
        for(BadItem item:mData)
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
    public void addItem(String code)
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
            mData.add(new BadItem(code,1));
        else
        {
            BadItem item=getItem(position);
            item.addCount();
            mData.set(position,item);
        }
        notifyView();
    }

    /**
     * 通知页面刷新
     */
    private void notifyView() {
        sharpBus.post(UPDATE_BAD_COUNT, getTotalCount());
        notifyDataSetChanged();
    }
    public List<BadItem> getmData() {
        return mData;
    }

}
