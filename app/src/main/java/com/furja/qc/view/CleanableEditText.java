package com.furja.qc.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.furja.qc.R;

import java.lang.reflect.Field;

import androidx.annotation.RequiresApi;

import static com.furja.qc.utils.Utils.showLog;

/**
 * 可清空显示并可带图标的输入EditText,防止内存泄漏
 */
public class CleanableEditText extends androidx.appcompat.widget.AppCompatEditText implements View.OnTouchListener {
    // 删除符号
    Drawable endImage = getResources().getDrawable(R.mipmap.nim_icon_edit_delete);
    Drawable icon;
    InputConnection inputConnection;
    public boolean isAdded() {
        return isAdded;
    }
    public void setAdded(boolean added) {
        isAdded = added;
    }
    boolean isAdded;
    boolean canClear;
    public CleanableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AppCompatTextHelper);
        if(typedArray.hasValue(R.styleable.AppCompatTextHelper_android_drawableEnd)){
            setCanClear(false);
            endImage = typedArray.getDrawable(R.styleable.AppCompatTextHelper_android_drawableEnd);
        }
        else {
            setCanClear(true);
        }
        typedArray.recycle();
        init();
    }

    public CleanableEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public CleanableEditText(Context context) {
        this(context, null);
    }

    private void init() {
        CleanableEditText.this.setOnTouchListener(this);
        endImage.setBounds(0, 0, endImage.getIntrinsicWidth(), endImage.getIntrinsicHeight());
        manageClearButton();
    }

    /**
     * 防止内存泄漏
     * @param outAttrs
     * @return
     */
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        inputConnection = super.onCreateInputConnection(outAttrs);
        return inputConnection;
    }

    /**
     * 传入显示的图标资源id
     * @param id
     */
    public void setIconResource(int id) {
        icon = getResources().getDrawable(id);
        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        manageClearButton();
    }

    /**
     * 传入删除图标资源id
     * @param id
     */
    public void setEndImage(int id) {
        endImage = getResources().getDrawable(id);
        endImage.setBounds(0, 0, endImage.getIntrinsicWidth(), endImage.getIntrinsicHeight());
        manageClearButton();
    }

    void manageClearButton() {
        if (this.getText().toString().equals(""))
            removeClearButton();
        else
            addClearButton();
    }

    public void removeClearButton() {
        setAdded(false);
        if(canClear) {
            this.setCompoundDrawables(this.icon, this.getCompoundDrawables()[1], null, this.getCompoundDrawables()[3]);
        }
        else {
            this.setCompoundDrawables(this.icon, this.getCompoundDrawables()[1], endImage,
                    this.getCompoundDrawables()[3]);
        }
    }

    void addClearButton() {
        setAdded(true);
        this.setCompoundDrawables(this.icon, this.getCompoundDrawables()[1], endImage,
                this.getCompoundDrawables()[3]);
    }


    public int getIntrinsicWidth() {
        return endImage.getIntrinsicWidth();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(canClear) {
            CleanableEditText et = CleanableEditText.this;
            if (et.getCompoundDrawables()[2] == null)
                return false;
            if (event.getAction() != MotionEvent.ACTION_UP)
                return false;
            if (event.getX() > et.getWidth() - et.getPaddingRight() - endImage.getIntrinsicWidth()) {
                et.setText("");
                CleanableEditText.this.removeClearButton();
            }
        }
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            if (inputConnection != null) {
                inputConnection.finishComposingText();
                Class inputClass = inputConnection.getClass();
                Field nameField = inputClass.getDeclaredField("mTextView");
                nameField.setAccessible(true);
                nameField.set(inputConnection, null);
                Class superClass = inputClass.getSuperclass();
                Field targetField = superClass.getDeclaredField("mTargetView");
                targetField.setAccessible(true);
                targetField.set(inputConnection, null);
                inputConnection = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDetachedFromWindow();
    }


    public boolean isCanClear() {
        return canClear;
    }

    public void setCanClear(boolean canClear) {
        this.canClear = canClear;
    }
}
