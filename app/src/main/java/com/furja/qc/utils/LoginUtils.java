package com.furja.qc.utils;

import com.alibaba.fastjson.JSON;
import com.furja.qc.beans.User;
import com.furja.qc.services.NetworkChangeReceiver;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;

import static com.furja.qc.utils.Constants.INTERNET_ABNORMAL;
import static com.furja.qc.utils.Constants.OPERATOR_LOGIN_ERROR;
import static com.furja.qc.utils.Constants.getVertxUrl;
import static com.furja.qc.utils.Utils.showLog;

/**
 * Created by zhangmeng on 2017/12/5.
 */

public class LoginUtils {
    private static int loginCount=0;
    public static void login(final String userName, final String password, final LoginCallBack loginCallBack) {
        String url = getVertxUrl();
        RetrofitHelper helper = RetrofitBuilder.getHelperByUrl(url);
        helper.login(userName,password)
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWhenUtils.create())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response->{
                    if(response.getCode()>0){
                        User user =response.getResult();
                        showLog(JSON.toJSONString(user)+"->登录成功");
                        loginCallBack.onSuccess(user);
                    }
                    else {
                        loginCallBack.onFail(response.getMessage());
                    }
                },error->{
                    error.printStackTrace();
                    loginCallBack.onFail(INTERNET_ABNORMAL);
                });
    }


    public interface LoginCallBack
    {
        public void onSuccess(User user);
        public void onFail(String errorMsg);
    }

}
