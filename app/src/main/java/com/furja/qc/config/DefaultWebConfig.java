package com.furja.qc.config;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Created by zhangmeng on 2017/11/10.
 */

public class DefaultWebConfig {
    public static void setDefaultConfig(WebView webView)
    {
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setSupportZoom(true);
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setDisplayZoomControls(false);
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        webSettings.setAllowFileAccess(true);
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webSettings.setLoadsImagesAutomatically(true);
//        webSettings.setDefaultTextEncodingName("utf-8");
    }
}
