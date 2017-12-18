package com.furja.qc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.furja.qc.beans.ButtonWorkScene;
import com.furja.qc.beans.EmptyWorkScene;
import com.furja.qc.beans.KeyWorkScene;
import com.furja.qc.beans.Preferences;
import com.furja.qc.beans.WorkScene;
import com.jakewharton.rxbinding2.view.RxView;
import com.furja.qc.R;
import com.furja.qc.QcApplication;
import com.furja.qc.beans.User;
import com.furja.qc.utils.LoginUtils;
import com.furja.qc.view.ClearableEditTextWithIcon;
import com.kyleduo.switchbutton.SwitchButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import io.reactivex.functions.Consumer;

import static com.furja.qc.utils.Constants.TYPE_BADLOG_EMPTY;
import static com.furja.qc.utils.Constants.TYPE_BADLOG_WITHBTN;
import static com.furja.qc.utils.Constants.TYPE_BADLOG_WITHKEY;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;

/**
 * 登录的Activity
 */

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.edit_login_operator)
    ClearableEditTextWithIcon operatorEdit;
    @BindView(R.id.edit_login_password)
    ClearableEditTextWithIcon passwordEdit;
    @BindView(R.id.login_startLoginBtn)
    Button startLoginBtn;
    @BindView(R.id.auto_login_checked)
    SwitchButton autoLogin_switch;
    private String password;
    private String user;

    private WorkScene workScene;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        operatorEdit.setIconResource(R.mipmap.ic_user_left);
        passwordEdit.setIconResource(R.mipmap.ic_password_left);

        password= Preferences.getOperatorPassword();
        user= Preferences.getOperatorId();

        if(!TextUtils.isEmpty(user))
        {
            operatorEdit.setText(user);
            passwordEdit.setText(password);
            if(Preferences.isAutoLogin())
                startLogin();
        }
        RxView.clicks(startLoginBtn)
              .subscribe(new Consumer<Object>() {
                  @Override
                  public void accept(Object o) throws Exception {
                      if(TextUtils.isEmpty(operatorEdit.getText().toString())
                              ||TextUtils.isEmpty(passwordEdit.getText().toString()))
                          showToast("请输入用户名/密码");
                      else
                          startLogin();
                  }
              });



        autoLogin_switch.setChecked(Preferences.isAutoLogin());

        //设置注塑车间工作场景
//        Preferences.saveSourceType(TYPE_BADLOG_WITHKEY+"");
//        Preferences.saveSourceType(TYPE_BADLOG_WITHBTN+"");
//        switchToLogBad();
    }

    @OnTouch({R.id.edit_login_operator,R.id.edit_login_password})
    public boolean OnTouch(View view, MotionEvent event)
    {
        ClearableEditTextWithIcon info_content=(ClearableEditTextWithIcon)view;
        if (info_content.getCompoundDrawables()[2] == null)
            return false;
        if (event.getAction() != MotionEvent.ACTION_UP)
            return false;
        if (event.getX() > info_content.getWidth() - info_content.getPaddingRight() - info_content.getIntrinsicWidth()) {
            info_content.setText("");
            info_content.removeClearButton();
        }
        return false;
    }
    /**
     * 开始登录
     */
    private void startLogin() {
        String user=operatorEdit.getText().toString();
        String password=passwordEdit.getText().toString();
        startLoginBtn.setText("登录中...");
        startLoginBtn.setEnabled(false);
        LoginUtils.login(user, password, new LoginUtils.LoginCallBack() {
            @Override
            public void onSuccess(User user) {
                QcApplication.setUserAndSave(user);
                Preferences.saveAutoLogin(autoLogin_switch.isChecked());
                if(Preferences.getSourceType()!=TYPE_BADLOG_EMPTY)
                {
                    switchToLogBad();
                    finish();
                }
                else
                    selectWorkScene();
            }

            @Override
            public void onFail(String errorMsg) {
                showLog(errorMsg);
                startLoginBtn.setText("登录");
                startLoginBtn.setEnabled(true);
                Snackbar.make(startLoginBtn,errorMsg,Snackbar.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 弹出对话框选择
     */
    private void selectWorkScene() {
        updateWorkScene();
        new MaterialDialog
                .Builder(this)
                .title(workScene.getDialogTitle())
                .content(workScene.getDialogContent())
                .positiveText(workScene.getYesButtonLabel())
                .negativeColor(getResources().getColor(R.color.colorNegative))
                .negativeText(workScene.getNoButtonLabel())
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        workScene.selectSwitchYes();
                        dialog.cancel();
                        switchToLogBad();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        workScene.selectSwitchNo();
                        dialog.cancel();
                        switchToLogBad();
                    }
                })
                .canceledOnTouchOutside(false)
                .show();

    }

    private void updateWorkScene() {

        switch( Preferences.getSourceType())
        {
            case TYPE_BADLOG_EMPTY:
                workScene=new EmptyWorkScene();
                break;
            case TYPE_BADLOG_WITHBTN:
                workScene=new ButtonWorkScene();
                break;
            case TYPE_BADLOG_WITHKEY:
                workScene=new KeyWorkScene();
                break;
        }
    }


    /**
     * 切换至异常收集界面
     */
    private void switchToLogBad() {
        Intent intent=new Intent(LoginActivity.this, BadLogActivity.class);
        startActivity(intent);
        finish();
    }

}
