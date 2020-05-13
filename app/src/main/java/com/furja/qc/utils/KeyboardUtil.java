package com.furja.qc.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
 
/**
 * 用于监听键盘是否弹出，并获取键盘高度
 */
public class KeyboardUtil {
    Activity mContext;
  
    int virtualKeyboardHeight;
    
    int screenHeight;
    
    int screenHeight6;
    View rootView;
 
    public KeyboardUtil(Activity context) {
        this.mContext = context;
        
        screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;
        screenHeight6 = screenHeight / 6;
        rootView = mContext.getWindow().getDecorView();
    }
 
    /**
     * @param listener
     */
    public void listen(final KeyboardChangeListener listener) {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
 
            @Override
            public void onGlobalLayout() {
                
                rootView.post(new Runnable() {
                    @Override
                    public void run() {
                        Rect rect = new Rect();                        
                        rootView.getWindowVisibleDisplayFrame(rect);
                        
                        int heightDifference = screenHeight - rect.bottom;
                        if (heightDifference < screenHeight6) {
                            virtualKeyboardHeight = heightDifference;
                            if (listener != null) {
                                listener.onKeyboardHide();
                            }
                        } else {
                            if (listener != null) {
                                listener.onKeyboardShow();
                            }
                        }
                    }
                });
            }
        });
    }
    /**
     * 软键盘状态切换监听
     */
    public interface KeyboardChangeListener {
        
        void onKeyboardShow();
        
        void onKeyboardHide();
    }
}