package com.furja.qc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.furja.qc.QcApplication;
import com.furja.qc.R;
import com.furja.qc.beans.Preferences;
import com.furja.qc.contract.WorkOrderContract;
import com.furja.qc.fragment.BaseFragment;
import com.furja.qc.fragment.LogBadWithBtnFragment;
import com.furja.qc.fragment.LogBadWithKeyFragment;
import com.furja.qc.presenter.WorkOrderPresenter;
import com.furja.qc.utils.SharpBus;
import com.furja.qc.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.furja.qc.utils.Constants.RESET_CONFIG;
import static com.furja.qc.utils.Constants.TYPE_BADLOG_EMPTY;
import static com.furja.qc.utils.Constants.TYPE_BADLOG_WITHKEY;
import static com.furja.qc.utils.Constants.UPDATE_BAD_COUNT;
import static com.furja.qc.utils.Constants.BADLOG_FRAGMENT_INITFINISH;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;

/**
 * Created by zhangmeng on 2017/12/3.
 */

public class BadLogActivity extends AppCompatActivity implements WorkOrderContract.View {
    private BaseFragment tempFragment;
    private FragmentManager fm;
    //工单信息ListView与数据交互使用的presenter
    private WorkOrderPresenter mWorkOrderPresenter;
    @BindView(R.id.workinfo_list)
    ListView workinfoList;
    private LogBadWithBtnFragment logBadWithBtnFragment;
    private LogBadWithKeyFragment logBadWithKeyFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defectivelog);
        ButterKnife.bind(this);
        fm=getSupportFragmentManager();

        logBadWithBtnFragment =new LogBadWithBtnFragment();
        logBadWithKeyFragment =new LogBadWithKeyFragment();

        initMvpUnit();
        if(Preferences.getSourceType()!=TYPE_BADLOG_WITHKEY)
            transferFragment(logBadWithBtnFragment,"TYPE_BADLOG_WITHBTN");
        else
            transferFragment(logBadWithKeyFragment,"TYPE_BADLOG_WITHKEY");

    }


    private void initMvpUnit() {
        mWorkOrderPresenter=new WorkOrderPresenter(this);
        //将工单信息传入 录入 显示品质异常button的视图
        SharpBus sharpBus= SharpBus.getInstance();
        sharpBus.register(BADLOG_FRAGMENT_INITFINISH)
          .subscribe(new Observer<Object>() {
              @Override
              public void onSubscribe(Disposable d) {

              }

              @Override
              public void onNext(Object value) {
                  showLog(getClass()+"->收到了"+value.toString());
                  syncAndUpdateBadData();
              }

              @Override
              public void onError(Throwable e) {
              }

              @Override
              public void onComplete() {

              }
          });

    }

    /**
     * 切换Fragment
     * @param to
     * @param tag
     */
    private void transferFragment(BaseFragment to,String tag) {
        BaseFragment from=tempFragment;
        FragmentTransaction fts = fm.beginTransaction();
        if(from!=null)
        {
            if(from==to)
                return;
            fts.hide(from).commit();
        }
        this.tempFragment=to;
        showFragment(to, tag);
    }

    /**
     * 显示Fragment
     * @param to
     * @param tag
     */
    private void showFragment(BaseFragment to,String tag) {
        FragmentTransaction fts = fm.beginTransaction();
        if(to.isAdded())
        {
            fts.show(to);
        }
        else
        {
            fts.add(R.id.button_frame, to,tag);
        }
        fts.commit();
    }

    /**
     * 设置工单信息适配器并设置监听模块录入特定信息
     * @param baseAdapter
     */
    @Override
    public void setListAdapter(BaseAdapter baseAdapter) {
        workinfoList.setAdapter(baseAdapter);
        workinfoList.setItemsCanFocus(true);
    }

    @Override
    public ListView getAdapterView() {
        return workinfoList;
    }



    /**
     * 以新录入的数据更新
     * 显示品质异常button的视图
     */
    @Override
    public void syncAndUpdateBadData()
    {
        if(Preferences.getSourceType()!=TYPE_BADLOG_WITHKEY)
            logBadWithBtnFragment.syncAndUpdateBtnBadData(
                    mWorkOrderPresenter.getWorkOrderInfo());
        else
            logBadWithKeyFragment.syncAndUpdateKeyBadData
                    (mWorkOrderPresenter.getWorkOrderInfo());
    }

    @Override
    public void setSelection(int position) {
        workinfoList.setSelection(position);
    }

    @Override
    public void requestFocus() {
        workinfoList.requestFocus();
    }

    @Override
    public void clearFocus() {
        workinfoList.clearFocus();
    }

    @Override
    public void onBackPress() {
        onBackPressed();
    }

    /**
     * 销毁Activity时注销SharpBus
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharpBus sharpBus=SharpBus.getInstance();
        sharpBus.unregister(BADLOG_FRAGMENT_INITFINISH);
        sharpBus.unregister(UPDATE_BAD_COUNT);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_badlog, menu);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logOut)
        {
            syncAndUpdateBadData();
            Preferences.saveAutoLogin(false);
            QcApplication.setUserAndSave(null);
            toLogin();
        }
        else if(id==R.id.action_switchScene)
        {   //切换工作场景的
            syncAndUpdateBadData();
            Preferences.saveSourceType(""+TYPE_BADLOG_EMPTY);
            toLogin();
        }
        if(id==R.id.action_resetConfig)
        {
            syncAndUpdateBadData();
            Intent intent=new Intent(this,SplashActivity.class);
            intent.setAction(RESET_CONFIG);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void toLogin() {
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        showLog("你按了:"+"onBackPressed");
        syncAndUpdateBadData();
        super.onBackPressed();
    }

}
