package com.furja.qc.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.furja.qc.beans.EmptyWorkScene;
import com.furja.qc.beans.Preferences;
import com.furja.qc.beans.WorkScene;
import com.furja.qc.utils.StatusBarUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.furja.qc.R;
import com.furja.qc.QcApplication;
import com.furja.qc.beans.User;
import com.furja.qc.utils.LoginUtils;
import com.furja.qc.view.CleanableEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

import static com.furja.qc.utils.Constants.TYPE_BADLOG_EMPTY;
import static com.furja.qc.utils.Constants.TYPE_BADLOG_WITHBTN;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;

/**
 * 登录的Activity
 */

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.edit_login_operator)
    CleanableEditText operatorEdit;
    @BindView(R.id.edit_login_password)
    CleanableEditText passwordEdit;
    @BindView(R.id.login_startLoginBtn)
    Button startLoginBtn;
    @BindView(R.id.auto_login_checked)
    CheckBox autoLogin_switch;
    private String password;
    private String user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        new StatusBarUtils()
                .setStatusBarColor(this, R.color.color_loginbg);
        operatorEdit.setIconResource(R.mipmap.ic_user_left);
        passwordEdit.setIconResource(R.mipmap.ic_password_left);
        password= Preferences.getOperatorPassword();
        user= Preferences.getOperatorID();

        if(!TextUtils.isEmpty(user)) {
            operatorEdit.setText(user);
            passwordEdit.setText(password);
            if(Preferences.isAutoLogin()) {
                startLogin();
            }
        }
        RxView.clicks(startLoginBtn)
              .subscribe(new Consumer<Object>() {
                  @Override
                  public void accept(Object o) throws Exception {
                      if(TextUtils.isEmpty(operatorEdit.getText().toString())
                              ||TextUtils.isEmpty(passwordEdit.getText().toString()))
                          showToast("请输入用户名/密码");
                      else
                      {
//                          switchToLogAssembly();
                          startLogin();
                      }
                  }
              });
        autoLogin_switch.setChecked(Preferences.isAutoLogin());
    }


    /**
     * 开始登录
     */
    private void startLogin() {
        String user=operatorEdit.getText().toString();
        String password=passwordEdit.getText().toString();
        startLoginBtn.setText("登录中...");
        startLoginBtn.setEnabled(false);
        operatorEdit.setEnabled(false);
        passwordEdit.setEnabled(false);
        LoginUtils.login(user, password, new LoginUtils.LoginCallBack() {
            @Override
            public void onSuccess(User user) {
                QcApplication.setUserAndSave(user);
                Preferences.saveAutoLogin(autoLogin_switch.isChecked());
                if(Preferences.getSourceType()!=TYPE_BADLOG_EMPTY)
                {
                    if(Preferences.getSourceType()==TYPE_BADLOG_WITHBTN)
                        switchToLogInjection();
                    else
                        switchToLogAssembly();
                }
                else
                    selectWorkScene();
            }

            @Override
            public void onFail(String errorMsg) {
                showLog(errorMsg);
                startLoginBtn.setText("登录");
                startLoginBtn.setEnabled(true);
                operatorEdit.setEnabled(true);
                passwordEdit.setEnabled(true);
                Snackbar.make(startLoginBtn,errorMsg,Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void switchToLogAssembly() {
        Intent intent=new Intent(LoginActivity.this, AssemblyLogActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 弹出对话框选择
     */
    private void selectWorkScene() {
        WorkScene workScene=new EmptyWorkScene();
        new MaterialDialog
                .Builder(this)
                .title(workScene.getDialogTitle())
                .content(workScene.getDialogContent())
                .positiveText(workScene.getYesButtonLabel())
                .negativeColorRes(R.color.colorNegative)
                .negativeText(workScene.getNoButtonLabel())
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        workScene.selectSwitchYes();
                        dialog.cancel();
                        switchToLogInjection();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        workScene.selectSwitchNo();
                        dialog.cancel();
                        switchToLogAssembly();
                    }
                })
                .canceledOnTouchOutside(false)
                .show();
    }

    /**
     * 切换至异常收集界面
     */
    private void switchToLogInjection() {
        Intent intent=new Intent(LoginActivity.this, InjectionLogActivity.class);
        startActivity(intent);
        finish();
    }
}
