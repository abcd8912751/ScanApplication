package com.furja.qc.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.furja.qc.utils.BadTypeQuery;

/**
 * 根据输入的代码自动从本地或远端数据库提取异常代码提示
 * 供用户选择
 */

public class SelectBadCodeActivity extends AppCompatActivity {

    public BadTypeQuery badTypeQuery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
