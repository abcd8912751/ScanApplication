package com.furja.qc.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.furja.qc.QcApplication;
import com.furja.qc.databases.BadMaterialLog;
import com.furja.qc.beans.WorkOrderInfo;
import com.furja.qc.R;
import com.furja.qc.utils.Constants;
import com.furja.qc.view.BadItem;
import com.furja.qc.view.GridSpacingItemDecoration;
import com.furja.qc.view.KeyRecyclerAdapter;
import com.furja.qc.view.MyAutoAdapter;
import com.furja.qc.utils.SharpBus;
import com.furja.qc.utils.Utils;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.furja.qc.utils.Constants.FRAGMENT_ON_TOUCH;
import static com.furja.qc.utils.Constants.UPDATE_BAD_COUNT;
import static com.furja.qc.utils.Constants.UPLOAD_FINISH;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;

/**
 * KEY界面的登录视图
 */

public class LogBadWithKeyFragment extends BaseFragment {
    @BindView(R.id.edit_keyboardShow)
    AppCompatAutoCompleteTextView edit_keyboardShow; //显示异常码

    @BindView(R.id.btn_upload_keyFrag)
    ImageButton btn_upload;
    @BindView(R.id.btn_edit_keyFrag)
    ImageButton btn_edit;
    @BindView(R.id.recyclerview_keyfrag)
    RecyclerView recyclerView;
    private BadMaterialLog badMaterialLog;  //界面执有的数据库实例
    private SharpBus sharpBus;
    private WorkOrderInfo workOrderInfo;
    private KeyRecyclerAdapter recyclerAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_badlogwithkey,container,false);
        ButterKnife.bind(this,view);

        MyAutoAdapter myAutoAdapter=new MyAutoAdapter(mContext,R.layout.simplelistitem);
        edit_keyboardShow.setAdapter(myAutoAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext,4));
        GridSpacingItemDecoration itemDecoration
                =new GridSpacingItemDecoration(4,20,true);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerAdapter=new KeyRecyclerAdapter();
        recyclerView.setAdapter(recyclerAdapter);
        notifyInitFinish();
        sharpBus=SharpBus.getInstance();
        edit_keyboardShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String badCode= (String) adapterView.getItemAtPosition(position);
                String[] spit=badCode.split(Constants.INTER_SPLIT);
                badCode=spit[0];
                addBadItem(badCode);
            }
        });
        return view;
    }

    /**
     * 往异常列表里添加item
     * @param badCode
     */
    private void addBadItem(String badCode) {
        if(TextUtils.isEmpty(badCode))
        {
            showToast("无有效输入,不予记录");
            return;
        }
        cleanKeyEdit();
        if(badMaterialLog==null||
                TextUtils.isEmpty(badMaterialLog.getMaterialISN()))
        {
            showToast("设置物料代码后方可记录");
            sharpBus.post(UPLOAD_FINISH,"ISN is NULL");
            return;
        }
        if(recyclerAdapter!=null)
            recyclerAdapter.addItem(badCode);
    }




    @OnClick({R.id.btn_edit_keyFrag,R.id.btn_upload_keyFrag,
            R.id.edit_keyboardShow})
    public void onClick(View v)
    {

        sharpBus.post(FRAGMENT_ON_TOUCH,"TOUCH");
        switch (v.getId())
        {

            //编辑按钮
            case R.id.btn_edit_keyFrag:
                try{
                    if(recyclerAdapter.getTotalCount()>0)
                    {
                        if(!recyclerAdapter.isEditing())
                        {
                            ((ImageButton)v).setImageResource(R.mipmap.ic_editing_src);
                        }
                        else
                        {
                            ((ImageButton)v).setImageResource(R.mipmap.ic_edit_src);
                        }
                        recyclerAdapter.setEditing(!recyclerAdapter.isEditing());
                    }
                    else
                        showToast("暂无可编辑的对象");
                }catch(Exception e){e.printStackTrace();}
                break;
            //上传按钮
            case R.id.btn_upload_keyFrag:
                if (badMaterialLog!=null
                        &&recyclerAdapter.getTotalCount()<1)
                    showToast("未记录,不予上传");
                else
                    syncAndUpdateKeyBadData(null);
                break;
        }
    }

    /**
     * 当type为2时
     * @param info
     */
    public void syncAndUpdateKeyBadData(final WorkOrderInfo info) {
        if(info!=null)
        {
            workOrderInfo=info;
            showLog("传入workInfo:"+workOrderInfo.toString());
            resetBadMaterialLog();
        }
        Observable
                .fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                syncToLocal();
                return "SaveDataBaseFinish";
            }})
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if(!badMaterialLog.isUploaded())
                        {
                            if(badMaterialLog.getBadCount()>0)
                            {
                                Utils.toUpload();
                                sharpBus.post(UPLOAD_FINISH,"finish");
                                recyclerAdapter.clearData();
                                resetBadMaterialLog();
                            }
                        }
                    }
                });
    }

    /**
     * 上传数据后重置当前 执有的数据库实例
     */
    private void resetBadMaterialLog() {
        if(workOrderInfo==null)
            workOrderInfo=new WorkOrderInfo("","","");
        if(QcApplication.getUser()!=null)
        {
            List<Long> markCounts=new ArrayList<Long>();
            badMaterialLog=
                    new BadMaterialLog(workOrderInfo,2, markCounts,0);
        }
    }



    /**
     * 将当前数据保存至本地
     */
    private void syncToLocal() {
        if(badMaterialLog!=null&&recyclerAdapter!=null)
        {
            List<BadItem> datas=recyclerAdapter.getmData();
            List<String> badcodes=new ArrayList<String>();
            List<String> codeCounts=new ArrayList<String>();
            for(BadItem item:datas)
            {
                badcodes.add(item.getBadCode());
                codeCounts.add(item.getCodeCount()+"");
            }
            badMaterialLog.setBadTypeCode(badcodes);
            badMaterialLog.setBadCodeCount(codeCounts);
            badMaterialLog.setBadCount(recyclerAdapter.getTotalCount());
            Utils.saveToLocal(badMaterialLog);
        }
        else
            resetBadMaterialLog();
    }




    /**
     * 将edit_keyboardShow
     * 的AutoCompleteTextView清空
     */
    private void cleanKeyEdit() {
        edit_keyboardShow.setText("");
    }

}
