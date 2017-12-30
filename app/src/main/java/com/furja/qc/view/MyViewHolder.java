package com.furja.qc.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.furja.qc.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.furja.qc.utils.Utils.showLog;

/**
 * Created by zhangmeng on 2017/12/3.
 */

public class MyViewHolder {
    @BindView(R.id.workinfo_title)
    TextView info_title;
    @BindView(R.id.workinfo_content)
    ClearableEditTextWithIcon info_content;

    int currPosition=0;
    public MyViewHolder(View itemView)
    {
        ButterKnife.bind(this,itemView);
    }

    public void setContentHint(String hint)
    {
        info_content.setHint(hint);
    }

    public void setNonFocusable()
    {
        info_content.clearFocus();
        info_content.setFocusable(false);
        info_content.setClickable(false);
        info_content.setFocusableInTouchMode(false);
        info_content.removeClearButton();
    }

    public boolean onTouch(MotionEvent event)
    {
        if(getContent().length()>0&&info_content.isClickable())
            info_content.addClearButton();
//        showLog("点击了:"+event.getX());
        if (event.getX() > info_content.getWidth() - info_content.getPaddingRight() - info_content.getIntrinsicWidth()) {
            info_content.setText("");
            showLog("清空了infotext");
            info_content.removeClearButton();
            return true;
        }
        return false;
    }

    public void setFocusable()
    {
        info_content.setFocusable(true);
        info_content.setClickable(true);
        info_content.setFocusableInTouchMode(true);
    }

    public void requestFocus()
    {
        info_content.requestFocus();

    }

    public void clearFocus()
    {
        info_content.clearFocus();
    }

    public ClearableEditTextWithIcon getEditText()
    {
        return info_content;
    }
    public void hideKeyBoard()
    {
        showLog("触发了我隐藏软键盘的想法");
        InputMethodManager imm = (InputMethodManager)info_content.getContext() .getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm!=null&&imm.isActive())
            imm.hideSoftInputFromWindow(info_content.getWindowToken(), 0);
    }

    public boolean isEditing()
    {
        InputMethodManager imm = (InputMethodManager)info_content.getContext() .getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm!=null&&imm.isActive())
            return true;
        else
            return false;
    }


    public String getContent()
    {
        if(TextUtils.isEmpty(info_content.getText()))
            return "";
        else
            return info_content.getText().toString();
    }
    public void setInfo_title(String title)
    {
        info_title.setText(title);
    }

    public void setInfo_content(String content)
    {
        info_content.setText(content);
    }

    /**
     * 根据传入的position设置该EditText的hint和是否可获取焦点、编辑
     * @param position
     */
    public void setPosition(int position) {
        switch (position)
        {
            case 0:
                setContentHint("扫描物料条码");
                setFocusable();
                break;
            case 4:
                setContentHint("输入员工号");
                setFocusable();
                break;
            case 5:
                setContentHint("录入机台号");
                setFocusable();
                break;
            case 1:
            case 2:
            case 3:
            case 6:
                setContentHint("");
                setNonFocusable();
                break;
        }
        if(position==currPosition)
        {
            requestFocus();
            if(getContent().length()>0)
                info_content.addClearButton();
            info_content.setSelection(info_content.getText().length());
        }
        else
        {
            info_content.clearFocus();
            info_content.removeClearButton();
        }
    }


    public int getCurrPosition() {
        return currPosition;
    }

    public void setCurrPosition(int currPosition) {
        this.currPosition = currPosition;
    }

    /**
     * 如果该EditText内容非空则清空
     */
    public void clearText() {
        if(getContent().length()>0)
            info_content.getEditableText().clear();
    }
}
