package com.furja.qc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.furja.qc.QcApplication;
import com.furja.qc.R;
import com.furja.qc.beans.User;
import com.furja.qc.beans.Preferences;
import com.furja.qc.services.UploadServices;
import com.furja.qc.utils.LoginUtils;
import com.furja.qc.utils.SharpBus;
import com.furja.qc.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static com.furja.qc.utils.Constants.RESET_CONFIG;
import static com.furja.qc.utils.Constants.SYNCOVER_BADTYPE_CONFIG;
import static com.furja.qc.utils.Constants.TYPE_BADLOG_EMPTY;
import static com.furja.qc.utils.Utils.showLog;

/**
 * 启动配置页
 */

public class SplashActivity extends AppCompatActivity {
    @BindView(R.id.image_setting_splash)
    ImageView image_Setting;
    @BindView(R.id.splash_operatorLabel)
    TextView loadingLabel;
    @BindView(R.id.splash_startLoginBtn)
    Button startLoginBtn;
    @BindView(R.id.splash_switchOperatorBtn)
    Button switchOperatorBtn;
    int callCount=0;
    boolean isResetConfig;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        Intent fromIntent=getIntent();
        isResetConfig =false;
        if(fromIntent!=null)
        {
            if(!TextUtils.isEmpty(fromIntent.getAction())
                    &&fromIntent.getAction().equals(RESET_CONFIG))
                isResetConfig =true;
        }
        showLog("isReset:->"+isResetConfig);
        if(!isResetConfig)
        {
            checkFisrtStart();
            if(QcApplication.getUser()!=null)
            {
                if(Preferences.getSourceType()!=TYPE_BADLOG_EMPTY)
                {
                    showLog(getClass()+"阻止不了我去LogBad");
                    toLogBad();
                }
            }
            else
            {
                Intent intent=new Intent(this, UploadServices.class);
                startService(intent);
                showLog("启动上传服务");
                syncLogConfigAndLogin();
            }
        }
        else
        {
            showLog("重置配置");
            syncLogConfigAndLogin();
        }

    }

    /**
     * 是否是第一次启动,如果不是直接finish
     */
    private void checkFisrtStart() {
        if (!this.isTaskRoot()) { // 判断当前activity是不是所在任务栈的根
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                }
            }
        }
    }

    /**
     * 从服务器同步异常类型配置至本地数据库
     */
    private void syncLogConfigAndLogin() {
        rotateSettingImage();
        SharpBus.getInstance().register(SYNCOVER_BADTYPE_CONFIG)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object value) {
                        showLog(getClass()+"->"+value.toString());
                        if(value.toString().contains("true"))
                        {
                            callCount=0;
                            image_Setting.clearAnimation();
                            loadingLabel.setVisibility(View.GONE);
                            switchToLogin();
                        }
                        else
                        {
                            callCount++;
                            if(callCount<3)
                                Utils.syncBadTypeConfig(isResetConfig);
                            else
                            {
                                image_Setting.clearAnimation();
                                loadingLabel.setVisibility(View.GONE);
                                switchToLogin();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        Utils.syncBadTypeConfig(isResetConfig);
    }

    /**
     * 旋转Setting的 ImageView
     */
    private void rotateSettingImage() {
        Animation circle_anim = AnimationUtils.loadAnimation(this, R.anim.anim_round_rotate);
        if (circle_anim != null) {
            image_Setting.startAnimation(circle_anim);  //开始动画
        }
    }

    @OnClick({R.id.splash_switchOperatorBtn,R.id.splash_startLoginBtn})
    public void OnClick(View view)
    {
        if(view.getId()==R.id.splash_startLoginBtn)
            loginUsePreference();
        else
            switchToLogin();
    }
    /**
     * 使用SharedPreferences存储的用户名/密码进行登录
     */
    private void loginUsePreference() {
        String password= Preferences.getOperatorPassword();
        String user= Preferences.getOperatorId();
        if(!TextUtils.isEmpty(user))
        {
            LoginUtils.login(user, password, new LoginUtils.LoginCallBack() {
                @Override
                public void onSuccess(User user) {
                    QcApplication.setUserAndSave(user);
                    Snackbar.make(startLoginBtn,"登录成功",Snackbar.LENGTH_SHORT).show();
                    toLogBad();
                }

                @Override
                public void onFail(String errorMsg) {
                    showLog(errorMsg);
                    Snackbar.make(startLoginBtn,errorMsg,Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 跳转至记录异常的界面
     */
    private void toLogBad() {
        Intent intent
                =new Intent(SplashActivity.this, BadLogActivity.class);
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
