package com.furja.qc.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.furja.qc.R;


/**
 * 可清空显示并可带图标的输入EditText
 */

public class ClearableEditTextWithIcon extends android.support.v7.widget.AppCompatEditText implements  TextWatcher {
    // 删除符号
    Drawable deleteImage = getResources().getDrawable(R.mipmap.nim_icon_edit_delete);

    Drawable icon;

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    boolean isAdded;

    public ClearableEditTextWithIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ClearableEditTextWithIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearableEditTextWithIcon(Context context) {
        super(context);
        init();
    }

    private void init() {
        ClearableEditTextWithIcon.this.addTextChangedListener(this);
        deleteImage.setBounds(0, 0, deleteImage.getIntrinsicWidth(), deleteImage.getIntrinsicHeight());
        manageClearButton();
    }

    /**
     * 传入显示的图标资源id
     *
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
    public void setDeleteImage(int id) {
        deleteImage = getResources().getDrawable(id);
        deleteImage.setBounds(0, 0, deleteImage.getIntrinsicWidth(), deleteImage.getIntrinsicHeight());
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
        this.setCompoundDrawables(this.icon, this.getCompoundDrawables()[1], null, this.getCompoundDrawables()[3]);
    }

    void addClearButton() {
        setAdded(true);
        this.setCompoundDrawables(this.icon, this.getCompoundDrawables()[1], deleteImage,
                this.getCompoundDrawables()[3]);
    }



    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        ClearableEditTextWithIcon.this.manageClearButton();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public int getIntrinsicWidth() {
        return deleteImage.getIntrinsicWidth();
    }
}
