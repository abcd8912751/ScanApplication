package com.furja.qc.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.furja.qc.QcApplication;
import com.furja.qc.databases.BadTypeConfig;
import com.furja.qc.databases.BadTypeConfigDao;
import com.furja.qc.databases.DaoSession;
import com.furja.qc.utils.Caretaker;
import com.furja.qc.utils.SharpBus;
import com.jakewharton.rxbinding2.view.RxView;
import com.furja.qc.R;
import com.furja.qc.beans.WorkOrderInfo;
import com.furja.qc.contract.LogBadWithBnContract;
import com.furja.qc.model.LogBadWithBtnModel;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import q.rorbin.badgeview.QBadgeView;

import static com.furja.qc.utils.Constants.FRAGMENT_ON_TOUCH;
import static com.furja.qc.utils.Constants.UPDATE_BAD_COUNT;
import static com.furja.qc.utils.Constants.UPLOAD_FINISH;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;

/**
 * 用来作为显示品质异常button的视图与数据交互的桥
 */

public class BadLogWithBtnPresenter implements LogBadWithBnContract.Presenter {
    private LogBadWithBnContract.View mDefectiveView;   //用于连接BadLogWithBtnFragment内方法
    private LogBadWithBtnModel mDefectiveModel; //数据库操作类
    private SharpBus sharpBus;
    private MyRecyclerAdapter myRecyclerAdapter;    //显示按钮列表RecyclerView适配器
    private Caretaker caretaker;
    private boolean isEditing=false;
    public BadLogWithBtnPresenter(LogBadWithBnContract.View defectiveView)
    {
        this.mDefectiveView=defectiveView;
        sharpBus=SharpBus.getInstance();
        setThisModel();
        myRecyclerAdapter=new MyRecyclerAdapter();
        mDefectiveView.setRecyclerAdapter(myRecyclerAdapter);
        mDefectiveView.setButtonClickListener(new BtnClickListener());
        caretaker=new Caretaker();
    }

    /**
     * 设置 MVP架构下与此作数据交换的Model
     * 并设定上传成功监听以更新视图
     */
    public void setThisModel()
    {
        Observable.fromCallable(new Callable<List<BadTypeConfig>>() {
            @Override
            public List<BadTypeConfig> call() throws Exception {
                DaoSession daoSession=QcApplication.getDaoSession();
                BadTypeConfigDao typeConfigDao
                        =daoSession.getBadTypeConfigDao();
                List<BadTypeConfig> allResults=typeConfigDao.queryBuilder()
                        .where(BadTypeConfigDao.Properties.SourcType.eq(1))
                        .list();
                if(allResults!=null)
                    return allResults;
                else
                    return Collections.EMPTY_LIST;
            }})
                .subscribe(new Consumer<List<BadTypeConfig>>() {
                    @Override
                    public void accept(List<BadTypeConfig> badTypeConfigs) throws Exception {
                        int list_size=badTypeConfigs.size();
                        String[] btn_titles;
                        btn_titles = readBtn_titles(badTypeConfigs, list_size);
                        mDefectiveModel=new LogBadWithBtnModel(btn_titles);
                    }

                    @NonNull
                    private String[] readBtn_titles(List<BadTypeConfig> badTypeConfigs, int list_size) {
                        String[] btn_titles;
                        if(list_size>0)
                        {
                            btn_titles=new String[list_size];
                            showLog(getClass()+">找到配置信息条数: "+list_size);
                            for(int i=0;i<list_size;i++)
                            {
                                BadTypeConfig typeConfig=badTypeConfigs.get(i);
                                btn_titles[i]=typeConfig.getTypeDesp();
                            }
                        }
                        else
                        {
                            //如果本地数据库没有相应数据使用默认配置信息
                            showLog("没有找到相应异常配置信息");
                            Context context= QcApplication.getContext();
                            btn_titles
                                    = context.getResources().getStringArray(R.array.operate_options);
                        }
                        return btn_titles;
                    }
                });
    }



