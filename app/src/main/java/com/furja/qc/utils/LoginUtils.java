package com.furja.qc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.furja.qc.QcApplication;
import com.furja.qc.beans.User;
import com.furja.qc.ui.BadLogActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import okhttp3.Call;

import static com.furja.qc.utils.Constants.FURIA_LOGIN_URL;
import static com.furja.qc.utils.Constants.INTERNET_ABNORMAL;
import static com.furja.qc.utils.Constants.OPERATOR_LOGIN_ERROR;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;

/**
 * Created by zhangmeng on 2017/12/5.
 */

public class LoginUtils {

    public static void login(final String userName, final String password, final LoginCallBack loginCallBack)
    {
        OkHttpUtils
                .get()
                .url(FURIA_LOGIN_URL +"GetLoginList")
                .addParams("user",userName)
                .addParams("password",password)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        loginCallBack.onFail(INTERNET_ABNORMAL);
                    }

                    @Override
                    public void onResponse(String response, int i) {
                        showLog(response);
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            if(jsonObject!=null)
                            {
                                showLog("jsonLength:"+jsonObject.length());
                                String info=jsonObject.getString("LoginInfo");
                                showLog("用户信息:"+info);
                                if(!isLoginSuccess(info))
                                {
                                    loginCallBack.onFail(OPERATOR_LOGIN_ERROR);
                                    return;
                                }
                                User user =new User(userName,password);
                                user.formatUserId(info);
                                showLog(getClass().toString()+"->登录至这一步即成功");
                                loginCallBack.onSuccess(user);
                            }
                        } catch (Exception e) {
                            loginCallBack.onFail(OPERATOR_LOGIN_ERROR);
                        }
                    }
                    public boolean isLoginSuccess(String loginInfo)
                    {
                        if(loginInfo==null)
                            return false;   //为空
                        if(loginInfo.contains("false"))
                            return false;
                        else
                            return true;
                    }

                });

    }


    /**
     * 使用默认登录回调 登录
     * @param user
     * @param password
     * @param useDefaultCallBack
     */
    public static void login(String user, String password, boolean useDefaultCallBack,final Context context)
    {
        login(user, password, new LoginUtils.LoginCallBack() {
            @Override
            public void onSuccess(User user) {
                QcApplication.setUserAndSave(user);
                Intent intent=new Intent(context, BadLogActivity.class);
                context.startActivity(intent);
                ((Activity)context).finish();//跳转至指定界面
            }

            @Override
            public void onFail(String errorMsg) {
                showLog(errorMsg);
                showToast(errorMsg);
            }
        });
    }
    public interface LoginCallBack
    {
        public void onSuccess(User user);
        public void onFail(String errorMsg);
    }

}
