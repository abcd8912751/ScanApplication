package com.furja.qc.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.furja.qc.QcApplication;
import com.furja.qc.R;
import com.furja.qc.beans.Preferences;
import com.furja.qc.utils.SharpBus;
import com.furja.qc.utils.Utils;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.furja.qc.utils.Constants.ACTION_UPDATE_APK;
import static com.furja.qc.utils.Constants.RESET_CONFIG;
import static com.furja.qc.utils.Constants.SYNCOVER_BADTYPE_CONFIG;
import static com.furja.qc.utils.Constants.TYPE_BADLOG_EMPTY;
import static com.furja.qc.utils.Constants.TYPE_BADLOG_WITHBTN;
import static com.furja.qc.utils.Utils.showLog;

/**
 * 启动配置页
 */

public class SplashActivity extends AppCompatActivity {
    @BindView(R.id.image_setting_splash)
    ImageView image_Setting;
    @BindView(R.id.splash_operatorLabel)
    TextView loadingLabel;
    int callCount=0;
    boolean isResetConfig;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!isFisrtStart())
            return;
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        Intent fromIntent=getIntent();
        isResetConfig =false;
        if(fromIntent!=null) {
            if(!TextUtils.isEmpty(fromIntent.getAction())
                    &&fromIntent.getAction().equals(RESET_CONFIG))
                isResetConfig =true;
        }
        if (!isResetConfig) {
            if (QcApplication.getUser()!=null) {
                if (!TextUtils.isEmpty(Preferences.getOperatorID())
                        &&Preferences.getSourceType()!=TYPE_BADLOG_EMPTY) {
                    showLog(getClass()+"阻止不了我去LogBad"+QcApplication.getUserID());
                    if(Preferences.getSourceType()==TYPE_BADLOG_WITHBTN)
                        toLogBad();
                    else
                        toLogAssembly();
                }
                else
                    switchToLogin();
            }
            else
                syncLogConfigAndLogin();
        }
        else {
            showLog("重置配置");
            syncLogConfigAndLogin();
        }
    }

    private void toLogAssembly() {
        Intent intent=new Intent(this, AssemblyLogActivity.class);
        intent.setAction(ACTION_UPDATE_APK);
        startActivity(intent);
        finish();
    }

    /**
     * 是否是第一次启动,如果不是直接finish
     */
    private boolean isFisrtStart() {
        if (!this.isTaskRoot()) {   // 判断当前activity是不是所在任务栈的根
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction(); //因为重新配置数据时会跳转至这一界面故判断action
                if (Intent.ACTION_MAIN.equals(action)) {
                    showLog("任务栈根");
                    finish();
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 从服务器同步异常类型配置至本地数据库
     */
    private void syncLogConfigAndLogin() {
        rotateSettingImage();
        SharpBus.getInstance().register(SYNCOVER_BADTYPE_CONFIG)
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(value->{
                    showLog(getClass()+"更新配置完成->"+value.toString());
                    if (value.toString().contains("true")) {
                        callCount=0;
                        image_Setting.clearAnimation();
                        loadingLabel.setVisibility(View.GONE);
                        switchToLogin();
                    }
                    else {
                        callCount++;
                        if(callCount<3)
                            Utils.syncBadTypeConfig(isResetConfig);
                        else {
                            image_Setting.clearAnimation();
                            loadingLabel.setVisibility(View.GONE);
                            switchToLogin();
                        }
                    }
                });
        Utils.syncBadTypeConfig(isResetConfig);
    }

    /**
     * 旋转Setting的 ImageView
     */
    private void rotateSettingImage() {
        Animation circle_anim = AnimationUtils.loadAnimation(this, R.anim.anim_round_rotate);
        if (circle_anim != null)
            image_Setting.startAnimation(circle_anim);  //开始动画
    }



    /**
     * 跳转至记录异常的界面
     */
    private void toLogBad() {
        Intent intent
                =new Intent(SplashActivity.this, InjectionLogActivity.class);
        intent.setAction(ACTION_UPDATE_APK);
        startActivity(intent);
        finish();
    }

    /**
     * 跳转页面登录
     */
    private void switchToLogin() {
        Intent intent
                =new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
