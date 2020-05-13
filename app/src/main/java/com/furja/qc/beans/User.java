package com.furja.qc.beans;

import android.text.TextUtils;

/**
 * 操作员使用员工号数据库进行登录审核
 */

public class User {
    private String userName;
    private String userId;
    private String password;
    private String loginInfo;
    private String packageName;
    private int token;
    private String note;

    public User() {
    }

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

    public void formatUserId(String info) {
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
        return userId;
    }

    public String getLoginInfo() {
        return loginInfo;
    }

    public User setLoginInfo(String loginInfo) {
        this.loginInfo = loginInfo;
        return this;
    }

    public String getPackageName() {
        return packageName;
    }

    public User setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public int getToken() {
        return token;
    }

    public User setToken(int token) {
        this.token = token;
        return this;
    }

    public String getNote() {
        return note;
    }

    public User setNote(String note) {
        this.note = note;
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
