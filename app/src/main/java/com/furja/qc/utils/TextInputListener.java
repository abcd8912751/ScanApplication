package com.furja.qc.utils;

/**
 * 输入框 输入完成监听
 */
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import static com.furja.qc.utils.Constants.TAG_SCAN_BARCODE;
import static com.furja.qc.utils.Utils.showLog;
import static com.furja.qc.utils.Utils.showToast;

/**
 * 输入框输入完成的监听,当输入完成调用其中方法
 */
public class TextInputListener implements View.OnKeyListener,TextView.OnEditorActionListener {
    private long lastTimeMillis;
    SharpBus sharpBus;
    public static String INPUT_ERROR="格式错误";
    public TextInputListener(){
        sharpBus=SharpBus.getInstance();
    }

    public void bindEditText(EditText editBarCode) {
        if(editBarCode!=null){
            editBarCode.addTextChangedListener(TextChanger.flat(new TextChanger.ChangeListener() {
                @Override
                public void afterTextChanged(String text) {
                    if(text.endsWith("\n")) {
                        sharpBus.post(TAG_SCAN_BARCODE, getPureString(text));
                    }
                }
            }));
            editBarCode.setOnKeyListener(this);
            editBarCode.setOnEditorActionListener(this);
        }
    }

    public static void bind(EditText editBarCode) {
        new TextInputListener().bindEditText(editBarCode);
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if (System.currentTimeMillis() - lastTimeMillis > 1000)
                lastTimeMillis = System.currentTimeMillis();
            else
                return false;
            TextView textView = (TextView) view;
            if(TextUtils.isEmpty(textView.getText()))
                shakeOwnSelf(textView);
            excuteInput(textView.getText()+"");
        }
        return false;
    }
    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE) {
            if (System.currentTimeMillis() - lastTimeMillis > 1000)
                lastTimeMillis = System.currentTimeMillis();
            else
                return false;
            if(TextUtils.isEmpty(textView.getText()))
                shakeOwnSelf(textView);
            excuteInput(textView.getText()+"");
        }
        return false;
    }

    /**
     * 处理输入的字符
     * @param input
     */
    private void excuteInput(String input) {
        input = getPureString(input);
        if(!TextUtils.isEmpty(input))
            sharpBus.post(TAG_SCAN_BARCODE,input);
    }



    /**
     * 抖动自己
     * @param view
     */
    private void shakeOwnSelf(View view)
    {
        Animation shakeAnimation
                = new TranslateAnimation(0, 4, 0, 0);
        //设置一个循环加速器，使用传入的次数就会出现摆动的效果。
        shakeAnimation.setInterpolator(new CycleInterpolator(2));
        shakeAnimation.setDuration(500);
        view.startAnimation(shakeAnimation);
    }

    /**
     * 获取去除 回车/换行符的字符串
     * @param input
     * @return
     */
    private String getPureString(String input) {
        input=input.toUpperCase();
        input = input.replace("\n", "");
        input = input.replace("\r", "");
        return input;
    }
}
