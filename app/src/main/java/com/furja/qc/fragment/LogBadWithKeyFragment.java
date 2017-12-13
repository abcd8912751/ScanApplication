package com.furja.qc.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.furja.qc.databases.BadMaterialLog;
import com.furja.qc.beans.WorkOrderInfo;
import com.furja.qc.R;
import com.furja.qc.utils.SharpBus;
import com.furja.qc.utils.Utils;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.furja.qc.utils.Constants.UPDATE_BAD_COUNT;
import static com.furja.qc.utils.Utils.showLog;

/**
 * KEY界面的登录视图
 */

public class LogBadWithKeyFragment extends BaseFragment {
    @BindView(R.id.edit_keyboardShow)
    EditText edit_keyboardShow; //显示异常码
    @BindView(R.id.btn_submit_keyFrag)
    ImageButton btn_submit;
    private BadMaterialLog badMaterialLog;  //界面执有的数据库实例
    private List<String> currBadCodes;    //本界面当前携有的异常集
    private long badCounts; //当前工单已经记录的异常总数
    private SharpBus sharpBus;
    private WorkOrderInfo workOrderInfo;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_badlogwithkey,container,false);
        ButterKnife.bind(this,view);

        currBadCodes=new ArrayList<String>();
        badCounts=0;

        RxView.clicks(btn_submit)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {

                        badCounts++;
                        currBadCodes.add(edit_keyboardShow.getText().toString());
                        postBadCounts();
                        edit_keyboardShow.setText("");
                        showLog(currBadCodes.toString());
                        if(isMostOfList())
                            uploadSection();
                    }
                });

        RxView.longClicks(btn_submit)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        badCounts++;
                        currBadCodes.add(edit_keyboardShow.getText().toString());
                        postBadCounts();
                        Snackbar.make(btn_submit,"上传数据中",Snackbar.LENGTH_SHORT).show();
                        syncAndUpdateKeyBadData(null);
                    }
                });

        InputMethodManager imm =
                (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(edit_keyboardShow.getWindowToken(), 0);

        notifyInitFinish();
        return view;
    }



    @OnTextChanged(value = R.id.edit_keyboardShow,callback= OnTextChanged.Callback.TEXT_CHANGED)
    public void onTextChanged(CharSequence s,int start,int before, int count)
    {
        if(count>0||currBadCodes.size()>0)
        {
            btn_submit.setVisibility(View.VISIBLE);
            if(s.toString().contains(","))
            {
                edit_keyboardShow.setText("");
                Snackbar.make
                        (edit_keyboardShow,"异常代码格式错误",Snackbar.LENGTH_SHORT).show();
            }

        }
        else
        {
            btn_submit.setVisibility(View.GONE);
        }
    }

    /**
     * 当type为2时,每一次输入计数1
     * @param info
     */
    public void syncAndUpdateKeyBadData(final WorkOrderInfo info) {
        if(info!=null)
            workOrderInfo=info;
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
                            if(badMaterialLog.getBadTypeCode().size()>0)
                                badMaterialLog.uploadToRemote();
                        }
                        if(info!=null)
                            badCounts=0;
                        resetBadMaterialLog();
                        postBadCounts();
                    }
                });
    }

    /**
     * 上传数据后重置当前 执有的数据库实例
     */
    private void resetBadMaterialLog() {
        currBadCodes.clear();
        badMaterialLog=
                new BadMaterialLog(workOrderInfo,2, Collections.<Long>emptyList(),0);
        badMaterialLog.setBadTypeCode(currBadCodes);
    }

    /**
     * 先行分段上传,以防参数过长
     */
    private void uploadSection() {
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
                        if(badMaterialLog.getBadTypeCode().size()>0)
                            badMaterialLog.uploadToRemote();
                        resetBadMaterialLog();

                    }
                });

    }

    /**
     * 将当前数据保存至本地
     */
    private void syncToLocal() {
        if(badMaterialLog!=null)
        {
            if(badMaterialLog.getBadTypeCode().size()>0)
            {
                List<String> counts=new ArrayList<String>();
                for(int i=0;i<currBadCodes.size();i++)
                    counts.add("1");
                badMaterialLog.setBadTypeCode(currBadCodes);
                badMaterialLog.setBadCodeCount(counts);
                Utils.saveToLocal(badMaterialLog);
            }
        }
        else
            resetBadMaterialLog();
    }

    /**
     * 检验存储异常的List是否较长
     *以分割上传数据
     * @return
     */
    public boolean isMostOfList()
    {
        if(currBadCodes.size()>50)
            return true;
        return false;
    }

    /**
     * 将异常总数传出
     */
    private void postBadCounts() {
        showLog(getClass()+">sharpBus post->"+badCounts);
        if(sharpBus==null)
            sharpBus=SharpBus.getInstance();
        sharpBus.post(UPDATE_BAD_COUNT,badCounts);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


}