    /**
     * 同步数据后根据MaterialInfo从数据库中提取数据再显示
     * @param workOrderInfo
     */
    @Override
    public void syncAndUpdateData(final WorkOrderInfo workOrderInfo) {
        Observable observable= Observable.fromCallable(new Callable() {
            @Override
            public Object call() throws Exception {
//                syncData(); //同步至本地并上传至服务器
                caretaker.clear();
                mDefectiveModel.updateData(workOrderInfo);
                return "syncFinished";
            }
        });
        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        sharpBus.post(UPDATE_BAD_COUNT,mDefectiveModel.getTotalBad());
                        myRecyclerAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * 将界面当前数据保存至本地数据库
     */
    @Override
    public void syncData() {
        mDefectiveModel.syncData();
        //同步数据后清空备忘录内容
        caretaker.clear();
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
    }

    /**
     * 放置标记Button的Recycleview
     */
    private class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private boolean isFirst;
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
            viewHolder.setText(mDefectiveModel.getOptionTitle(position));
            viewHolder.setMarkNum((int) mDefectiveModel.getMarkerCount(position));

            RxView.clicks(viewHolder.markerButton)
                    .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception
                    {
                        if(mDefectiveModel.infoHasNull())
                        {
                            showToast("设置物料代码/员工号/机台号后方可记录");
                            return;
                        }
                        if(isEditing())
                        {
                            showEditDialog(viewHolder,position);
                        }
                        else
                        {
                            if (caretaker.isEmpty())
                                caretaker.appendUndo(mDefectiveModel.getMarkCountString());
                            sharpBus.post(UPDATE_BAD_COUNT, mDefectiveModel.getTotalBad());
                            mDefectiveModel.addMarkerCount(position);
                            viewHolder.setMarkNum((int) mDefectiveModel.getMarkerCount(position));
                            caretaker.appendUndo(mDefectiveModel.getMarkCountString());  //保存这个记录
                            sharpBus.post(FRAGMENT_ON_TOUCH, "TOUCH");
                        }
                    }
                });
            RxView.longClicks(viewHolder.markerButton)
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            if(isEditing)
                                return;
                            showEditDialog(viewHolder,position);
                        }
                    });
        }

        private void showEditDialog(final LogViewHolder viewHolder,final int position) {
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
                            if(!TextUtils.isEmpty(input))
                            {
                                count= Long.valueOf(input.toString());
                            }
                            caretaker.appendUndo(mDefectiveModel.getMarkCountString());  //保存这个记录
                            mDefectiveModel.setMarkerCount(position,count);
                            viewHolder.setMarkNum((int) mDefectiveModel.getMarkerCount(position));
                            sharpBus.post(UPDATE_BAD_COUNT,mDefectiveModel.getTotalBad());
                            dialog.cancel();
                        }
                    }).show();
        }

        @Override
        public int getItemCount() {
            return mDefectiveModel.getItemCount();
        }
    }

    /**
     * 撤销、重做、提交 按钮的监听
     */
    public class BtnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            sharpBus.post(FRAGMENT_ON_TOUCH,"TOUCH");
            switch (v.getId())
            {
                //undo按钮
                case R.id.btn_undo_btnFrag:
                    List<Long> codeCounts=caretaker.getUndoMemo();
                    if(codeCounts!=null)
                    {
                        mDefectiveModel.setMarkCounts(codeCounts);
                        sharpBus.post(UPDATE_BAD_COUNT,mDefectiveModel.getTotalBad());
                        myRecyclerAdapter.notifyDataSetChanged();
                    }
                    break;
                //redo按钮
                case R.id.btn_redo_btnFrag:
                    List<Long> counts=caretaker.getRedoMemo(mDefectiveModel.getMarkCountString());
                    if(counts!=null)
                    {
                        mDefectiveModel.setMarkCounts(counts);
                        sharpBus.post(UPDATE_BAD_COUNT,mDefectiveModel.getTotalBad());
                        myRecyclerAdapter.notifyDataSetChanged();
                    }
                    break;
                //编辑按钮
                case R.id.btn_edit_btnFrag:
                    try{
                        if(mDefectiveModel.infoHasNull())
                        {
                            showToast("设置物料代码/员工号/机台号后方可记录");
                            showLog("设置物料代码/员工号/机台号后方可记录");
//                            sharpBus.post(UPLOAD_FINISH,"ISN is NULL");
                            return;
                        }
                        if(!isEditing)
                        {
                            ((ImageButton)v).setImageResource(R.mipmap.ic_editing_src);
                            showToast("点击异常按钮快速编辑计数");
                        }
                        else
                        {
                            ((ImageButton)v).setImageResource(R.mipmap.ic_edit_src);
                        }
                        setEditing(!isEditing);
                    }catch(Exception e){e.printStackTrace();}
                    break;
                //submit按钮、传递上传完成
                case R.id.btn_submit_btnFrag:
                    if(!mDefectiveModel.infoHasNull())
                    {
                        syncData();
                        mDefectiveModel.clearCount();
                        myRecyclerAdapter.notifyDataSetChanged();
                        sharpBus.post(UPLOAD_FINISH,UPLOAD_FINISH);
                    }
                    else
                    {
                        showToast("设置物料代码/员工号/机台号后方可");

//                        showReadBarCodeDialog(v);
                    }

                    break;
            }
        }
    }

    private void showReadBarCodeDialog(View view)
    {
        new MaterialDialog.Builder(view.getContext())
                .title(R.string.materialUpdate)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .autoDismiss(false)
                .input("扫描物料条码","", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(final MaterialDialog dialog, CharSequence input) {
                        //刷新视图读取数据
                        if(TextUtils.isEmpty(input))
                        {
                            showToast("需录入信息");
                        }
                        else
                        {
                            showLog("你输入的是:"+input);
                            String barcode=input.toString();
                            mDefectiveModel.getMaterialISNbyBarCode(barcode);
                            dialog.cancel();
                        }
                    }
                }).keyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if(keyEvent!=null)
                {
                    if(keyEvent.getKeyCode()== KeyEvent.KEYCODE_ENTER)
                    {
                    }
                }
                return false;
            }
        }).canceledOnTouchOutside(false).show();
    }



    /**
     * RecyclerView的View装载
     */
    public class LogViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.marker_button)
        Button markerButton;
        private int markNum;
        private QBadgeView badgeView;
        public LogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            badgeView=new QBadgeView(itemView.getContext());
            badgeView.bindTarget(markerButton);
            badgeView.setBadgeTextSize(15,true);
            badgeView.setExactMode(true);
        }

        public Context getContext()
        {
            return itemView.getContext();
        }

        public int getMarkNum() {
            return markNum;
        }
        public void setText(String title)
        {
            markerButton.setText(title);
        }
        public void setMarkNum(int markNum) {
            this.markNum = markNum;
            badgeView.setBadgeNumber(markNum);
            if(markNum==0)
            {
                markerButton.setGravity(Gravity.CENTER);
            }
            else if(markerButton.getGravity()!=(Gravity.LEFT|Gravity.BOTTOM))
            {
                markerButton.setGravity(Gravity.LEFT|Gravity.BOTTOM);
            }
        }
    }


}
