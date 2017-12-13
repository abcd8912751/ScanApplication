package com.furja.qc.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.furja.qc.R;
import com.furja.qc.config.DefaultWebConfig;

/**
 * Created by zhangmeng on 2017/12/1.
 */

public class SurfActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_websurf);
        WebView webView=(WebView)findViewById(R.id.websurf_webView);
        DefaultWebConfig.setDefaultConfig(webView);
        Intent intent=getIntent();
        Uri uri=intent.getData();
        if(uri==null)
            finish();
        String url=uri.toString();
        webView.loadUrl(url);
    }


}
