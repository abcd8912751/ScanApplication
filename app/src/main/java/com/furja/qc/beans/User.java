package com.furja.qc.beans;

import android.text.TextUtils;

/**
 * 操作员使用员工号数据库进行登录审核
 */

public class User {
    private String userName;
    private String userId;
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public void formatUserId(String info)
    {
        String string[]= info.split(",");
        setUserId(string[0]);
    }


    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getUserId() {
        if(TextUtils.isEmpty(userId))
            return "1";
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
