package com.furja.qc.utils;
/*
*Http提交的回执
*/
public interface HttpCallback<T> {
    public void onSuccess(T message);
    public void onFail(String errorMsg);
}
