package com.furja.qc.utils;

import android.accounts.NetworkErrorException;

import com.alibaba.fastjson.JSONException;
import com.furja.qc.beans.RequestLog;
import com.furja.qc.services.NetworkChangeReceiver;

import java.net.ConnectException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

import static com.furja.qc.utils.Constants.INTERNET_ABNORMAL;

public class RetryWhenUtils implements Function<Observable<Throwable>, ObservableSource<?>> {
    RequestLog requestLog=new RequestLog();
    @Override
    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                throwable.printStackTrace();
                requestLog.error();
                if(throwable instanceof JSONException)
                    return Observable.error(throwable);
                if(requestLog.isOverTimes())
                    return Observable.error(new NetworkErrorException(INTERNET_ABNORMAL));
                if(requestLog.isLastTimes()) {   //即将超时,检查下网络内外网问题
                    NetworkChangeReceiver.getInstance()
                            .checkNetworkDomain();
                }
                return Observable.timer(1, TimeUnit.SECONDS); // 1S后重新请求
            }
        });
    }
    public static RetryWhenUtils create(){
        return new RetryWhenUtils();
    }

}
